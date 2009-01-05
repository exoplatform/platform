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
package org.exoplatform.portal.application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.exoplatform.Constants;
import org.exoplatform.commons.utils.WriterPrinter;
import org.exoplatform.commons.utils.PortalPrinter;
import org.exoplatform.commons.utils.CharsetTextEncoder;
import org.exoplatform.commons.utils.TextEncoder;
import org.exoplatform.commons.utils.CharsetCharEncoder;
import org.exoplatform.commons.utils.TableCharEncoder;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.resources.Orientation;
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
  final static public String CACHE_LEVEL = "portal:cacheLevel" ;  
  final static public String REQUEST_TITLE = "portal:requestTitle".intern();
  final static public String REQUEST_METADATA = "portal:requestMetadata".intern();  

  private String portalOwner_ ;
  private String nodePath_ ;
  private String requestURI_ ;
  private String portalURI ;

  private int accessPath = -1 ;  

  private HttpServletRequest request_ ;
  private HttpServletResponse response_ ;

  private String cacheLevel_ = "cacheLevelPortlet";
  private boolean  ajaxRequest_ = true ;
  private boolean  forceFullUpdate = false;
  private Writer writer_ ;
  protected JavascriptManager jsmanager_ = new  JavascriptManager() ;

  public  JavascriptManager getJavascriptManager() { return jsmanager_ ; }

  public PortalRequestContext(WebuiApplication app, HttpServletRequest req, HttpServletResponse res) throws Exception {
    super(app);
    request_ = req ;
    response_ =  res ;
    response_.setBufferSize(1024 * 100) ;
    setSessionId(req.getSession().getId()) ;
    ajaxRequest_ = "true".equals(req.getParameter("ajaxRequest")) ;
    String cache = req.getParameter(CACHE_LEVEL);
    if(cache != null) cacheLevel_ = cache;

    requestURI_ = URLDecoder.decode(req.getRequestURI(), "UTF-8");
    String pathInfo = req.getPathInfo() ;
    if(pathInfo == null) pathInfo = "/" ;
    int colonIndex = pathInfo.indexOf("/", 1)  ;
    if(colonIndex < 0) colonIndex = pathInfo.length();
    portalOwner_ =  pathInfo.substring(1, colonIndex) ;
    nodePath_ = pathInfo.substring(colonIndex , pathInfo.length()) ;

    portalURI = requestURI_.substring(0, requestURI_.lastIndexOf(nodePath_)) + "/";

    if(requestURI_.indexOf("/public/") >= 0) accessPath =  PUBLIC_ACCESS ;
    else if(requestURI_.indexOf("/private/") >= 0) accessPath =  PRIVATE_ACCESS ;

    //TODO use the encoding from the locale-config.xml file
    response_.setContentType("text/html; charset=UTF-8");
    try {
      request_.setCharacterEncoding("UTF-8");
    }catch (UnsupportedEncodingException e) {
      log.error("Encoding not supported", e);
    }

    urlBuilder = new PortalURLBuilder(requestURI_);
  }

  public void refreshResourceBundle() throws Exception {
    appRes_ = getApplication().getResourceBundle(getLocale()) ;
  }

  public String getTitle() {
    String title = (String)request_.getAttribute(REQUEST_TITLE);
    if(title != null) return title;
    UIPortal uiportal = Util.getUIPortal();
    return uiportal.getSelectedNode().getResolvedLabel();    
  }

  public Orientation getOrientation() {
    return ((UIPortalApplication)uiApplication_).getOrientation();
  }

  public Locale getLocale() {  return ((UIPortalApplication)uiApplication_).getLocale();}
  
  @SuppressWarnings("unchecked")
  public Map<String,String> getMetaInformation() {
    return (Map<String,String>)request_.getAttribute(REQUEST_METADATA);    
  }

  public String getCacheLevel() { return cacheLevel_ ; }

  public String getRequestParameter(String name) { return request_.getParameter(name) ; }

  public String[] getRequestParameterValues(String name)  {
    return request_.getParameterValues(name) ;
  }  

  public Map<String, String[]> getPortletParameters() {
    Map<String, String[]> unsortedParams = getRequest().getParameterMap();
    Map<String, String[]> sortedParams = new HashMap<String, String[]>(); 
    Set<String> keys = unsortedParams.keySet();
    for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
      String key = iter.next();
      if(!key.startsWith(Constants.PARAMETER_ENCODER)) {
        sortedParams.put(key, unsortedParams.get(key));
      }
    }    
    return sortedParams;
  }

  final public String getRequestContextPath() { return  request_.getContextPath(); }

  public  String getActionParameterName() {  return PortalRequestContext.UI_COMPONENT_ACTION ; }

  public  String getUIComponentIdParameterName() { return PortalRequestContext.UI_COMPONENT_ID; }

  public String getPortalOwner() { return portalOwner_ ; }

  public String getNodePath() { return nodePath_  ; }

  public String getRequestURI()  { return requestURI_ ; }

  public String getPortalURI() { return portalURI ; }

  public URLBuilder getURLBuilder() { return urlBuilder; }

  public int  getAccessPath() { return accessPath ;}

  final public String getRemoteUser() { return request_.getRemoteUser() ; }
  final public boolean isUserInRole(String roleUser){ return request_.isUserInRole(roleUser); }

  /** The optimized encoder. */
  private static final TextEncoder encoder = new CharsetTextEncoder(new TableCharEncoder(CharsetCharEncoder.getUTF8()));

  final public Writer getWriter() throws Exception { 
    if(writer_ == null) {

      //
      PortalPrinter printer = new PortalPrinter(encoder, response_.getOutputStream());

      //
      if (HtmlValidator.DEBUG_MODE) {
        writer_ = new WriterPrinter(new HtmlValidator(printer)) ;
      } else {
        writer_ = printer;
      }
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

  final public void sendRedirect(String url) throws IOException {
  	setResponseComplete(true);
  	response_.sendRedirect(url);
  }

  public void setHeaders(Map<String, String> headers) {
    Set<String> keys = headers.keySet();
    for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
      String key = iter.next();
      response_.setHeader(key, headers.get(key));
    }
  }


}
