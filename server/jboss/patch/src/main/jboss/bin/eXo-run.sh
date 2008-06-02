#!/bin/sh
### ====================================================================== ###
##                                                                          ##
##  eXo Platform Bootstrap Script for Jboss                                    
##                                                                          ##
### ====================================================================== ###

JAVA_OPTS="$JAVA_OPTS -Dorg.apache.catalina.STRICT_SERVLET_COMPLIANCE=false"

export JAVA_OPTS

./run.sh $@