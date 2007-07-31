#!/bin/sh

cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
esac

PRG="$0"
PRGDIR=`dirname "$PRG"`

LIB_DIR=`cd "$PRGDIR/.." ; pwd`
LIB_DIR="$LIB_DIR/lib"

JAVA_OPTS="-Xshare:auto -Xms128m -Xmx256m" 
CLASSPATH="$LIB_DIR/exo.tool.webunit-2.0.3.jar:$LIB_DIR/js-1.6R5.jar"

if $cygwin; then
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

java -classpath $CLASSPATH $JAVA_OPTS org.exoplatform.swing.Application &
