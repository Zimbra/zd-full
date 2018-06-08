#!/usr/bin/perl

use strict;

use Getopt::Std;
use LWP::Simple;
use MIME::Lite;
use Net::SMTP;
use File::Basename;

$SIG{QUIT} = sub { my $sig = shift; &quit("KILLED", "Received signal $sig"); };
$SIG{INT} = sub { my $sig = shift; &quit("KILLED", "Received signal $sig"); };
$SIG{KILL} = sub { my $sig = shift; &quit("KILLED", "Received signal $sig"); };
my $build_args=join(' ',@ARGV);
my $build_id=getpwnam("build");
if ( $> != $build_id ) {
  warn "*** RUN AS build USER!\n\n";
  exit (1);
}

my %GlobalOpts = ();
my %config = ();

$config{buildUser} = "build";
$config{buildHome} = "/home/$config{buildUser}";
$config{sshKey} = "$config{buildHome}/dfood.key";
$config{buildDir} = "$config{buildHome}/builds";
$config{confDir} = "$config{buildHome}/conf";
$config{scriptDir} = "$config{buildHome}/scripts";
$config{htmlDir} = "$config{buildHome}/httpd/htdocs";
$config{keepBuilds} = 12;
$config{tstart} = time;

$ENV{JAVA_HOME}="/usr/local/java";
$ENV{PATH}="/usr/local/mysql/bin:/usr/local/ant/bin:$ENV{JAVA_HOME}/bin:$ENV{PATH}:/Developer/Applications/Utilities/PackageMaker.app/Contents/Resources/:/usr/local/p4/bin";

usage() unless (getopts('b:t:a:RTr:c:s:wudH:vz', \%GlobalOpts));

checkOpts();
$config{dateStamp} = getDateStamp($GlobalOpts{r});
$config{verbose} = ($GlobalOpts{v} ? 1 : 0);
$config{arch} = $GlobalOpts{a};
$config{type} = $GlobalOpts{t};
$config{branch} = $GlobalOpts{b};
$config{change} = $GlobalOpts{c};
$config{mainHost} = $GlobalOpts{H};
$config{release} = "$config{dateStamp}_$config{type}";
$config{subrelease} = $GlobalOpts{s};
if ($config{change} ne "") {
 $config{subrelease}=getCurrentDayStamp() unless $config{subrelease};
 $config{buildRoot} = "$config{buildDir}/$config{arch}/$config{branch}/$config{release}-$config{subrelease}";
} else {
 $config{buildRoot} = "$config{buildDir}/$config{arch}/$config{branch}/$config{release}";
}

#$config{keepBuilds} = 12 if ($config{type} eq "ZDESKTOP");

if (-d $config{buildRoot}) {
  if (-f "$config{buildRoot}/FAILED") {
    `rm -rf $config{buildRoot}`;
  } else {
    addToReport("Build already exists!");
    exit 0;
  }
}

$config{logDir}     = "$config{buildRoot}/logs";
$config{buildLog}   = "$config{logDir}/build.log";
$config{reportLog}  = "$config{logDir}/buildReport.txt";
$config{p4Log}      = "$config{logDir}/p4.log";
$config{installLog} = "$config{logDir}/install.log";
$config{testLog}    = "$config{logDir}/test.log";
$config{changeLog}  = "$config{logDir}/change.log";
$config{bugLog}     = "$config{logDir}/bug.log";

$config{p4User}     = "build";
if ($config{arch} eq "MACOSXx86") {
  $config{p4Port}     = "perforce-zimbra.eng.vmware.com:1666";
} else {
  $config{p4Port}     = "zre-matrix.eng.vmware.com:1666";
}
$config{P4} = "p4 -u $config{p4User} -p $config{p4Port}";

$config{lastBuild}  = getLastBuild();
$config{lastRelease} = getLastRelease();

createLock();
addToReport("*** build.pl $build_args\n");
addToReport("*** Creating log dir $config{logDir}\n");
my $rc = 0xffff & system("mkdir -p $config{logDir}");
quit("SETUP", "Failed to create $config{logDir}") if ($rc);
my $start_str = localtime($config{tstart});
addToReport("*** Building $config{type} on $config{arch} at $config{dateStamp}\n");
addToReport("*** Last build: $config{lastBuild}\n");
addToReport("*** Last release: $config{lastRelease}\n");
addToReport("*** Overall start time: $start_str\n");

$config{p4Client} = createClient();

deleteOldBuilds();

linkBuilds($config{arch},$config{branch});

updateMainBuildServer() if $GlobalOpts{H};

checkOut();

if (!$config{mainhost}) {
  getChanges();
  getBugs();
}

updateMainBuildServer() if $GlobalOpts{H};

$config{revision} = getRevision();

build();

addSourceTag() if ($GlobalOpts{r} eq "");

createDocs();

reInstall() if ($GlobalOpts{R} || $GlobalOpts{T});

linkBuilds($config{arch},$config{branch});

if ($GlobalOpts{u}) {
  if (lc($config{type}) eq "zdesktop") {
    if ($config{branch} =~ /FRANKLIN/) {
      updateZdesktopBits() ;
  }
  } else {
    updateBits();
  }
}

runTests() if ($GlobalOpts{T});

$config{buildNumber} = updateBuildNum() if ($GlobalOpts{r} eq "");

deleteClient($config{p4Client});

updateMainBuildServer() if $GlobalOpts{H};

informQA();
$config{tfinish} = time;
my $finish_str = localtime($config{tfinish});
my $duration = getDuration($config{tstart},$config{tfinish});

