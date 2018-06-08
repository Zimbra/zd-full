#!/usr/bin/perl
#/*
# * 
# */
#
# MacOS pre-installation script
#

use strict;
use warnings;

my $app_root = $ARGV[1];
if ($app_root =~ /.+\/Zimbra Desktop$/) {
	system("rm -rf \"$app_root\"");
} else {
	system("mv -f \"$app_root\" \"$app_root.old\"");
}
