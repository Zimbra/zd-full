#!/usr/bin/perl -w
# 
# 
# 

use strict;
use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use XmlDoc;
use Soap;
use ZimbraSoapTest;

# specific to this app
my ($tests);

#standard options
my ($user, $pw, $host, $help); #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "t=s@" => \$tests,
          );



if (!defined($user) || defined($help)) {
    my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER [-t tests]
END_OF_USAGE
    die $usage;
}

my %soapargs;
$soapargs{'TIMEOUT'} = 10 * 60;

my $z = ZimbraSoapTest->new($user, $host, $pw, \%soapargs);
$z->doAdminAuth();

my $d = new XmlDoc;
my $request = "RunUnitTestsRequest";

$d->start($request, $Soap::ZIMBRA_ADMIN_NS);
{
  foreach (@$tests) {
    $d->add("test", undef, undef, $_);
  }
    
} $d->end();

my $response = $z->invokeAdmin($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

