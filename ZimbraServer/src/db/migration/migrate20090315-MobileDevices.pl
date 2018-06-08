#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

Migrate::verifySchemaVersion(60);

addMobileDevicesTable();

Migrate::updateSchemaVersion(60, 61);

exit(0);

#####################

sub addMobileDevicesTable() {
    my $sql = <<CREATE_MOBILE_DEVICES_EOF;
CREATE TABLE mobile_devices (
   mailbox_id          INTEGER UNSIGNED NOT NULL,
   device_id           VARCHAR(64) NOT NULL,
   device_type         VARCHAR(64) NOT NULL,
   user_agent          VARCHAR(64),
   protocol_version    VARCHAR(64),
   provisionable       BOOLEAN NOT NULL DEFAULT 0,
   status              TINYINT UNSIGNED NOT NULL DEFAULT 0,
   policy_key          INTEGER UNSIGNED,
   recovery_password   VARCHAR(64),
   first_req_received  INTEGER UNSIGNED NOT NULL,
   last_policy_update  INTEGER UNSIGNED,
   remote_wipe_req     INTEGER UNSIGNED,
   remote_wipe_ack     INTEGER UNSIGNED,

   PRIMARY KEY (mailbox_id, device_id),
   CONSTRAINT fk_mobile_mailbox_id FOREIGN KEY (mailbox_id) REFERENCES mailbox(id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE_MOBILE_DEVICES_EOF
    
    Migrate::log("Adding ZIMBRA.MOBILE_DEVICES table.");
    Migrate::runSql($sql);
}
