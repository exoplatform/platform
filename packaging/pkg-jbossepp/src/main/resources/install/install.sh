#!/bin/bash
#
#Thank you for choosing eXo Platform 3 for JBoss EPP Site Publisher.
#
#This file will automate the installation process of the eXo Platform 3 for JBoss EPP-SP.
#
#**************
# Prerequisites
#**************
#
#- a JBoss EPP-SP server
#- a zip file of eXoPlatform-3-for-EPPSP
#
#**************
# The command
#**************
#
# ./install.sh path-to-EPPSP-server [name-of-server-JbossEPPSP]
#
# You can find name-of-server-JbossEPPSP in jboss-as/server/name-of-server-JbossEPPSP
# name-of-server-JbossEPPSP = default (by default)
# path-to-EPPSP-server: the path to jboss-as directory
#

if [ $# -lt 1 ]
then
  echo "Usage: install.sh path-to-EPPSP-server [name-of-server-JbossEPPSP]"
  exit 1
fi

# Extract the eXo Platform for JBoss EPPSP package and the JbossEPPSP server package
#echo "Extracting the eXo Platform for JBoss EPPSP package..."
#unzip -q $1 -d "eXoPlatform-for-eppsp"

# Sets some variables
nameserver=$2
PathAddons="$PWD/.."

cd $1
PathServer=$PWD
 
if [ "$nameserver" = "" ] ;
then 
nameserver=default
fi

if [ -d "$PathServer/server/$nameserver" ] ;
     then
     echo "Starting the install.sh script"
     echo ""
     # Copy the Platform apps
     echo "* Copying the eXo Platform applications..."
     echo ""
     cp $PathAddons/addons/binaries/*.ear $PathServer/server/$nameserver/deploy 
     cp $PathAddons/demos/binaries/*.ear $PathServer/server/$nameserver/deploy 
     cp $PathAddons/demos/binaries/*.war $PathServer/server/$nameserver/deploy 

     #Configure the server
     echo "* Copying the eXo Platform configuration..."
     echo ""
     cp $PathAddons/addons/conf/*.pem $PathServer/bin
     rm -f $PathServer/bin/run.conf
     rm -f $PathServer/bin/run.conf.bat
     cp $PathAddons/addons/conf/run.conf $PathServer/bin
     cp $PathAddons/addons/conf/run.conf.bat $PathServer/bin
     rm -f $PathServer/server/$nameserver/conf/gatein/configuration.xml
     cp $PathAddons/addons/conf/configuration.xml $PathServer/server/$nameserver/conf/gatein
     echo "* Info: Now you have to add the last part (Other and Chat Server) of the configuration file $PathAddons/addons/conf/configuration.properties
into your existing configuration.properties file: $PathServer/server/$nameserver/conf/gatein/configuration.properties"
     echo "* For your information, we display the end of eXo's configuration example:"
     echo "--BEGIN"
     tail -30 $PathAddons/addons/conf/configuration.properties
     echo "--END"
     echo ""
     echo "* Info: Finally, activate the SSO in $PathServer/server/$nameserver/deploy/jbossweb.sar/server.xml
Simply uncomment the block <Valve className="org.apache.catalina.authenticator.SingleSignOn" />"
     echo "* For your information, we display the diff between these two files below (your file <> eXo's file):"
     echo "--BEGIN"
     diff -rcs $PathServer/server/$nameserver/deploy/jbossweb.sar/server.xml $PathAddons/addons/conf/server.xml
     echo "--END"
     echo ""
     echo "Stopping the install.sh script"
     echo "Thank you for using eXo Platform. You are now ready to start your JBoss server with the usual command line."
  else 
     echo "Warning: Name Server Error or path-to-EPPSP-server error. Please relaunch the script with the correct values"
     rm -rf $PathAddons
     exit 1
  fi


#************************
#Start using eXo Platform
#************************
#
#You are now ready to start your JBoss server with the usual command line. Once started, you can access the demos via this url:
# http://localhost:8080/portal/


