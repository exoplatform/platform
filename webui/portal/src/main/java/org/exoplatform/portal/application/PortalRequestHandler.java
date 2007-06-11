/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.WebAppController;
import org.exoplatform.web.WebRequestHandler;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.event.Event.Phase;

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
    Map<String, String> parameters = new HashMap<String, String>();
    
    List<ApplicationLifecycle> lifecycles = app.getApplicationLifecycle();
    try {
      for(ApplicationLifecycle lifecycle :  lifecycles) {
        lifecycle.onStartRequest(app, context) ;
      }
      String url = req.getRequestURI().toString();
     
      UIApplication uiApp = app.getStateManager().restoreUIRootComponent(context) ;
      String requetsParams  = "";
      if(url.split("/").length > 4) {requetsParams =  url.split("/")[4]; }
      
      if(requetsParams != null && requetsParams.length() > 0) { 
        String[] params = requetsParams.split("&");
        for(String param: params){
          String[] temp = param.split("=");
          if(temp.length < 1) continue;
          System.out.println(temp[0] + "-" + temp[1]);
          parameters.put(temp[0], temp[1]);
        }
      }
      if(parameters.containsKey("portal:componentId") && parameters.containsKey("portal:action")){
        UIComponent uicomponent = uiApp.findComponentById(parameters.get("portal:componentId"));
        if(uicomponent != null) {
          Phase phase = Phase.PROCESS;
          if(parameters.containsKey("portal:phase")){
            String phaseStr = parameters.get("portal:phase").toLowerCase();
            if (phaseStr.equals("decode")) phase = Phase.DECODE;
            else if(phaseStr.equals("render")) phase = Phase.RENDER;
            else if(phaseStr.equals("init")) phase = Phase.INIT;
            else if(phaseStr.equals("destroy")) phase = Phase.DESTROY;
            else if(phaseStr.equals("restore")) phase = Phase.RESTORE;
          }
          System.out.println("\n\n\n-------------------Action: " + parameters.get("portal:action"));
          uicomponent.createEvent(parameters.get("portal:action"), phase, context );
//          uicomponent.processAction(context);
        }
      }
      
      if(context.getUIApplication() != uiApp){ context.setUIApplication(uiApp); }
      
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