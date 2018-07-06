# Building Zimbra Desktop

## Prerequisites For Windows
- Java SDK V1.6
- ant
- http://strawberryperl.com/
- http://wixtoolset.org/ (Version 3)
- Sign tool and certificate (only if you want to sign exe)(https://revolution.screenstepslive.com/s/revolution/m/10695/l/112948-installing-signtool-exe)
- RCEdit (https://github.com/atom/node-rcedit/blob/master/bin/rcedit.exe)

## Prerequisites For Mac
- Java SDK V1.6
- ant

## Prerequisites For Linux
- Java SDK V1.6
- ant

## Build Steps
- Setup RCEdit (Windows Only)
	- Copy `rcedit.exe` to `C:\rcedit` folder
- Setup perl (Windows Only)
	- Install Strawberry Perl in `C:\strawberry` folder
- Setup Wix Toolkit (Windows Only)
	- Install Wix Toolkit V3 in `C:\Program Files\Windows Installer XML v3` folder
- To run build execute below step
	- ant -f installer-ant.xml installer-clean
	- ant -f installer-ant.xml

## Signing in Windows
- Create a folder `C:\signtool` and copy signtol.exe and certificate to sign in that
- Also create zimbra_code_signing.prop file which should contain this line - `signcert.pass=<password for certificate>`
- Copy certificate and rename to `zimbra_code_signing_cert.pfx`, if you don't want to rename it then you can pass it as ant options as well like ant -f installer-ant.xml `-Dsign.cert=C:\signtool\zimbra_code_signing_cert.pfx`

## Signing in Mac
- Pass certificate name when creating installer
	- ant -f installer-ant.xml `-Dosx_installer_cert=Developer ID Installer: Zimbra, Inc.`
- If you want to disable code signing then you use
	- ant -f installer-ant.xml `-Dosx.dontsign=true`


## Development version
- Above steps will build production version of Zimbra Desktop, which will not have developer tools to debug javascript code
- If you want to create developer version then use below command
	- ant -f installer-ant.xml `-Dnode-webkit.flavor=-sdk`