#!/usr/bin/perl

#/*
# * 
# */
#
# ZD installer for application files
#

use strict;
use warnings;

my $license = "PLEASE READ THIS AGREEMENT CAREFULLY BEFORE USING THE SOFTWARE.  SYNACOR INC. WILL ONLY LICENSE THIS SOFTWARE TO YOU IF YOU FIRST ACCEPT THE TERMS OF THIS AGREEMENT. BY DOWNLOADING OR INSTALLING THE SOFTWARE, OR USING THE PRODUCT, YOU ARE CONSENTING TO BE BOUND BY THIS AGREEMENT. IF YOU DO NOT AGREE TO ALL OF THE TERMS OF THIS AGREEMENT, THEN DO NOT DOWNLOAD, INSTALL OR USE THE PRODUCT.
License Terms for this Zimbra Desktop Software: http://www.zimbra.com/license/zimbra_public_eula.html";
my $locale = "en_US";
my $app_root;
my $default_app_root = "/opt/zimbra/zdesktop";

my $messages = {
    en_US => {
        AcceptDecline => "(A)ccept or (D)ecline",
        ChooseAppRoot => "Choose the folder where you would like to install Zimbra Desktop's application files",
        ConfirmAppRoot => "Are you sure you would like to install Zimbra Desktop under folder {0} ? NOTE: All files under this directory will be deleted.",
        Continue => "Press enter to continue",
        Done => "done",
        Installing => "Installing application files...",
        Success => 'You have finished installing application files.',
        UserInstall => "Would you like to continue to install data files for user: {0} ?",
        UserInstallNote => "To install data files for additional users, please login as the user and run this command:",
        Welcome => "Welcome to Zimbra Desktop setup wizard. This will install Zimbra Desktop on your computer.",
        YesNo => "(Y)es or (N)o"
    }
};

sub get_message($;$) {
    my ($key, $vars) = @_;

    my $msgs = $messages->{$locale};
    $msgs = $messages->{'en_US'} unless ($msgs);
    my $msg = $msgs->{$key};
    return '' unless ($msg);
    
    if ($vars) {
        my $c = 0;
        for my $v (@$vars) {
            my $k = '{' . $c . '}';
            my $pos = index($msg, $k);
            substr($msg, $pos, length($k)) = $v if ($pos >= 0);
            $c++;
        }
    }
    return $msg;
}

sub get_input($;$$) {
    my ($prompt, $default, $allow_null) = @_;
    my $ret = '';

    print "------------------------------\n";
    while (!$ret) {
	print $prompt;
	print " [$default]" if ($default);
	print ": ";

	$| = 1;
	$_ = <STDIN>;
	chomp();
	$ret = $default ? ($_ ? $_ : $default) : $_;
	last if ($allow_null);
    }
    print "\n\n";
    return $ret;
}

sub stop_process($) {
    my $exec = shift();
    my @lines = ();
    my @procs;
    my $pid;

    if (open(PSINFO, "ps -fe 2>&1 |")) {
        @lines = <PSINFO>;
        close(PSINFO);
    }
    @procs = grep(/$exec/, @lines);
    if (@procs) {
        my @cols = split(/\s+/, $procs[0]);
        $pid = $cols[1];
    }

    return unless ($pid);
    system("kill $pid");

    my $c = 12;
    while ($c--) {
        open(PSINFO, "ps -p $pid 2>&1 |");
        @lines = <PSINFO>;
        close(PSINFO);
        return unless (grep(/^$pid/, @lines));
        sleep(1);
    }
    system("kill -9 $pid");
}

sub dialog_welcome() {
    print "\n\n";
    print get_message('Welcome'), "\n";
    get_input(get_message('Continue'), '', 1);
}

sub dialog_license() {
    print $license, "\n\n";
    my $in = lc(get_input(get_message('AcceptDecline'), 'A'));
    exit 1 if (substr($in, 0, 1) ne 'a');
}

sub java_check() {
    my $javaversionhelp = `java -version 2>&1` || "";
    chomp $javaversionhelp;
    if( $javaversionhelp =~ m/java version "(.+)"/s || $javaversionhelp =~ m/openjdk version "(.+)"/s){
        my $javaversion = $1;
        my $javaver= substr $javaversion, 0, 3;
        if( $javaver < 1.6 ) { print "Current Java SE Runtime Environment (JRE) version is not supported. Please update JRE to version 1.6 or later.\n"; exit; }
    }
    else { print "Java SE Runtime Environment (JRE) not found. Please install JRE 1.6 or later.\n"; exit; }
}

sub java_variant_check() {
    my $java_variant=system("java -d64 -version >/dev/null 2>&1");
    if ( $java_variant != 0 ){
        print "Zimbra Desktop (64-bit) requires a 64-bit Java SE Runtime Environment (JRE), which is not installed or is outdated. Please install JRE 1.6 (64-bit) or later.\n";
        exit;
    }
}

sub dialog_app_root() {
    return get_input(get_message("ChooseAppRoot"), $default_app_root);
}

sub dialog_confirm_app_root() {
    if ($app_root ne $default_app_root) {
        print get_message('ConfirmAppRoot', [$app_root]), "\n";
        my $in = lc(get_input(get_message('YesNo'), 'Y'));
        exit 1 if (substr($in, 0, 1) ne 'y');
    }
}

sub dialog_user_install() {
    my $user = $ENV{USER};
    print get_message('UserInstall', [$user]), "\n";
    return lc(get_input(get_message('YesNo'), $user eq 'root' ? 'N' : 'Y'));
}

# main
my $script_dir = substr($0, 0, length($0) - 11);
chdir($script_dir);

dialog_welcome();
dialog_license();
java_check();
java_variant_check();
$app_root = dialog_app_root();

stop_process("$app_root/linux/prism/zdclient");
stop_process("$app_root/linux/jre/bin/java");

# copy app files;
dialog_confirm_app_root();
print get_message('Installing');
system("rm -rf $app_root") if (-e $app_root);
exit 1 if system("mkdir -p $app_root");
exit 1 if system("cp -r -p ./app/* $app_root");
print get_message('Done'), "\n\n";
print get_message('Success'), "\n\n";

if (dialog_user_install() eq 'y') {
    system("$app_root/linux/user-install.pl");
}

print get_message('UserInstallNote'), "\n";
print "$app_root/linux/user-install.pl\n\n";
