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
# After a change has been comitted, this script updates bugzilla with
# the bug description
#

use strict;
use warnings;
use IO::File;
use Data::Dumper;
use WWW::Bugzilla;
use WWW::Mechanize;
use Getopt::Long;
use ZP4;

my $P4 = ZP4::get_p4();

my $debugMode;                  # 1 = debug
my $testData; # if set, then use test data
my $cl; # changelist
my $log;

GetOptions("d=s" => \$debugMode,
           "c=s" => \$cl,
           "t" => \$testData,
           "log=s" => \$log
          );

ZP4::set_debug_level($debugMode);
ZP4::start_logging($log);

if ((!defined($cl) && !defined($testData)) || (defined($cl) && defined($testData))) {
  my $usage = <<END_OF_USAGE;
USAGE: $0 [-d LEVEL] (-t OR -c CHANGEID)
    -d LEVEL:     set debug level (1, 2 or 3)
    -t:           use test data (embedded in this script)
    -c CHANGEID:  p4 changelist ID
    -log FILENAME log output (for debugging p4)
END_OF_USAGE
  die $usage;
}

if (defined($cl)) {
  my $cmd = "$P4 -p eric:1666 -c DEPOT -u review_daemon -P Reviewer describe -s $cl";
  ZP4::debug(2, "Command: $cmd");
  my $clDesc = `$cmd`;
  if ($clDesc eq "") {
    print "could not fetch changelist #$cl\n";
    ZP4::my_exit(1);
  }
  do_bug_update($clDesc);
} else {
  run_with_test_data();
}
ZP4::my_exit(0);

#######################################################################


#################
# Given a changelist descriptor, update all bugs w/ the
# change data
#
sub do_bug_update($$) {
  my $cl = shift();
  my $skipWrite = shift();
  
  ZP4::debug(1,"Changelist is:\n $cl\n---------------\n");
  my @bugIds = get_bug_ids($cl);
  if (@bugIds > 0 && $bugIds[0] ne "NONE") {
    my $fitText = fit_to_width($cl, 70);
    foreach my $id (@bugIds) {
      ZP4::debug(1, "APPENDING TO BUG $id:\n$fitText");
      if (!defined($skipWrite)) {
        update_bug($id, $fitText);
      }
    }
  }  # else of bugUrls eq ""
} 

#################
# Scan the changelist description and find all the "bug:" lines,
# return a bunch of URLs (into bugzilla) separated by \n's
#
sub get_bug_ids($) {
  my ($message) = @_;
  my @toRet;
  
  if ($message=~/^[\t\s]+bug\:?\s*none/mgi) {
    push @toRet, "NONE";
    return @toRet;
  }
  
  my @lines = split("\n", $message);

  foreach (@lines) {
    ZP4::debug(3, "Line is \"$_\"");
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
        ZP4::debug(3, "found BUGID: $bugid\n");
		$found_bugid = 1;
        push @toRet, $bugid;
      }

      if ($found_bugid == 1) {
        return @toRet;
      }
	} # if starts with bug
  } # foreach line
  return @toRet;
}

#################
#
# update the bugzilla bug w/ the changelist info
#
sub update_bug($$) {

  my ($bugid, $msg) = @_;

  #    my $BUGSERVER = 'bugzilla.zimbra.com';
  my $BUGSERVER = '72.3.250.100';
  my $BUGEMAIL = 'cvsuser@zimbra.com';
  my $BUGPASSWORD = "test123";

  # the bugzilla form is now buried by flickerbox
  # we can't use WWW::Bugzilla anymore
  my $mech = WWW::Mechanize->new();


  # Login as the cvs user
  #
  $mech->get("http://$BUGSERVER/query.cgi?GoAheadAndLogIn=1");

  # Find the bugzilla form on the page
  $mech->form_number(2);

  $mech->field('Bugzilla_login', $BUGEMAIL);
  $mech->field('Bugzilla_password', $BUGPASSWORD);

  $mech->submit_form();

  # Change the specified bugid
  #
  $mech->get("http://$BUGSERVER/show_bug.cgi?id=$bugid");

  if ($mech->title() eq "Invalid Bug ID") {
    die "Invalid bug ID: $bugid";
  }

  # Find the bugzilla form on the page
  $mech->form_number(2);

  $mech->field('comment', $msg);

  $mech->submit_form();

}

#################
#
# Fit the passed-in text to 70 cols, for better bugzilla importing
#
sub fit_to_width($$) {
  my ($text, $width) = @_;
  my $fitText = '';

  my @lines = split("\n", $text);
  my $foundFileList = 0;
  foreach my $line (@lines) {
    chomp($line);
    if ($foundFileList) {
      $fitText .= $line . "\n";
      next;
    }

    if ($line =~ /^\s*Affected files \.\.\./) {
      $foundFileList = 1;
      $fitText .= $line . "\n";
      next;
    }

    # Note: Each line starts with a tab character.
    # p4 must be doing that for some reason.
    if (length($line) > $width) {
      while (length($line) > $width) {
        # Find nearest whitespace before $width.
        my $splitAt = $width - 1;
        while ($splitAt > 0) {
          my $ch = substr($line, $splitAt, 1);
          if ($ch =~ /^\s$/) {
            last;
          }
          $splitAt--;
        }
        if ($splitAt == 0) {
          $splitAt = $width;
        }

        # Include all trailing whitespaces on current line.
        while ($splitAt < length($line)) {
          my $ch = substr($line, $splitAt, 1);
          if ($ch =~ /^\s$/) {
            $splitAt++;
            next;
          } else {
            last;
          }
        }

        my $piece = substr($line, 0, $splitAt);
        if (length($line) > $splitAt) {
          $line = "\t" . substr($line, $splitAt);
        } else {
          $line = '';
        }
        $fitText .= $piece . "\n";
      }
      $fitText .= $line . "\n" if ($line ne ''); # last remaining piece
    } else {
      $fitText .= $line . "\n";
    }
  }

  return $fitText;
}


#################
#
#
#
sub run_with_test_data()
{  
  foreach my $entry (ZP4::get_test_changelist_data()) {
	print "\n********************************************************\n\n";
    ZP4::debug(1, "RUNNING: $entry\n");
	do_bug_update($entry, 1);
    print "DONE!\n";
  }
}
