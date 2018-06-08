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

#standard options
my ($sessionId, $authToken, $user, $pw, $host, $help, $verbose); #standard
my ($idle);

GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "v" => \$verbose,
           "sessionId=s" => \$sessionId,
           "at=s" => \$authToken,
           "idle" => \$idle,
          );

my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER [-v] [-at authToken] [-s sessionId] [-idle]
END_OF_USAGE

if (!defined($user)) {
  die $usage;
}

my %soapargs;
$soapargs{ 'NOTIFY'} = 1;

if (defined($sessionId)) {
  $soapargs{'SESSIONID'} = $sessionId;
} else {
  die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw, \%soapargs);
$z->verbose(3);

if (defined($sessionId) && defined($authToken)) {
  $z->setAuthContext($authToken, $sessionId, \%soapargs);
} else {
  print "AUTH REQUEST:\n--------------------";
  $z->doStdAuth();
}

my $d = new XmlDoc;

my %args = ( );

if (defined($idle)) {
  $args{'isIdle'} = 1;
} else {
  $args{'isIdle'} = 0;
}

$args{'idleTime'} = 300;

$d->add('IMSetIdleRequest', $Soap::ZIMBRA_IM_NS, \%args);

print "\n\nEND_SESSION:\n--------------------";
my $response = $z->invokeMail($d->root());

#print "REQUEST:\n-------------\n".$z->to_string_simple($d);
#print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