addToReport("*** Finished at: $finish_str\n");
addToReport("*** Total build duration: $duration\n\n");

sendMail("COMPLETE", "");

addToReport("*** BUILD COMPLETE\n\n");

if ($GlobalOpts{d}) {
  tagBuild();
}

saveReport();

deleteLock();

updateMainBuildServer() if $GlobalOpts{H};
#======================================================================
# Subroutines
#======================================================================

sub getDuration {
  my ($stime,$ftime) = @_;
  $ftime = time if (!defined $ftime);
  my $timediff = $ftime-$stime;
  my($hours) = int($timediff/3600);
  my($minutes) = int(($timediff - ($hours * 3600))/60);
  my($seconds) = int (($timediff - ($hours * 3600) - ($minutes * 60)));
  return sprintf("%2.2d:%2.2d:%2.2d",$hours,$minutes,$seconds);
}

sub updateMainBuildServer {
  my $failed = shift;
  if ($config{arch} =~ /MACOSX/) {
    addToReport("*** RSYNCING $config{buildRoot} to $config{mainHost}\n");
  my ($cmd,$ssh,$ddir,$rc);
    $ssh = "/usr/bin/ssh -o StrictHostKeyChecking=no -o CheckHostIP=no -i $config{sshKey} $config{buildUser}\@$config{mainHost}";
  if ($config{subrelease}) {
    $ddir = "$config{buildDir}/$config{arch}/$config{branch}/$config{release}-$config{subrelease}";
  } else {
    $ddir = "$config{buildDir}/$config{arch}/$config{branch}/$config{release}";
  }
  if ($config{arch} eq "MACOSX") {
      $cmd = "mkdir -p ${ddir}/logs ${ddir}/ZimbraBuild/ppc";
  } elsif ($config{arch} eq "MACOSXx86_10.7") {
      $cmd = "mkdir -p ${ddir}/logs ${ddir}/ZimbraBuild/x86_64";
  } else {
      $cmd = "mkdir -p ${ddir}/logs ${ddir}/ZimbraBuild/i386";
  }
    $rc = 0xffff & system ("$ssh \"$cmd\"");
    quit("RSYNC","Remote command FAILED: $ssh $cmd $!") if ($rc);

  unless ($failed) {
      $cmd = "rm -f ${ddir}/FAILED 2>/dev/null";
      $rc = 0xffff & system ("$ssh \"$cmd\"");
    }

    my $rsync;
    if ($failed) {
      $rsync = "/usr/bin/rsync --rsh=\"ssh -o StrictHostKeyChecking=no -o CheckHostIP=no -l $config{buildUser} -i $config{sshKey}\" -av --stats --delete $config{buildRoot}/FAILED $config{mainHost}:${ddir}/FAILED >> $config{logDir}/rsync.log";
      addToReport("RSYNC: $rsync\n");
      $rc = 0xffff & system ("$rsync");
      exit 1 if ($rc);
    if ($config{arch} =~ /MACOSX/) {
        $rsync = "/usr/bin/rsync --rsh=\"ssh -o StrictHostKeyChecking=no -o CheckHostIP=no -l $config{buildUser} -i $config{sshKey}\" -av --stats --delete $config{buildRoot}/logs/ $config{mainHost}:${ddir}/logs/ >> $config{logDir}/rsync.log";
        addToReport("RSYNC: $rsync\n");
        $rc = 0xffff & system ("$rsync");
        exit 1 if ($rc);
    }
    } else {
    my @rsyncdirs = qw(logs/ ZimbraBuild/ppc/ ZimbraBuild/i386/ ZimbraBuild/x86_64/ RELEASED .inprogress FAILED);
    foreach my $i (@rsyncdirs) {
      next unless (-e "$config{buildRoot}/$i");
       $rsync = "/usr/bin/rsync --rsh=\"ssh -o StrictHostKeyChecking=no -o CheckHostIP=no -l $config{buildUser} -i $config{sshKey}\" -av --stats --delete $config{buildRoot}/$i $config{mainHost}:${ddir}/$i >> $config{logDir}/rsync.log";
      addToReport("$rsync\n");
      $rc = 0xffff & system ("$rsync");
      quit ("RSYNC","$!") if ($rc);
    }
    
    }

  if (! -e "$config{buildRoot}/.inprogress" ) {
      $cmd = "rm -f ${ddir}/.inprogress 2>/dev/null";
      $rc = 0xffff & system ("$ssh \"$cmd\"");
    }

  }

  my $ssh = "/usr/bin/ssh -o StrictHostKeyChecking=no -o CheckHostIP=no -i $config{sshKey} $config{buildUser}\@$config{mainHost}";
  my $cmd = "$config{scriptDir}/build.pl -w";
  my $rc = 0xffff & system ("$ssh \"$cmd\"");
  quit("SSH","Remote ssh command FAILED: $ssh $cmd $!") if ($rc);

}

sub createLock {
  if ($GlobalOpts{r} eq "") {
    if ( -f "$config{buildDir}/$config{arch}/$config{branch}/.lock" ) {
      addToReport("Build in progress!\n\n");
      exit (1);
    }
  }
  if ( -f "$config{buildRoot}/.inprogress") {
    addToReport("Build in progress!\n\n");
    exit (1);
  }
  `mkdir -p $config{buildRoot}`;
  if ($GlobalOpts{r} eq "") {
    `touch "$config{buildDir}/$config{arch}/$config{branch}/.lock"`;
  }
  `touch "$config{buildRoot}/.inprogress"`;
}

