#!/usr/bin/perl -w
# 
# 
# 

#
# Simple SOAP test-harness for the AddMsg API
#

use lib "$ENV{HOME}/perl";

use Date::Parse;
use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;

use XmlElement;
use XmlDoc;
use Soap;

my $account = $ARGV[0];
my $password = $ARGV[1];

die "Usage: getskin.pl account password" 
    if (!defined($account) || !defined($password));

my $URN_ZIMBRA_ACCOUNT = "urn:zimbraAccount";

my $url = "http://localhost:7070/service/soap/";

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $URN_ZIMBRA_ACCOUNT);
$d->add('account', undef, { by => "name"}, $account);
$d->add('password', undef, undef, $password);
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
$d->start('GetAvailableSkinsRequest', $URN_ZIMBRA_ACCOUNT);
$d->end();

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

my $response = $SOAP->invoke($url, $d->root(), $context);
print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

