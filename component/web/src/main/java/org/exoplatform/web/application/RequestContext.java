/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application;

import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
/**
 * Created by The eXo Platform SAS
 * May 7, 2006
 * 
 * This abstract class is a wrapper on top of the request information such as the Locale in use,
 * the application (for instance PortalApplication, PortletApplication...), an access to the JavascriptManager
 * as well as a reference to the URLBuilder in use.
 * 
 * It also contains a ThreadLocal object for an easy access.
 * 
 *  Context can be nested and hence a getParentAppRequestContext() is also available
 * 
 */
abstract public class RequestContext {
  
  final static public String ACTION   = "op"; 
  private  static ThreadLocal<RequestContext> tlocal_ = new ThreadLocal<RequestContext>()  ;
  
  private Application app_ ;
  protected RequestContext parentAppRequestContext_ ;
  private Map<String, Object> attributes ;
  
  protected URLBuilder urlBuilder;
  
  public RequestContext(Application app) {
    app_ =  app ;
  }
  
  public Application getApplication() { return  app_ ; }
  
  public Locale getLocale() { return parentAppRequestContext_.getLocale() ; }
  
  public ResourceBundle getApplicationResourceBundle() { return null; }
  
  abstract  public String getRequestParameter(String name)  ;
  abstract  public String[] getRequestParameterValues(String name)  ;
  
  public  JavascriptManager getJavascriptManager() { 
    return getParentAppRequestContext().getJavascriptManager() ;
  }
  
  abstract public URLBuilder getURLBuilder() ;
  
  public String getRemoteUser() { return parentAppRequestContext_.getRemoteUser() ; }
  public boolean isUserInRole(String roleUser) { return parentAppRequestContext_.isUserInRole(roleUser) ; }
  
  
  abstract public  boolean useAjax() ;
  public boolean getFullRender() { return true; }
  
  public ApplicationSession getApplicationSession()  {
    throw  new RuntimeException("This method is not supported");
  }
  
  public Writer getWriter() throws Exception { return parentAppRequestContext_.getWriter() ; }
  
  final public Object  getAttribute(String name) { 
    if(attributes == null) return null ;
    return attributes.get(name) ; 
  }
  
  final public void setAttribute(String name, Object value) {
    if(attributes == null) attributes = new HashMap<String, Object>() ;
    attributes.put(name, value) ; 
  }
  
  final public Object  getAttribute(Class type) { return getAttribute(type.getName()) ; }
  final public void    setAttribute(Class type, Object value) { setAttribute(type.getName(), value) ; }
 
  public RequestContext getParentAppRequestContext() { return parentAppRequestContext_ ; }
  public void setParentAppRequestContext(RequestContext context) { parentAppRequestContext_ = context ; }
  
  @SuppressWarnings("unchecked")
  public static <T extends RequestContext> T getCurrentInstance()  { return (T)tlocal_.get() ; }
  public static void setCurrentInstance(RequestContext ctx) { tlocal_.set(ctx) ; }

}