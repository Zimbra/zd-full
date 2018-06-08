#!/usr/bin/perl
# 
# 
# 

use strict;
use lib "/opt/zimbra/libexec/scripts";
use lib "/opt/zimbra/zimbramon/lib";
use Migrate;
use Getopt::Long;
my $concurrent = 10;

sub usage() {
	print STDERR "Usage: $0\n";
	exit(1);
}
my $opt_h;
GetOptions("help" => \$opt_h);
usage() if $opt_h;

my @groups = Migrate::getMailboxGroups();

my @sql = ();
foreach my $group (@groups) {
  foreach my $table qw(mail_item appointment imap_folder imap_message open_conversation pop3_message revision tombstone data_source_item) {
   print "Adding $group.$table to be optimized\n";
    #push(@sql, "OPTIMIZE TABLE $group.$table;");
    push(@sql, "ALTER TABLE $group.$table ENGINE=InnoDB;");
  }
}
foreach my $table qw(volume current_volumes mailbox deleted_account mailbox_metadata out_of_office config table_maintenance scheduled_task mobile_devices ) {
  print "Adding zimbra.$table to be optimized\n";
  #push(@sql, "OPTIMIZE TABLE zimbra.$table;");
  push(@sql, "ALTER TABLE zimbra.$table ENGINE=InnoDB;");
}
my $start = time();
Migrate::runSqlParallel($concurrent, @sql);
my $elapsed = time() - $start;
my $numGroups = scalar @groups;
print "\nAltered $numGroups mailbox groups in $elapsed seconds\n";

exit(0);
