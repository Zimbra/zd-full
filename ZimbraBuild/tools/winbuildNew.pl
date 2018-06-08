use strict;
use Cwd;

my $migWizSrcPrefix = "ZCSMigWiz";
my $migWizPrefix = "ZCSExchangeMigrationWizard";
my $impWizSrcPrefix = "ZCSImportWiz";
my $impWizPrefix = "ZCSPSTImportWizard";
my $nsfImpWizPrefix = "NSFMigration";
my $domWizSrcPrefix = "DominoMigrationWizard";
my $domWizPrefix = "ZCSDominoMigrationWizard";
my $gwWizPrefix = "ZCSGroupwiseMigrationWizard";

# the PDBs have different names than the desired EXEs
my $migWizPrefix2 = "MigrationWizard";
my $impWizPrefix2 = "PSTImportWizard";

my $toastPrefix = "ZimbraToast";

my $branch = $ARGV[0];
exit unless $branch;

my $gServerMajorVersion = "1";
my $gServerMinorVersion = "1";
my $gMapiVersion = "1.0.0";
my $installerVersionString = $gMapiVersion;
my $gToastVersion = "1.0.0";
my $gToastFullVersion = "1.0.0";
my $VTAG = "Unknown";
my $serverVersion = "Unknown";
my $p4user = "build";
my $p4pass = "build1pass";
my $p4client = "build-win-$branch";
my $p4port = "depot:1666";

my $base = 'c:\\src';
my $logdir = $base."\\log";
my $src = "$base\\$branch";
my $scmlog   = $logdir."\\$branch-p4.log";
my $buildlog = $logdir."\\$branch-proj-build.log"; 
my $miglog = $logdir."\\$branch-proj-mig-build.log"; 
my $toastlog = $logdir."\\$branch-proj-toast-build.log"; 
my $installlog = $logdir."\\$branch-proj-install-build.log"; 

my $P4 = "p4 -d $src -c $p4client -u $p4user -P $p4pass -p $p4port";

my $importResult = 1;
my $mapiResult = 1;
my $msiResult = 1;
my $setupResult = 1;
my $toasterResult = 1;

my @d = localtime();
my $date = sprintf ("%d%02d%02d%02d%02d%02d", $d[5]+1900,$d[4]+1,$d[3],$d[2],$d[1],$d[0]);

my $archive_base = "e:\\drop\\$branch\\$date";
my $latest = "e:\\drop\\$branch\\latest";

my @mapi_components = ("LSLIB32","LSMIME32","LSMSABP32","LSMSCFG32","LSMSSP32","LSMSUTIL32","LSMSXP32", "SharingAddin");

init();
getSource();
getServerVersion();
buildImport();
updateImportBN();
buildMapi();
updateMapiBN();
buildInstaller();
updateToasterBN();
buildToaster();
archiveMigWiz();
archiveImportWiz();
archiveDominoMig();
archiveNSFWiz();
archiveGroupwiseMig();
archiveZCO();
archiveToaster();
archiveFinalize();
sendResults(); # sendResults() should happen at the very end
exit (0);

sub doCmd {
  my $cmdLine = shift(@_);
  print $cmdLine . "\n";
  system($cmdLine);
}

sub init {
	doCmd( "del /q $scmlog" );
	doCmd( "del /q $buildlog" );
	doCmd( "del /q $miglog" );
	doCmd( "del /q $toastlog" );
	doCmd( "del /q $installlog" );
	doCmd( "rmdir /s /q $src" );
}


sub getSource {
	# rename ($src, $src."$date");
	print "Getting source into $src\n";
	mkdir "$logdir";
	mkdir "$src";
	doCmd ("$P4 sync -f ... > $scmlog");
}

sub buildImport {
	print "Building Import\n";
	doCmd ("$src\\import\\doBuild.bat $branch > $miglog");
	$importResult = $?;
}

