#!/usr/bin/perl -w
# 
# 
# 

use Date::Parse;
use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;

use XmlElement;
use XmlDoc;
use Soap;

my $userId;

if (defined $ARGV[0] && $ARGV[0] ne "") {
    $userId = $ARGV[0];
} else {
    print "USAGE: imGetRoster USERID\n";
    exit 1;
}

my $ACCTNS = "urn:zimbraAccount";
my $MAILNS = "urn:zimbraIM";

my $url = "http://localhost:7070/service/soap/";

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, $userId);
$d->add('password', undef, undef, "test123");
$d->end();

my $authResponse = $SOAP->invoke($url, $d->root());

print "AuthResponse = ".$authResponse->to_string("pretty")."\n";

my $authToken = $authResponse->find_child('authToken')->content;
print "authToken($authToken)\n";

#my $sessionElt = $authResponse->find_child('sessionId');
#if (!defined($sessionElt)) {
#  $sessionElt = $authResponse->find_child('session');
#}
#my $sessionId = $sessionElt->content;
#print "sessionId = $sessionId\n";

my $context = $SOAP->zimbraContext($authToken);

my $contextStr = $context->to_string("pretty");
print("Context = $contextStr\n");

$d = new XmlDoc;
$d->start('IMGetRosterRequest', $MAILNS);

$d->end(); #

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

my $response = $SOAP->invoke($url, $d->root(), $context);
print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";
