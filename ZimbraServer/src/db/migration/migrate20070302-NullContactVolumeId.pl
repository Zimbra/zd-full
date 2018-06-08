#!/usr/bin/perl
# 
# 
# 


use strict;
use Migrate;

foreach my $group (Migrate::getMailboxGroups()) {
    nullContactVolumeId($group);
}

exit(0);

#####################

sub nullContactVolumeId($) {
  my ($group) = @_;

  my $sql = <<NULL_CONTACT_VOLUME_ID_EOF;
UPDATE $group.mail_item
SET volume_id = NULL
WHERE type = 6;
NULL_CONTACT_VOLUME_ID_EOF

  Migrate::runSql($sql);
}
