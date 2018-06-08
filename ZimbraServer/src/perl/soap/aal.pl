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
my ($id, $category, $level);
GetOptions("u|user=s" => \$user,
           "admin" => \$admin,
           "ah|adminHost=s" => \$adminHost,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "i=s" => \$id,
           "c=s" => \$category,
           "l=s" => \$level,
          );

if (!defined($user) || defined($help) || !defined($id) || !defined($category) || !defined($level)) {
  my $usage = <<END_OF_USAGE;

USAGE: $0 -u USER -i ID -c CATEGORY -l LEVEL
END_OF_USAGE
  die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw, undef, $adminHost);
$z->doAdminAuth();

my $SOAP = $Soap::Soap12;

my $d = new XmlDoc;
$d->start('AddAccountLoggerRequest', $Soap::ZIMBRA_ADMIN_NS);
$d->add('account', undef, { "by" => "name" }, $id);
$d->add("logger", undef, { "category" => $category, "level" => $level} );
$d->end();

my $response = $z->invokeAdmin($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

