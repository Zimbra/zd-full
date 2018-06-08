#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(43);

my $sqlStmt = <<_SQL_;
CREATE TABLE deleted_account (
    email VARCHAR(255) NOT NULL PRIMARY KEY,
    account_id CHAR(36) NOT NULL,
    mailbox_id INTEGER UNSIGNED NOT NULL,
    deleted_at INTEGER UNSIGNED NOT NULL      -- UNIX-style timestamp
) ENGINE = InnoDB;
_SQL_

Migrate::runSql($sqlStmt);

Migrate::updateSchemaVersion(43, 44);

exit(0);
