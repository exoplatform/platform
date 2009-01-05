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
package org.exoplatform.webui.application.portlet;

import java.io.Writer;

import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderResponse;
import javax.portlet.StateAwareResponse;

import org.exoplatform.web.application.URLBuilder;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.HtmlValidator;
import org.exoplatform.services.resources.Orientation;
import org.exoplatform.commons.utils.WriterPrinter;

/**
 * The request context of a portlet
 *
 */
public class PortletRequestContext extends WebuiRequestContext {
  /**
   * Portlet Window ID
   */
  private String windowId_;
  /**
   * The request
   */
  private PortletRequest request_; 
  /**
   * The response
   */
  private PortletResponse response_ ;
  private Writer writer_ ;
  private boolean hasProcessAction_ = false ;
  
  public PortletRequestContext(WebuiApplication app, Writer writer, 
      PortletRequest req, PortletResponse res) {
    super(app) ;
    init(writer, req, res) ;
    setSessionId(req.getPortletSession(true).getId()) ;
    
    urlBuilder = new PortletURLBuilder();
  }
  
  public void init(Writer writer, PortletRequest req, PortletResponse res) {
    request_ = req ;
    response_ =  res ;
    writer_ = HtmlValidator.DEBUG_MODE ? new WriterPrinter(new HtmlValidator(writer)) : new WriterPrinter(writer);
    windowId_ = req.getWindowID();
  }
  
  public void  setUIApplication(UIApplication uiApplication) throws Exception { 
    uiApplication_ = uiApplication ;
    appRes_ = getApplication().getResourceBundle(getParentAppRequestContext().getLocale()) ;
  }
  
  final public String getRequestParameter(String name) {
    return request_.getParameter(name);
  }

  final public String[] getRequestParameterValues(String name) {
    return request_.getParameterValues(name);
  }

  public Orientation getOrientation() {
    return parentAppRequestContext_.getOrientation(); 
  }

  public String getRequestContextPath() { return  request_.getContextPath(); }
  
  @SuppressWarnings("unchecked")
  public PortletRequest  getRequest() { return request_ ; }
  
  @SuppressWarnings("unchecked")
  public PortletResponse getResponse() {  return response_ ; }
  
  public String getRemoteUser() { return parentAppRequestContext_.getRemoteUser() ; }
  
  final public boolean isUserInRole(String roleUser){ return request_.isUserInRole(roleUser); }
  
  public  PortletMode getApplicationMode() {
    return request_.getPortletMode() ;
  }
  
  public void setApplicationMode(PortletMode mode) throws PortletModeException {
    if(response_ instanceof StateAwareResponse) {
      StateAwareResponse res = (StateAwareResponse)response_ ;
      res.setPortletMode(mode) ;
    } else {
      throw new PortletModeException("The portlet don't support to set a portlet mode by current runtime environment", mode) ;
    }
  }
  
  public Writer getWriter() throws Exception {  return writer_ ; }

  final public boolean useAjax() { return getParentAppRequestContext().useAjax(); } 
  
  public void sendRedirect(String url) throws Exception {
  	setResponseComplete(true);
  	if(response_ instanceof ActionResponse) ((ActionResponse) response_).sendRedirect(url) ;
  }
  
  public  boolean hasProcessAction() { return hasProcessAction_ ;}
  
  public  void    setProcessAction(boolean b) { hasProcessAction_ = b ; }
  
  public URLBuilder getURLBuilder() {
    RenderResponse renderRes = (RenderResponse)  response_ ;
    urlBuilder.setBaseURL(renderRes.createActionURL().toString());
    return urlBuilder ;
  }
  
  /**
   * Puts the component to update inside the parent request context
   * 
   * Here it will be the PortalRequestHandler and hence it will be responsible of
   * making the distinction between 3rd parties portlets (that need a full portlet fragment refresh)
   *  and our portlets that also allow some UIComponent within the portlet to be refreshed
   */
//  public void addUIComponentToUpdateByAjax(UIComponent uicomponent) {   
//	((WebuiRequestContext)getParentAppRequestContext()).addUIComponentToUpdateByAjax(uicomponent);
//  }  
//  
//  public List<UIComponent>  getUIComponentToUpdateByAjax() {  
//	return ((WebuiRequestContext)getParentAppRequestContext()).getUIComponentToUpdateByAjax() ;
//  }
  
  public String getWindowId() {
    return windowId_;
  }
}
