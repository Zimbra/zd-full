#!/usr/bin/perl -w
# 
# 
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
my ($thread, $op);

GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           "thread=s" => \$thread,
           "op=s" => \$op,
          );

if (!defined($user) || (!defined($thread)) || (!defined($op))) {
    print "USAGE: $0 -u USER -t thread -o op \n";
    exit 1;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doStdAuth();

my $d = new XmlDoc;
$d->start('IMModifyChatRequest', $Soap::ZIMBRA_IM_NS, { 'thread' => $thread, 'op'=>$op }); {
  if ($op eq "configure") {
    $d->add("var", undef, { 'name'=>"persistent" }, "false");
    $d->add("var", undef, { 'name'=>"publicroom" }, "1");
    $d->add("var", undef, { 'name'=>"moderated" }, "false");
    $d->add("var", undef, { 'name'=>"semianonymous" }, "true");
    $d->add("var", undef, { 'name'=>"noanonymous" }, "false");
    $d->add("var", undef, { 'name'=>"password" }, "test123");
    $d->add("var", undef, { 'name'=>"passwordprotect" }, "1");
  }
} $d->end();

my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

