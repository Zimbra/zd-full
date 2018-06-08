use strict;
use Cwd;

my $time=0;

my $branch=$ARGV[0];
if ($#ARGV > 0) {
  $time=$ARGV[1];
}

chomp($branch);
chomp($time);

exit unless $branch;

my $base;
my $src;

$base = 'C:\\src';
$src = "$base\\$branch";

my $arch = "WINDOWS";
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
my $builddir;
my $logdir;
my $p4log;
my $buildlog;

$builddir = "$src\\$release";
$logdir = "$builddir\\logs";
$p4log = "$logdir\\p4.log";
$buildlog = "$logdir\\build.log";

my $p4client;
my $P4 = "p4 -d $builddir -u $p4user -P $p4pass -p $p4port";

init();
createLock();
$p4client = createClient();
getSource();
build();
#sendResults();
deleteLock();
exit(0);

sub createLock {
  if (-f "$src\\.lock") {
     print "Error: Build in progress\n";
     exit (1);
  }
  open(LOCK, ">$src\\.lock");
  close(LOCK);
  open(PROGR, ">$builddir\\.inprogress");
  close(PROGR);
}

sub deleteLock {
  unlink "$src\\.lock";
  unlink "$builddir\\.inprogress";
}

sub doCmd {
  my $cmdLine = shift(@_);
  print $cmdLine . "\n";
  system($cmdLine);
}

sub init {
	doCmd( "rmdir /s /q $builddir" );
	mkdir "$src";
  mkdir "$builddir";
	mkdir "$logdir";
}

sub getSource {
	print "Getting source into $builddir\n";
	doCmd ("$P4 -c $p4client sync -f ...@".$date." >> $p4log");
}

sub build {
  chdir("$builddir\\ZimbraOffline");
  doCmd("$ant -f installer-ant.xml >$buildlog");
}

sub createClient {
  my $client="BUILD_".$branch."_".$release."_".$arch;
  my $template="$base\\template\\BUILD_init.txt";
  my $ctemplate="$builddir\\logs\\build_int_client.txt";
  
  print "Creating client $client\n";
  open(IN, "<$template");
  open(OUT, ">$ctemplate");
  while (<IN>) {
    $_ =~ s/\@\@ARCH\@\@/$arch/g;
    $_ =~ s/\@\@BRANCH\@\@/$branch/g;
    $_ =~ s/\@\@TAG\@\@/$branch/g;
    $_ =~ s/\@\@RELEASE\@\@/$release/g;
    $_ =~ s/\@\@BUILD_ROOT\@\@/$builddir/g;
    print OUT "$_";
  }
  doCmd("p4 -c $client client -i < $ctemplate >>$p4log");
  doCmd("$P4 -c $client sync -f ...@".$date." >> $p4log");
  $template="$builddir\\ZimbraBuild\\templates\\BUILD_template_ZDESKTOP";
  $ctemplate="$builddir\\logs\\build_final_client.txt";
  open(IN, "<$template");
  open(OUT, ">$ctemplate");
  while (<IN>) {
    $_ =~ s/\@\@ARCH\@\@/$arch/g;
    $_ =~ s/\@\@BRANCH\@\@/$branch/g;
    $_ =~ s/\@\@TAG\@\@/$branch/g;
    $_ =~ s/\@\@RELEASE\@\@/$release/g;
    $_ =~ s/\@\@BUILD_ROOT\@\@/$builddir/g;
    print OUT "$_";
  }
  doCmd("p4 -c $client client -i < $ctemplate >>$p4log");
  return $client;
}