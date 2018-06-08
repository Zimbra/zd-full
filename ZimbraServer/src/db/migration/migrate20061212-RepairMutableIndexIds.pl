#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;
my $concurrent = 10;

repairMutableIndexIds();

exit(0);

#####################

sub repairMutableIndexIds($) {
  my ($group) = @_;
  my @groups = Migrate::getMailboxGroups();

  Migrate::verifySchemaVersion(34);

  my @sql = ();
  foreach my $group (@groups) {
    my $sql = <<REPAIR_INDEX_IDS_EOF;
UPDATE $group.mail_item
SET index_id = id
WHERE index_id IS NOT NULL AND index_id <> id AND (type <> 5 OR index_id = 0);
REPAIR_INDEX_IDS_EOF
    push(@sql, $sql);
  }
  Migrate::runSqlParallel($concurrent, @sql);

  Migrate::updateSchemaVersion(34, 35);
}
