package org.exoplatform.webui.application.portlet;

import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.application.WebuiRequestContext;

public class PortletApplicationLifecycle  implements  ApplicationLifecycle<WebuiRequestContext> {
  
  @SuppressWarnings("unused")
  public void onInit(Application app) {
    
  }
  
  @SuppressWarnings("unused")
  public void onStartRequest(Application app, WebuiRequestContext context) throws Exception {
    
  }
  
  @SuppressWarnings("unused")
  public void onEndRequest(Application app, WebuiRequestContext context) throws Exception {
  }
  
  @SuppressWarnings("unused")
  public void onDestroy(Application app) {
    
  }

}