sub deleteLock {
  unlink "$config{buildDir}/$config{arch}/$config{branch}/.lock";
  unlink "$config{buildRoot}/.inprogress";
}

sub deleteBuild {
  my ($build) = @_;
  my ($label);
  addToReport("*** Deleting build $build\n");
  if (-f "$build/.label") {
    chomp($label = `cat $build/.p4label`);
    #deleteSourceTag($label);
  }
  &deleteQAbuild($build);
  `rm -rf $build`;
}

sub getRevision {
  my ($major,$minor,$micro,$build);
  chomp($major=`cat $config{buildRoot}/ZimbraBuild/RE/MAJOR`);
  chomp($minor=`cat $config{buildRoot}/ZimbraBuild/RE/MINOR`);
  chomp($micro=`cat $config{buildRoot}/ZimbraBuild/RE/MICRO`);
  if ($config{type} eq "ZDESKTOP") {
    chomp($build=`grep ^offline.buildid= $config{buildRoot}/ZimbraOffline/build.properties`);
  $build =~ s/^offline.buildid=//;
  } else {
    chomp($build=`cat $config{buildRoot}/ZimbraBuild/RE/BUILD`);
  }

  return "${major}_${minor}_${micro}_${build}";
}

sub updateBuildNum {
  my ($rc, $curBuild, $cmd);

  return if ($config{type} =~ m/ISYNC|TOASTER/);

  if ($config{type} eq "ZDESKTOP") {
    my $buildfile="$config{buildRoot}/ZimbraOffline/build.properties";
    open(BUILD, "$buildfile");
    open(NEW,">${buildfile}.tmp");
    while (<BUILD>) {
      if (/^offline.buildid=(\d+)/) {
        $curBuild = $1;
        $curBuild++;
        print NEW "offline.buildid=$curBuild\n";
        next;
      }
      print NEW;
     
    }
    close(BUILD);
    close(NEW);
    $cmd = "$config{P4} -c $config{p4Client} edit ${buildfile}";
    $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
    if ($rc) {
      includeLinesFromLog("12", "$config{p4Log}");
      quit ("P4","updateBuildNum (edit): $!");
    }
    rename("${buildfile}.tmp", ${buildfile}) if (-s "${buildfile}.tmp");

    $cmd = "$config{P4} -c $config{p4Client} change -o | ".
      "sed -e 's/<enter description here>/bug: 57717 ".
      "Auto update ZCS BUILD to $curBuild/' | ".
      "$config{P4} -c $config{p4Client} submit -i";
  
    $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
    if ($rc) {
      includeLinesFromLog("12", "$config{p4Log}");
      quit("P4","updateBuildNum (submit): $!");
    }
  
  } else {
    chomp($curBuild=`cat $config{buildRoot}/ZimbraBuild/RE/BUILD`);
    $curBuild++;

    $cmd = "$config{P4} -c $config{p4Client} sync  $config{buildRoot}/ZimbraBuild/RE/BUILD";
    $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
    $cmd = "$config{P4} -c $config{p4Client} edit $config{buildRoot}/ZimbraBuild/RE/BUILD";
    $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
    if ($rc) {
      includeLinesFromLog("12", "$config{p4Log}");
      quit ("P4","updateBuildNum (edit): $!");
    }
  
    open(F, ">$config{buildRoot}/ZimbraBuild/RE/BUILD");
    print F "${curBuild}";
    close(F);
  
    $cmd = "$config{P4} -c $config{p4Client} change -o | ".
      "sed -e 's/<enter description here>/bug: 57717 ".
      "Auto update ZCS BUILD to $curBuild/' | ".
      "$config{P4} -c $config{p4Client} submit -i";
  
    $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
    if ($rc) {
      includeLinesFromLog("12", "$config{p4Log}");
      quit("P4","updateBuildNum (submit): $!");
    }
  }

  return($curBuild);
}

sub checkOut {
  my $stime = time;

  return if (lc($config{type}) eq "zca");
  addToReport("*** Syncing P4 to $config{buildRoot} at $config{dateStamp}\n");
  my $r = dateStampToRevDate($config{dateStamp});
  my $cmd;
  $cmd = "cd $config{buildRoot}; $config{P4} -Zproxyverbose -c $config{p4Client} sync -f ...\@$r >> $config{p4Log} 2>&1";
  addToReport("$cmd\n");
  my $rc = 0xffff & system($cmd);
  if ($rc) {
    includeLinesFromLog("12", "$config{p4Log}");
    quit("P4","checkOut: $!");
  }
  if ($config{change} ne "") {
    foreach my $c (sort { $a <=> $b } split(/,/,$config{change})) {
      $cmd = "cd $config{buildRoot}; $config{P4} -c $config{p4Client} sync -f ...\@$c,$c >> $config{p4Log} 2>&1";
      addToReport("$cmd\n");
      my $rc = 0xffff & system($cmd);
      if ($rc) {
        includeLinesFromLog("12", "$config{p4Log}");
        quit("P4","checkOut: $!");
      }
    }
  }
  my $duration = getDuration($stime);
  addToReport("*** Source checkout duration: $duration\n");

}

