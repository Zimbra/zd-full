#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(62);
addMailboxIndexDeferredCountColumn();
Migrate::updateSchemaVersion(62, 63);

exit(0);

#####################

sub addMailboxIndexDeferredCountColumn() {
  Migrate::log("Adding idx_deferred_count column to Mailbox table.");
  
  my $sql = <<ALTER_TABLE_EOF;
ALTER TABLE zimbra.mailbox ADD COLUMN highest_indexed VARCHAR(21);
ALTER_TABLE_EOF
  
  Migrate::runSql($sql);
}
