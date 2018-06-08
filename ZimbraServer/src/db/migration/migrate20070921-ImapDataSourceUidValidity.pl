#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(46);
foreach my $group (Migrate::getMailboxGroups()) {
    alterImapFolderSchema($group);
}
Migrate::updateSchemaVersion(46, 47);

exit(0);

#####################

sub alterImapFolderSchema($) {
  my ($group) = @_;
  my $table = $group . ".imap_folder";

  # The DELETE statement removes bogus folder trackers
  # (remote_path prefixed by /) that were discovered when fixing bug 19108.

  my $sql = <<ALTER_TABLE_EOF;
DELETE FROM $table
WHERE remote_path like '/%';

ALTER TABLE $table
ADD COLUMN uid_validity INTEGER UNSIGNED;

CREATE UNIQUE INDEX i_local_path
ON $table (local_path(200), data_source_id, mailbox_id);

CREATE UNIQUE INDEX i_remote_path
ON $table (remote_path(200), data_source_id, mailbox_id);
ALTER_TABLE_EOF

  Migrate::runSql($sql);
}
