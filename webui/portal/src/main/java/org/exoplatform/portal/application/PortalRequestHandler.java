/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.WebRequestHandler;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIApplication;

/**
 * Created by The eXo Platform SAS
 * Dec 9, 2006  
 * 
 * This class handle the request that target the portal paths /public and /private
 * 
 */
public class PortalRequestHandler extends WebRequestHandler {
  
  protected static Log log = ExoLogger.getLogger("portal:PortalRequestHandler");  

  static String[]  PATHS = {"/public", "/private"} ;

  public String[] getPath() { return PATHS ; }

  /**
   * This method will handle incoming portal request. It gets a reference to the WebAppController
   * 
   * Here are the steps done in the method:
   * 
   *   1) set the header Cache-Control to no-cache
   *   2) Get the PortalApplication reference from the controller
   *   3) Create a PortalRequestContext object that is a convenient wrapper on all the request information
   *   4) Set that context in a ThreadLocal to easily access it
   *   5) Get the collection of ApplicationLifecycle referenced in the PortalApplication and defined in the 
   *      webui-configuration.xml of the portal application
   *   6) Call onStartRequest() on each ApplicationLifecycle object
   *   7) Get the StateManager object from the PortalApplication (also referenced in the XML file) 
   *   8) Use the StateManager to get a reference on the root UI component: UIApplication; the method used is
   *      restoreUIRootComponent(context)
   *   9) If the UI component is not the current one in used in the PortalContextRequest, then replace it
   *   10) Process decode on the PortalApplication
   *   11) Process Action on the PortalApplication
   *   12) Process Render on the UIApplication UI component        
   *   11) call onEndRequest on all the ApplicationLifecycle 
   *   12) Release the context from the thread
   * 
   */
  @SuppressWarnings("unchecked")
  public void execute(WebAppController controller,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    log.debug("Session ID = " + req.getSession().getId());
    res.setHeader("Cache-Control", "no-cache");
    
    PortalApplication app =  controller.getApplication(PortalApplication.PORTAL_APPLICATION_ID) ;
    WebuiRequestContext context = new  PortalRequestContext(app, req, res) ;  ;
    WebuiRequestContext.setCurrentInstance(context) ;
    List<ApplicationLifecycle> lifecycles = app.getApplicationLifecycle();
    try {
      for(ApplicationLifecycle lifecycle :  lifecycles) lifecycle.onStartRequest(app, context) ;
      UIApplication uiApp = app.getStateManager().restoreUIRootComponent(context) ;
      if(context.getUIApplication() != uiApp) context.setUIApplication(uiApp) ;
      
      if(uiApp != null) app.processDecode(uiApp, context) ;
      
      if(!context.isResponseComplete() && !context.getProcessRender()) {
        app.processAction(uiApp, context) ;
      }
      
      if(!context.isResponseComplete()) uiApp.processRender(context) ;
      
      if(uiApp != null) uiApp.setLastAccessApplication(System.currentTimeMillis()) ;
    } catch(Exception ex){
      log.error("Error while handling request",ex);
    } finally {
      try {
        for(ApplicationLifecycle lifecycle :  lifecycles) lifecycle.onEndRequest(app, context) ;
      } catch (Exception exception){
    	log.error("Error while ending request on all ApplicationLifecycle",exception);
      }
      WebuiRequestContext.setCurrentInstance(null) ;
    }
  }
}