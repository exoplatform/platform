#!/bin/bash

# Copyright (C) 2010 eXo Platform SAS.
#
# This is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as
# published by the Free Software Foundation; either version 2.1 of
# the License, or (at your option) any later version.
#
# This software is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this software; if not, write to the Free
# Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
# 02110-1301 USA, or see the FSF site: http://www.fsf.org.

# Set some variables

cmdLineJMXJar=./cmdline-jmxclient-0.10.3.jar
jmxHost=localhost
port=9012
CATALINA_OPTS="\$CATALINA_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=${port} -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

# The Script takes no parameters, exit in other cases..
if [ $# = 0 ] ; then
	echo -e "\t" $0 "takes two arguments: <BOS-Server-location> & <backup-location>. Bonita Migration Process: exit."
	exit
else

	echo -e "\t" "Starting the Bonita Migration Process.."

	bosLocation=$1
	
# See if the user already added a JMX configuration
	if ! [[ $(grep -m1 "com.sun.management.jmxremote" $bosLocation/bin/setenv.sh) ]]; then
	# Add the JMX configuration
		echo -e "\t" "Patching the server's execution environment.."
		echo -e "\t" "Adding configurations for JMX.."
		if [ -f $bosLocation/bin/setenv.sh -a -r $bosLocation/bin/setenv.sh ]; then
			# Stores the original file "setenv.sh" as a backup
			cp -f $bosLocation/bin/setenv.sh $bosLocation/bin/setenv-backup.sh
			# Writes the JMX OPTS in the "setenv.sh" file
			sed -i "s/export\ CATALINA\_OPTS/CATALINA_OPTS=\"${CATALINA_OPTS}\"\nexport\ CATALINA\_OPTS/g" $bosLocation/bin/setenv.sh
		else
			echo -e "\t" "Error: Cannot read $bosLocation/bin/setenv.sh"
		fi
	else
	# If JMX is configured, just print this information [THE USER NEEDS TO EDIT IN THIS FILE IN ORDER TO HAVE THE SAME PORT NUMBER (here we use port=9012 as a default value)]
		echo -e "\t" "The BOS standalone server alredy uses a JMX configuration.."
		grep -m1 "com.sun.management.jmxremote" $bosLocation/bin/setenv.sh
	fi
	
	backupLocation=$2

# Calling the remote JMX MBEan BOSBackupService
# java -jar ${cmdLineJMXJar} - ${jmxHost}:${port} exo:service=bonita-ext,name=BOSBackupService,type=platform doBackup=$backupLocation/backup
	echo -e "\t" "Starting backup for repository db1 at" $backupLocation/backup
	echo -e "\t" "..."

	if ! [[ $(java -jar ${cmdLineJMXJar} - ${jmxHost}:${port} exo:service=bonita-ext,name=BOSBackupService,type=platform doBackup=${backupLocation}/backup) ]]; then
		echo -e "\t" "done."
	else
		echo -e "\t" "Bonita Migration Process: exit."
		exit 
	fi

# End.
	echo -e "\t" "Bonita document library backup has finished successfully."

fi
