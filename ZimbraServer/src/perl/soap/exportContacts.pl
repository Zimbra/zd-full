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
my ($format);

#standard options
my ($user, $pw, $host, $help); #standard
GetOptions("u|user=s" => \$user,
           "f|format=s" => \$format,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,);



if (!defined($user) || defined($help)) {
    my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -f FORMAT 
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;
           
$d->start("ExportContactsRequest", $Soap::ZIMBRA_MAIL_NS, { 'ct' => "csv", 'csvfmt' => "$format" });
$d->end();

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

