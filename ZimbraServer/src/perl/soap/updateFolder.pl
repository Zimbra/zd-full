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
my ($fid);
my ($parent_fid);
my ($folderName);

#standard options
my ($user, $pw, $host, $help); #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "i|id=s" => \$fid,
           "l|parent=s" => \$parent_fid,
           "n|name=s" => \$folderName
          );

if (!defined($user) || defined($help) || !defined($fid) || !defined($parent_fid) || !defined($folderName) )
  {
    my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -i FOLDERID -l PARENTFOLDERID -n FOLDERNAME
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;
$d->start('FolderActionRequest', $Soap::ZIMBRA_MAIL_NS);
{
  $d->start('action', undef, { 'id' => $fid,
							 'l' => $parent_fid,
                             'op' => "update",
                             'name' => $folderName } );
                             
  $d->start('acl', undef, undef, undef );
  $d->end();
  $d->end();
}

$d->end(); # 'FolderActionRequest'

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);
 
