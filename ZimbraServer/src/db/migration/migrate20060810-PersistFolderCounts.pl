#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(26);

my @mailboxIds = Migrate::getMailboxIds();
my $sql = addContactCountColumn();
foreach my $id (@mailboxIds) {
    $sql .= resizeUnreadColumn($id);
}

Migrate::runSql($sql);

Migrate::updateSchemaVersion(26, 27);

exit(0);

#####################

sub addContactCountColumn() {
    my $sql = <<ADD_CONTACT_COUNT_COLUMN_EOF;
ALTER TABLE zimbra.mailbox
ADD COLUMN contact_count INTEGER UNSIGNED DEFAULT 0 AFTER item_id_checkpoint;

UPDATE zimbra.mailbox
SET contact_count = NULL;

ADD_CONTACT_COUNT_COLUMN_EOF

    return $sql;
}

sub resizeUnreadColumn($) {
    my ($mailboxId) = @_;
    my $sql = <<RESIZE_UNREAD_COLUMN_EOF;
ALTER TABLE mailbox$mailboxId.mail_item
MODIFY COLUMN unread INTEGER UNSIGNED;

RESIZE_UNREAD_COLUMN_EOF

    return $sql;
}
