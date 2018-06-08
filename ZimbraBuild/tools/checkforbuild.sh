#!/bin/sh
if [ $# -lt 2 ]; then
   echo "usage:takes 2 parameters, build type, branch such as HELIX"
   exit 1
fi
mytype=$1
mybranch=$2
cn=`ls /home/build/scripts/index*|wc -l`
echo $cn
if [ $cn -gt 0 ]; then
  rm -rf /home/build/scripts/index*
fi
myurl='http://buildapi.eng.vmware.com/sb/build/?branch='$mybranch'&product=zimbra_va&buildstate=succeeded&_order_by=-id&_limit=1'
/usr/bin/wget no-check-certificate $myurl > /tmp/all 2>&1
myval=`grep 'build=' index.html* |awk '{print $28}'`
len=${#myval}
len=$(expr $len - 1)
buildid=`expr substr $myval 1 $len`
/build/apps/bin/bld -k $mytype tree $buildid > /tmp/buildpath
mybuildpath = `cat /tmp/buildpath`
mydir=`date +%Y%m%d%H%M%d`
./copybuild.sh $mydir $mybranch $mybuildpath $mybuild &
exit 0
