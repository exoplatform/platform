/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.framework.portlet.mvc;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.exoplatform.javascript.DefaultJavaScriptEngine;
import org.exoplatform.javascript.TemplateContext;
import org.exoplatform.resolver.ApplicationResourceResolver;
import org.exoplatform.resolver.PortletResourceResolver;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 8, 2006
 */
abstract public class MVCPortlet extends GenericPortlet {
  final public static String EVENT_HANDLER = "EventHandler" ;
  
  private DefaultJavaScriptEngine javascriptEngine_ ;
  private ApplicationResourceResolver resourceResolver_ ;
  
  public void init(PortletConfig config) throws PortletException {
    super.init(config) ;
    javascriptEngine_ = new DefaultJavaScriptEngine() ;
    resourceResolver_ = new ApplicationResourceResolver() ;
    resourceResolver_.addResourceResolver(new PortletResourceResolver(config.getPortletContext(), "app:")) ;
  }
  
  final public void processAction(ActionRequest req, ActionResponse res) throws PortletException, IOException {
    EventHandler handler = null ;
    try {
      Class eventHandlerType = getDefaultEventHandler() ;
      String eventHandlerName = req.getParameter(EVENT_HANDLER) ;
      if(eventHandlerName == null) {
        eventHandlerType = getDefaultEventHandler() ;
      } else {
        eventHandlerType = 
          Thread.currentThread().getContextClassLoader().loadClass(eventHandlerName) ;
      }
      handler = (EventHandler)eventHandlerType.newInstance() ;
      TemplateContext context = new TemplateContext(javascriptEngine_, resourceResolver_, null) ;
      handler.setTemplateContext(context) ;
      handler.setPortletRequest(req) ;
      handler.setPortletResponse(res) ;
      handler.onAction() ;
    } catch(Throwable ex) {
      handler.setError(ex) ;
    } finally {
      req.getPortletSession().setAttribute(EVENT_HANDLER, handler) ;
      
    }
  }
  
  final public  void render(RenderRequest req,  RenderResponse res) throws PortletException, IOException {
    res.setContentType("text/html; charset=UTF-8");
    try { 
      EventHandler handler =  (EventHandler)req.getPortletSession().getAttribute(EVENT_HANDLER) ;
      if(handler == null)  {
        handler = (EventHandler) getDefaultEventHandler().newInstance() ;
        TemplateContext context = new TemplateContext(javascriptEngine_, resourceResolver_, null) ;
        handler.setTemplateContext(context) ;
      }
      handler.setPortletRequest(req) ;
      handler.setPortletResponse(res) ;
      handler.onRender() ;
    } catch(Throwable t) {
      t.printStackTrace() ;
    } finally {
      req.getPortletSession().removeAttribute(EVENT_HANDLER) ;
    }
  } 
  
  abstract protected Class getDefaultEventHandler()  ; 
  
}