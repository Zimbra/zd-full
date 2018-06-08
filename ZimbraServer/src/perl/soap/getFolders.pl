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
my ($root);

#standard options
my ($user, $pw, $host, $help); #standard
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "b|base=s" => \$root
          );

if (!defined($user) || defined($help)) {
    my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER [-b BASE_FOLDER]
END_OF_USAGE
    die $usage;
  }
    
    my $z = ZimbraSoapTest->new($user, $host, $pw);
    $z->doStdAuth();
    
    my $d = new XmlDoc;
    
    if (defined($root)) {
      $d->start('GetFolderRequest', $Soap::ZIMBRA_MAIL_NS);
      {
        $d->add('folder', undef,  { 'l' => $root });
      } $d->end();
    } else {
      $d->add('GetFolderRequest', $Soap::ZIMBRA_MAIL_NS);
    }
    
    my $response = $z->invokeMail($d->root());
    
    print "REQUEST:\n-------------\n".$z->to_string_simple($d);
    print "RESPONSE:\n--------------\n".$z->to_string_simple($response);
    
 
