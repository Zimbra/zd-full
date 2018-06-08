#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(42);

my $sqlStmt = <<_SQL_;
CREATE TABLE scheduled_task (
   class_name      VARCHAR(255) BINARY NOT NULL,
   name            VARCHAR(255) NOT NULL,
   mailbox_id      INTEGER UNSIGNED,
   exec_time       DATETIME,
   interval_millis INTEGER UNSIGNED,
   metadata        MEDIUMTEXT,

   PRIMARY KEY (name, mailbox_id, class_name),
   CONSTRAINT fk_st_mailbox_id FOREIGN KEY (mailbox_id)
      REFERENCES mailbox(id) ON DELETE CASCADE
) ENGINE = InnoDB;
_SQL_

Migrate::runSql($sqlStmt);

Migrate::updateSchemaVersion(42, 43);

exit(0);
