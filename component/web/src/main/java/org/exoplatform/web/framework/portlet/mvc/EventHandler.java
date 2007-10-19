/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.framework.portlet.mvc;

import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.javascript.TemplateContext;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 24, 2007
 */
abstract public class EventHandler {
  protected PortletResponse response_ ;
  protected PortletRequest  request_ ;
  
  protected TemplateContext templateContext_ ; 
  protected String useTemplate_ ;
  protected Throwable error_ ;
  
  public void setPortletRequest(PortletRequest req) { request_ =  req ; }
  public void setPortletResponse(PortletResponse res) { response_  = res ; }
  
  public void setTemplateContext(TemplateContext context) { templateContext_  = context ; }
  
  public void setUseTemplate(String template) { useTemplate_ =  template ; }
  public void setError(Throwable error) { error_ =  error ; }
  
  public <T extends EventHandler> PortletURL createEventURL(String handler) {
    PortletURL url = ((MimeResponse) response_).createActionURL() ;
    url.setParameter(MVCPortlet.EVENT_HANDLER, handler) ;
    return url ;
  }
  
  public <T>  T getService(Class<T> type) throws Exception {
    return (T) PortalContainer.getInstance().getComponentInstanceOfType(type) ;
  }
  
  public void onAction() throws Exception { 
    throw new Exception("You need to override this method in class " + getClass().getName()) ;
  } 
  
  public void onRender() throws Exception {
    templateContext_.setVariable("request", request_) ;
    templateContext_.setVariable("response", response_) ;
    templateContext_.setVariable("EventHandler", this) ;
    templateContext_.setWriter(((RenderResponse) response_).getWriter());
    templateContext_.render(useTemplate_) ;
  }
} 