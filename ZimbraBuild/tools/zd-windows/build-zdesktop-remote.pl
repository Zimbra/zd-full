#!/usr/bin/perl
# 
# 
# 
use strict;
use Cwd;
use MIME::Lite;
use Net::SMTP;
use File::Basename;

my $time=0;

my $branch=$ARGV[0];
if ($#ARGV > 0) {
  $time=$ARGV[1];
}

chomp($branch);
chomp($time);

exit unless $branch;

my $tstart = time;

$ENV{JAVA_HOME}="C:\\Progra~1\\Java\\jdk1.6.0_21";
$ENV{P4USER}="build";
$ENV{P4PASSWD}="build1pass";
$ENV{P4PORT}="perforce-zimbra.eng.vmware.com:1666";

my $cygwin=1;
my $keepBuilds=8;

my $base = '/src';
my $cygbase = "/cygdrive/c";

my $arch = "WINDOWS";
my $buildbase="/cygdrive/z/current/$arch";
my $buildsrc="$buildbase/$branch";
my $winroot="Z:/current/$arch";

my $p4user = "build";
my $p4pass = "build1pass";
my $p4port = "depot.lab.zimbra.com:1666";

my $ant = "ant.bat";

my (@d, $date, $dtime);

if ($time == 0) {
  @d = localtime();
  $date = sprintf ("%d/%02d/%02d:%02d:%02d:%02d", $d[5]+1900,$d[4]+1,$d[3],$d[2],$d[1],$d[0]);
  $dtime = sprintf ("%d%02d%02d%02d%02d%02d", $d[5]+1900,$d[4]+1,$d[3],$d[2],$d[1],$d[0]);
} else {
  my ($year,$month,$day,$hour,$minute,$second);
  if (length($time) == 6) {
    @d = localtime();
    $year=$d[5]+1900;
    $month=$d[4]+1;
    $day=$d[3];
    $hour=substr($time,0,2);
    $minute=substr($time,2,2);
    $second=substr($time,4,2);
    $date = sprintf("%d/%02d/%02d:%02d:%02d:%02d",$year,$month,$day,$hour,$minute,$second);
    $dtime = sprintf("%d%02d%02d%02d%02d%02d",$year,$month,$day,$hour,$minute,$second);
  } elsif (length($time) == 14) {
    $year=substr($time,0,4);
    $month=substr($time,4,2);
    $day=substr($time,6,2);
    $hour=substr($time,8,2);
    $minute=substr($time,10,2);
    $second=substr($time,12,2);
    $date= sprintf("%d/%02d/%02d:%02d:%02d:%02d",$year,$month,$day,$hour,$minute,$second);
    $dtime=$time;
  } else {
    print "Error: Supplied time must either be hhmmss or YYYYMMDDHHMMSS\n";
    exit 1;
  }
}

my $release = $dtime."_ZDESKTOP";

my $builddir = "$buildsrc/$release";
my $winbuilddir = "$winroot/$branch/$release";
my $logdir = "$builddir/logs";
my $p4log = "$logdir/p4.log";
my $buildlog = "$logdir/build.log";
my $reportLog = "$logdir/buildReport.txt";
my @message;

my $p4client;
my $P4 = "p4 -d $winbuilddir -u $p4user -P $p4pass -p $p4port";

addToReport("*** build-zdesktop-remote.pl $P4 $winbuilddir\n");
init();
createLock();
my $start_str = localtime($tstart);
addToReport("*** Building ZDESKTOP on WINDOWS at $dtime\n");
addToReport("*** Overall start time: $start_str\n");
$p4client = createClient();
#deleteOldBuilds();
getSource();
build();
my $tfinish = time;
my $finish_str = localtime($tfinish);
my $duration = getDuration($tstart,$tfinish);
addToReport("*** Finished at: $finish_str\n");
addToReport("*** Total build duration: $duration\n\n");
sendMail("COMPLETE", "");
saveReport();
deleteLock();
exit(0);

sub createLock {
  if (-f "$buildsrc/.lock") {
     print "Error: Build in progress\n";
     exit (1);
  }
  open(LOCK, ">$buildsrc/.lock");
  close(LOCK);
  open(PROGR, ">$builddir/.inprogress");
  close(PROGR);
}

sub deleteLock {
  unlink "$buildsrc/.lock";
  unlink "$builddir/.inprogress";
}

sub deleteBuild {
  my ($build) = @_;
  my ($label);
  doCmd("rm -rf $build");
}

