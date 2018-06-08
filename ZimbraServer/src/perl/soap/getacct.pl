#!/usr/bin/perl -w
# 
# 
# 

use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;

use XmlElement;
use XmlDoc;
use Soap;

my $ACCTNS = "urn:zimbraAdmin";
my $MAILNS = "urn:zimbraAdmin";

# If you're using ActivePerl, you'll need to go and install the Crypt::SSLeay
# module for htps: to work...
#
#         ppm install http://theoryx5.uwinnipeg.ca/ppms/Crypt-SSLeay.ppd
#
my $url = "https://localhost:7071/service/admin/soap/";

my $name;

if (defined $ARGV[0] && $ARGV[0] ne "") {
    $name = $ARGV[0];
} else {
    die "Usage getacct NAME";
}

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('name', undef, undef, "zimbra");
$d->add('password', undef, undef, "zimbra");
$d->end();

my $authResponse = $SOAP->invoke($url, $d->root());

print "AuthResponse = ".$authResponse->to_string("pretty")."\n";

my $authToken = $authResponse->find_child('authToken')->content;
print "authToken($authToken)\n";

my $sessionId = $authResponse->find_child('sessionId')->content;
print "sessionId = $sessionId\n";

my $context = $SOAP->zimbraContext($authToken, $sessionId);

my $contextStr = $context->to_string("pretty");
print("Context = $contextStr\n");

$d = new XmlDoc;

$d->start('GetAccountRequest', $MAILNS); {
    $d->add('account', $MAILNS, { "by" => "name" }, $name);
} $d->end();

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty");
$out =~ s/ns0\://g;
print $out."\n";

my $start = time;
my $firstStart = time;
my $response;

$response = $SOAP->invoke($url, $d->root(), $context);

my $acctInfo = $response->find_child('account');
my $acctId = $acctInfo->attr("id");



print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty");
$out =~ s/ns0\://g;
print $out."\n";

print "AccountID is $acctId\n";


