#!/bin/sh

# Computes the absolute path of eXo
UNIX_DIR=`dirname "$0"`
JONAS_ROOT=$UNIX_DIR/../..

# Sets some variables
LOG_OPTS="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog"
EXO_OPTS="-Dexo.webui.reloadable.template=true"
export JAVA_OPTS="-Xshare:auto -Xms128m -Xmx512m $LOG_OPTS $EXO_OPTS"

# Launches the server
cd $UNIX_DIR
exec jonas "$@"
