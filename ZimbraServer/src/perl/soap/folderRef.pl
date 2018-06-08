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

use XmlElement;
use XmlDoc;
use Soap;

my $userId;
my $folderId;
my $refUrl;

if (defined $ARGV[1] && $ARGV[1] ne "") {
    $userId = $ARGV[0];
    $folderId = $ARGV[1];

    if (defined $ARGV[2]) {
        $refUrl = $ARGV[2];
    }
} else {
    print "USAGE: folderRef USERID FOLDERID [URL]\n";
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
    my $out =  $d->to_string("pretty");
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
$d->start('FolderActionRequest', $MAILNS); {
    if (defined $refUrl) {
        $d->add('action', $MAILNS, { 'id' => $folderId, 'op' => 'urlRefresh', 'url' => $refUrl});
    } else {
        $d->add('action', $MAILNS, { 'id' => $folderId, 'op' => 'urlRefresh'});
    }
} $d->end();

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty");
$out =~ s/ns0\://g;
print $out."\n";

my $response;

$response = $SOAP->invoke($url, $d->root(), $context);

print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty");
$out =~ s/ns0\://g;
print $out."\n";

