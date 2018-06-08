#!/usr/bin/perl -w
# 
# 
# 

#
# Simple SOAP test-harness for the GetAppointment
#

use strict;

use lib '.';

use LWP::UserAgent;

use XmlElement;
use XmlDoc;
use Soap;

my $userId;
my $apptId;

if ($ARGV[1] ne "") {
    $userId = $ARGV[0];
    $apptId = $ARGV[1];
} else {
    print "USAGE: getAppt USERID ApptId\n";
    exit 1;
}

my $ACCTNS = "urn:zimbraAccount";
my $MAILNS = "urn:zimbraMail";

my $url = "http://localhost:7070/service/soap/";

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, $userId);
$d->add('password', undef, undef, "test123");
$d->end();

{
    print "\nOUTGOING XML:\n-------------\n";
    my $out =  $d->to_string("pretty")."\n";
    $out =~ s/ns0\://g;
    print $out."\n";
}


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
$d->start('GetAppointmentRequest', $MAILNS, { "id" => $apptId});

$d->end(); # 'GetAppointment';'

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

my $response;

$response = $SOAP->invoke($url, $d->root(), $context);

print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

