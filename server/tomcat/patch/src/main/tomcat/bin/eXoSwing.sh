#!/bin/sh

JAVA_OPTS="-Xshare:auto -Xms128m -Xmx256m" 

java $JAVA_OPTS  -jar ../lib/exo.tool.webunit-2.0.3.jar &
