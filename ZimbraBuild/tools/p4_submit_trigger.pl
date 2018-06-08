#!/usr/bin/perl -w
# 
# 
# 
##########################################
#
# **** CHANGES TO THIS FILE _MUST_ BE CHECKED INTO P4  *****
#
# This file lives in ZimbraBuild/tools, if you modify this file you
# MUST checkin the new version, otherwise your edits may be overwritten
# at any time.
#
# YOU HAVE BEEN WARNED!
#
############################################

#
# This script validates a checkin description by looking for the
# bug:  entry.  It is intended to run as a perforce "submit" trigger,
# and will return with an exit code of (1) if the submit should
# not be allowed
#

##
## Perforce requires messages on stdout
##
open(STDERR, ">&STDOUT") or die "Can't dup stdout";

use lib '/data/p4/bin';

use strict;
use warnings;
use IO::File;
use Data::Dumper;
use WWW::Bugzilla;
use WWW::Mechanize;
use Getopt::Long;
use ZP4;

sub do_bug_check($);
sub parse_bug_specifier($);
sub run_with_test_data();


my $P4 = ZP4::get_p4();

my $debugMode;                  # 1 = debug
my $noneMode;                   # if defined, then "bug:none" is valid
my $testData;                   # if set, then use test data
my $cl;                         # changelist
my $log;

GetOptions("oknone" => \$noneMode,
           "d=s" => \$debugMode,
           "c=s" => \$cl,
           "t" => \$testData,
           "l=s" => \$log,
          );


if ((!defined($cl) && !defined($testData)) || (defined($cl) && defined($testData))) {
  my $usage = <<END_OF_USAGE;
USAGE: $0 [-l logfile] [-oknone] [-d LEVEL] (-t OR -c CHANGEID)
    -l logfile:   write debug output to logfile
    -oknone:      "bug:none" is allowed in the input
    -d:           debug mode
    -t:           use test data (embedded in this script)
    -c CHANGEID:  p4 changelist ID
END_OF_USAGE
  die $usage;
}

ZP4::set_debug_level($debugMode);
ZP4::start_logging($log);

if (defined($cl)) {
  my $cmd = "$P4 -p eric:1666 -c DEPOT -u review_daemon -P Reviewer describe -s $cl";
  ZP4::debug(2, "Command: $cmd");
  my $clDesc = `$cmd`;
  if ($clDesc eq "") {
    print "could not fetch changelist #$cl\n";
    ZP4::do_exit(1);
  }
  if (do_bug_check($clDesc) == 0) {
    print "exiting with error exit code\n";
    ZP4::do_exit(1);
  } else {
    print "exiting with success exit code\n";
    ZP4::do_exit(0);
  }
} else {
  ZP4::debug(1, "Forcing NONE-OK mode for test data");
  $noneMode = 1; 
  if (run_with_test_data() == 0) {
    print "At least one test FAILED\n";
    ZP4::do_exit(1);
  } else {
    print "All tests successful\n";
    ZP4::do_exit(0);
  }
}
die("notreached");

######################################################################



#################
# Given a string, and the okNone setting, check to see
# if this string is valid.  Return FAILURE on error
#
sub do_bug_check($) {
  my $cl = shift();
  ZP4::debug(1,"Changelist is:\n $cl\n---------------\n");
  my $bugUrls = parse_bug_specifier($cl);
  if ($bugUrls eq "") {
    # no bug: found at all!
    print "No bug found in submission\n";
    return 0;
  } else {
	if ($bugUrls eq "NONE") {
      if (defined($noneMode)) {
        ZP4::debug(1, "Allowing \"Bug: none\" entry b/c -oknone switch is set\n");
        return 1;
      } else { # defined(noneMode)
        print "\"bug:none\" is not valid for this tree\n";
        return 0;
      } 
    } else {
      return 1;
    } # else of bugUrls eq NONE
  }  # else of bugUrls eq ""
} 

#################
# Scan the changelist description and find all the "bug:" lines,
# return a bunch of URLs (into bugzilla) separated by \n's
#
sub parse_bug_specifier($) {
  my ($message) = @_;
  my $bugText = "";
  
  my @lines = split("\n", $message);

  foreach (@lines) {
    ZP4::debug(3, "Line is \"$_\"");

    if (/^[\t\s]+bug\:?\s*none/mgi) {
      ZP4::debug(3, "Bug is NONE");
      return "NONE";
    }
    
	if (/^[\t\s]+bug/i) {
      #
      # Chop the bug: part off the front
      # 
      s/^[\t\s]+bug:?\s*//i;

      #
      # End the line at the first nondigit...
      #
      #	    s/\d+[^\d,\s]/CENSORED/g;
      s/[\d]*[^\d\s,]/END/;
	    
      my @foo = split("END");
      $_ = $foo[0];
	    
      #	    print "LINE IS NOW: \"$_\"\n";

      #
      # add space to the end, easier to write regex below
      #
      $_ = $_ . " ";

      # only allow one bug: line, stop after that...
      my $found_bugid = 0;
      
      #	    print "\tRegexing: \"$_\"\n";
      my @matches = /([\d]+)[\s,]+/g;
      foreach (@matches) {
		my $bugid = $_;
        ZP4::debug(3, "found BUGID: $bugid");
		$found_bugid = 1;
        $bugText .= "\thttp://bugzilla.zimbra.com/show_bug.cgi?id=$bugid\n";
      }

      if ($found_bugid == 1) {
		return $bugText;
      }
	} # if starts with bug
  } # foreach line
  return $bugText;
}


#################
#
#
#
sub run_with_test_data()
{
  my $retVal = 1;
  my $count = 0;
  foreach (ZP4::get_test_changelist_data()) {
	ZP4::debug(1,"\n*******************************************************************\n");
    my $toCheck = $_;
	if (do_bug_check($toCheck) == 0) {
      print "Test $count FAILED for:\n$toCheck\n";
      $retVal = 0;
    } else {
      print "Test $count Succeeded!\n";
    }
    $count++;
  }
  return $retVal;
}

