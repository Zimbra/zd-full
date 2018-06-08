#!/bin/bash
if [ $# -lt 4 ]; then
   echo "usage:takes 4 parameters, the datestamp for the build folder such as 20110103040000, branch such as HELIX, location of the sandbox or buildweb bits, buildname such as Zimbra_700.765"
   exit 1
fi
mydate=$1
mybranch=$2
myloc=$3
mybuild=$4
mkdir /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE
mkdir /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild
mkdir /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64
mkdir /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/logs/
mkdir /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}
mkdir /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}/exports
mkdir /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}/exports/ovf
mkdir /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}/exports/iso
mkdir /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}/exports/zip
mkdir /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}/status
cp -f ${myloc}/publish/exports/zip/* /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}/exports/zip/
cp -Rf ${myloc}/publish/exports/ovf/* /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}/exports/ovf/
cp -Rf ${myloc}/publish/exports/iso/* /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}/exports/iso/
cp -Rf ${myloc}/logs/linux/status/* /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}/status/
cp -Rf ${myloc}/logs/* /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/logs/
cp -Rf ${myloc}/publish/exports/Update_Repo/* /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/x86_64/${mybuild}/exports/zip/
chmod -R 777 /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild
chown -R build:build /data/zbuild3/current/APPLIANCE/${mybranch}/${mydate}_APPLIANCE/ZimbraBuild/
echo 'done'
exit 0
