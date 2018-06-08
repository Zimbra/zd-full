#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;
my $concurrent = 10;

my $sqlGroupsWithSmallMetadata = <<_SQL_;
SELECT table_schema FROM information_schema.columns
WHERE table_name = 'mail_item' AND column_name = 'metadata' AND data_type = 'text'
ORDER BY table_schema;
_SQL_
my @groups = Migrate::runSql($sqlGroupsWithSmallMetadata);

Migrate::verifySchemaVersion(37);

my @sql = ();
foreach my $group (@groups) {
    my $sql = <<_SQL_;
ALTER TABLE $group.mail_item MODIFY COLUMN metadata MEDIUMTEXT;
_SQL_
    push(@sql, $sql);
}
Migrate::runSqlParallel($concurrent, @sql);

Migrate::updateSchemaVersion(37, 38);

exit(0);
