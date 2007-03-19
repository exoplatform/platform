#!/bin/sh

PRG="$0"
 
PRGDIR=`dirname "$PRG"`
EXECUTABLE=catalina.sh
LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
SECURITY_OPTS="-Djava.security.auth.login.config=$PRGDIR/../conf/jaas.conf"
EXO_OPTS="-Dexo.product.developing=true"

#DYLD_LIBRARY_PATH="/Users/tuannguyen/Desktop/YourKit.app/bin/mac/"
#export  DYLD_LIBRARY_PATH
#YOURKIT_PROFILE_OPTION="-agentlib:yjpagent"

JAVA_OPTS="$YOURKIT_PROFILE_OPTION $JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_OPTS"

exec "$PRGDIR"/"$EXECUTABLE" "$@"
