/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.web.framework.portlet.mvc;

import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

import org.exoplatform.container.ExoContainerContext;
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
    return (T) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(type) ;
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