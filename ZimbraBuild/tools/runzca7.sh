#!/bin/bash
NOWSTAMP=`date +%Y%m%d%H%M%S`
touch /home/build/cronlogs/${NOWSTAMP}.zca7.log
chmod 777 /home/build/cronlogs/${NOWSTAMP}.zca7.log
/build/apps/bin/gobuild sandbox queue zimbra_va --changeset=False --branch=ZCA-7-BETA1 --email pjoseph --syncto=272862 --buildtype=beta --user=pjoseph --no-store-trees > /home/build/cronlogs/${NOWSTAMP}.zca7.log
exit 0
