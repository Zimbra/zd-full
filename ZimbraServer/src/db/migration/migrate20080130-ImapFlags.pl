#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(50);
foreach my $group (Migrate::getMailboxGroups()) {
    addImapFlagsColumn($group);
}
Migrate::updateSchemaVersion(50, 51);

exit(0);

#####################

sub addImapFlagsColumn($) {
  my ($group) = @_;
  
  Migrate::log("Adding flags column to $group.imap_message.");

  my $sql = <<ALTER_TABLE_EOF;
ALTER TABLE $group.imap_message
ADD COLUMN flags INTEGER NOT NULL DEFAULT 0;

UPDATE $group.imap_message
SET flags = (
  SELECT flags
  FROM $group.mail_item
  WHERE $group.imap_message.item_id = $group.mail_item.id
  AND $group.imap_message.mailbox_id = $group.mail_item.mailbox_id
);

ALTER_TABLE_EOF

  Migrate::runSql($sql);
}
