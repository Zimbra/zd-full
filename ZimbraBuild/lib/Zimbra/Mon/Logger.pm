# 
# 
# 
#!/usr/bin/perl

package Zimbra::Mon::Logger;

use strict;

use Sys::Syslog qw(:DEFAULT setlogsock);;
use Data::UUID;

require Exporter;

my @ISA = qw(Exporter);

my $ident="zimbramon";
my $facility="local0";
my $stats_facility = "local1";

my @EXPORT = qw (Log);

our %loglevels = ('debug' => 0, 'info' => 1, 'err' => 3, 'crit' => 4);

my $LOG_LEVEL = $loglevels{'info'};
my $ug = new Data::UUID;

sub Log
{
	my ($level,$msg) = (@_);
	if ($loglevels{$level} >= $LOG_LEVEL) {
		# native is a better choice but unix provide a consistent output
		# across multiple nodes that makes parsing easier
		setlogsock('unix');
		eval { openlog($ident, "pid,ndelay,nowait,nofatal", $facility); };
		return if ($@);
		if (length($msg) <= 800) {
			 syslog($level, "$$:$level: $msg");
		} else {
			my $last_uuid = undef;
			my $m = $msg;
			do {
				my $substring = substr $m, 0, 800;
				$m = substr $m, 800;
				if (defined $last_uuid) {
					$substring = ":::${last_uuid}:::${substring}";
				}
				$last_uuid = $ug->to_string($ug->create());
				syslog($level, "$$:$level: ${substring}:::${last_uuid}:::");
			} while (length($m) > 800);
			syslog($level, ":::${last_uuid}:::${m}");
			
		}
		if ($::DEBUG) {
			print STDERR scalar localtime().":$$:$level: $msg\n";
		}
		closelog();
	}
}

sub LogStats
{
	my ($level,$msg) = (@_);
	if ($loglevels{$level} >= $LOG_LEVEL) {
		setlogsock('unix');
		eval { openlog($ident, "pid,ndelay,nowait,nofatal", $stats_facility);};
		return if ($@);
		if (length($msg) <= 800) {
			 syslog($level, "$$:$level: $msg");
		} else {
			my $last_uuid = undef;
			my $m = $msg;
			do {
				my $substring = substr $m, 0, 800;
				$m = substr $m, 800;
				if (defined $last_uuid) {
					$substring = ":::${last_uuid}:::${substring}";
				}
				$last_uuid = $ug->to_string($ug->create());
				syslog($level, "$$:$level: ${substring}:::${last_uuid}:::");
			} while (length($m) > 800);
			syslog($level, ":::${last_uuid}:::${m}");
			
		}
		if ($::DEBUG) {
			print STDERR scalar localtime().":$$:$level: $msg\n";
		}
		closelog();
	}
}


1

