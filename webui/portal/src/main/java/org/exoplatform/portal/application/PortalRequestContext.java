package org.exoplatform.portal.application;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.webui.application.Application;
import org.exoplatform.webui.application.Parameter;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.config.Event;

public class PortalRequestContext extends RequestContext {
  
  final  static public int PUBLIC_ACCESS  =   0 ;
  final  static public int PRIVATE_ACCESS =   1 ;
  final  static public int ADMIN_ACCESS   =   2 ;
  
  private String portalOwner ;
  private String nodePath ;
  private String nodeURI ;
  
  private int accessPath = -1 ;  
  
  final static public String UI_COMPONENT_ACTION = "portal:action" ;
  final static public String UI_COMPONENT_ID = "portal:componentId" ;
  
  private HttpServletRequest request_ ;
  private HttpServletResponse response_ ;
  private HttpSession session_ ;
  private boolean  ajaxRequest_ = true ;
  private boolean  forceFullUpdate = false;
  private boolean  portalRequest_ ;
  
  private StringBuilder javascript = new StringBuilder(1000) ;
  private StringBuilder customizedOnloadJavascript ;
  
  public PortalRequestContext(Application app, HttpServletRequest req, HttpServletResponse res) {
    super(app);
    
    request_ = req ;
    response_ =  res ;
    session_ = req.getSession() ;
    setSessionId(session_.getId()) ;
    ajaxRequest_ = "true".equals(req.getParameter("ajaxRequest")) ;
    portalRequest_ = req.getParameter("portal:type") ==  null ;
    nodeURI = req.getRequestURI() ;
    
    String pathInfo = req.getPathInfo() ;
    int colonIndex = pathInfo.indexOf(':') ;
    portalOwner =  pathInfo.substring(1, colonIndex) ;
    nodePath = pathInfo.substring(colonIndex + 1, pathInfo.length()) ;
    if(nodeURI.indexOf("/public/") >= 0) accessPath =  PUBLIC_ACCESS ;
    else if(nodeURI.indexOf("/private/") >= 0) accessPath =  PRIVATE_ACCESS ;
    else if(nodeURI.indexOf("/admin/") >= 0) accessPath =  ADMIN_ACCESS ;
    
    res.setContentType("text/html; charset=UTF-8");
  }
  
  public void  setUIApplication(UIApplication uiApplication) throws Exception { 
    super.setUIApplication(uiApplication) ;    
  }
  
  public  boolean useAjax() {
    UIPortalApplication uiPortalApp = (UIPortalApplication) getUIApplication();
    return uiPortalApp.useAjax() ;
  }
  
  public String getRequestParameter(String name)  { return request_.getParameter(name) ; }
  
  public String[] getRequestParameterValues(String name)  {
    return request_.getParameterValues(name) ;
  }  
  
  public Object getSessionAttribute(String name) {
    if(session_ == null)  return null ;
    return session_.getAttribute(name) ;
  }
  
  public void setSessionAttribute(String name, Object value) {
    if(session_ == null) session_ = request_.getSession(true) ;
    session_.setAttribute(name,  value) ;
  }
  
  public Object getRequestAttribute(String name) { return request_.getAttribute(name);}
  
  public void setRequestAttribute(String name, Object value) {
    request_.setAttribute(name, value) ;  
  }
  
  final public String getRequestContextPath() { return  request_.getContextPath(); }
  
  public  String getActionParameterName() {  return PortalRequestContext.UI_COMPONENT_ACTION ; }
  
  public  String getUIComponentIdParameterName() { return PortalRequestContext.UI_COMPONENT_ID; }
  
  public String getPortalOwner() { return portalOwner ; }
  
  public String getNodePath() { return nodePath  ; }
  
  public String getNodeURI()  { return nodeURI ; }
  
  public String getBaseURL() { return nodeURI ; } 
  
  public int  getAccessPath() { return accessPath ;}
  
  final public String getRemoteUser() { return request_.getRemoteUser() ; }
  
  final public boolean isUserInRole(String roleUser){ return request_.isUserInRole(roleUser); }
  
  final public boolean isLogon() { return accessPath == PortalRequestContext.PRIVATE_ACCESS; }
  
  final public Writer getWriter() throws Exception { return response_.getWriter() ; }
  
  final public  boolean isAjaxRequest() {  return ajaxRequest_; }
  
  final public  boolean isPortalRequest() {  return portalRequest_; }
  
  final public  boolean isPortletRequest() {  return !portalRequest_; }
  
  @SuppressWarnings("unchecked")
  final public HttpServletRequest getRequest(){ return request_; }
  
  @SuppressWarnings("unchecked")
  final public HttpServletResponse getResponse(){ return response_; }
  
  final public boolean isForceFullUpdate() { return forceFullUpdate; }

  final public void setForceFullUpdate(boolean forceFullUpdate) { this.forceFullUpdate = forceFullUpdate; }
  
  public void addJavascript(CharSequence s) { javascript.append(s).append(" \n") ; }
  
  public void importJavascript(CharSequence s) {
    javascript.append("eXo.require('").append(s).append("'); \n") ;
  }
  
  public void importJavascript(String s, String location) {
    if(!location.endsWith("/")) location =  location + '/' ;
    javascript.append("eXo.require('").append(s).append("', '").append(location).append("'); \n") ;
  }
  
  public void addOnLoadJavascript(CharSequence s) {
    String id = Integer.toString(Math.abs(s.hashCode())) ;
    javascript.
      append("eXo.core.Browser.addOnLoadCallback('mid").append(id).
      append("',").append(s).append("); \n") ;
  }
  
  public void addOnResizeJavascript(CharSequence s) {
    String id = Integer.toString(Math.abs(s.hashCode())) ;
    javascript.
      append("eXo.core.Browser.addOnResizeCallback('mid").append(id).
      append("',").append(s).append("); \n") ;
  }
  
  public void addOnScrollJavascript(CharSequence s) {
    String id = Integer.toString(Math.abs(s.hashCode())) ;
    javascript.
      append("eXo.core.Browser.addOnScrollCallback('mid").append(id).
      append("',").append(s).append("); \n") ;
  }
  
  public String getJavascript() { return javascript.toString() ; }
  
  public void addCustomizedOnLoadScript(CharSequence s) {
    if(customizedOnloadJavascript == null) customizedOnloadJavascript = new StringBuilder() ;
    customizedOnloadJavascript.append(s).append("\n") ;
  }
  
  public String getCustomizedOnLoadScript() { 
    if(customizedOnloadJavascript == null)  return "" ;
    return customizedOnloadJavascript.toString() ; 
  }
  
  public StringBuilder createURL(UIComponent uicomponent, Event event, 
                                 boolean supportAjax, String beanId, Parameter ... params) {
    builderURL.setLength(0);
    if(supportAjax) builderURL.append("javascript:ajaxGet('") ;
    builderURL.
      append(getBaseURL()).append("?").
      append(PortalRequestContext.UI_COMPONENT_ID).append('=').append(uicomponent.getId()) ;
    if(event != null) {
      builderURL.
        append("&amp;").
        append(PortalRequestContext.UI_COMPONENT_ACTION).append('=').append(event.getName()) ;
    }
    
    if(beanId != null) {
      builderURL.append("&amp;").append(UIComponent.OBJECTID).append('=').append(beanId) ;
    }
    
    if(params != null && params.length > 0) {
      for(Parameter param : params) {
        builderURL.append("&amp;").append(param.getName()).append('=').append(param.getValue()) ;
      }
    }
    if(supportAjax) builderURL.append("&amp;ajaxRequest=true')") ;
    return builderURL;    
  }
  
}