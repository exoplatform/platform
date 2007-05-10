package org.exoplatform.portal.application;

import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.application.JavascriptManager;
import org.exoplatform.web.application.URLBuilder;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIApplication;

public class PortalRequestContext extends WebuiRequestContext {
   
  final  static public int PUBLIC_ACCESS  =   0 ;
  final  static public int PRIVATE_ACCESS =   1 ;
  
  final static public String UI_COMPONENT_ACTION = "portal:action" ;
  final static public String UI_COMPONENT_ID = "portal:componentId" ;
  
  private String portalOwner_ ;
  private String nodePath_ ;
  private String nodeURI_ ;
  
  private int accessPath = -1 ;  
  
  private HttpServletRequest request_ ;
  private HttpServletResponse response_ ;
  private boolean  ajaxRequest_ = true ;
  private boolean  forceFullUpdate = false;
  private Writer writer_ ;
  protected JavascriptManager jsmanager_ = new  JavascriptManager() ;
  
  public  JavascriptManager getJavascriptManager() { return jsmanager_ ; }
  
  public PortalRequestContext(WebuiApplication app, HttpServletRequest req, HttpServletResponse res) throws Exception {
    super(app);
    request_ = req ;
    response_ =  res ;
    setSessionId(req.getSession().getId()) ;
    ajaxRequest_ = "true".equals(req.getParameter("ajaxRequest")) ;
    nodeURI_ = req.getRequestURI() ;
    
    String pathInfo = req.getPathInfo() ;
    int colonIndex = pathInfo.indexOf(':') ;
    portalOwner_ =  pathInfo.substring(1, colonIndex) ;
    nodePath_ = pathInfo.substring(colonIndex + 1, pathInfo.length()) ;
    if(nodeURI_.indexOf("/public/") >= 0) accessPath =  PUBLIC_ACCESS ;
    else if(nodeURI_.indexOf("/private/") >= 0) accessPath =  PRIVATE_ACCESS ;
    
    response_.setContentType("text/html; charset=UTF-8");
    try {
      request_.setCharacterEncoding("UTF-8");
    }catch (UnsupportedEncodingException e) {
      System.err.println(e.toString());
    }
    
    writer_ = new HtmlValidator(res.getWriter()) ;
    urlBuilder = new PortalURLBuilder(nodeURI_);
  }
  
  public void  setUIApplication(UIApplication uiApplication) throws Exception { 
    super.setUIApplication(uiApplication) ;    
  }
  
  public String getRequestParameter(String name) { return request_.getParameter(name) ; }
  
  public String[] getRequestParameterValues(String name)  {
    return request_.getParameterValues(name) ;
  }  
  
  final public String getRequestContextPath() { return  request_.getContextPath(); }
  
  public  String getActionParameterName() {  return PortalRequestContext.UI_COMPONENT_ACTION ; }
  
  public  String getUIComponentIdParameterName() { return PortalRequestContext.UI_COMPONENT_ID; }
  
  public String getPortalOwner() { return portalOwner_ ; }
  
  public String getNodePath() { return nodePath_  ; }
  
  public String getNodeURI()  { return nodeURI_ ; }
  
  public URLBuilder getURLBuilder() { return urlBuilder; }
  
  public int  getAccessPath() { return accessPath ;}
  
  final public String getRemoteUser() { return request_.getRemoteUser() ; }
  final public boolean isUserInRole(String roleUser){ return request_.isUserInRole(roleUser); }
  
  final public Writer getWriter() throws Exception { return writer_ ; }
  
  final public  boolean useAjax() {  return ajaxRequest_; }
  
  @SuppressWarnings("unchecked")
  final public HttpServletRequest getRequest(){ return request_; }
  
  @SuppressWarnings("unchecked")
  final public HttpServletResponse getResponse(){ return response_; }
  
  final public boolean getFullRender() { return forceFullUpdate; }

  final public void setFullRender(boolean forceFullUpdate) { this.forceFullUpdate = forceFullUpdate; }
  
}