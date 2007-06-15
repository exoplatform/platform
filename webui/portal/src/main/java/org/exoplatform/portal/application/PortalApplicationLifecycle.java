package org.exoplatform.portal.application;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.SessionContainer;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.application.WebuiRequestContext;

public class PortalApplicationLifecycle  implements  ApplicationLifecycle<WebuiRequestContext> {
  
  @SuppressWarnings("unused")
  public void onInit(Application app) {
  }
 
  @SuppressWarnings("unused")
  public void onStartRequest(Application app, WebuiRequestContext rcontext) throws Exception {
    PortalContainer pcontainer = PortalContainer.getInstance() ;
    SessionContainer.setInstance(pcontainer.getSessionManager().getSessionContainer(rcontext.getSessionId())) ;
  }

  @SuppressWarnings("unused")
  public void onEndRequest(Application app, WebuiRequestContext rcontext) throws Exception {
    SessionContainer.setInstance(null) ;
    PortalContainer.setInstance(null) ;
  }
  
  @SuppressWarnings("unused")
  public void onDestroy(Application app) {
  }

}