#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;


#Migrate::verifyLoggerSchemaVersion(0);

addConfig();

#Migrate::updateLoggerSchemaVersion(1,2);

exit(0);

#####################

sub addConfig() {
    Migrate::log("Adding Config");

    my $sql = <<EOF;
DROP TABLE IF EXISTS config;
CREATE TABLE config (
	name        VARCHAR(255) NOT NULL PRIMARY KEY,
	value       TEXT,
	description TEXT,
	modified    TIMESTAMP
) ENGINE = MyISAM;
EOF

    Migrate::runLoggerSql($sql);

	$sql = <<EOF;
DELETE from zimbra_logger.config WHERE name = 'db.version';
INSERT into zimbra_logger.config (name,value) values ('db.version',2);
EOF
    Migrate::runLoggerSql($sql);
}
