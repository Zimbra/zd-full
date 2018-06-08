#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;


Migrate::verifyLoggerSchemaVersion(4);

addLogHostName();

Migrate::updateLoggerSchemaVersion(4,5);

exit(0);

#####################

sub addLogHostName() {
    Migrate::log("Adding loghostname");

	my $sql = <<EOF;
alter table mta add INDEX i_qid (qid);
EOF

    Migrate::runLoggerSql($sql);
}
