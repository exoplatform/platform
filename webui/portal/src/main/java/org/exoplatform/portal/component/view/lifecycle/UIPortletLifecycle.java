/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.lifecycle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.exoplatform.commons.utils.ExceptionUtil;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.RenderInput;
import org.exoplatform.services.portletcontainer.pci.RenderOutput;
import org.exoplatform.templates.groovy.ResourceResolver;
import org.exoplatform.webui.application.Application;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.lifecycle.Lifecycle;
import org.exoplatform.webui.component.lifecycle.WebuiBindingContext;
import org.exoplatform.webui.event.Event;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 8, 2006
 */
public class UIPortletLifecycle extends Lifecycle {
  
  public void processAction(UIComponent uicomponent, RequestContext context) throws Exception {
    String action =  context.getRequestParameter(PortalRequestContext.UI_COMPONENT_ACTION) ;
    if(action != null) {
      Event event = uicomponent.createEvent(action, Event.Phase.PROCESS, context) ;
      if(event != null) event.broadcast()  ;
      return ;
    }
    
    boolean addUpdateComponent = false ;
    String portletMode = context.getRequestParameter("portal:portletMode") ;
    if(portletMode != null) {
      Event event = uicomponent.createEvent("ChangePortletMode", Event.Phase.PROCESS,context) ;
      if(event != null) event.broadcast()  ;
      addUpdateComponent = true ;
    }
    String windowState = context.getRequestParameter("portal:windowState");
    if(windowState != null) {
      Event event = uicomponent.createEvent("ChangeWindowState", Event.Phase.PROCESS,context) ;
      if(event != null) event.broadcast()  ;
      addUpdateComponent = true ;
    }
    
    String portletActionType = context.getRequestParameter("portal:type");
    if (portletActionType != null ) {
      if (portletActionType.equals("action")) {
        Event event = uicomponent.createEvent("ProcessAction", Event.Phase.PROCESS,context) ;
        if(event != null)  event.broadcast()  ;
      } else {
        Event event = uicomponent.createEvent("Render", Event.Phase.PROCESS,context) ;
        if(event != null) event.broadcast()  ;
      }
      addUpdateComponent = true ;
    }  
    if(addUpdateComponent) context.addUIComponentToUpdateByAjax(uicomponent) ;
  }
  
  public void processRender(UIComponent uicomponent , RequestContext context) throws Exception {    
    UIPortlet  uiPortlet =  (UIPortlet)  uicomponent ;
    PortalRequestContext prcontext = (PortalRequestContext) context ;
    ExoContainer container = context.getApplication().getApplicationServiceContainer() ;
    UIPortal uiPortal = Util.getUIPortal();
    PortletContainerService portletContainer = 
      (PortletContainerService) container.getComponentInstanceOfType(PortletContainerService.class); 
    OrganizationService service = uicomponent.getApplicationComponent(OrganizationService.class);
    UserProfile userProfile = service.getUserProfileHandler().findUserProfileByName(uiPortal.getOwner()) ;
    RenderInput input = new RenderInput(); 
    String baseUrl = new StringBuilder(prcontext.getNodeURI()).
                         append("?" + PortalRequestContext.UI_COMPONENT_ID).append("=").
                         append(uiPortlet.getId()).toString()  ;
    input.setBaseURL(baseUrl);
    if(userProfile != null) input.setUserAttributes(userProfile.getUserInfoMap()) ;
    else input.setUserAttributes(new HashMap());
    input.setPortletMode(uiPortlet.getCurrentPortletMode());
    input.setWindowState(uiPortlet.getCurrentWindowState());
    input.setMarkup("text/html");
    input.setTitle(uiPortlet.getTitle());
    input.setWindowID(uiPortlet.getExoWindowID());
    input.setRenderParameters(getRenderParameterMap(uiPortlet, prcontext)) ;
    RenderOutput output = null;
    StringBuilder portletContent = new StringBuilder("EXO-ERROR: Portlet container throw an exception\n") ;
    String  portletTitle = null ;
    try {        
      output = portletContainer.render(prcontext.getRequest(),  prcontext.getResponse(), input);
      if(output.getContent() == null) {
        portletContent.append(uiPortlet.getId()).append(" has error");
      } else {
        portletContent.setLength(0);
        portletContent.append(output.getContent()) ;
      }
    } catch (Throwable ex) {
      ex = ExceptionUtil.getRootCause(ex) ;
      portletContent.append(ExceptionUtil.getStackTrace(ex, 100));      
      //TODO  Need to  find a way to use the log service
    }
    if(output != null ) portletTitle = output.getTitle() ;
    if(portletTitle == null ) portletTitle = "Portlet" ;
    
    if(context.isAjaxRequest() && !uiPortlet.isShowEditControl() && !prcontext.isForceFullUpdate()) {
      context.getWriter().write(portletContent.toString()) ;
    } else {
      Application app = context.getApplication() ;
      ResourceResolver resolver =  app.getResourceResolver() ;
      WebuiBindingContext bcontext = 
        new WebuiBindingContext(resolver, context.getWriter(), uicomponent, context) ;    
      bcontext.put("uicomponent", uicomponent) ;
      bcontext.put("portletContent", portletContent) ;
      bcontext.put("portletTitle", portletTitle) ;
      try { 
        renderTemplate(uicomponent.getTemplate(), bcontext) ;
      } catch (Throwable ex) {
        ex = ExceptionUtil.getRootCause(ex) ;
        portletContent.append(ExceptionUtil.getStackTrace(ex, 100));      
        ex.printStackTrace();
        //TODO  Need to  find a way to use the log service
      }
    }
    try { 
      prcontext.getResponse().flushBuffer() ;
    } catch (Throwable ex) {
      ex = ExceptionUtil.getRootCause(ex) ;
      portletContent.append(ExceptionUtil.getStackTrace(ex, 100));
      ex.printStackTrace();
      //TODO  Need to  find a way to use the log service
    }
  }
  
  @SuppressWarnings({ "unchecked" })
  private Map getRenderParameterMap(UIPortlet uiPortlet, PortalRequestContext prcontext) {
    Map temp = uiPortlet.getRenderParametersMap() ;
    if(temp != null)  return temp ;
    temp = new HashMap(10) ;
    Map map = prcontext.getRequest().getParameterMap() ;
    Iterator keys = map.keySet().iterator() ;
    while (keys.hasNext()) {
      String key = (String) keys.next() ;
      temp.put(key, map.get(key)) ;
    }
    uiPortlet.setRenderParametersMap(temp) ;
    return temp ;
  }
  
}
