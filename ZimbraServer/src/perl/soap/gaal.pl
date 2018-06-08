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
GetOptions("u|user=s" => \$user,
           "admin" => \$admin,
           "ah|adminHost=s" => \$adminHost,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help
          );

if (!defined($user) || defined($help)) {
  my $usage = <<END_OF_USAGE;

USAGE: $0 -u USER
END_OF_USAGE
  die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw, undef, $adminHost);
$z->doAdminAuth();

my $SOAP = $Soap::Soap12;

my $d = new XmlDoc;
$d->start('GetAllAccountLoggersRequest', $Soap::ZIMBRA_ADMIN_NS);
$d->end();

my $response = $z->invokeAdmin($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