sub deleteOldBuilds {
  my $dir="$buildsrc";
  
  print "Dir is $dir\n";
  
  opendir DIR, $dir;
  my @builds= grep !/^\.\.?/, readdir DIR;
  closedir DIR;

  @builds = reverse sort @builds;

  for (my $i = 0; $i <= $#builds; ) {
    if (-f "$dir/$builds[$i]/FAILED") {
      deleteBuild ("$dir/$builds[$i]");
      splice (@builds, $i, 1);
    } elsif (-f "$dir/$builds[$i]/RELEASED" ||
      -f "$dir/$builds[$i]/ARCHIVED" ||
      -f "$dir/$builds[$i]/.inprogress") {
      splice (@builds, $i, 1);
    } else {
      $i++;
    }
  }
  if ($#builds >= $keepBuilds) {
    for (my $i = $keepBuilds; $i <= $#builds; $i++) {
      #print "Would delete: $dir/$builds[$i]\n";
      deleteBuild ("$dir/$builds[$i]");
    }
  }
}

sub doCmd {
  my $cmdLine = shift(@_);
  my $rc;
  print $cmdLine . "\n";
  $rc = 0xffff & system($cmdLine);
  return $rc;
}

sub init {
  my $rc;
  if (-d "$builddir") {
    if (-f "$builddir/FAILED") {
      doCmd( "rm -rf $builddir");
    } else {
      addToReport("Build already exists!");
      exit(0);
    }
  }
  if ( !-d $buildsrc ) {
    $rc=mkdir "$buildsrc";
    quit("SETUP", "Failed to create $buildsrc") if (!$rc);
  }
  $rc=mkdir "$builddir";
  quit("SETUP", "Failed to create $builddir") if (!$rc);
  addToReport("*** Creating log dir $logdir\n");
  $rc=mkdir "$logdir";
  quit("SETUP", "Failed to create $logdir") if (!$rc);
}

sub getSource {
  my $stime = time;
  my $rc;
  addToReport("*** Syncing P4 to $builddir at $dtime\n");
  addToReport("$P4 -c $p4client sync -f ...@".$date."\n");
  $rc=doCmd ("$P4 -c $p4client sync -f ...@".$date." >> $p4log");
  if($rc) {
    includeLinesFromLog("12", "$p4log");
    quit("P4","checkOut: $!");
  }
  my $duration = getDuration($stime);
  addToReport("*** Source checkout duration: $duration\n");
}

sub build {
  my $rc;
  chdir("$builddir/ZimbraOffline");
  addToReport("*** BUILDING IN $builddir\n");
  addToReport("$ant -f installer-ant.xml\n");
  my $stime = time;
  $rc=doCmd("$ant -f installer-ant.xml >$buildlog");
  $duration = getDuration($stime);
  addToReport("*** Compile duration: $duration\n");
  if ($rc) {
    includeLinesFromLog("30", "$buildlog");
    quit("BUILD","$!");
  }
}

sub createClient {
  my $stime = time;
  my $rc;
  my $client="BUILD_".$branch."_".$release."_".$arch;
  my $template="$cygbase$base/template/BUILD_init.txt";
  my $ctemplate="$builddir/logs/build_int_client.txt";
  
  addToReport("*** Creating client $client\n");
  open(IN, "<$template");
  open(OUT, ">$ctemplate");
  while (<IN>) {
    $_ =~ s/\@\@ARCH\@\@/$arch/g;
    $_ =~ s/\@\@BRANCH\@\@/$branch/g;
    $_ =~ s/\@\@TAG\@\@/$branch/g;
    $_ =~ s/\@\@RELEASE\@\@/$release/g;
    $_ =~ s/\@\@BUILD_ROOT\@\@/$winbuilddir/g;
    print OUT "$_";
  }
  $rc=doCmd("$P4 -c $client client -i < $ctemplate >>$p4log");
  if ($rc) {
    includeLinesFromLog("12", "$p4log");
    quit("P4","createClient: $!");
  }
  addToReport("*** Initial P4 sync to $builddir at $dtime\n");
  addToReport("$P4 -c $client sync -f ...@".$date."\n");
  $rc=doCmd("$P4 -c $client sync -f ...@".$date." >>$p4log");
  if ($rc) {
    includeLinesFromLog("12", "$p4log");
    quit("P4","checkOut: $!");
  }
  my $duration = getDuration($stime);
  addToReport("*** Initial p4 sync checkout duration: $duration\n");
  $template="$builddir/ZimbraBuild/templates/BUILD_template_ZDESKTOP";
  $ctemplate="$builddir/logs/build_final_client.txt";
  open(IN, "<$template");
  open(OUT, ">$ctemplate");
  while (<IN>) {
    $_ =~ s/\@\@ARCH\@\@/$arch/g;
    $_ =~ s/\@\@BRANCH\@\@/$branch/g;
    $_ =~ s/\@\@TAG\@\@/$branch/g;
    $_ =~ s/\@\@RELEASE\@\@/$release/g;
    $_ =~ s/\@\@BUILD_ROOT\@\@/$winbuilddir/g;
    print OUT "$_";
  }
  addToReport("*** Updating client $client\n");
  $rc=doCmd("$P4 -c $client client -i < $ctemplate >>$p4log");
  if ($rc) {
    includeLinesFromLog("12", "$p4log");
    quit("P4","createClient: $!");
  }
  return $client;
}

