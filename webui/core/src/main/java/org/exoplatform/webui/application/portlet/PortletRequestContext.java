package org.exoplatform.webui.application.portlet;

import java.io.Writer;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderResponse;

import org.exoplatform.web.application.Parameter;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.config.Event;

public class PortletRequestContext extends WebuiRequestContext {
  static public int VIEW_MODE =  0 ;
  static public int EDIT_MODE =  1 ;
  static public int HELP_MODE =  2 ;
  static public int CONFIG_MODE = 3 ;
  
  private int applicationMode_ ;
  private PortletRequest request_; 
  private PortletResponse response_ ;
  private Writer writer_ ;
  private boolean hasProcessAction_ = false ;
  private String baseURL_ ;
  
  public PortletRequestContext(WebuiApplication app, Writer writer, 
                               PortletRequest req, PortletResponse res) {
    super(app) ;
    init(writer, req, res) ;
    setSessionId(req.getPortletSession(true).getId()) ;
    PortletMode mode = req.getPortletMode() ;
    if(mode.equals(PortletMode.VIEW))  applicationMode_ = VIEW_MODE ;
    else if(mode.equals(PortletMode.EDIT))  applicationMode_ = EDIT_MODE ;
    else if(mode.equals(PortletMode.HELP))  applicationMode_ = HELP_MODE ;
    else  applicationMode_ = VIEW_MODE ;
  }
  
  public void init(Writer writer,  PortletRequest req, PortletResponse res) {
    request_ = req ;
    response_ =  res ;
    writer_ =  writer ;
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

  public String getRequestContextPath() { return  request_.getContextPath(); }
  
  @SuppressWarnings("unchecked")
  public PortletRequest  getRequest() { return request_ ; }
  
  @SuppressWarnings("unchecked")
  public PortletResponse getResponse() throws Exception {  return response_ ; }
  
  public String getRemoteUser() { return parentAppRequestContext_.getRemoteUser() ; }
  
  final public boolean isUserInRole(String roleUser){ return request_.isUserInRole(roleUser); }
  
//  final public boolean isLogon() { return getParentAppRequestContext().isLogon(); }
  
  public int getApplicationMode() { return applicationMode_ ; }
  
  public void setApplicationMode(int mode) { applicationMode_ = mode; }
  
  public Writer getWriter() throws Exception {  return writer_ ; }

  final public boolean useAjax() { return getParentAppRequestContext().useAjax(); } 
  
  public  boolean hasProcessAction() { return hasProcessAction_ ;}
  
  public  void    setProcessAction(boolean b) { hasProcessAction_ = b ; }
  
  public String getBaseURL() {
    if(baseURL_ != null)  return baseURL_;
    if(writer_ == null) {
      throw new RuntimeException("Cannot create ActionURL or RenderURL in the process action phase") ;
    }
    RenderResponse renderRes = (RenderResponse)  response_ ;
    baseURL_ =  renderRes.createActionURL().toString() ;
    return baseURL_ ;
  }
  
  public StringBuilder createURL(UIComponent uicomponent, Event event, 
                                 boolean supportAjax, String beanId, Parameter ... params) {
    builderURL.setLength(0);
    if(supportAjax) builderURL.append("javascript:ajaxGet('") ;
    builderURL.append(getBaseURL()).append("&amp;")
              .append(UIComponent.UICOMPONENT).append('=').append(uicomponent.getId()) ;
    if(event != null) {
      builderURL.append("&amp;")
                .append(WebuiRequestContext.ACTION).append('=').append(event.getName()) ;
    }
    if(beanId != null){
      builderURL.append("&amp;").append(UIComponent.OBJECTID).append('=').append(beanId) ;
    }
    if(params != null && params.length > 0){
      for(Parameter param : params){
        builderURL.append("&amp;").append(param.getName()).append('=').append(param.getValue()) ;
      }
    }
    if(supportAjax) builderURL.append("&amp;ajaxRequest=true')") ;    
    return builderURL;    
  }
  
}