package org.exoplatform.portal.application;

import javax.servlet.ServletContext;

import org.exoplatform.container.PortalContainer ;
import org.exoplatform.container.RootContainer ;
import org.exoplatform.container.SessionContainer;
import org.exoplatform.webui.application.Application;
import org.exoplatform.webui.application.ApplicationLifecycle;
import org.exoplatform.webui.application.RequestContext;

public class PortalApplicationLifecycle  implements  ApplicationLifecycle {
  
  public void init(Application app) {
    PortalApplication papp = (PortalApplication) app ;
    ServletContext context  = papp.getServletConfig().getServletContext();
    RootContainer rootContainer = RootContainer.getInstance();
    PortalContainer pcontainer = rootContainer.getPortalContainer(context.getServletContextName());
    if(pcontainer == null) pcontainer = rootContainer.createPortalContainer(context);
    PortalContainer.setInstance(pcontainer) ;
  }
 
  public void beginExecution(Application app, RequestContext rcontext) throws Exception {
    PortalApplication papp = (PortalApplication) app ;
    ServletContext context  = papp.getServletConfig().getServletContext();
    RootContainer rootContainer = RootContainer.getInstance();
    PortalContainer pcontainer = rootContainer.getPortalContainer(context.getServletContextName());
    PortalContainer.setInstance(pcontainer) ;
    SessionContainer.setInstance(pcontainer.getSessionManager().getSessionContainer(rcontext.getSessionId())) ;
  }

  @SuppressWarnings("unused")
  public void endExecution(Application app, RequestContext rcontext) throws Exception {
    SessionContainer.setInstance(null) ;
    PortalContainer.setInstance(null) ;
  }
  
  public void destroy(Application app) {
    PortalApplication papp = (PortalApplication) app ;
    ServletContext context  = papp.getServletConfig().getServletContext();
    RootContainer rootContainer = RootContainer.getInstance();
    PortalContainer pcontainer = rootContainer.getPortalContainer(context.getServletContextName());
    if(pcontainer.isStarted())  pcontainer.stop();
    rootContainer.removePortalContainer(context);
  }

}