sub sendMail {
  my $stage = shift;
  my $err = shift;
  my $state = ($err ? "failed" : "passed");

  my $subj = "Build $state on $stage WINDOWS ZDESKTOP $dtime";
  my $dest;
  if ($state eq "failed") {
    $dest = "zcs-builds\@zimbra.com,desktop-bugs\@zimbra.com";
  } else {
    $dest = "zcs-builds\@zimbra.com";
  }
  my $from = "build\@zimbra.com";
  my $smtp = "mta01.zimbra.com";
  my $mesg = "Build $state in stage $stage\n";
  $mesg .= "$dtime ZDESKTOP $branch on WINDOWS\n\n";
  $mesg .= "Source not labeled.\n\n";
  $mesg .= "@message\n";

  eval {
    my $msg = MIME::Lite->new(
      From => $from,
      To   => $dest,
      Subject => $subj,
      Type    => 'multipart/mixed',
    ) or warn "ERROR: Can't open: $!\n";

    $msg->attach(
      Type => 'text',
      Data => $mesg,
    ) or warn "Error adding the text message: $!\n";

    addAttachment($msg, $p4log)
      if ($state eq "failed" && $stage eq "P4");
    addAttachment($msg, $buildlog)
      if ($state eq "failed");
    #addAttachment($msg, $config{changeLog});
    #addAttachment($msg, $config{bugLog});
    #addAttachment($msg, $config{installLog});
    #addAttachment($msg, $config{testLog});
    MIME::Lite->send('smtp', $smtp, Timeout=>120);
    $msg->send;
  };

  if ($@) {
    addToReport("Failed to email report: $@\n");
  } else {
    addToReport("Email report sent to $dest\n");
  }

}

sub addAttachment($$) {
  my ($ref, $file) = @_;

  return unless (-f "$file");

  my $filename = basename($file);
  $filename =~ s/\.log$/\.txt/;
  $ref->attach(
    Type => 'text/plain',
    Path => $file,
    Filename => $filename,
    Disposition => 'attachment',
  ) or warn "Error adding $file: $!\n";

}

sub addToReport($) {
  my ($line) = @_;
  open(R, ">>$reportLog");
  print R $line;
  close(R);
  push(@message, $line);
}

sub getDuration {
  my ($stime,$ftime) = @_;
  $ftime = time if (!defined $ftime);
  my $timediff = $ftime-$stime;
  my($hours) = int($timediff/3600);
  my($minutes) = int(($timediff - ($hours * 3600))/60);
  my($seconds) = int (($timediff - ($hours * 3600) - ($minutes * 60)));
  return sprintf("%2.2d:%2.2d:%2.2d",$hours,$minutes,$seconds);
}

sub saveReport() {
        open(R, ">reportLog");
        print R @message;
        close(R);
}

sub deleteClient {
  my $c = shift;
  addToReport("*** DELETING CLIENT $c\n");
  my $cmd = "$P4 -c $c client -d $c >> $p4log 2>&1";
  my $rc = 0xffff & system($cmd);
  return $rc;
}

sub includeLinesFromLog($$) {
  my ($num,$log) = @_;
  open(L, "$log");
  my @log = <L>;
  close(L);
  my $s = ($#log > $num ? $#log-$num : 0);
  foreach my $i ($s..$#log) {
    addToReport($log[$i]);
  }
}

sub quit() {
  my ($stage,$err) = (@_);
  $err = ($err ? $err : "unknown");
  addToReport("FAILED IN $stage: $err\n\n");
  #revertChanges($config{p4Client});
  deleteLock();
  deleteClient($p4client);
  `echo "FAILED IN $stage: $err\n\n" > $builddir/FAILED`;
  sendMail($stage, $err);
  exit (1);
}
