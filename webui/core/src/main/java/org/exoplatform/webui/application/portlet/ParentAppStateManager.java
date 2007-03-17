package org.exoplatform.webui.application.portlet;

import org.exoplatform.webui.application.Application;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.application.StateManager;
import org.exoplatform.webui.component.UIApplication;

public class ParentAppStateManager extends StateManager {
  @SuppressWarnings("unchecked")
  public UIApplication restoreUIRootComponent(RequestContext context) throws Exception {
    return context.getParentAppRequestContext().getStateManager().restoreUIRootComponent(context) ;
  }
  
  @SuppressWarnings("unused")
  public void storeUIRootComponent(RequestContext context) {
  }

  @SuppressWarnings("unused")
  public void expire(String sessionId, Application app) {
  }
}