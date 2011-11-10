#!/bin/sh
#
# Copyright (C) 2011 eXo Platform SAS.
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

# production script to set environment variables for eXo Platform

LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
SECURITY_OPTS="-Djava.security.auth.login.config=../conf/jaas.conf"

EXO_OPTS="-Dexo.product.developing=false -Dexo.conf.dir.name=gatein/conf"
IDE_OPTS="-Djavasrc=$JAVA_HOME/src.zip -Djre.lib=$JAVA_HOME/jre/lib"

EXO_CLOUD_OPTS="-javaagent:../lib/cloud-instrument-1.1-M2.jar=../gatein/conf/cloud/agent-configuration.xml \
	-Dgroovy.script.method.iteration.time=60000"
EXO_CLOUD_SECURITY_OPTS="-Djava.security.manager=org.exoplatform.cloudmanagement.security.TenantSecurityManager \
	-Djava.security.policy==../conf/catalina.policy"

# set Cloud Admin properties in command line (can be used in standalone admin server)
EXO_CLOUD_ADMIN_OPTS="-Dcloud.admin.log.dir=../logs/cloud-admin \
	-Dcloud.admin.data.dir=../gatein/data 
	-Dcloud.admin.configuration.dir=../gatein/conf/cloud/cloud-admin 
	-Dcloud.admin.configuration.file=../gatein/conf/cloud/cloud-admin/admin.properties"

JMX_OPTS="-Dcom.sun.management.jmxremote.password.file=${CATALINA_HOME}/conf/jmxremote.password \
	-Dcom.sun.management.jmxremote.access.file=${CATALINA_HOME}/conf/jmxremote.access \
	-Dcom.sun.management.jmxremote.authenticate=true \
	-Dcom.sun.management.jmxremote.ssl=false"
	
# Remote debug configuration
#REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

JAVA_OPTS="$JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_OPTS $IDE_OPTS $EXO_CLOUD_OPTS $EXO_CLOUD_SECURITY_OPTS $EXO_CLOUD_ADMIN_OPTS $JMX_OPTS $REMOTE_DEBUG"
export JAVA_OPTS

export CLASSPATH="$CATALINA_HOME/lib/cloud-security-1.1-M2.jar:$CATALINA_HOME/conf/:$CATALINA_HOME/lib/jul-to-slf4j-1.5.8.jar:$CATALINA_HOME/lib/slf4j-api-1.5.8.jar:$CATALINA_HOME/lib/cloud-logback-logging-1.1-M2.jar:$CATALINA_HOME/lib/logback-classic-0.9.20.jar:$CATALINA_HOME/lib/logback-core-0.9.20.jar"



