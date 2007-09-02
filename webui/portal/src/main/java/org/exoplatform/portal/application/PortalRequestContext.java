package org.exoplatform.portal.application;

import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.web.application.JavascriptManager;
import org.exoplatform.web.application.URLBuilder;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.lifecycle.HtmlValidator;

/**
 * This class extends the abstract WebuiRequestContext which itself extends the RequestContext one
 * 
 * It mainly implements the abstract methods and overide some.
 */
public class PortalRequestContext extends WebuiRequestContext {
  
  protected static Log log = ExoLogger.getLogger("portal:PortalRequestContext");  
  
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
    int colonIndex = pathInfo.indexOf("/", 1)  ;
    if(colonIndex < 0) colonIndex = pathInfo.length();
    portalOwner_ =  pathInfo.substring(1, colonIndex) ;
    nodePath_ = pathInfo.substring(colonIndex , pathInfo.length()) ;
    
    if(nodeURI_.indexOf("/public/") >= 0) accessPath =  PUBLIC_ACCESS ;
    else if(nodeURI_.indexOf("/private/") >= 0) accessPath =  PRIVATE_ACCESS ;
    
    //TODO use the encoding from the locale-config.xml file
    response_.setContentType("text/html; charset=UTF-8");
    try {
      request_.setCharacterEncoding("UTF-8");
    }catch (UnsupportedEncodingException e) {
      log.error("Encoding not supported", e);
    }
    
    urlBuilder = new PortalURLBuilder(nodeURI_);
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
  
  final public Writer getWriter() throws Exception { 
	if(writer_ == null) {
	    writer_ = new HtmlValidator(response_.getWriter()) ;
	}
	return writer_ ; 
  }
  
  final public  boolean useAjax() {  return ajaxRequest_; }
  
  @SuppressWarnings("unchecked")
  final public HttpServletRequest getRequest(){ return request_; }
  
  @SuppressWarnings("unchecked")
  final public HttpServletResponse getResponse(){ return response_; }
  
  final public boolean getFullRender() { return forceFullUpdate; }

  final public void setFullRender(boolean forceFullUpdate) { this.forceFullUpdate = forceFullUpdate; }

}