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

#Production Script to launch GateIn
#See gatein-dev.sh for development starup

# Computes the absolute path of eXo
cd `dirname "$0"`

# Sets some variables
LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
SECURITY_OPTS="-Djava.security.auth.login.config=../conf/jaas.conf"
EXO_OPTS="-Dexo.product.developing=false -Dexo.conf.dir.name=gatein/conf"
IDE_OPTS="-Djavasrc=$JAVA_HOME/src.zip -Djre.lib=$JAVA_HOME/jre/lib"
if [ "$EXO_PROFILES" = "" ] ; then 
	EXO_PROFILES="-Dexo.profiles=default"
fi

BPM_HOSTNAME=localhost
BPM_HTTP_PORT=8080

# if port is not 80, use http://${BPM_HOSTNAME}:${BPM_HTTP_PORT}
#BPM_URI=http://${BPM_HOSTNAME}
BPM_URI=http://${BPM_HOSTNAME}:${BPM_HTTP_PORT}

BPM_OPTS="-Dorg.exoplatform.runtime.conf.gatein.host=${BPM_HOSTNAME} -Dorg.exoplatform.runtime.conf.gatein.port=${BPM_HTTP_PORT} -Dorg.exoplatform.runtime.conf.gatein.portal=portal"
BPM_OPTS="-Dorg.exoplatform.runtime.conf.cas.server.name=${BPM_URI} ${BPM_OPTS}"

BONITA_HOME="-DBONITA_HOME=../bonita"
REST="-Dorg.ow2.bonita.rest-server-address=${BPM_URI}/bonita-server-rest -Dorg.ow2.bonita.api-type=REST"

JAVA_OPTS="-Xms256m -Xmx1024m -XX:MaxPermSize=256m $JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_OPTS $EXO_PROFILES $BONITA_HOME $REST $BPM_OPTS" 

JAVA_OPTS="-Xms256m -Xmx1024m -XX:MaxPermSize=256m $JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_OPTS $IDE_OPTS $EXO_PROFILES"
export JAVA_OPTS

# Launches the server
exec "$PRGDIR"./catalina.sh "$@"
