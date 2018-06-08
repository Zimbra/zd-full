#!/usr/bin/perl
# 
# 
# 
use strict;
use lib "/opt/zimbra/zimbramon/lib";
use Zimbra::Util::Common;
use Data::UUID;
use Net::LDAPapi;

my ($binddn,$bindpwd,$host,$junk,$result,@localconfig,$ismaster);
@localconfig=`/opt/zimbra/bin/zmlocalconfig -s ldap_master_url zimbra_ldap_userdn zimbra_ldap_password ldap_is_master`;

$host=$localconfig[0];
($junk,$host) = split /= /, $host, 2;
chomp $host;

$binddn=$localconfig[1];
($junk,$binddn) = split /= /, $binddn, 2;
chomp $binddn;

$bindpwd=$localconfig[2];
($junk,$bindpwd) = split /= /, $bindpwd, 2;
chomp $bindpwd;

$ismaster=$localconfig[3];
($junk,$ismaster) = split /= /, $ismaster, 2;
chomp $ismaster;

if ($ismaster ne "true") {
	exit;
}

print "Updating old Identity classes";
my @attrs = ("zimbraPrefBccAddress", "zimbraPrefForwardIncludeOriginalText", "zimbraPrefForwardReplyFormat", "zimbraPrefForwardReplyPrefixChar", "zimbraPrefMailSignature",
			"zimbraPrefMailSignatureEnabled", "zimbraPrefMailSignatureStyle", "zimbraPrefReplyIncludeOriginalText", "zimbraPrefSaveToSent", "zimbraPrefSentMailFolder",
			"zimbraPrefUseDefaultIdentitySettings");
my $ld = Net::LDAPapi->new(-url=>"$host");
my $status;
if ($host !~ /^ldaps/i) {
  $status=$ld->start_tls_s();
}
$status = $ld->bind_s($binddn,$bindpwd);
$status = $ld->search_s("",LDAP_SCOPE_SUBTREE,"objectClass=zimbraIdentity",\@attrs,0,$result);

my ($ent,$dn,$attr);

foreach ($ent = $ld->first_entry; $ent != 0; $ent = $ld->next_entry) {
	if (($dn = $ld->get_dn) eq "")
	{
		$ld->unbind;
		die "get_dn: ", $ld->errstring, ": ", $ld->extramsg;
	}
	$attr=$ld->first_attribute;
	foreach ($attr = $ld->first_attribute; $attr ne ""; $attr = $ld->next_attribute) {
		my %ldap_modifications = (
			"$attr", "",
		);
		$ld->modify_s($dn,\%ldap_modifications);
	}
	print ".";
}
print "done!\n";

$ld->unbind();
