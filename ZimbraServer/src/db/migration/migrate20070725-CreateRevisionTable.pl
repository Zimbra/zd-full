#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(44);
foreach my $group (Migrate::getMailboxGroups()) {
    createRevisionTable($group);
}
Migrate::updateSchemaVersion(44, 45);

exit(0);

#####################

sub createRevisionTable($) {
  my ($group) = @_;

  my $sql = <<CREATE_TABLE_EOF;
CREATE TABLE IF NOT EXISTS $group.revision (
   mailbox_id    INTEGER UNSIGNED NOT NULL,
   item_id       INTEGER UNSIGNED NOT NULL,
   version       INTEGER UNSIGNED NOT NULL,
   date          INTEGER UNSIGNED NOT NULL,  -- stored as a UNIX-style timestamp
   size          BIGINT UNSIGNED NOT NULL,
   volume_id     TINYINT UNSIGNED,
   blob_digest   VARCHAR(28) BINARY,         -- reference to blob, meaningful for messages only (type == 5)
   name          VARCHAR(128),               -- namespace entry for item (e.g. tag name, folder name, document filename)
   metadata      MEDIUMTEXT,
   mod_metadata  INTEGER UNSIGNED NOT NULL,  -- change number for last row modification
   change_date   INTEGER UNSIGNED,           -- UNIX-style timestamp for last row modification
   mod_content   INTEGER UNSIGNED NOT NULL,  -- change number for last change to "content" (e.g. blob)

   PRIMARY KEY (mailbox_id, item_id, version),

   CONSTRAINT fk_revision_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES zimbra.mailbox(id),
   CONSTRAINT fk_revision_item_id FOREIGN KEY (mailbox_id, item_id) REFERENCES $group.mail_item(mailbox_id, id) ON DELETE CASCADE
) ENGINE = InnoDB;
CREATE_TABLE_EOF

  Migrate::runSql($sql);
}
