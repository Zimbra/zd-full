#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(24);

my @mailboxIds = Migrate::getMailboxIds();
foreach my $id (@mailboxIds) {
    setCheckedCalendarFlag($id);
}

Migrate::updateSchemaVersion(24, 25);

exit(0);

#####################

sub setCheckedCalendarFlag($) {
    my ($mailboxId) = @_;
    my $sql = <<EOF_SET_CHECKED_CALENDAR_FLAG;
    
UPDATE mailbox$mailboxId.mail_item mi, zimbra.mailbox mbx
SET flags = flags | 2097152,
    mod_metadata = change_checkpoint + 100,
    change_checkpoint = change_checkpoint + 200
WHERE mi.id = 10 AND mbx.id = $mailboxId;

EOF_SET_CHECKED_CALENDAR_FLAG
    Migrate::runSql($sql);
}
