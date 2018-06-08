#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

my @mailboxIds = Migrate::getMailboxIds();

Migrate::verifySchemaVersion(21);
foreach my $id (@mailboxIds) {
    moveApptsOutOfInbox($id);
}

exit(0);

#####################

sub moveApptsOutOfInbox($) {
    my ($mailboxId) = @_;
    my $sql = <<EOF;
UPDATE mailbox$mailboxId.mail_item SET folder_id=10
WHERE folder_id=2 AND type=11;

EOF
    
    Migrate::log("Fixing appointments in mailbox$mailboxId.mail_item.");
    Migrate::runSql($sql);
}
