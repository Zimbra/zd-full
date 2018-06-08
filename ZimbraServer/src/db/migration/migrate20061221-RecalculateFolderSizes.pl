#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(35);

resetFolderCounts();

Migrate::updateSchemaVersion(35, 36);

exit(0);

#####################

sub resetFolderCounts() {
    my $sql = <<RESET_CONTACT_COUNT_EOF;
UPDATE zimbra.mailbox
SET contact_count = NULL;

RESET_CONTACT_COUNT_EOF

    Migrate::runSql($sql);
}
