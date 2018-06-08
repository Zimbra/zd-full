#!/usr/bin/perl
# 
# 
# 

use strict;
use Migrate;
my $concurrent = 10;

my @groups = Migrate::getMailboxGroups();
my $sqlGroupsWithSmallMailitem = <<_SQL_;
SELECT table_schema FROM information_schema.columns
WHERE table_name = 'mail_item' AND column_name = 'size' AND data_type = 'int'
ORDER BY table_schema;
_SQL_
my %mailItemGroups  = map { $_ => 1 } Migrate::runSql($sqlGroupsWithSmallMailitem);

my $sqlGroupsWithSmallRevisions = <<_SQL_;
SELECT table_schema FROM information_schema.columns
WHERE table_name = 'revision' AND column_name = 'size' AND data_type = 'int'
ORDER BY table_schema;
_SQL_
my %revisionGroups = map { $_ => 1 } Migrate::runSql($sqlGroupsWithSmallRevisions);

Migrate::verifySchemaVersion(49);

my @sql = ();
foreach my $group (@groups) {
  if (exists $mailItemGroups{$group}) {
    my $sql = <<_MAILITEM_SQL_;
ALTER TABLE $group.mail_item MODIFY COLUMN size BIGINT UNSIGNED NOT NULL;
_MAILITEM_SQL_
    push(@sql, $sql);
  } 
  if (exists $revisionGroups{$group}) {
    my $sql = <<_REVISION_SQL_;
ALTER TABLE $group.revision MODIFY COLUMN size BIGINT UNSIGNED NOT NULL;
_REVISION_SQL_
    push(@sql, $sql);
  } 
}
Migrate::runSqlParallel($concurrent, @sql);

Migrate::updateSchemaVersion(49, 50);

exit(0);

#####################
