#!/usr/bin/perl
#
# 
#

use strict;
no strict "refs";

use Zimbra::Util::LDAP;

sub logMsg{
	print join (' ',@_),"\n";
}

our %config = (
	ldap_is_master	=>	$ARGV[0],
	ldap_root_password	=>	$ARGV[1],
	);

Zimbra::Util::LDAP->doLdap($ARGV[2],$ARGV[3]);
