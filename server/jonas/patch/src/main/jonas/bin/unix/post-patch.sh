#!/bin/sh
# ---------------------------------------------------------------------------
# eXo / JOnAS Post installation Patch
# This patch moves the appropriate file to complete the installation

cd `dirname $0`
JONAS_ROOT=`pwd`/../..
chmod +x "$JONAS_ROOT/bin/unix/jonas"

if [ -f $JONAS_ROOT/rars/autoload/joram_for_jonas_ra.rar ]
then
  echo "[PATCH] Moving joram_for_jonas_ra.rar from autoload..." 
  mv $JONAS_ROOT/rars/autoload/joram_for_jonas_ra.rar $JONAS_ROOT/rars/
else
  echo "[PATCH] Nothing to do"
fi

if [ -f $JONAS_ROOT/lib/endorsed/xml-apis.jar ]
then
  echo "[PATCH] Renaming xml-apis.jar to xml-apis.jar.backup..." 
  mv $JONAS_ROOT/lib/endorsed/xml-apis.jar $JONAS_ROOT/lib/endorsed/xml-apis.jar.backup 
else
  echo "[PATCH] Nothing to do"
fi

CONF_DIR=$JONAS_ROOT/templates/conf
if [ -d $CONF_DIR -a ! -d $CONF_DIR.bak ]
then
  echo "[PATCH] Creating an eXo JOnAS BASE template..."
  cp -R $CONF_DIR $CONF_DIR.bak
  cp -R $JONAS_ROOT/apps/autoload/exoplatform.ear $CONF_DIR/apps/autoload
  cp -R $JONAS_ROOT/conf/* $CONF_DIR/conf
  cp -R $JONAS_ROOT/lib/apps/* $CONF_DIR/lib/apps
else
  echo "[PATCH] Nothing to do"
fi

echo "[PATCH] Post patch complete"
