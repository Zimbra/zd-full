#!/bin/bash
# 
# 
# 

CORE_PACKAGES="zimbra-core"

PACKAGES="zimbra-ldap \
zimbra-logger \
zimbra-mta \
zimbra-snmp \
zimbra-store \
zimbra-apache \
zimbra-spell \
zimbra-convertd \
zimbra-memcached \
zimbra-proxy \
zimbra-archiving \
zimbra-cluster"

SERVICES=""

OPTIONAL_PACKAGES="zimbra-qatest"

PACKAGE_DIR=`dirname $0`/packages


LOGFILE="/tmp/install.log.$$"
touch $LOGFILE
chmod 600 $LOGFILE

SAVEDIR="/opt/zimbra/.saveconfig"

if [ x$RESTORECONFIG = "x" ]; then
	RESTORECONFIG=$SAVEDIR
fi

#
# Initial values
#

AUTOINSTALL="no"
INSTALLED="no"
INSTALLED_PACKAGES=""
REMOVE="no"
UPGRADE="no"
HOSTNAME=`hostname --fqdn`
LDAPHOST=""
LDAPPORT=389
fq=`isFQDN $HOSTNAME`

if [ $fq = 0 ]; then
	HOSTNAME=""
fi

SERVICEIP=`hostname -i`

SMTPHOST=$HOSTNAME
SNMPTRAPHOST=$HOSTNAME
SMTPSOURCE="none"
SMTPDEST="none"
SNMPNOTIFY="0"
SMTPNOTIFY="0"
INSTALL_PACKAGES="zimbra-core"
STARTSERVERS="yes"
LDAPROOTPW=""
LDAPZIMBRAPW=""
LDAPPOSTPW=""
LDAPREPPW=""
LDAPAMAVISPW=""
LDAPNGINXPW=""
CREATEDOMAIN=$HOSTNAME
CREATEADMIN="admin@${CREATEDOMAIN}"
CREATEADMINPASS=""
MODE="http"
ALLOWSELFSIGNED="yes"
RUNAV=""
RUNSA=""
AVUSER=""
AVDOMAIN=""