sub createClient {


  my $initial_template = "$config{confDir}/templates/BUILD_init";
  my $client = "BUILD_$config{branch}_$config{dateStamp}_$config{type}_$config{arch}";

  my $zimbra;

  addToReport("*** Creating client $client\n");
  my $cmd = "cat $initial_template | ".
    "sed -e \"s|depot/\@\@TAG|depot/zimbra/\@\@TAG|\" |" .
    "sed -e \"s|\@\@TAG\@\@|$config{branch}|g\" |".
    "sed -e \"s|\@\@BUILD_ROOT\@\@|$config{buildRoot}|g\" |".
    "sed -e \"s|\@\@RELEASE\@\@|$config{release}|g\" |".
    "sed -e \"s|\@\@ARCH\@\@|$config{arch}|g\" |".
    "$config{P4} -c $client client -i >> $config{p4Log} 2>&1";
  
  my $rc = 0xffff & system($cmd);
  if ($rc) {
      includeLinesFromLog("12", "$config{p4Log}");
      quit("P4","createClient: $!");
  }

  my $stime = time;
  addToReport("*** Initial P4 sync to $config{buildRoot} at $config{dateStamp}\n");
  my $r = dateStampToRevDate($config{dateStamp});
  $cmd = "cd $config{buildRoot}; $config{P4} -Zproxyverbose -c $client sync -f ...\@$r >> $config{p4Log} 2>&1";
  addToReport("$cmd\n");
  $rc = 0xffff & system($cmd);
  if ( !-d "$config{buildRoot}/ZimbraBuild" ) {
    $cmd = "cat $initial_template | ".
      "sed -e \"s|\@\@TAG\@\@|$config{branch}|g\" |".
      "sed -e \"s|\@\@BUILD_ROOT\@\@|$config{buildRoot}|g\" |".
      "sed -e \"s|\@\@RELEASE\@\@|$config{release}|g\" |".
      "sed -e \"s|\@\@ARCH\@\@|$config{arch}|g\" |".
      "$config{P4} -c $client client -i >> $config{p4Log} 2>&1";
    $zimbra=0;
    $rc = 0xffff & system($cmd);
    if ($rc) {
      includeLinesFromLog("12", "$config{p4Log}");
      quit("P4","createClient: $!");
    }
    $cmd = "cd $config{buildRoot}; $config{P4} -Zproxyverbose -c $client sync -f ...\@$r >> $config{p4Log} 2>&1";
    $rc = 0xffff & system($cmd);
  } else {
    $zimbra=1;
  }
  if ($rc) {
    includeLinesFromLog("12", "$config{p4Log}");
    quit("P4","checkOut: $!");
  }
  my $duration = getDuration($stime);
  addToReport("*** Initial p4 sync checkout duration: $duration\n");
  my $template;
  if ( -f "$config{buildRoot}/ZimbraBuild/templates/BUILD_template_$config{type}" ) {
    if ($GlobalOpts{z}) {
      $template="$config{buildRoot}/ZimbraBuild/templates/BUILD_template_".$config{type}."_ThirdParty"; 
  } else {
      $template="$config{buildRoot}/ZimbraBuild/templates/BUILD_template_$config{type}"; 
  }
  } else {
    $template="$config{confDir}/templates/BUILD_template_$config{type}";
  }
  addToReport("*** Updating client $client\n");
  if ($zimbra) {
    $cmd = "cat $template | ".
      "sed -e \"s|depot/\@\@TAG|depot/zimbra/\@\@TAG|\" |" .
      "sed -e \"s|\@\@TAG\@\@|$config{branch}|g\" |".
      "sed -e \"s|\@\@BUILD_ROOT\@\@|$config{buildRoot}|g\" |".
      "sed -e \"s|\@\@RELEASE\@\@|$config{release}|g\" |".
      "sed -e \"s|\@\@ARCH\@\@|$config{arch}|g\" |".
      "$config{P4} -c $client client -i >> $config{p4Log} 2>&1";
  } else {
    $cmd = "cat $template | ".
      "sed -e \"s|\@\@TAG\@\@|$config{branch}|g\" |".
      "sed -e \"s|\@\@BUILD_ROOT\@\@|$config{buildRoot}|g\" |".
      "sed -e \"s|\@\@RELEASE\@\@|$config{release}|g\" |".
      "sed -e \"s|\@\@ARCH\@\@|$config{arch}|g\" |".
      "$config{P4} -c $client client -i >> $config{p4Log} 2>&1";
  }
  $rc = 0xffff & system($cmd);
  if ($rc) {
    includeLinesFromLog("12", "$config{p4Log}");
    quit("P4","createClient: $!");
  }
  return $client;
}

sub quit {
  my ($stage,$err) = (@_);
  $err = ($err ? $err : "unknown");
  addToReport("FAILED IN $stage: $err\n\n");
  revertChanges($config{p4Client});
  deleteLock();
  deleteClient($config{p4Client});
  `echo "FAILED IN $stage: $err\n\n" > $config{buildRoot}/FAILED`;
  updateMainBuildServer("failed") if ($GlobalOpts{H} && ($stage ne "RSYNC"));
  sendMail($stage, $err);
  exit (1);
}

sub deleteClient {
  my $c = shift;
	return unless ($c);
  addToReport("*** DELETING CLIENT $c\n");
  my $cmd = "$config{P4} -c $c client -d $c >> $config{p4Log} 2>&1";
  my $rc = 0xffff & system($cmd);
  return $rc;
}

sub revertChanges {
  my $c = shift;
  addToReport("*** REVERTING unsubmitted changes in client $c\n");
  my $cmd = "$config{P4} -c $c revert $config{buildRoot}/... >> $config{p4Log} 2>&1";
  my $rc = 0xffff & system($cmd);
  return $rc;
}

