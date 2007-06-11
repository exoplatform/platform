package org.exoplatform.webui.application.portlet;

import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.StateManager;
import org.exoplatform.webui.core.UIApplication;

public class ParentAppStateManager extends StateManager {
  @SuppressWarnings("unchecked")
  public UIApplication restoreUIRootComponent(WebuiRequestContext context) throws Exception {
    WebuiRequestContext pcontext = (WebuiRequestContext)  context.getParentAppRequestContext() ;
    return pcontext.getStateManager().restoreUIRootComponent(context) ;
  }
  
  @SuppressWarnings("unused")
  public void storeUIRootComponent(WebuiRequestContext context) {
  }

  @SuppressWarnings("unused")
  public void expire(String sessionId, WebuiApplication app) {
  }
}