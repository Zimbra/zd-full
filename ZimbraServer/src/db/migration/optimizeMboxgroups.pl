#!/usr/bin/perl
# 
# 
# 

use strict;
use Migrate;
use Getopt::Long;
my $concurrent = 10;

sub usage() {
	print STDERR "Usage: optimizeMboxgroups.pl\n";
	exit(1);
}
my $opt_h;
GetOptions("help" => \$opt_h);
usage() if $opt_h;

my @groups = Migrate::getMailboxGroups();

my @sql = ();
foreach my $group (@groups) {
  foreach my $table qw(mail_item appointment imap_folder imap_message open_conversation pop3_message revision tombstone) {
   print "Adding $group.$table to be optimized\n";
    push(@sql, "OPTIMIZE TABLE $group.$table;");
  }
}
my $start = time();
Migrate::runSqlParallel($concurrent, @sql);
my $elapsed = time() - $start;
my $numGroups = scalar @groups;
print "\nOptimized $numGroups mailbox groups in $elapsed seconds\n";

exit(0);
