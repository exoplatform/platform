package org.exoplatform.webui.application.mock;

import org.exoplatform.webui.application.Application;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.application.StateManager;
import org.exoplatform.webui.component.UIApplication;

public class MockStateManager extends StateManager {
  
  @SuppressWarnings("unused")
  public UIApplication restoreUIRootComponent(RequestContext context) {
    return null;
  }
  
  @SuppressWarnings("unused")
  public void storeUIRootComponent(RequestContext context) {
  }
  
  @SuppressWarnings("unused")
  public void expire(String sessionId, Application app) {    
  
  }
}
