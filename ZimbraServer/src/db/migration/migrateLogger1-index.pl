#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;


#Migrate::verifyLoggerSchemaVersion(0);

addIndices();

#Migrate::updateLoggerSchemaVersion(0,1);

exit(0);

#####################

sub addIndices() {
    Migrate::log("Adding Indices");

    my $sql = <<EOF;
alter table mta add index i_arrive_time (arrive_time);
alter table amavis add index i_arrive_time (arrive_time);
alter table mta_aggregate add index i_period_start (period_start);
alter table mta_aggregate add index i_period_end (period_end);
alter table amavis_aggregate add index i_period_start (period_start);
alter table amavis_aggregate add index i_period_end (period_end); 
CREATE TABLE config (
	name        VARCHAR(255) NOT NULL PRIMARY KEY,
	value       TEXT,
	description TEXT,
	modified    TIMESTAMP
) ENGINE = MyISAM;
EOF

    Migrate::runLoggerSql($sql);
}
