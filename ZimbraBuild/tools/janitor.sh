#!/bin/bash
if [[ $# -eq 0 ]]; then
   echo "usage:takes atleast 1 parameter,appliance_build_number,this script cleans up 10 builds by default before the specified build"
   echo "You can also specify the second parameter how many builds you want to go far back instead of the default 10"
   exit 0
fi
myval=$1
echo $myval
lpcnt=10
if [[ $# -eq 2 ]]; then
lpcnt=$2
fi
for (( i=0; i<$lpcnt; i++ )) 
do
let myval=myval-1
echo $myval
mycnt=`ls -ltr /opt/vmware/www/build/Zimbra_700.${myval}*|wc -l`
if [[ $mycnt -gt 0 ]]; then
rm -rf /opt/vmware/www/build/Zimbra_700.${myval}*
fi
mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-zimbra-storage_2.0.5.${myval}*|wc -l`
if [[ $mycnt -gt 0 ]]; then
rm -rf /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-zimbra-storage_2.0.5.${myval}*
fi
mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-zimbra-administration_2.0.5.${myval}*|wc -l`
if [[ $mycnt -gt 0 ]]; then
rm -rf /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-zimbra-administration_2.0.5.${myval}*
fi
mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-zimbra-configuration_2.0.5.${myval}*|wc -l`
if [[ $mycnt -gt 0 ]]; then
rm -rf /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-zimbra-configuration_2.0.5.${myval}*
fi
mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/hyperic-agent_4.2.${myval}*|wc -l`
if [[ $mycnt -gt 0 ]]; then
rm -rf /opt/vmware/cache/repository/package-pool/hyperic-agent_4.2.${myval}*
fi
mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/zimbra-installer_7.0.0.${myval}*|wc -l`
if [[ $mycnt -gt 0 ]]; then
rm -rf /opt/vmware/cache/repository/package-pool/zimbra-installer_7.0.0.${myval}*
fi
done


#removeall but the last one

mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-core_2.1.1.0*|wc -l`
AVAIL=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-core_2.1.1.0*|awk '{print $8}'`
if [[ ${mycnt} -gt 1 ]]; then
let j=1
for i in ${AVAIL}; do
        if [[ ${j} -lt ${mycnt} ]]; then
                echo $i
                rm -rf $i
                let j+=1
        fi
done
fi

mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-init_2.1.1.0*|wc -l`
AVAIL=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-init_2.1.1.0*|awk '{print $8}'`
if [[ ${mycnt} -gt 1 ]]; then
let j=1
for i in ${AVAIL}; do
        if [[ ${j} -lt ${mycnt} ]]; then
                echo $i
                rm -rf $i
                let j+=1
        fi
done
fi

mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-system_2.1.1.0*|wc -l`
AVAIL=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-system_2.1.1.0*|awk '{print $8}'`
if [[ ${mycnt} -gt 1 ]]; then
let j=1
for i in ${AVAIL}; do
        if [[ ${j} -lt ${mycnt} ]]; then
                echo $i
                rm -rf $i
                let j+=1
        fi
done
fi

mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-update_2.1.1.0*|wc -l`
AVAIL=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-update_2.1.1.0*|awk '{print $8}'`
if [[ ${mycnt} -gt 1 ]]; then
let j=1
for i in ${AVAIL}; do
        if [[ ${j} -lt ${mycnt} ]]; then
                echo $i
                rm -rf $i
                let j+=1
        fi
done
fi


mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-network_2.1.1.0*|wc -l`
AVAIL=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-vami-service-network_2.1.1.0*|awk '{print $8}'`
if [[ ${mycnt} -gt 1 ]]; then
let j=1
for i in ${AVAIL}; do
        if [[ ${j} -lt ${mycnt} ]]; then
                echo $i
                rm -rf $i
                let j+=1
        fi
done
fi

mycnt=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-appliance-config_2.1.1.0*|wc -l`
AVAIL=`ls -ltr /opt/vmware/cache/repository/package-pool/vmware-studio-appliance-config_2.1.1.0*|awk '{print $8}'`
if [[ ${mycnt} -gt 1 ]]; then
let j=1
for i in ${AVAIL}; do
        if [[ ${j} -lt ${mycnt} ]]; then
                echo $i
                rm -rf $i
                let j+=1
        fi
done
fi
