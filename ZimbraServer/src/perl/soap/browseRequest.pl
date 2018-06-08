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
my ($user, $pw, $host, $help); #standard
my ($type, $regex, $max);

GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "re|regex=s" => \$regex,
           "max=s" => \$max,
           # add specific params below:
           "t=s" => \$type,
          );


if (!defined($user) || defined($help) || !defined($type)) {
    my $usage = <<END_OF_USAGE;
USAGE: $0 -u USER -t {domains|attachments|objects} [-re regex] [-max max]
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;

my %args =  ( 'browseBy' => $type,
            );

if (defined($regex)) {
  $args{'regex'} = $regex;
}

if (defined($max)) {
  $args{'maxToReturn'} = $max;
}

 
$d->start("BrowseRequest", $Soap::ZIMBRA_MAIL_NS, \%args);
$d->end(); # 'BrowseRequest'

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