sub buildToaster {
	print "Building Toaster\n";
	my $cwd = getcwd();
	chdir "$src\\ZimbraToastInstaller";
	doCmd ("doBuild.bat > $toastlog");
	$toasterResult = $?;
	chdir $cwd;
}

sub buildMapi {
	print "Building MAPI\n";
	doCmd ("$src\\mapi\\doBuild.bat $branch >> $buildlog");
	$mapiResult = $?;
}

sub updateToasterBN {
    print "Updating Toaster Build Number/Version\n\n";
    doCmd ("$P4 edit ZimbraToast\\AssemblyInfo.cs >> $scmlog");
    doCmd ("$P4 edit ZimbraCSharpClient\\Properties\\AssemblyInfo.cs >> $scmlog");
    open C, "$P4 change -o |";
    my @CS = <C>;
    close C;
    
    open(V, "$src\\ZimbraToast\\AssemblyInfo.cs") or die "Can't open AssemblyInfo.cs: $!\n";
    my @lines=<V>;
    close V;
    open(V, ">$src\\ZimbraToast\\AssemblyInfo.cs") or die "Can't open AssemblyInfo.cs: $!\n";
    foreach (@lines) {
		chomp;
		if (/AssemblyVersion/) {
		    my ($maj,$min,$b,$foo) = m/AssemblyVersion\(\"(\d+)\.(\d+)\.(\d+)\.(\d+)\"\)/;
			$b++;
		    $gToastVersion = "$gServerMajorVersion.$gServerMinorVersion.$b";
			$gToastFullVersion = "$gServerMajorVersion.$gServerMinorVersion.$b.$foo";
			$_ = "[assembly: AssemblyVersion(\"$gToastFullVersion\")]";
		} elsif (/AssemblyFileVersion/) {
			$_ = "[assembly: AssemblyFileVersion(\"$gToastFullVersion\")]";
		}
		print V $_,"\n";
	}
	close V;
	
	open(V, "$src\\ZimbraCSharpClient\\Properties\\AssemblyInfo.cs") or die "Can't open AssemblyInfo.cs: $!\n";
    my @lines=<V>;
    close V;
    open(V, ">$src\\ZimbraCSharpClient\\Properties\\AssemblyInfo.cs") or die "Can't open AssemblyInfo.cs: $!\n";
    foreach (@lines) {
		chomp;
		if (/AssemblyVersion/) {
			$_ = "[assembly: AssemblyVersion(\"$gToastFullVersion\")]";
		} elsif (/AssemblyFileVersion/) {
			$_ = "[assembly: AssemblyFileVersion(\"$gToastFullVersion\")]";
		}
		print V $_,"\n";
	}
	close V;

    print "Submitting change\n";
    print "version is $gToastVersion\n";
	open C, "| $P4 submit -i";
	foreach (@CS) {
		s/<enter description here>/bug: 6038\n\tAUTO UPDATE OF Toaster BUILD NUMBER\n\t$gToastVersion/;
		print C $_;
	}
	close C;
    

}

sub updateImportBN() {
	print "Updating Import BNs\n\n";
	doCmd ("$P4 edit import\\ZimbraVersion.h >> $scmlog");
	
	open C, "$P4 change -o |";
	my @CS = <C>;
	close C;

	open (V, "$src\\import\\ZimbraVersion.h") or die "Can't open V: $!";
	my @lines=<V>;
	close V;
	open V, ">$src\\import\\ZimbraVersion.h" or die "Can't open V: $!";

	my $newVersionNumber = "1.0.0";
	my $newVersionString = "1.0.0";

	foreach (@lines) {
		chomp;
		my (undef, $k, $val) = split (' ',$_,3);
		if (/ZIMBRA_MIG_VERSION_NUMBER/) {
			# this is the first one that should be found
			my ($maj,$min,$b,$foo) = split ',', $val;
			$b++;

			# stash away these strings for use later
			$newVersionNumber = "$gServerMajorVersion,$gServerMinorVersion,$b,$foo";
			$newVersionString = "$gServerMajorVersion.$gServerMinorVersion.$b";

		    # write back the next version number using the server major and minor
			$_="#define $k $newVersionNumber";
		} elsif (/ZIMBRA_MIG_VERSION_STRING/) {
			$_="#define $k \"$newVersionString\"";
		} elsif (/ZIMBRA_IMPORT_VERSION_NUMBER/) {
			$_="#define $k $newVersionNumber";
		} elsif (/ZIMBRA_IMPORT_VERSION_STRING/) {
			$_="#define $k \"$newVersionString\"";
		} elsif (/ZIMBRA_DOMINO_VERSION_NUMBER/) {
			$_="#define $k $newVersionNumber";
		} elsif (/ZIMBRA_DOMINO_VERSION_STRING/) {
			$_="#define $k \"$newVersionString\"";
		} elsif (/ZIMBRA_NSF_VERSION_NUMBER/) {
			$_="#define $k $newVersionNumber";
		} elsif (/ZIMBRA_NSF_VERSION_STRING/) {
			$_="#define $k \"$newVersionString\"";
		} elsif (/ZIMBRA_GW_VERSION_NUMBER/) {
			$_="#define $k $newVersionNumber";
		} elsif (/ZIMBRA_GW_VERSION_STRING/) {
			$_="#define $k \"$newVersionString\"";
		}
		print V $_,"\n";
	}
	close V;

	print "Submitting change\n";
	open C, "| $P4 submit -i";
	foreach (@CS) {
		s/<enter description here>/bug: 6038\n\tAUTO UPDATE OF BUILD NUMBER\n\t$newVersionString/;
		print C $_;
	}
	close C;

}

sub updateMapiBN {
	print "Updating BN\n\n";
	doCmd ("$P4 edit mapi\\src\\INCLUDE\\ZimbraVersion.h >> $scmlog");
	doCmd ("$P4 edit mapiInstaller\\Version.wxs >> $scmlog");
	doCmd ("$P4 edit mapiInstaller\\Setup\\Version.h >> $scmlog");
	
	open C, "$P4 change -o |";
	my @CS = <C>;
	close C;

	open (V, "$src\\mapi\\src\\INCLUDE\\ZimbraVersion.h") or die "Can't open V: $!";
	my @lines=<V>;
	close V;
	open V, ">$src\\mapi\\src\\INCLUDE\\ZimbraVersion.h" or die "Can't open V: $!";

	my $newVersionString = "1.0";

	foreach (@lines) {
		chomp;
		my (undef, $k, $val) = split (' ',$_,3);
		if (/ZIMBRA_VERSION_NUMBER/) {
			my ($maj,$min,$b,$foo) = split ',', $val;
			$gMapiVersion="$gServerMajorVersion.$gServerMinorVersion.$b.$foo";
			$installerVersionString = "$gServerMajorVersion.$gServerMinorVersion.$b";
			$b++;
			$newVersionString = "$gServerMajorVersion.$gServerMinorVersion.$b";
		    # write back the next version number using the server major and minor
			$_ = "#define $k $gServerMajorVersion,$gServerMinorVersion,$b,$foo";
		} elsif (/ZIMBRA_VERSION_STRING/) {
			$_ = "#define $k \"$newVersionString\"";
		}
		print V $_,"\n";
	}
	close V;

	# so now gMapiVersion has the current mapi version number
	# so now installerVersionString has the current installer version number
	# but what was written to the ZimbraVersion file has the version numbers
	# for the next build
	# we need this for the installer right now because
	# it gets called after this function is executed

	open (V, "$src\\mapiInstaller\\Version.wxs");
	my @lines=<V>;
	close V;
	open V, ">$src\\mapiInstaller\\Version.wxs" or die "Can't open V: $!";

	# just write what we read from the ZCO ZimbraVersion.h file
	foreach (@lines) {
		chomp;
		my (undef, $k, $val) = split (' ',$_,3);
		if (/<?define ZIMBRA_VERSION_STRING/) {
			print V "<?define ZIMBRA_VERSION_STRING=\"$gMapiVersion\"?>\n";
		} elsif (/<?define ZIMBRA_PRODUCT_GUID/) {
			open G, "uuidgen |";
			my $g = <G>;
			close G;
			chomp $g;
			print V "<?define ZIMBRA_PRODUCT_GUID=\"$g\"?>\n";
		} elsif (/<?define ZIMBRA_PACKAGE_GUID/) {
			open G, "uuidgen |";
			my $g = <G>;
			close G;
			chomp $g;
			print V "<?define ZIMBRA_PACKAGE_GUID=\"$g\"?>\n";
		} else {
			print V $_,"\n";
		}
	}
	close V;
	
	open (V, "$src\\mapiInstaller\\Setup\\Version.h") or die "Can't open V: $!";
	my @lines=<V>;
	close V;
	open V, ">$src\\mapiInstaller\\Setup\\Version.h" or die "Can't open V: $!";

	# just write what we read from the ZCO ZimbraVersion.h file
	foreach (@lines) {
		chomp;
		my (undef, $k, $val) = split (' ',$_,3);
		if (/ZIMBRA_VERSION_NUMBER/) {
			$_ = "#define $k $gMapiVersion";
		} elsif (/ZIMBRA_VERSION_STRING/) {
			$_ = "#define $k \"$installerVersionString\"";
		}
		print V $_,"\n";
	}
	close V;

	print "Submitting change\n";
	print "version is $gMapiVersion\n";
	print "installer version is $installerVersionString\n";
	open C, "| $P4 submit -i";
	foreach (@CS) {
		s/<enter description here>/bug: 6038\n\tAUTO UPDATE OF BUILD NUMBER\n\t$gMapiVersion/;
		print C $_;
	}
	close C;

}

sub buildInstaller {
	print "Building installer\n";
	if( $mapiResult == 0 ) {
	
		my $installerSrc = "$src\\mapiInstaller\\SourceDir\\";
	
		#ensure the SourceDir exists
		mkdir "$installerSrc";
		
		#copy the new DLL's to be packaged int the installer over to SourceDir of installer project
		foreach my $comp (@mapi_components) {
			doCmd("copy $src\\mapi\\out\\$comp\\dbg\\usa\\*dll $installerSrc");
		}

		#copy the zcologctl to the source dir
		doCmd("copy $src\\mapi\\out\\ZCOLogCtl\\dbg\\usa\\*exe $installerSrc");

		#build the sucker
		doCmd("$src\\mapiInstaller\\doBuild.bat $branch >> $installlog");
		$msiResult = $?;

		#remove the contents of SourceDir so next build is fresh
		doCmd("del /q $installerSrc\\*");
		
		
		## build the vista setup shim
		
		my $setupSrc = "$src\\mapiInstaller\\Setup";
		
		#copy the new msi over
		doCmd("copy $src\\mapiInstaller\\bin\\debug\\ZimbraOlkConnector.msi $setupSrc\\");
		
		#build the setup
		doCmd("$src\\mapiInstaller\\Setup\\doBuild.bat >> $installlog");
		$setupResult = $?;
		
		#remove the cached msi
		doCmd("del /q $setupSrc\\ZimbraOlkConnector.msi");		
		
	}

}

sub getServerVersion {

	open (FOO, "<$src\\RE\\MAJOR");
	$VTAG = <FOO>;
    chomp $VTAG;
	$gServerMajorVersion = $VTAG;
	close FOO;

	$VTAG .= ".";
	open (FOO, "<$src\\RE\\MINOR");
	$gServerMinorVersion = <FOO>;
    chomp $gServerMinorVersion;
	$VTAG .= $gServerMinorVersion;
	close FOO;

	$VTAG .= ".";
	open (FOO, "<$src\\RE\\MICRO");
	$VTAG .= <FOO>;
    chomp $VTAG;
	close FOO;

	$VTAG .= "_";
	open (FOO, "<$src\\RE\\BUILD");
	$VTAG .= <FOO>;
    chomp $VTAG;
	close FOO;
	
	$serverVersion = $VTAG;
	
}


sub archiveMigWiz {

	print "Archiving migration wizard...\n";
	mkdir "$archive_base";

	#
	#  MigrationWizard
	#
	mkdir "$archive_base\\import";
	mkdir "$archive_base\\import\\bin";
	mkdir "$archive_base\\import\\sym";
	doCmd ("copy $src\\import\\Release\\${migWizSrcPrefix}.exe $archive_base\\import\\bin\\${migWizPrefix}-$VTAG.exe");
	doCmd ("copy $src\\import\\Release\\${migWizSrcPrefix}.pdb $archive_base\\import\\sym\\${migWizPrefix}-$VTAG.pdb");
	doCmd ("copy $src\\import\\Release\\${migWizPrefix2}.pdb $archive_base\\import\\sym\\${migWizPrefix2}-$VTAG.pdb");
	doCmd ("copy $src\\import\\Release\\vc70.pdb $archive_base\\import\\sym");
	
}

sub archiveImportWiz {

	print "Archiving import wizard...\n";

	#
	#  ImportWizard
	#
	mkdir "$archive_base\\pstimport";
 	mkdir "$archive_base\\pstimport\\bin";
	mkdir "$archive_base\\pstimport\\sym";
	doCmd ("copy $src\\import\\ReleasePST\\${impWizSrcPrefix}.exe $archive_base\\pstimport\\bin\\${impWizPrefix}-$VTAG.exe");
	doCmd ("copy $src\\import\\ReleasePST\\${impWizSrcPrefix}.pdb $archive_base\\pstimport\\sym\\${impWizPrefix}-$VTAG.pdb");
	doCmd ("copy $src\\import\\ReleasePST\\${impWizPrefix2}.pdb $archive_base\\pstimport\\sym\\${impWizPrefix2}-$VTAG.pdb");
	doCmd ("copy $src\\import\\ReleasePST\\vc70.pdb $archive_base\\pstimport\\sym");
	
}


sub archiveDominoMig {

	print "Archiving domino migration...\n";

	#
	#  DominoMigration
	#
    mkdir "$archive_base\\domino";
    mkdir "$archive_base\\domino\\bin";
    mkdir "$archive_base\\domino\\sym";
	doCmd ("copy $src\\import\\ReleaseDomino\\${domWizSrcPrefix}.exe $archive_base\\domino\\bin\\${domWizPrefix}-$VTAG.exe");
	doCmd ("copy $src\\import\\ReleaseDomino\\${domWizSrcPrefix}.pdb $archive_base\\domino\\sym\\${domWizPrefix}-$VTAG.pdb");

}

sub archiveNSFWiz {

	print "Archiving NSF import wizard...\n";

	#
	#  ImportWizard
	#
	mkdir "$archive_base\\nsfimport";
 	mkdir "$archive_base\\nsfimport\\bin";
	mkdir "$archive_base\\nsfimport\\sym";
	doCmd ("copy $src\\import\\ReleaseNSF\\${nsfImpWizPrefix}.exe $archive_base\\nsfimport\\bin\\${nsfImpWizPrefix}-$VTAG.exe");
	doCmd ("copy $src\\import\\ReleaseNSF\\${nsfImpWizPrefix}.pdb $archive_base\\nsfimport\\sym\\${nsfImpWizPrefix}-$VTAG.pdb");
	doCmd ("copy $src\\import\\ReleaseNSF\\${nsfImpWizPrefix}.pdb $archive_base\\nsfimport\\sym\\${nsfImpWizPrefix}-$VTAG.pdb");
	doCmd ("copy $src\\import\\ReleaseNSF\\vc70.pdb $archive_base\\nsfimport\\sym");
	
}


sub archiveGroupwiseMig {

	print "Archiving groupwise migration...\n";

	#
	#  GroupWise Migration
	#
    mkdir "$archive_base\\groupwise";
    mkdir "$archive_base\\groupwise\\bin";
    mkdir "$archive_base\\groupwise\\sym";
	doCmd ("copy $src\\import\\ReleaseGW\\ZCSGWMigWiz.exe $archive_base\\groupwise\\bin\\${gwWizPrefix}-$VTAG.exe");
	doCmd ("copy $src\\import\\ReleaseGW\\ZCSGWMigWiz.pdb $archive_base\\groupwise\\sym\\${gwWizPrefix}-$VTAG.pdb");


}


sub archiveZCO {

	print "Archiving ZCO...\n";

	#
	#  ZCO
	#
	mkdir "$archive_base\\mapi";
	mkdir "$archive_base\\mapi\\bin";
	mkdir "$archive_base\\mapi\\sym";
	mkdir "$archive_base\\mapi\\msi";
	foreach my $i (@mapi_components) {
		mkdir "$archive_base\\mapi\\sym\\$i";
		doCmd ("copy $src\\mapi\\out\\$i\\dbg\\usa\\*dll $archive_base\\mapi\\bin");
		doCmd ("copy $src\\mapi\\out\\$i\\dbg\\usa\\*pdb $archive_base\\mapi\\sym\\$i");
	}
	mkdir "$archive_base\\mapi\\sym\\ZCOLogCtl";
	mkdir "$archive_base\\mapi\\msi";
	mkdir "$archive_base\\mapi\\sym\\ZMapiProCA";
	mkdir "$archive_base\\mapi\\sym\\Setup";
	doCmd ("copy $src\\mapi\\out\\ZCOLogCtl\\dbg\\usa\\*exe $archive_base\\mapi\\bin");
	doCmd ("copy $src\\mapi\\out\\ZCOLogCtl\\dbg\\usa\\*pdb $archive_base\\mapi\\sym\\ZCOLogCtl");
	doCmd("copy $src\\mapiInstaller\\bin\\Debug\\*.msi $archive_base\\mapi\\msi");
	doCmd("ren $archive_base\\mapi\\msi\\ZimbraOlkConnector.msi ZimbraOlkConnector-${VTAG}_$installerVersionString.msi");
	doCmd("copy $src\\mapiInstaller\\bin\\Debug\\ZMapiProCA.dll $archive_base\\mapi\\bin" );
	doCmd("copy $src\\mapiInstaller\\bin\\Debug\\ZMapiProCA.pdb $archive_base\\mapi\\sym\\ZMapiProCA");
	
	#
	# Not copying the setup shim over now that the msi works on vista
	# leaving this here cause we'll need it when the localizable installer is completed
	#
	#doCmd("copy $src\\mapiInstaller\\Setup\\Debug\\Setup.exe $archive_base\\mapi\\msi\\ZimbraOlkConnectorSetup-${VTAG}_$installerVersionString.exe");
	#doCmd("copy $src\\mapiInstaller\\Setup\\Debug\\Setup.pdb $archive_base\\mapi\\sym\\Setup\\");
	
}


sub archiveToaster {

	print "Archiving toaster...\n";

	#
	#  Toaster
	#
	mkdir "$archive_base\\toaster";
	mkdir "$archive_base\\toaster\\bin";
	mkdir "$archive_base\\toaster\\sym";
	mkdir "$archive_base\\toaster\\msi";
	doCmd ("copy $src\\ZimbraToast\\bin\\Debug\\Zimbra.Client.pdb        $archive_base\\toaster\\sym\\");
	doCmd ("copy $src\\ZimbraToast\\bin\\Debug\\Zimbra.Client.dll        $archive_base\\toaster\\bin\\");
	doCmd ("copy $src\\ZimbraToast\\bin\\Debug\\ZToast.pdb               $archive_base\\toaster\\sym\\${toastPrefix}-${VTAG}-${gToastVersion}.pdb");
	doCmd ("copy $src\\ZimbraToast\\bin\\Debug\\ZToast.exe               $archive_base\\toaster\\bin\\${toastPrefix}-${VTAG}-${gToastVersion}.exe");
	doCmd ("copy $src\\ZimbraToastInstaller\\bin\\Debug\\ZimbraToast.msi $archive_base\\toaster\\msi\\${toastPrefix}-${VTAG}-${gToastVersion}.msi");
	
}

sub archiveFinalize {
	#
	#  Build log files
	#
	doCmd ("copy $buildlog $archive_base\\");
	doCmd ("copy $miglog $archive_base\\");
	doCmd ("copy $toastlog $archive_base\\");
	doCmd ("copy $installlog $archive_base\\");
	doCmd ("copy $scmlog   $archive_base\\");


	#
	#  Save these bits as the latest bits
	#

    #delete everything from latest
	doCmd ("rmdir /s /q $latest" );
    #create the latest directory
	doCmd ("mkdir $latest" );
	#copy the most recent stuff to latest
	doCmd ("xcopy /E $archive_base\\* $latest" );

}

sub sendResults {

	use MIME::Lite;
	
	MIME::Lite->send('smtp', "dogfood.zimbra.com", Timeout=>60);
	
	my $body = "Zimbra Server Version: $serverVersion";

	$body .= "\nMigration Wizard build ";
	if( $importResult == 0 ) { $body .= "succeeded"; } 
	else { $body .= "FAILED"; }

	$body .= "\nMAPI build $gMapiVersion ";
	if( $mapiResult == 0 ) { $body .= "succeeded"; } 
	else { $body .= "FAILED"; }

	$body .= "\nMSI build ";
	if( $msiResult == 0 ) { $body .= "succeeded"; }
	else { $body .= "FAILED"; }
	
	$body .= "\nVista Setup build ";
	if( $setupResult == 0 ) { $body .= "succeeded"; }
	else { $body .= "FAILED"; }
	
	$body .= "\nToaster build $gToastVersion ";
	if( $toasterResult == 0 ) { $body .= "succeeded"; }
	else { $body .= "FAILED"; }
	
	my $subject = "windows build $branch-$date ";
	if( $mapiResult != 0 ||  $importResult != 0 || $msiResult != 0 )
	{
		$subject .= "FAILED";
	}

	my $msg = MIME::Lite->new(
		From		=>'Windows Build Server <build-win@zimbra.com>',
		To		=>'build-win@zimbra.com',
		Subject	=>"$subject",
		Data		=>"$body" );

	$msg->attach(
		Type		=>'text/plain',
		Path		=>"$scmlog",
		Filename	=>'p4.log',
		Encoding	=>'quoted-printable');

	$msg->attach(
		Type		=>'text/plain',
		Path		=>"$buildlog",
		Filename	=>'build.log',
		Encoding	=>'quoted-printable');

	$msg->attach(
		Type		=>'text/plain',
		Path		=>"$miglog",
		Filename	=>'migration.log',
		Encoding	=>'quoted-printable');

	$msg->attach(
		Type		=>'text/plain',
		Path		=>"$toastlog",
		Filename	=>'toaster.log',
		Encoding	=>'quoted-printable');

	$msg->attach(
		Type		=>'text/plain',
		Path		=>"$installlog",
		Filename	=>'installer.log',
		Encoding	=>'quoted-printable');

	if( $mapiResult != 0 ||  $importResult != 0 || $msiResult != 0 )
	{
		$msg->add( 'X-Priority' => '1');
	}
	else
	{
		$msg->add( 'X-Priority' => '5');
	}


	$msg->send;
}
