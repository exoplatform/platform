package org.exoplatform.webui.application.mock;

import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.StateManager;
import org.exoplatform.webui.component.UIApplication;

public class MockStateManager extends StateManager {
  
  @SuppressWarnings("unused")
  public UIApplication restoreUIRootComponent(WebuiRequestContext context) {
    return null;
  }
  
  @SuppressWarnings("unused")
  public void storeUIRootComponent(WebuiRequestContext context) {
  }
  
  @SuppressWarnings("unused")
  public void expire(String sessionId, WebuiApplication app) {    
  
  }
}
