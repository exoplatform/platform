package org.exoplatform.webui.application;

import org.exoplatform.webui.event.MonitorEvent;


public class MonitorApplicationLifecycle implements  ApplicationLifecycle {

  public void init(Application app) throws Exception {
    MonitorEvent<Application> event = 
      new MonitorEvent<Application>(app, MonitorEvent.PORTAL_APPLICATION_LIFECYCLE_EVENT, null) ;
    event.setStartExecutionTime(System.currentTimeMillis()) ;
    app.setAttribute(MonitorEvent.PORTAL_APPLICATION_LIFECYCLE_EVENT, event) ;
    app.broadcast(event) ;
  }

  @SuppressWarnings("unchecked")
  public void destroy(Application app) throws Exception {
    MonitorEvent event = (MonitorEvent)app.getAttribute(MonitorEvent.PORTAL_APPLICATION_LIFECYCLE_EVENT) ;
    event.setEndExecutionTime(System.currentTimeMillis()) ;
    app.broadcast(event) ;
  }

  public void beginExecution(Application app, RequestContext rcontext) throws Exception {
    MonitorEvent<Application> event = 
      new MonitorEvent<Application>(app, MonitorEvent.PORTAL_EXECUTION_LIFECYCLE_EVENT, rcontext) ;
    event.setStartExecutionTime(System.currentTimeMillis()) ;
    rcontext.setAttribute(MonitorEvent.PORTAL_EXECUTION_LIFECYCLE_EVENT, event) ;
  }

  @SuppressWarnings("unchecked")
  public void endExecution(Application app, RequestContext rcontext) throws Exception {
    MonitorEvent event = 
      (MonitorEvent)rcontext.getAttribute(MonitorEvent.PORTAL_EXECUTION_LIFECYCLE_EVENT) ;
    event.setEndExecutionTime(System.currentTimeMillis()) ;
    event.setError(rcontext.getExecutionError()) ;
    app.broadcast(event) ;
  }
  
}