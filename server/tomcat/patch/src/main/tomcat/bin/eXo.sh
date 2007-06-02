#!/bin/sh

# Computes the absolute path of eXo
cd `dirname "$0"`

# Sets some variables
LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
SECURITY_OPTS="-Djava.security.auth.login.config=../conf/jaas.conf"
EXO_OPTS="-Dexo.product.developing=true"
JPDA_TRANSPORT=dt_socket
JPDA_ADDRESS=8000
JAVA_OPTS="$YOURKIT_PROFILE_OPTION $JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_OPTS"
export JAVA_OPTS

# For profiling
#DYLD_LIBRARY_PATH="/Users/tuannguyen/Desktop/YourKit.app/bin/mac/"
#export  DYLD_LIBRARY_PATH
#YOURKIT_PROFILE_OPTION="-agentlib:yjpagent"

# Launches the server
exec "$PRGDIR"/catalina.sh "$@"
