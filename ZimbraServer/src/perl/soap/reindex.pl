#!/usr/bin/perl -w
# 
# 
# 

use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use XmlElement;
use XmlDoc;
use Soap;
use ZimbraSoapTest;

my $ACCTNS = "urn:zimbraAdmin";
my $MAILNS = "urn:zimbraAdmin";

# If you're using ActivePerl, you'll need to go and install the Crypt::SSLeay
# module for htps: to work...
#
#         ppm install http://theoryx5.uwinnipeg.ca/ppms/Crypt-SSLeay.ppd
#

# app-specific options
my ($mbox, $action, $types, $ids);

#standard options
my ($user, $pw, $host, $help);  #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "m|mbox=s" => \$mbox,
           "a|action=s" => \$action,
           "t|types=s" => \$types,
           "ids=s" => \$ids,
           "help|?" => \$help);

if (!defined($user)) {
  die "USAGE: $0 -u USER -m MAILBOXID -a ACTION [-pw PASSWD] [-h HOST] [-t TYPES] [-ids IDS]";
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doAdminAuth();

my %args = ( 'action' => $action );


my $d = new XmlDoc;
$d = new XmlDoc;
$d->start('ReIndexRequest', $MAILNS, \%args); {
  my %mbxArgs = ( 'id' => $mbox );
  if (defined $ids) {
    $mbxArgs{'ids'} = $ids;
  }
  if (defined $types) {
    $mbxArgs{'types'} = $types;
  }
  $d->add('mbox', $MAILNS, \%mbxArgs);
} $d->end();

print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty");
$out =~ s/ns0\://g;
print $out."\n";

my $start = time;
my $firstStart = time;

my $response = $z->invokeAdmin($d->root());

print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty");
$out =~ s/ns0\://g;
print $out."\n";


