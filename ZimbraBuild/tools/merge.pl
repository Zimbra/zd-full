#!/usr/bin/perl
# 
# 
# 

##############################################################################
#
# merge [branch spec] [flags] [chg1 ... , chgN]
#
# Simple merge script that merges one or more change numbers according to the
# given branch spec. Before using, make sure that your p4 client is set to
# the destination branch.
#
# [branch spec]		name of a P4 branch specification
# [flags]			additional flags to pass to integrate, eg "-f"
# [chgN]			a P4 change number
#
# Handles integration, resolution, and submission. The description from the
# source change is propagated forward, and it follows a summary of the merge.
# If there are conflicts, the script exits so they can be handled manually.
#
# TODO:
#	- option to do reverse integration
#	- option not to do resolve/submit
##############################################################################

use strict;

my @flags;
my $branch_spec = shift || &usage();
my $branch_info = &get_branch_info($branch_spec);
foreach my $arg (@ARGV) {
	if ($arg =~ /^-\w$/) {
		push(@flags, $arg);
	} else {
		&usage() unless ($arg =~ /^\d+$/);
		&merge($branch_info, $arg, @flags);
	}
}

exit 0;



sub merge {
	my ($branch_info, $change, @flags) = @_;
	
	my $info = &get_info($change);

	my $change_list = &create_changelist($change, $branch_info, $info);

	print "Integrating ...\n";
	my $flags = join(" ", @flags);
	system "p4 integ $flags -c $change_list -b $branch_info->{'name'} \@$change,\@$change";
	
	print "\nResolving ...\n";
	my $num_conflicts = &resolve();
	if ($num_conflicts == 0) {
		my $desc = join("\n", @{$info->{'desc'}});
		my $sb = $branch_info->{'src_branch'};
		my $db = $branch_info->{'dest_branch'};
		$desc = "Integrate $change from $sb to $db\n\n$desc";
		print "\nSubmitting ...\n";
		system "p4 submit -c $change_list";
	} else {
		my $s = ($num_conflicts == 1) ? "" : "s";
		print "$num_conflicts conflict$s trying to integrate change $change via change $change_list, bailing...\n";
	}
}

sub get_branch_info {
	my $branch_spec = shift;
	
	my $info = {};
	my @lines = grep { m!//depot/! } `p4 branch -o $branch_spec`;
	$lines[0] =~ m!//depot/(\w+)/\S* //depot/(\w+)/!;
	$info->{'name'} = $branch_spec;
	$info->{'src_branch'} = $1;
	$info->{'dest_branch'} = $2;	

	return $info;
}

sub get_info {
	my $change = shift;
	my $info = {};
	my $desc = [];
	my $files = [];
	my @lines = `p4 describe -s $change 2>&1`;
	die "Bad change number: $change\n" if ($lines[0] =~ /no such changelist/);
	my $i = 2; # ignore first two lines
	while ($lines[$i] !~ /^Affected files/ && ($i <= $#lines)) {
		if ($lines[$i] =~ /\S/ && $lines[$i] !~ /show_bug/) {
			push(@{$desc}, $lines[$i]);
		}
		$i++;
	}
	$i++ until ($lines[$i] =~ /^\.\.\./ || ($i > $#lines));
	while ($lines[$i] =~ /^\.\.\./ && ($i <= $#lines)) {
		push(@{$files}, $lines[$i]);
		$i++;
	}
	$info->{'desc'} = $desc;
	$info->{'files'} = $files;
	
	return $info;
}

sub create_changelist {
	my ($change, $branch_info, $info) = @_;

	my $tmp_file = "changelist.out";
	
	my $change_spec = <<EOF;
Change: new

Status: new
Description:
	Integrate change $change from $branch_info->{'src_branch'} to $branch_info->{'dest_branch'}

	-------------
EOF

	foreach my $line (@{$info->{'desc'}}) {
	    $change_spec .= "$line\n";
	}

	open CL, "|p4 change -i > $tmp_file" or die "Could not execute p4 change -i";
	print CL $change_spec;
	close CL;

	open CL, "<$tmp_file" or die "Open of $tmp_file failed: $!\n";
	my @lines = <CL>;
	close CL;
	foreach my $line (@lines) {
		if ($line =~ /Change ([0-9]+) created/) {
			return $1;
		}
	}
	die "Could not find change number in $tmp_file\n";
}

sub resolve {
	my $out = `p4 resolve -am`;
	my ($num) = $out =~ /(\d+) conflicting/i;
	return $num;
}

sub usage {
	print "merge [branch_spec] [flags] [chg1 ... , chgN]\n\n";
	exit 1;
}
