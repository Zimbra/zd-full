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

# ZimbraSoapTest package
#
# a bunch of not-general-purpose code to make it easy to write short
# perl test scripts against the Zimbra server
package ZP4;

# collection of shared-code between the various zimbra p4 trigger scripts

use strict;
use warnings;

sub get_p4() {
  my $toRet;
  if (defined $ENV{'P4'}) {
    $toRet =  $ENV{'P4'};
  } else {
    $toRet = "/data/p4/bin/p4";
  }
  if (!(-x $toRet)) {
    die "Could not find P4 environment variable, and couldn't find p4 at /data/p4/bin/p4";
  }
  return $toRet;
}

my $zp4Log;
my $zp4DebugLevel;

##################
#
#
sub set_debug_level($) {
  $zp4DebugLevel = shift();
}

##################
# Debug output, based on setting of debugMode variable
#
sub debug($$) {
  my ($level, $str) = @_;
  if (defined $zp4Log) {
    print LOG "$str\n";
  }
  if (!defined($zp4DebugLevel)) {
    return;
  }
  if ($zp4DebugLevel >= $level) {
    print "$str\n";
  }
}

##################
#
# Initialize disk debug logging
#
sub start_logging($) {
  $zp4Log = shift();
  if (defined $zp4Log) {
    open LOG, ">$zp4Log";
  }
}

#################
#
# Wrapper for exit(), closs log file if necessary
#
sub do_exit($) {
  my $level = shift();

  if (defined($zp4Log)) {
    close LOG;
  }
  exit($level);
}

################
#
# Return array of sample change-form data
#
sub get_test_change_form_data() {
  my @TEST_CHANGE_FORMS;
  my $tmp = <<_TEST_FORM_TEXT_;
# A Perforce Change Specification.
#
#  Change:      The change number. 'new' on a new changelist.  Read-only.
#  Date:        The date this specification was last modified.  Read-only.
#  Client:      The client on which the changelist was created.  Read-only.
#  User:        The user who created the changelist. Read-only.
#  Status:      Either 'pending' or 'submitted'. Read-only.
#  Description: Comments about the changelist.  Required.
#  Jobs:        What opened jobs are to be closed by this changelist.
#               You may delete jobs from this list.  (New changelists only.)
#  Files:       What opened files from the default changelist are to be added
#               to this changelist.  You may delete files from this list.
#               (New changelists only.)

Change:	49244

Date:	2007/04/09 21:58:35

Client:	timsmac

User:	tim

Status:	pending

Description:
     Bug: 896, 896
     This is a test

Files:
     //depot/main/ZimbraIM/blah.txt

_TEST_FORM_TEXT_
  push @TEST_CHANGE_FORMS, $tmp;
  return @TEST_CHANGE_FORMS;
}

################
#
# Return array of sample changelist data
#
sub get_test_changelist_data() {
    #
    # NOTE TO REMEMBER -- test bug is #896
    #
  my @TEST_BUG_TEXT;
  push(@TEST_BUG_TEXT,"\tbug: 1, 1, 2, 3  added 50MB crap to source tree\n\tHi 99 mom\n\tbug:100,101\n\tbug:305 10MB of more crap");
  push(@TEST_BUG_TEXT,"\tTest\n\tbug: 896 10MB\n\tfoo\n");
  push(@TEST_BUG_TEXT,"\tTest\n\tbug: none This is a quick checking for 10 20 test.\n");
  my $tmp = <<_TEST_BUG_TEXT_;
\tbug: 6803 (addendum) - fixes sending new messages..

This is a long line of text.  This is a long line of text.  This is a long line of text.  This is a long line of text.  This is a long line of text.  This is a long line of text.  This is a long line of text.  This is a long line of text.  
Another long line of text.  Another long line of text.  Another long line of text.  Another long line of text.  Another long line of text.  Another long line of text.  Another long line of text.  Another long line of text.  

A short line here.
One more short line.

Affected files ...

Yet another long line of text...  Yet another long line of text...  Yet another long line of text...  Yet another long line of text...  Yet another long line of text...  
One more short line
_TEST_BUG_TEXT_
  push(@TEST_BUG_TEXT, $tmp);

  $tmp = <<_TEST_BUG_TEXT3_;
\tbug: 4210, 6019, 6954
      
      Add an IMAP_ID column to the user mailbox database; this defaults to the item ID for most leaf-node types.  The ZIMBRA.MAILBOX table now has a TRACKING_IMAP boolean which is set the first time a user logs in via IMAP.  The TRACKING_SYNC column in ZIMBRA.MAILBOX is now an INTEGER; we will be using this in the future to track trimming the TOMBSTONE table.  Database version is now 23; please reset-the-world or run the script ZimbraServer/src/db/migrate20060515-AddImapId.pl to bring your build into the new world.
      
      When loading non-search folders in IMAP, we now directly fetch only the relevant fixed-width columns from the database (ID, IMAP_ID, TYPE, FLAGS, TAGS, UNREAD) and don't instantiate any MailItems in the Mailbox.  Items that have an IMAP_ID <=0 have a new IMAP ID assigned at folder load time; this state occurs automatically when an item is moved via a non-IMAP interface when TRACKING_IMAP is true for the account.  Certain message attributes that are seldom fetched without fetching the message body (date, size) are no longer precached when the IMAP folder is loaded but are instead retrieved by fetching the item on demand from the Mailbox.

Expose contacts via IMAP as messages with a text/x-vcard Content-Type with UTF-8 content.  The Contacts folder is readable but not writable.

Move IMAP ID up to MailItem.  Also collect blob-writing code in MailItem.setContent(), so we're no longer duplicating the code in Message and Appointment.  Note that Document does not yet share this blob-wrangling code.

vCards are now \r\n-delimited.  Please let me know if this breaks anything...

Affected files ...

... //depot/main/ZimbraServer/src/db/create_database.sql#11 edit
... //depot/main/ZimbraServer/src/db/db.sql#19 edit
... //depot/main/ZimbraServer/src/db/migrate20060515-AddImapId.pl#1 add
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/db/DbMailItem.java#48 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/db/DbMailbox.java#21 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/db/Versions.java#18 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/imap/ImapAppendOperation.java#4 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/imap/ImapFolder.java#22 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/imap/ImapHandler.java#66 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/imap/ImapListOperation.java#6 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/imap/ImapMessage.java#21 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/imap/ImapSession.java#31 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/imap/ImapSessionHandler.java#7 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/imap/OzImapConnectionHandler.java#53 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/index/DBQueryOperation.java#43 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/mailbox/Appointment.java#79 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/mailbox/Contact.java#19 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/mailbox/Document.java#28 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/mailbox/MailItem.java#43 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/mailbox/Mailbox.java#137 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/mailbox/Message.java#37 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/mime/Mime.java#24 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/redolog/op/RedoableOp.java#25 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/redolog/op/SetImapUid.java#8 edit
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/redolog/op/TrackImap.java#1 add
... //depot/main/ZimbraServer/src/java/com/zimbra/cs/service/formatter/VCard.java#7 edit

_TEST_BUG_TEXT3_
  push(@TEST_BUG_TEXT, $tmp);

  return @TEST_BUG_TEXT;
}

1;
