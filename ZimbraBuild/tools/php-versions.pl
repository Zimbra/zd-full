#!/usr/bin/perl
# 
# 
# 
use strict;
use LWP::Simple;
use Getopt::Std;
my $url="http://files.zimbra.com/downloads";
our ($opt_c,$opt_d);
getopts('cd');
my @list = <>;
my @NETWORK;
my @FOSS;
my $failed=0;
$|=1;

foreach my $dl (@list) {
  my ($build,$temp,$platform,$version,$network,$critical,$major,$minor,$micro,$shortv,$release,$format);
  $network=0;
  $critical=0;
  chomp($dl);
  if ($dl =~ /^zcs-NETWORK/) {
    $network=1;
  }
  $temp=$dl;
  $temp =~ s/^zcs-(NETWORK-){0,1}//;
  ($version,$build) =
            $temp =~ m/(\d\.\d\.\d+_\S+)_(\S+)\.\S+.\.\S+\..*/;
  $temp =~ s/^\d\.\d\.\d+_\w+\.//;
  $temp=reverse($temp);
  ($format,$release)=
            $temp =~ m/(\w+)\.(\w+)\..*/;
  $release=reverse($release);
  $temp =~ s/^\w+\.\w+\.//;
  $platform = $temp;
  $platform=reverse($platform);
  next if ($version eq "");
  my $myurl = "${url}/${version}/${dl}";
  if ($opt_c) {
    $critical=1;
  }
  ($major,$minor,$micro) =
           $version =~ m/(\d)\.(\d)\.(\d+)_.*/;
  $shortv="$major.$minor.$micro";
  if ($opt_d) {
    print "\n";
    print "build:$build\n";
    print "platform:$platform\n";
    print "version:$version\n";
    print "major:$major\n";
    print "minor:$minor\n";
    print "micro:$micro\n";
    print "shortversion:$shortv\n";
    print "url:$myurl\n";
    print "release:$release\n";
    print "network:$network\n";
    print "critial:$critical\n";
  }
  if ($network == 1) {
    push @NETWORK, { build => "$build", platform => "$platform", version => "$version", network => "$network",
                     critical => "$critical", major => "$major", minor => "$minor", micro => "$micro",
                     shortv => "$shortv", url => "$myurl", release => "$release" };
  } else {
    push @FOSS, { build => "$build", platform => "$platform", version => "$version", network => "$network",
                  critical => "$critical", major => "$major", minor => "$minor", micro => "$micro",
                  shortv => "$shortv", url => "$myurl", release => "$release" };
  }
}
print "<?php\n";
print "  \$versions = array (\n";
print "    \"NETWORK\" => array (//type\n";
my $count = scalar (@NETWORK);
for (my $i =0; $i < $count; $i++) {
  print "      \"".$NETWORK[$i]{'platform'}."\"=>array (//platform\n";
  print "        ".$NETWORK[$i]{'major'}."=>array (//major\n";
  print "          ".$NETWORK[$i]{'minor'}."=>array (//minor\n";
  print "            ".$NETWORK[$i]{'micro'}."=>array (//micro\n";
  print "              ".$NETWORK[$i]{'build'}."=>array (//build\n";
  print "                \"shortversion\"=>\"".$NETWORK[$i]{'shortv'}."\",\n";
  print "                \"critical\"=>".$NETWORK[$i]{'critical'}.",\n";
  print "                \"updateURL\"=>\"".$NETWORK[$i]{'url'}."\",\n";
  print "                \"release\"=>\"".$NETWORK[$i]{'release'}."\",\n";
  print "                \"version\"=>\"".$NETWORK[$i]{'version'}."_".$NETWORK[$i]{'build'}."\",\n";
  print "                \"buildnum\"=>\"".$NETWORK[$i]{'build'}."\",\n";
  print "                \"description\"=>\" \",\n";
  print "                \"platform\"=>\"".$NETWORK[$i]{'platform'}."\",\n";
  print "                \"buildtype\"=>\"NETWORK\",\n";
  print "              )\n";
  print "            )\n";
  print "          )\n";
  print "        )\n";
  if ($i == $count-1) {
    print "      )\n";
  } else {
    print "      ),\n";
  }
}
print "    ),\n";
print "    \"FOSS\" => array (//type\n";
$count = scalar (@FOSS);
for (my $i =0; $i < $count; $i++) {
  print "      \"".$FOSS[$i]{'platform'}."\"=>array (//platform\n";
  print "        ".$FOSS[$i]{'major'}."=>array (//major\n";
  print "          ".$FOSS[$i]{'minor'}."=>array (//minor\n";
  print "            ".$FOSS[$i]{'micro'}."=>array (//micro\n";
  print "              ".$FOSS[$i]{'build'}."=>array (//build\n";
  print "                \"shortversion\"=>\"".$FOSS[$i]{'shortv'}."\",\n";
  print "                \"critical\"=>".$FOSS[$i]{'critical'}.",\n";
  print "                \"updateURL\"=>\"".$FOSS[$i]{'url'}."\",\n";
  print "                \"release\"=>\"".$FOSS[$i]{'release'}."\",\n";
  print "                \"version\"=>\"".$FOSS[$i]{'version'}."_".$FOSS[$i]{'build'}."\",\n";
  print "                \"buildnum\"=>\"".$FOSS[$i]{'build'}."\",\n";
  print "                \"description\"=>\" \",\n";
  print "                \"platform\"=>\"".$FOSS[$i]{'platform'}."\",\n";
  print "                \"buildtype\"=>\"FOSS\",\n";
  print "              )\n";
  print "            )\n";
  print "          )\n";
  print "        )\n";
  if ($i == $count-1) {
    print "      )\n";
  } else {
    print "      ),\n";
  }
}
print "    )\n";
print "  );\n";
print "?>\n";
exit $failed;
