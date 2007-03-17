package org.exoplatform.webui.application;

import org.exoplatform.webui.component.UIApplication;

abstract public class StateManager {
  abstract public UIApplication restoreUIRootComponent(RequestContext context) throws Exception ;
  abstract public void storeUIRootComponent(RequestContext context) throws Exception ;
  abstract public void expire(String sessionId, Application app) throws Exception ;
}