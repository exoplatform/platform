/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.framework.portlet.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.javascript.JavaScriptEngine;
import org.exoplatform.resolver.ApplicationResourceResolver;
import org.mozilla.javascript.Script;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 24, 2007
 */
abstract public class EventHandler {
  protected PortletResponse response_ ;
  protected PortletRequest  request_ ;
  protected JavaScriptEngine javascriptEngine_ ;
  protected ApplicationResourceResolver resourceResolver_ ;
  
  protected Map<String, Object> context_ = new HashMap<String, Object>() ;
  protected String useTemplate_ ;
  protected Throwable error_ ;
  
  public void setPortletRequest(PortletRequest req) { request_ =  req ; }
  public void setPortletResponse(PortletResponse res) { response_  = res ; }
  public void setJavaScriptEngine(JavaScriptEngine engine) { javascriptEngine_ = engine ; }
  public void setApplicationResourceResolver(ApplicationResourceResolver resolver) {
    resourceResolver_ = resolver ;
  }
  
  public void setUseTemplate(String template) { useTemplate_ =  template ; }
  public void setError(Throwable error) { error_ =  error ; }
  
  
  public <T extends EventHandler> PortletURL createEventURL(Class<T> handler) {
    PortletURL url = response_.createActionURL() ;
    url.setParameter(MVCPortlet.EVENT_HANDLER, handler.getName()) ;
    return url ;
  }
  
  public <T>  T getService(Class<T> type) throws Exception {
    return (T) PortalContainer.getInstance().getComponentInstanceOfType(type) ;
  }
  
  public void onAction() throws Exception { 
    throw new Exception("You need to override this method in class " + getClass().getName()) ;
  } 
  
  public void onRender() throws Exception {
    Map<String, Object> context = this.context_ ;
    this.context_ = null ;
    
    context.put("request", request_) ;
    context.put("response", response_) ;
    context.put("eventHandler", this) ;
    
    Script template = javascriptEngine_.loadTemplate(resourceResolver_, useTemplate_,  true) ;
    RenderResponse renderResponse = (RenderResponse) response_ ;
    javascriptEngine_.merge(template, context, renderResponse.getWriter()) ;
  }
} 