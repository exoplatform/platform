#!/bin/sh
#
# Copyright (C) 2009 eXo Platform SAS.
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
#

# Computes the absolute path of eXo
cd `dirname "$0"`

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# set CATALINA_TEMP
[ -z "$CATALINA_TEMP" ] && CATALINA_TEMP=`cd "$PRGDIR" >/dev/null; pwd`

# set PID in the file /temp/catalina.tmp
PID=$$
echo $PID > "$CATALINA_TEMP"/temp/catalina.tmp

echo Starting eXo ...

cd ./bin

#for debug mode##########
EXO_CONFIG="-Dorg.exoplatform.container.configuration.debug"
REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
DEBUG_MODE="-Dexo.product.developing=true"
########################

if [ "$1" = "" ] ; then 
	EXO_PROFILES="-Dexo.profiles=default"
	CATALINA_OPTS="-Xms256m -Xmx1024m -XX:MaxPermSize=256m $CATALINA_OPTS $EXO_PROFILES"
else
        if [ "$1" = "-debug" ]; then
                if [ "$2" = "" ]; then
        	        EXO_PROFILES="-Dexo.profiles=default"        
	        else
	                EXO_PROFILES="-Dexo.profiles=$2"
	        fi
        	CATALINA_OPTS="-Xms256m -Xmx1024m -XX:MaxPermSize=256m $CATALINA_OPTS $EXO_PROFILES $DEBUG_MODE $EXO_CONFIG $REMOTE_DEBUG"  
                echo This is debug mode        	      
	else
	        EXO_PROFILES="-Dexo.profiles=$*"
        	CATALINA_OPTS="-Xms256m -Xmx1024m -XX:MaxPermSize=256m $CATALINA_OPTS $EXO_PROFILES"
	fi
fi

echo eXo is launched with the $EXO_PROFILES option as profile

export EXO_PROFILES
#CATALINA_OPTS="$JVM_OPTS $CATALINA_OPTS $EXO_PROFILES"
export CATALINA_OPTS

# Launches the server
exec "$PRGDIR"./bin/catalina.sh run "$@"
