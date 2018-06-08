#!/bin/bash
if [[ $# -lt 1 ]]; then
  echo "usage: takes one parameter, the branch such as ./runjanitor.sh main"
  exit 0
fi
branch=$1
p4 sync -f /opt/p4/${branch}/ZimbraAppliance/APPLIANCE_BUILD_NUMBER
myval=`cat /opt/p4/${branch}/ZimbraAppliance/APPLIANCE_BUILD_NUMBER`
let myval=myval-1
echo $myval
/home/build/scripts/janitor.sh ${myval} 10 > /home/build/cronlogs/janrun.log
exit 0
