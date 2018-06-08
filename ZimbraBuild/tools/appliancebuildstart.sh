#!/bin/bash
if [ $# -eq 0 ]; then
   echo "usage:takes atleast 1 parameter, branch and appliance_build_number as paramter 2 if you are just posting a build"
   exit 1
fi
export P4CONFIG=/root/.p4
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/opt/vmware/bin:/opt/vmware/bin
export TZ=America/Los_Angeles
NOWSTAMP=`date +%Y/%m/%d:%H:%M:%S`
DATESTAMP=`date +%Y%m%d%H%M%S`
unset TZ
failcnt=0
checkFailed()
{
	if [ -f "/opt/vmware/www/build/Zimbra_700.${APPLIANCE_BUILD_NUMBER}/status/verbose.log" ]; then
        	let failcnt+=`grep '\[error\]' /opt/vmware/www/build/Zimbra_700.${APPLIANCE_BUILD_NUMBER}/status/verbose.log| wc -l`
	fi
	if [ -f "/opt/vmware/www/build/Zimbra_700.${APPLIANCE_BUILD_NUMBER}/status/verbose.log" ]; then
        	let failcnt+=`grep 'Error *[0-9]' /opt/vmware/www/build/Zimbra_700.${APPLIANCE_BUILD_NUMBER}/status/verbose.log| wc -l`
	fi
	if [ -f "/opt/vmware/www/build/Zimbra_700.${APPLIANCE_BUILD_NUMBER}/status/appliancebuild.log" ]; then
        	let failcnt+=`grep 'Error *[0-9]' /opt/vmware/www/build/Zimbra_700.${APPLIANCE_BUILD_NUMBER}/status/appliancebuild.log| wc -l`
	fi
}
postBuild()
{
mkdir /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE
mkdir /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/ZimbraBuild
mkdir /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/ZimbraBuild/x86_64
mkdir /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/logs/
checkFailed
if [[ ${failcnt} -gt 0 ]]; then
                cd /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/
                touch FAILED
		cd /opt/p4/${branch}/ZimbraAppliance
		exit 1
fi
cp -Rf /opt/vmware/www/build/Zimbra_700.${APPLIANCE_BUILD_NUMBER} /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/ZimbraBuild/x86_64/
if [ -f "/home/build/cronlogs/appliancebuild.log" ]; then
	cp /home/build/cronlogs/appliancebuild.log /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/logs/
fi
cp -Rf /opt/vmware/www/build/Zimbra_700.${APPLIANCE_BUILD_NUMBER}/status/*.log /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/logs/
}
p4tag()
{
if [[ $# -gt 3 ]]; then
  NOWSTAMP=$4
fi
p4 tag -l APPLIANCE_${APPLIANCE_BUILD_NUMBER} //depot/${branch}/ZimbraAppliance...@${NOWSTAMP} > /tmp/p4tag
sleep 300
let failcnt+=`grep 'error:' /tmp/p4tag|wc -l`
let failcnt+=`grep 'exit: *[1-9]' /tmp/p4tag|wc -l`
if [[ ${failcnt} -gt 0 ]]; then
  exit 1
fi
}
p4NumberUpdate()
{
p4 edit APPLIANCE_BUILD_NUMBER > /tmp/p4tag
failcnt+=`grep 'error:' /tmp/p4tag|wc -l`
failcnt+=`grep 'exit: *[1-9]' /tmp/p4tag|wc -l`
if [ ${failcnt} -gt 0 ]; then
  exit 1
fi

p4 -c build-appliance change -o | sed -e 's/<enter description here>/bug: none AUTO UPDATE OF APPLIANCE BUILD NUMBER/' |p4 -c build-appliance submit -i > /tmp/p4tag
failcnt+=`grep 'error:' /tmp/p4tag|wc -l`
failcnt+=`grep 'exit: *[1-9]' /tmp/p4tag|wc -l`
if [ ${failcnt} -gt 0 ]; then
  exit 1
fi

}
p4tagandpostbuild()
{
if [[ $# -gt 3 ]]; then
  DATESTAMP=$4
fi
if [[ $# -gt 4 ]]; then
  NOWSTAMP=$5
fi
p4tag
mkdir /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE
mkdir /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/ZimbraBuild
mkdir /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/ZimbraBuild/x86_64
mkdir /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/logs/
checkFailed
if [[ ${failcnt} -gt 0 ]]; then
    cd /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/
    touch FAILED
    cd /opt/p4/${branch}/ZimbraAppliance
    exit 1
fi

cp -Rf /opt/vmware/www/build/Zimbra_700.${APPLIANCE_BUILD_NUMBER} /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/ZimbraBuild/x86_64/
if [ -f "/home/build/cronlogs/appliancebuild.log" ]; then
	cp /home/build/cronlogs/appliancebuild.log /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/logs/
fi
cp -Rf /opt/vmware/www/build/Zimbra_700.${APPLIANCE_BUILD_NUMBER}/status/*.log /data/zbuild3/current/APPLIANCE/${branch}/${DATESTAMP}_APPLIANCE/logs/
}
makeonward()
{
cd /opt/p4/${branch}/ZimbraAppliance/
make clean
sleep 300
make clean all MYBRANCH=${branch} > /tmp/mcnow
sleep 3000
count=`grep 'Error *[0-9]' /tmp/mcnow| wc -l`
checkFailed
if [[ ${count} -eq 0 || ${failcnt} -eq 0 ]]; then
        p4NumberUpdate
        p4tag
        postBuild
else
        exit 1
fi
}
branch=$1
echo "${branch}"
if [ $# -eq 2 ]; then
   APPLIANCE_BUILD_NUMBER=$2
   echo $APPLIANCE_BUILD_NUMBER
   postBuild
   exit 0
fi
if [ $# -gt 2 ]; then
  if [ $3='p4NumberUpdate' ]; then
  nowbuild=`cat APPLIANCE_BUILD_NUMBER`
  chmod 777 APPLIANCE_BUILD_NUMBER
  nextbuild=$2
sed "s/$nowbuild/$nextbuild/g" APPLIANCE_BUILD_NUMBER > /tmp/tmpbuild
mv /tmp/tmpbuild APPLIANCE_BUILD_NUMBER
echo `cat APPLIANCE_BUILD_NUMBER`
  else
    APPLIANCE_BUILD_NUMBER=$2
  fi
  $3
  exit 0
fi
cd /opt/p4/${branch}/ZimbraAppliance
echo now1.${failcnt}
p4 sync -f //depot/${branch}/... & > /tmp/p4now
sleep 600
echo now2.${failcnt}
let failcnt+=`grep 'error:' /tmp/p4now|wc -l`
let failcnt+=`grep 'exit: *[1-9]' /tmp/p4now|wc -l`
echo now3.${failcnt}
if [[ ${failcnt} -eq 0 ]]; then
nowbuild=`cat APPLIANCE_BUILD_NUMBER`
chmod 777 APPLIANCE_BUILD_NUMBER
echo $nowbuild
let nextbuild=$nowbuild+1
echo $nextbuild
echo `pwd`
sed "s/$nowbuild/$nextbuild/g" APPLIANCE_BUILD_NUMBER > /tmp/tmpbuild 
mv /tmp/tmpbuild APPLIANCE_BUILD_NUMBER
echo `cat APPLIANCE_BUILD_NUMBER`
rm -rf *tgz
latestlink=`perl /opt/p4/${branch}/ZimbraAppliance/getLatestLink.pl ${branch}`
echo $latestlink
wget $latestlink & > /tmp/wget
let failcnt+=`grep ERROR /tmp/wget|wc -l`
if [[ ${failcnt} -gt 0 ]]; then
  exit 1
fi
APPLIANCE_BUILD_NUMBER=$nextbuild
sleep 300
makeonward
exit 0
else
	echo failed
        exit 1
fi
