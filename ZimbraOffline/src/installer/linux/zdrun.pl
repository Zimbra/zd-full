#!/usr/bin/perl

#/*
# * 
# */
#
# ZD runner
#

use strict;
use warnings;

sub find_and_replace($$) {
    my ($file, $tokens) = @_;
    my $tmpfile = $file . '.tmp';

    open(FIN, "<$file") or die("Error: cannot open file $file\n");
    open(FOUT, ">$tmpfile") or die("Error: cannot open file $tmpfile\n");

    my $line;
    while($line = <FIN>){
        foreach my $key (keys(%$tokens)) {
            my $pos = index($line, $key);
            while ($pos >= 0) {
                substr($line, $pos, length($key), $tokens->{$key});
                $pos = index($line, $key);
            }
        }
        print FOUT $line;
    }

    close FIN;
    close FOUT;

    my (undef, undef, $mode) = stat($file);
    unlink $file;
    rename $tmpfile, $file;
    chmod $mode, $file;
}

    my $current_timestamp = (stat("@install.data.root@/conf/localconfig.xml"))[9];
    system("\"@install.data.root@/bin/zdesktop\" start");
    my $counter = 0;
    my $file_updated = 0;
    {
    do{
		my $new_timestamp = (stat("@install.data.root@/conf/localconfig.xml"))[9];
		if($new_timestamp != $current_timestamp) {
			$file_updated = 1;
			last;
		}
		else {
			sleep(2);
		}
		$counter = $counter + 1;
    }while( $counter < 10 );
    }
    if($file_updated == 0) {
	    print "localconfig.xml zimbra_admin_service_port not updated by jetty !\n";
        exit 1;
    }

my $jettyport=`grep -A1 zimbra_admin_service_port @install.data.root@/conf/localconfig.xml | tail -1 | cut -d">" -f2 | cut -d"<" -f1`;
my $installationkey=`grep -A1 zdesktop_installation_key @install.data.root@/conf/localconfig.xml | tail -1 | cut -d">" -f2 | cut -d"<" -f1`;

chomp($jettyport);
chomp($installationkey);

my $URL = "http://127.0.0.1:" . $jettyport . "/desktop/login.jsp?at=" . $installationkey;

exit 1 if system("cp -f -p \"@install.app.root@/linux/node-webkit/package.json_orig\" \"@install.app.root@/linux/node-webkit/package.json\"");

exec("\"@install.app.root@/linux/node-webkit/zdesktop\" data-path=\"@install.data.root@\" url=$URL");