sub usage {
  print STDERR "$0 -b <branch> -t <type> -a <arch> [-T] [-r release] [-c change] [-d] [-z]\n";
  print STDERR "\t-b <branch>\n";
  print STDERR "\t-t <FOSS|NETWORK|OCTOPUS|ZCA|ZDESKTOP>\n";
  print STDERR "\t-a <arch>\n";
  print STDERR "\t-r <release>\n";
  print STDERR "\t-c <change>\n";
  print STDERR "\t-d ANT debug build\n";
  print STDERR "\t-T run tests\n";
  print STDERR "\t-z build 3rd party\n";
  exit (1);
}

sub getLastBuild {
  my $buildFile = "$config{buildHome}/.dfoodinstalls";
  if (open LAST, $buildFile) {
    my @builds = <LAST>;
    close LAST;
    return dateStampToRevDate($builds[$#builds]);
  } else {
  }
}

sub getLastRelease {
  return;
}

sub tagBuild {
  `echo "DAILY_DEBUG\n" > $config{buildRoot}/RELEASED`;
}

sub dateStampToRevDate {
  my $d = shift;
  return sprintf "%04d/%02d/%02d:%02d:%02d:%02d", 
    substr($d,0,4),substr($d,4,2),substr($d,6,2),
    substr($d,8,2),substr($d,10,2),substr($d,12,2);
}

sub getDateStamp {
  my $r = shift;
  (defined ($r)) && return $r;
  my @d = localtime();
  return sprintf ("%4d%02d%02d%02d%02d%02d",$d[5]+1900,$d[4]+1,$d[3],$d[2],$d[1],$d[0]);
}

sub getCurrentDayStamp {
  my @d = localtime();
  return sprintf ("%4d%02d%02d",$d[5]+1900,$d[4]+1,$d[3]);
}

sub checkOpts {
  if (defined($GlobalOpts{w})) {
    # Link all builds
    opendir BUILD, $config{buildDir};
    my @arches = grep !/^\.\.?/, readdir BUILD;
    closedir BUILD;
    foreach my $arch (@arches) {
      opendir ARCH, "$config{buildDir}/${arch}";
      my @branches = grep !/^\.\.?/, readdir ARCH;
      closedir ARCH;
      foreach my $branch (@branches) {
        linkBuilds($arch, $branch);
      }
    }
    exit (0);
  }

  unless (defined ($GlobalOpts{b}) && defined ($GlobalOpts{t}) &&
    defined ($GlobalOpts{a}) ) { usage(); }
}

sub doApplianceBuild() {

	my $va = "zimbra_va" if (lc($config{type}) eq "zca");
	my $change = $config{change} || "False";
	my $stime = time;

	my $cmd = "/build/apps/bin/gobuild sandbox queue $va --branch $config{branch} --buildtype beta --changeset ${change} --syncto latest --user zimbra-build --no-store-trees --no-send-email";
  addToReport("$cmd\n");
  my $rc = 0xffff & system ("$cmd >> $config{buildLog} 2>&1");
  if ($rc) {
    includeLinesFromLog("30", "$config{buildLog}");
    quit("BUILD","$!");
  } else {
		my $jobid=getGobuildJobId();
		addToReport("*** Queued Gobuild build with jobid=$jobid\n");
		my $state = getGobuildState($jobid);
		my $srcTree;
		while ( $state eq "running" || $state eq "queued" ) {
			sleep(120);
			my $stamp=getDateStamp();
			$state=getGobuildState($jobid);
			$srcTree=getGobuildTree($jobid);
			addToReport("*** Build state at $stamp.  State: $state SrcTree: $srcTree\n");
			copyGobuildTree($srcTree) if ($srcTree ne "-" && -d $srcTree);
			
		}
  	$duration = getDuration($stime);
  	addToReport("*** Build duration: $duration\n");
		if ($state eq "not-needed") {
			addToReport("*** Build was cancelled.\n");
		} 

		if ($state eq "failed") {
			addToReport("*** Build failed.\n");
		} 

		if ($state eq "succeeded") {
			addToReport("*** Build Completed Successfully.\n");
		}
		
		$srcTree=getGobuildTree($jobid);
		copyGobuildTree($srcTree) if ($srcTree ne "-" && -d $srcTree);
	}
	return;
}

sub getGobuildJobId() {
	open(LOG, "$config{buildLog}");
	my $jobid;
	while (<LOG>) {
		if (/Build (\d+) queued/) {
			$jobid=$1;
		}
	}
	close(LOG);
	return($jobid);
}

sub getGobuildState($) {
	my $jobid = shift;
	if (-x "/build/apps/bin/bld") {
		my $state = `/build/apps/bin/bld -k sb metastate $jobid`;
		chomp($state);
		return $state;
	}
	return "error";
}

sub getGobuildTree($) {
	my $jobid = shift;
	if (-x "/build/apps/bin/bld") {
		my $state = `/build/apps/bin/bld -k sb tree $jobid`;
		chomp($state);
		return $state;
	}
	return "-";
}

sub copyGobuildTree($) {
	my $srcTree = shift;	
	return undef unless (-d $srcTree);
	addToReport("*** Copying gobuild source tree.\n");
  my $rsync = "/usr/bin/rsync -av --stats $srcTree/ $config{buildRoot} >> $config{logDir}/rsync.log";
  addToReport("$rsync\n");
  my $rc = 0xffff & system ("$rsync");
	addToReport("RSYNC $!\n") if ($rc);
	return undef if ($rc);
}

sub build {

  addToReport("*** BUILDING IN $config{buildRoot}\n");
  my ($cmd,$targets,$thirdPartyCmd);
  my $stime = time;

	if (lc($config{type}) eq "zca") {
		doApplianceBuild();
		return;
	}

  # define the targets based on build type
  if ($config{type} eq "FOSS") {
    $targets = "ajaxtar sourcetar all";
    if ($GlobalOpts{d}) {
      $targets = "$targets ANT_DEBUG=-Ddebug=true";
    }
  } elsif ($config{type} eq "NETWORK") {
    $targets = "sourcetar all";
    $targets = "$targets velodrome" 
      if ($config{branch} eq "main" || $config{branch} =~ /FRANKLIN/ || $config{branch} =~ /GNR/);
    $targets = "$targets customercare" 
      if ($config{branch} =~ /FRANKLIN/);
    $targets = "$targets ANT_DEBUG=-Ddebug=true"
  		if ($GlobalOpts{d});
  } elsif (lc($config{type}) eq "octopus") {
		$targets = "ZIMBRA_OCTOPUS_BUILD=1 octopus_all";
    $targets = "$targets ANT_DEBUG=-Ddebug=true"
  		if ($GlobalOpts{d});
  } else {
    $targets = lc($config{type});
  }

	# Set the build command
  if ($config{type} eq "NETWORK") {
    $cmd = "cd $config{buildRoot}/ZimbraBuild;".
      "make -f ../ZimbraNetwork/ZimbraBuild/Makefile $targets";
   	$thirdPartyCmd="cd $config{buildRoot}/ThirdParty; ./buildThirdParty.sh -c -o -p -z"
  		if($GlobalOpts{z});
  } elsif (lc($config{type}) eq "zdesktop") {
    if ($GlobalOpts{d}) {
      $cmd = "cd $config{buildRoot}/ZimbraOffline; ant -Ddebug=true -f installer-ant.xml";
  	} else {
      $cmd = "cd $config{buildRoot}/ZimbraOffline; ant -f installer-ant.xml";
  	}
	} elsif (lc($config{type}) eq "octopus") {
    $cmd = "cd $config{buildRoot}/ZimbraBuild;".
      "make -f ../ZimbraNetwork/ZimbraBuild/Makefile $targets";
  } else {
    $cmd = "cd $config{buildRoot}/ZimbraBuild; make -f Makefile $targets";
    $thirdPartyCmd="cd $config{buildRoot}/ThirdParty; ./buildThirdParty.sh -c -o -p -z"
  		if($GlobalOpts{z});
  }

  if ($GlobalOpts{z}) {
    addToReport("$thirdPartyCmd\n");
    my $rc = 0xffff & system ("$thirdPartyCmd >> $config{buildLog} 2>&1");
  }
  addToReport("$cmd\n");
  my $rc = 0xffff & system ("$cmd >> $config{buildLog} 2>&1");
  $duration = getDuration($stime);
  addToReport("*** Compile duration: $duration\n");
  if ($rc) {
    includeLinesFromLog("30", "$config{buildLog}");
    quit("BUILD","$!");
  }
}

sub getChanges {
  addToReport("*** Getting changes\n");

  open CHANGE, ">$config{changeLog}";
  my $r = dateStampToRevDate($config{release});
  print CHANGE "Changes in $GlobalOpts{b} from $config{lastBuild} to ${r} ".
    "(Full text below)\n";
  print CHANGE "---------------------------------\n\n";

  my $cmd = "$config{P4} -c $config{p4Client} changes  -t ".
    "$config{buildRoot}/...\@$config{lastBuild},$r";

  print CHANGE "$config{P4} -c $config{p4Client} changes  -t $config{buildRoot}/...\@$config{lastBuild},$r\n";

  system ("$cmd > /tmp/ch.txt");

  print CHANGE "---------------------------------\n\n";
  
  open CH, "/tmp/ch.txt";
  my @c = <CH>;
  close CH;

  print CHANGE "@c";

  open CHANGE, ">>$config{changeLog}";
  print CHANGE "\n---------------------------------\n\n";

  close CHANGE;

  foreach (@c) {
    my $n = (split)[1];
    #print "Describing change $n\n";
    my $cmd = "$config{P4} -c $config{p4Client} describe -s $n";
    #print "$cmd\n";

    system ("$cmd >> $config{changeLog}");
    `echo "---------------------------------" >> $config{changeLog}`;
  }

  unlink "/tmp/ch.txt";

}

sub getBugs {
  addToReport("*** Getting fixed bugs\n");
  open BUG, ">>$config{bugLog}";
  print BUG "Bugs fixed in $GlobalOpts{b} from $config{lastBuild} to $config{release}\n";
  print BUG "---------------------------------\n\n";
  close BUG;
  addToReport("$config{scriptDir}/get_fixed_bugs.sh $config{lastBuild} >> $config{bugLog}\n");
  `$config{scriptDir}/get_fixed_bugs.sh $config{lastBuild} >> $config{bugLog} &`;
}

sub createDocs {
  addToReport("*** CREATING DOCS\n");
}

sub deleteOldBuilds {
  my $dir = "$config{buildDir}/$GlobalOpts{a}/$GlobalOpts{b}";

  opendir DIR, $dir;
  my @builds = grep !/^\.\.?/, readdir DIR;
  closedir DIR;

  @builds = reverse sort @builds;

  for (my $i = 0; $i <= $#builds; ) {
    if (-f "$dir/$builds[$i]/FAILED") {
      deleteBuild ("$dir/$builds[$i]");
      #splice (@builds, $i, 1);
    }# elsif (-f "$dir/$builds[$i]/RELEASED" || 
#      -f "$dir/$builds[$i]/ARCHIVED" || 
#      -f "$dir/$builds[$i]/.inprogress") {
#      splice (@builds, $i, 1);
#    } else {
      $i++;
#    }
#  }
#
#  if ($#builds >= $config{keepBuilds}) {
#    for (my $i = $config{keepBuilds}; $i <= $#builds; $i++) {
#      deleteBuild ("$dir/$builds[$i]");
#    }
  }
}


sub linkBuilds {
  my ($arch, $branch) = (@_);

  my $dir = "$config{buildDir}/${arch}/${branch}";

  opendir DIR, $dir;
  my @builds = grep !/^\.\.?/, readdir DIR;
  closedir DIR;

  my $linkDir = "$config{htmlDir}/links/$arch/$branch";
  `rm -rf ${linkDir}`;
  `mkdir -p ${linkDir}`;

  @builds = sort @builds;

  for (my $i = 0; $i <= $#builds; $i++) {
    addToReport("Linking $dir/$builds[$i] to ${linkDir}/$builds[$i]\n") 
      if $config{verbose};
    symlink("$dir/$builds[$i]", "${linkDir}/$builds[$i]");
    if ($builds[$i] =~ /_ZCO/ && $arch eq "WINDOWS") {
      `rm -rf ${linkDir}/latest-ZCO`;
      symlink("$dir/$builds[$i]", "${linkDir}/latest-ZCO");
    }
    if (-f "$dir/$builds[$i]/ZimbraQA/results/QTPFlag.txt") {
      unlink "${linkDir}/installed";
      symlink("${dir}/$builds[$i]", "${linkDir}/installed");
    }
  }
  symlink("${dir}/$builds[$#builds]", "${linkDir}/latest");
}

sub runTests {
  my $pw=`cat /tmp/ldap.pw`;
  `chmod a+x $config{buildRoot}/ZimbraQA/src/bin/runtests.sh`;
  `touch $config{testLog}`;
  my $rc = 0xffff & system("$config{buildRoot}/ZimbraQA/src/bin/runtests.sh $config{buildRoot} $pw >> $config{testLog} 2>&1");
  quit ("TEST","$!") if ($rc);
}

sub reInstall {
  my $stime = time;
  unlink("$ENV{HOME}/TestMailRaw") 
    if (-e "$ENV{HOME}/TestMailRaw");
  symlink("$config{buildRoot}/ZimbraServer/data/TestMailRaw", "$ENV{HOME}/TestMailRaw") 
    if (-e "$config{buildRoot}/ZimbraServer/data/TestMailRaw");
  system("chmod -R a+r $config{buildRoot}/ZimbraServer/data");

  addToReport("*** REINSTALLING FROM $config{buildRoot}\n");
  my $cmd = "sudo $config{scriptDir}/reinstall.sh $config{buildRoot}/ZimbraBuild/i386 >> $config{installLog} 2>&1";
  my $rc = 0xffff & system ($cmd);
  addToReport("$cmd : $!") if $rc;
  my $duration = getDuration($stime);
  addToReport("*** Install duration: $duration\n");
  quit ("INSTALL","$!") if ($rc);
}

sub deleteQAbuild {
  my ($build) = @_;
  return unless ($config{type} =~ m/NETWORK|FOSS/);
  my $name = basename($build); 
  my $url = "http://tms.lab.zimbra.com/builds/deleteBuild?";
  $url .= "arch=$GlobalOpts{a}";
  $url .= "&branch=$GlobalOpts{b}";
  $url .= "&name=$name";
  unless (scalar head($url)) {
    warn "Failed to inform QA via $url\n";
  }
  $url = "http://zqa-004.eng.vmware.com/builds/deleteBuild?";
  $url .= "arch=$GlobalOpts{a}";
  $url .= "&branch=$GlobalOpts{b}";
  $url .= "&name=$name";
  unless (scalar head($url)) {
    warn "Failed to inform QA via $url\n";
  }
}

sub informQA {
  return unless ($config{type} =~ m/NETWORK|FOSS|ZDESKTOP|OCTOPUS|APPLIANCE|ZCA/);
  if ($config{branch} =~ /DOCREORG/) {
    return;
  }
  my $url = "http://zqa-tms.eng.vmware.com/builds/addBuild?";
  $url .= "arch=$GlobalOpts{a}";
  $url .= "&branch=$GlobalOpts{b}";
  $url .= "&name=$config{dateStamp}_$GlobalOpts{t}";
  addToReport("** Informing QA via $url\n");
  unless (scalar head($url)) {
    addToReport("Failed to inform QA via $url\n");
  }
}

sub sendMail {
  my $stage = shift;
  my $err = shift;
  my $state = ($err ? "failed" : "passed");

  my $subj = "Build $state on $stage $config{arch} $config{type} $config{dateStamp}";
  my $dest;
  if ($state eq "failed" && $config{type} eq "ZDESKTOP") {
    $dest = "zcs-builds\@zimbra.com,desktop-bugs\@zimbra.com";
  } else {
    if ($GlobalOpts{r} eq "" && $state eq "failed") {
      #$dest = "engineering\@zimbra.com,zcs-builds\@zimbra.com";
	$dest = "brian\@zimbra.com";
    } else {
      $dest = "zcs-builds\@zimbra.com";
    }
  }
  my $from = "build\@zimbra.com";
  my $smtp = "mail.zimbra.com";
  my $mesg = "Build $state in stage $stage\n";
  $mesg .= "$config{dateStamp} $GlobalOpts{t} $GlobalOpts{b} on $GlobalOpts{a}\n\n";
  if (exists $config{label}) {
    $mesg .= "Source labeled with $config{label}\n\n";
  } else {
    $mesg .= "Source not labeled.\n\n";
  }
  $mesg .= "@{$config{message}}\n";

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

    addAttachment($msg, $config{p4Log}) 
      if ($state eq "failed" && $stage eq "P4");
    addAttachment($msg, $config{buildLog}) 
      if ($state eq "failed");
    addAttachment($msg, $config{changeLog});
    addAttachment($msg, $config{bugLog});
    addAttachment($msg, $config{installLog});
    addAttachment($msg, $config{testLog});
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

sub deleteSourceTag($) {
  my ($tag) = @_;
  addToReport("*** Deleting label $tag\n");
  my $cmd = "$config{P4} label -f -d $tag >> $config{p4Log} 2>&1";
  $rc = 0xffff & system($cmd);
  addToReport("p4 label -d: $!") if ($rc);
  return($rc);
}

sub addSourceTag {
  my ($cmd,$rc,$label);
  return if (lc($config{type}) eq "zca");
  addToReport("*** Tagging BUILD to $config{buildRoot} at $config{release}\n");

  $config{label} = "BUILD_$config{revision}_$config{release}";
  open(L, ">$config{buildRoot}/.p4label") or quit("P4", "label: $!\n");
  print L "$config{label}\n";
  close(L) or quit("P4", "label: $!\n");
  $cmd = "$config{P4} -c $config{p4Client} tag -l $config{label} $config{buildRoot}/... >> $config{p4Log} 2>&1";
  $rc = 0xffff & system($cmd);
  quit ("P4","label: $!") if ($rc);
}

sub addToReport($) {
  my ($line) = @_;
  print $line if $config{verbose};
  open(R, ">>$config{reportLog}");
  print R $line;
  close(R);
  push(@{$config{message}}, $line);
}

sub saveReport() {
  open(R, ">$config{reportLog}");
  print R @{$config{message}};
  close(R);
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

sub updateZdesktopBits {

  my $mac = lc($config{type}).'*_build_*_macos_intel.dmg';
  my $linux = lc($config{type}).'*_build_*_linux_i686.sh';
  my $windows = lc($config{type}).'*_build_*_win32.exe';
  my $cmd = "$config{P4} -c $config{p4Client} delete $config{buildRoot}/ThirdPartyBuilds/zdesktop/$mac $config{buildRoot}/ThirdPartyBuilds/zdesktop/$linux $config{buildRoot}/ThirdPartyBuilds/zdesktop/$windows";
  print "CMD: $cmd\n\n";
  my $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
  #quit ("P4","$!") if ($rc);

  $cmd = "$config{P4} -c $config{p4Client} change -o | ".
    "sed -e 's/<enter description here>/bug: 57717/' | ".
    "$config{P4} -c $config{p4Client} submit -i";

  my $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
  #quit ("P4","$!") if ($rc);

  `mkdir -p $config{buildRoot}/ThirdPartyBuilds/zdesktop` if (! -d "$config{buildRoot}/ThirdPartyBuilds/zdesktop");

  `cp -f $config{buildRoot}/ZimbraBuild/i386/$mac $config{buildRoot}/ThirdPartyBuilds/zdesktop/`;
  `cp -f $config{buildRoot}/ZimbraBuild/i386/$linux $config{buildRoot}/ThirdPartyBuilds/zdesktop/`;
  `cp -f $config{buildRoot}/ZimbraBuild/i386/$windows $config{buildRoot}/ThirdPartyBuilds/zdesktop/`;

  $cmd = "$config{P4} -c $config{p4Client} add $config{buildRoot}/ThirdPartyBuilds/zdesktop/$mac $config{buildRoot}/ThirdPartyBuilds/zdesktop/$linux $config{buildRoot}/ThirdPartyBuilds/zdesktop/$windows";

  print "CMD: $cmd\n\n";

  my $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
  quit ("P4","$!") if ($rc);

  $cmd = "$config{P4} -c $config{p4Client} change -o | ".
    "sed -e 's/<enter description here>/bug: 57717/' | ".
    "$config{P4} -c $config{p4Client} submit -i";

  my $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
  quit ("P4","$!") if ($rc);

}

sub updateBits {

  my $pkg = 'zimbra-'.lc($config{type}).'*.dmg';
  my $cmd = "$config{P4} -c $config{p4Client} delete $config{buildRoot}/ZimbraServer/src/macosx/$pkg";
  print "CMD: $cmd\n\n";
  my $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
  #quit ("P4","$!") if ($rc);

  $cmd = "$config{P4} -c $config{p4Client} change -o | ".
    "sed -e 's/<enter description here>/bug: 57717/' | ".
    "$config{P4} -c $config{p4Client} submit -i";

  my $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
  #quit ("P4","$!") if ($rc);

  `cp -f $config{buildRoot}/ZimbraBuild/i386/$pkg $config{buildRoot}/ZimbraServer/src/macosx/`;

  $cmd = "$config{P4} -c $config{p4Client} add $config{buildRoot}/ZimbraServer/src/macosx/$pkg";  

  print "CMD: $cmd\n\n";

  my $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
  quit ("P4","$!") if ($rc);

  $cmd = "$config{P4} -c $config{p4Client} change -o | ".
    "sed -e 's/<enter description here>/bug: 57717/' | ".
    "$config{P4} -c $config{p4Client} submit -i";

  my $rc = 0xffff & system("$cmd >> $config{p4Log} 2>&1");
  quit ("P4","$!") if ($rc);

}
  

__END__
