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

# set CATALINA_PID
CATALINA_PID="$CATALINA_TEMP"/temp/catalina.tmp
export CATALINA_PID

echo Stopping eXo ...

cd ./bin

# shutdown.sh -force -> it makes a Kill -9 on the process
if [ -r ./shutdown.sh ]; then
	exec ./shutdown.sh $1
else
	echo shutdown.sh is missing.
fi
