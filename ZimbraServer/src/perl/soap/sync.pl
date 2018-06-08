#!/usr/bin/perl -w
# 
# 
# 

#
# Simple SOAP test-harness for the AddMsg API
#

use strict;

use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use XmlElement;
use XmlDoc;
use Soap;
use ZimbraSoapTest;

#standard options
my ($admin, $user, $pw, $host, $help, $adminHost); #standard
my ($token);
GetOptions("u|user=s" => \$user,
           "admin" => \$admin,
           "ah|adminHost=s" => \$adminHost,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "t=s" => \$token,
          );

if (!defined($user) || defined($help)) {
  my $usage = <<END_OF_USAGE;

USAGE: $0 -u USER -t TOKEN
END_OF_USAGE
  die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw, undef, $adminHost);
$z->doStdAuth();

my $SOAP = $Soap::Soap12;

my $d = new XmlDoc;
if (defined($token) && ($token ne "")) {
    $d->start('SyncRequest', $Soap::ZIMBRA_MAIL_NS, { "token" => $token});
} else {
    $d->start('SyncRequest', $Soap::ZIMBRA_MAIL_NS);
}
$d->end(); # 'SyncRequest';'

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

