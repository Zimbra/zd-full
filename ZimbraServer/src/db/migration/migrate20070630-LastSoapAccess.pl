#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(41);

my $sqlStmt = <<_SQL_;
ALTER TABLE zimbra.mailbox
ADD COLUMN last_soap_access INTEGER UNSIGNED NOT NULL DEFAULT 0 AFTER comment,
ADD COLUMN new_messages INTEGER UNSIGNED NOT NULL DEFAULT 0 AFTER last_soap_access;
_SQL_

Migrate::runSql($sqlStmt);

Migrate::updateSchemaVersion(41, 42);

exit(0);
