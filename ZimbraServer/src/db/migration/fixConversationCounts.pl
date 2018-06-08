#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;


my @mailboxIds = Migrate::getMailboxIds();
foreach my $id (@mailboxIds) {
    fixConversationCount($id);
}

exit(0);

#####################

sub fixConversationCount($) {
    my ($mailboxId) = @_;
    my $sql = <<EOF;
UPDATE mailbox$mailboxId.mail_item
LEFT JOIN (SELECT parent_id conv, COUNT(*) cnt FROM mailbox$mailboxId.mail_item WHERE type = 5 GROUP BY parent_id) c ON id = conv
SET size = IFNULL(cnt, 0)
WHERE type = 4;

DELETE FROM mailbox$mailboxId.mail_item WHERE type = 4 AND size = 0;

EOF
    
    Migrate::log("Updating SIZE for conversation rows in mailbox$mailboxId.mail_item.");
    Migrate::runSql($sql);
}
