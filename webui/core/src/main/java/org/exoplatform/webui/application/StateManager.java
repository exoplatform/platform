package org.exoplatform.webui.application;

import org.exoplatform.webui.component.UIApplication;

abstract public class StateManager {
  abstract public UIApplication restoreUIRootComponent(WebuiRequestContext context) throws Exception ;
  abstract public void storeUIRootComponent(WebuiRequestContext context) throws Exception ;
  abstract public void expire(String sessionId, WebuiApplication app) throws Exception ;
}