/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.WebAppController;
import org.exoplatform.web.WebRequestHandler;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIApplication;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Dec 9, 2006  
 */
public class PortalRequestHandler extends WebRequestHandler {

  static String[]  PATHS = {"/public", "/private"} ;

  public String[] getPath() { return PATHS ; }

  @SuppressWarnings("unchecked")
  public void execute(WebAppController controller,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    res.setHeader("Cache-Control", "no-cache");
    
    PortalApplication app =  controller.getApplication(PortalApplication.PORTAL_APPLICATION_ID) ;
    WebuiRequestContext context = new  PortalRequestContext(app, req, res) ;  ;
    WebuiRequestContext.setCurrentInstance(context) ;
    List<ApplicationLifecycle> lifecycles = app.getApplicationLifecycle();
    try {
      for(ApplicationLifecycle lifecycle :  lifecycles) lifecycle.onStartRequest(app, context) ;
      UIApplication uiApp = app.getStateManager().restoreUIRootComponent(context) ;
      if(context.getUIApplication() != uiApp) context.setUIApplication(uiApp) ;
      
      app.processDecode(uiApp, context) ;
      
      if(!context.isResponseComplete() && !context.getProcessRender()) {
        app.processAction(uiApp, context) ;
      }
      
      if(!context.isResponseComplete()) uiApp.processRender(context) ;
      
      uiApp.setLastAccessApplication(System.currentTimeMillis()) ;
    } catch(Exception ex){
      /*PortalContainer container  = PortalContainer.getInstance() ;
      LogService logService = (LogService)container.getComponentInstanceOfType(LogService.class);
      logService.getLog(PortalRequestHandler.class).error(ex);*/
      //TODO: Need to use the log service
      ex.printStackTrace() ;
    } finally {
      try {
        for(ApplicationLifecycle lifecycle :  lifecycles) lifecycle.onEndRequest(app, context) ;
      } catch (Exception exception){
        //TODO: Need to use the log service
        exception.printStackTrace() ;
      }
      WebuiRequestContext.setCurrentInstance(null) ;
    }
  }
}