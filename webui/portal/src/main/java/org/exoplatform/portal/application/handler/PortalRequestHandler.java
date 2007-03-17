/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application.handler;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.portal.application.PortalApplication;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.webui.application.ApplicationLifecycle;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIApplication;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Dec 9, 2006  
 */
public class PortalRequestHandler implements RequestHandler {
  public void execute(PortalApplication app, HttpServletRequest req, HttpServletResponse res) {
    RequestContext context = new  PortalRequestContext(app, req, res) ;  ;
    RequestContext.setCurrentInstance(context) ;
    List<ApplicationLifecycle> lifecycles = app.getApplicationLifecycle();
    try {
      for(ApplicationLifecycle lifecycle :  lifecycles)  {
        lifecycle.beginExecution(app, context) ;
      }
      UIApplication uiApp = app.getStateManager().restoreUIRootComponent(context) ;
      context.setUIApplication(uiApp) ;
      app.processDecode(uiApp, context) ;
      if(!context.isResponseComplete() && !context.getProcessRender()) {
        app.processAction(uiApp, context) ;
      }
      if(!context.isResponseComplete()) {
        uiApp.processRender(context) ;
      }
      uiApp.setLastAccessApplication(System.currentTimeMillis()) ;
    } catch(Exception ex){
      //TODO: Need to use the log service
      ex.printStackTrace() ;
    } finally {
      try {
        for(ApplicationLifecycle lifecycle :  lifecycles) {
          lifecycle.endExecution(app, context) ;
        }
      } catch (Exception exception){
        //TODO: Need to use the log service
        exception.printStackTrace() ;
      }
      RequestContext.setCurrentInstance(null) ;
    }
  }
}
