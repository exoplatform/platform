/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application.portlet;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.exoplatform.services.log.LogUtil;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIPortletApplication;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 8, 2006
 */
public class PortletApplicationController extends GenericPortlet {
  
  private PortletApplication application ;
  
  public void init(PortletConfig config) throws PortletException {
    try {    
      application = new PortletApplication(config) ;
      application.init() ;
    } catch(Exception exp){
      LogUtil.getLog(getClass()).error("Error: ", exp);
    }
  }
  
  public void processAction(ActionRequest req, ActionResponse res) throws PortletException, IOException {
    WebuiRequestContext parentAppRequestContext =  WebuiRequestContext.getCurrentInstance() ;
    PortletRequestContext context = createRequestContext(req, res, parentAppRequestContext)  ;
    WebuiRequestContext.setCurrentInstance(context) ;
    List<ApplicationLifecycle> lifecycles = application.getApplicationLifecycle();
    try {
      for(ApplicationLifecycle lifecycle :  lifecycles)  {
        lifecycle.beginExecution(application, context) ;
      } 
      UIApplication uiApp = application.getStateManager().restoreUIRootComponent(context) ;
      context.setUIApplication(uiApp) ;
      application.processDecode(uiApp, context) ;
      if(!context.isResponseComplete() && !context.getProcessRender()) {
        application.processAction(uiApp, context) ;
      }
    } catch(Exception exp){
      LogUtil.getLog(getClass()).error("Error: ", exp);
    } finally {
      context.setProcessAction(true) ;
      WebuiRequestContext.setCurrentInstance(parentAppRequestContext) ;
    }
  }
  
  public  void render(RenderRequest req,  RenderResponse res) throws PortletException, IOException {    
    WebuiRequestContext parentAppRequestContext =  WebuiRequestContext.getCurrentInstance() ;
    PortletRequestContext context = createRequestContext(req, res, parentAppRequestContext)  ;
    WebuiRequestContext.setCurrentInstance(context) ;
    List<ApplicationLifecycle> lifecycles = application.getApplicationLifecycle();
    try {
      if(context.hasProcessAction()) {
        for(ApplicationLifecycle lifecycle :  lifecycles)  {
          lifecycle.beginExecution(application, context) ;
        }
      }      
      UIApplication uiApp =  application.getStateManager().restoreUIRootComponent(context) ;
      context.setUIApplication(uiApp) ;
      if(!context.isResponseComplete()) {
        UIPortletApplication uiPortletApp = (UIPortletApplication)uiApp;
        uiPortletApp.processRender(application, context) ;
      }
      uiApp.setLastAccessApplication(System.currentTimeMillis()) ;
    } catch(Exception exp){
      LogUtil.getLog(getClass()).error("Error: ", exp);
    } finally {
      try {
        for(ApplicationLifecycle lifecycle :  lifecycles) {
          lifecycle.endExecution(application, context) ;
        }
      } catch (Exception exception){
        LogUtil.getLog(getClass()).error("Error: ", exception);
      }
      WebuiRequestContext.setCurrentInstance(parentAppRequestContext) ;
    }
  }
  
  public void destroy() {
    try {
      application.destroy() ;
    } catch(Exception ex) {
      LogUtil.getLog(getClass()).error("Error: ", ex);
    }
  }
  
  public PortletRequestContext createRequestContext(PortletRequest req, PortletResponse res,
                                                    WebuiRequestContext parentAppRequestContext) throws IOException {
    PortletRequestContext context = 
      (PortletRequestContext) parentAppRequestContext.getAttribute(application.getApplicationId()) ;
    Writer w  = null ;    
    if(res instanceof  RenderResponse){
      RenderResponse renderRes = (RenderResponse)res;
      renderRes.setContentType("text/html");
      w = renderRes.getWriter() ; 
    }
    if(context != null) {
      context.init(w, req, res) ;
    } else {
      context =  new PortletRequestContext(application, w, req, res) ;
      parentAppRequestContext.setAttribute(application.getApplicationId(), context) ;
    }
    context.setParentAppRequestContext(parentAppRequestContext) ;
    return context;
  }
  
}