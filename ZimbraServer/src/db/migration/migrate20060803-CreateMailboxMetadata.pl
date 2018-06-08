#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(25);

addMailboxMetadataTable();
removeConfigColumn();

Migrate::updateSchemaVersion(25, 26);

exit(0);

#####################

sub addMailboxMetadataTable() {
    my $sql = <<CREATE_MAILBOX_METADATA_EOF;
CREATE TABLE zimbra.mailbox_metadata (
   mailbox_id  INTEGER UNSIGNED NOT NULL,
   section     VARCHAR(64) NOT NULL,       # e.g. "imap"
   metadata    MEDIUMTEXT,

   PRIMARY KEY (mailbox_id, section),

   CONSTRAINT fk_metadata_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE_MAILBOX_METADATA_EOF
    
    Migrate::log("Adding ZIMBRA.MAILBOX_METADATA table.");
    Migrate::runSql($sql);
}

sub removeConfigColumn() {
    my $sql = <<REMOVE_CONFIG_EOF;
ALTER TABLE zimbra.mailbox
DROP COLUMN config;

REMOVE_CONFIG_EOF
    
    Migrate::log("Removing CONFIG column from ZIMBRA.MAILBOX.");
    Migrate::runSql($sql);
}
