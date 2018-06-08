#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(47);
Migrate::runSql("CREATE INDEX i_mailbox_id ON zimbra.scheduled_task (mailbox_id);");
Migrate::updateSchemaVersion(47, 48);
exit(0);
