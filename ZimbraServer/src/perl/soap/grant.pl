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

# specific to this app
my ($id, $perm, $granteeType);

#standard options
my ($user, $pw, $host, $help); #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "i|id=s" => \$id,
           "r|perm=s" => \$perm,
           "g|gt=s" => \$granteeType,
          );

if (!defined($user) || !defined($id) || !defined($perm) || !defined($granteeType) || defined($help)) {
    my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -i ID -r (rwidax) -g (usr|grp|dom|cos|all)
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;
$d->start('FolderActionRequest', $Soap::ZIMBRA_MAIL_NS);
{
  $d->start('action', undef, { 'id' => $id,
                               'op' => "grant"});

  {
    $d->add("grant", undef, { 'perm' => $perm,
                              'gt' => $granteeType
                            });
  } $d->end(); # action
}

$d->end(); # 'FolderActionRequest'

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);
 
