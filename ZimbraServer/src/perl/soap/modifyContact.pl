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
my ($id, $name, $value);
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "id=s" => \$id,
           "n=s" => \$name,
           "v=s" => \$value,
          );



if (!defined($user) || defined($help)) {
    my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -i id -n name -v value
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;

$d->start("ModifyContactRequest", $Soap::ZIMBRA_MAIL_NS, { 'replace' => "0" });
{
  $d->start("cn", undef, { 'id' => $id });
  {
    $d->add("a", undef, { 'n' => $name}, $value);
  } $d->end(); # cn
}
$d->end();                      # 'ModifyContactRequest'

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

