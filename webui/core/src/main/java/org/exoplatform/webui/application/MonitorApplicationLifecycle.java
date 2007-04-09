package org.exoplatform.webui.application;

import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.event.MonitorEvent;


public class MonitorApplicationLifecycle implements  ApplicationLifecycle<WebuiRequestContext> {

  public void onInit(Application app) throws Exception {
    WebuiApplication webuiapp = (WebuiApplication) app ;
    MonitorEvent<WebuiApplication> event = 
      new MonitorEvent<WebuiApplication>(webuiapp, MonitorEvent.PORTAL_APPLICATION_LIFECYCLE_EVENT, null) ;
    event.setStartExecutionTime(System.currentTimeMillis()) ;
    app.setAttribute(MonitorEvent.PORTAL_APPLICATION_LIFECYCLE_EVENT, event) ;
   webuiapp.broadcast(event) ;
  }

  @SuppressWarnings("unchecked")
  public void onDestroy(Application app) throws Exception {
    WebuiApplication webuiapp = (WebuiApplication) app ;
    MonitorEvent event = (MonitorEvent)app.getAttribute(MonitorEvent.PORTAL_APPLICATION_LIFECYCLE_EVENT) ;
    event.setEndExecutionTime(System.currentTimeMillis()) ;
    webuiapp.broadcast(event) ;
  }

  public void onStartRequest(Application app, WebuiRequestContext rcontext) throws Exception {
    WebuiApplication webuiapp = (WebuiApplication) app ;
    MonitorEvent<WebuiApplication> event = 
      new MonitorEvent<WebuiApplication>(webuiapp, MonitorEvent.PORTAL_EXECUTION_LIFECYCLE_EVENT, rcontext) ;
    event.setStartExecutionTime(System.currentTimeMillis()) ;
    rcontext.setAttribute(MonitorEvent.PORTAL_EXECUTION_LIFECYCLE_EVENT, event) ;
  }

  @SuppressWarnings("unchecked")
  public void onEndRequest(Application app, WebuiRequestContext rcontext) throws Exception {
    WebuiApplication webuiapp = (WebuiApplication) app ;
    MonitorEvent event = 
      (MonitorEvent)rcontext.getAttribute(MonitorEvent.PORTAL_EXECUTION_LIFECYCLE_EVENT) ;
    event.setEndExecutionTime(System.currentTimeMillis()) ;
    webuiapp.broadcast(event) ;
  }
  
}