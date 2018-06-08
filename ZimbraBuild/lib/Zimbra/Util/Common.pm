#!/usr/bin/perl
# 
# 

package Zimbra::Util::Common; 
use strict;


# Zimbra Specfic library locations
use lib "/opt/zimbra/zimbramon/lib";
use lib "/opt/zimbra/zimbramon/lib/Zimbra/SOAP";
use lib "/opt/zimbra/zimbramon/lib/Zimbra/Mon";
use lib "/opt/zimbra/zimbramon/lib/Zimbra/DB";
foreach my $arch qw(i386 x86_64 i486 i586 i686 darwin) {
  foreach my $type qw(linux-thread-multi linux-gnu-thread-multi linux thread-multi thread-multi-2level) {
    my $dir = "/opt/zimbra/zimbramon/lib/${arch}-${type}";
    unshift(@INC, "$dir") 
      if (-d "$dir");
  }
}

1
