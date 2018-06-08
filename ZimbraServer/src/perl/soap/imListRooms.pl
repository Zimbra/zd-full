#!/usr/bin/perl -w
# 
# 
# 

#
# Simple SOAP test-harness for the AddMsg API
#

use Date::Parse;
use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use ZimbraSoapTest;
use XmlElement;
use XmlDoc;
use Soap;

#standard options
my ($user, $pw, $host, $help);  #standard

my $svc;

GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "svc=s" => \$svc,
          );

if (!defined($user)) {
    print "USAGE: $0 -u USER -s CONFERENCE_SERVICE\n";
    exit 1;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;
$d->start('IMListConferenceRoomsRequest', $Soap::ZIMBRA_IM_NS, { "svc" => $svc });
$d->end(); #request

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

