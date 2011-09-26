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

JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
EXO_OPTS="-Dexo.product.developing=false -Dexo.conf.dir.name=gatein/conf"
EXO_CLOUD_OPTS="-javaagent:../lib/cloud-instrument-1.1-M2.jar=../gatein/conf/cloud/agent-configuration.xml \
		-Dtenant.masterhost=localhost \
		-Dtenant.repository.name=repository"
# -Dtenant.data.dir=../gatein/data/jcr"
EXO_CLOUD_SECURITY_OPTS="-Djava.security.manager=org.exoplatform.cloudmanagement.security.TenantSecurityManager -Djava.security.policy==../conf/catalina.policy"
EXO_CLOUD_ADMIN_OPTS="-Dcloud.admin.log.dir=../logs/cloud-admin -Dcloud.admin.data.dir=../gatein/data -Dcloud.admin.configuration.dir=../gatein/conf/cloud/cloud-admin -Dcloud.admin.configuration.file=../gatein/conf/cloud/cloud-admin/admin.properties"

if [ "$EXO_PROFILES" = "" ] ; then 
	EXO_PROFILES="-Dexo.profiles=default,cloud"
fi

JAVA_OPTS="-Xms512m -Xmx2g -XX:MaxPermSize=256m -XX:+UseCompressedOops $JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_OPTS $EXO_CLOUD_ADMIN_OPTS $EXO_PROFILES $EXO_CLOUD_SECURITY_OPTS $EXO_CLOUD_OPTS $JMX_OPTS"
export JAVA_OPTS

# Launches the server
exec "$PRGDIR"./catalina.sh "$@"
