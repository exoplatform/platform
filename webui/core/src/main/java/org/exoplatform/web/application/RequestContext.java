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
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
abstract public class RequestContext {
  
  final static public String ACTION   = "op"; 
  private  static ThreadLocal<RequestContext> tlocal_ = new ThreadLocal<RequestContext>()  ;
  
  private Application app_ ;
  protected RequestContext parentAppRequestContext_ ;
  private Map<String, Object> attributes ;
  private Throwable executionError_ ;
  
  public RequestContext(Application app) {
    app_ =  app ;
  }
  
  public Application getApplication() { return  app_ ; }
  
  abstract public Locale getLocale()  ;
  public ResourceBundle getApplicationResourceBundle() { return null; }
  public ResourceBundle getOwnerResourceBundle() { return null; }
  
  abstract  public String getRequestParameter(String name)  ;
  abstract  public String[] getRequestParameterValues(String name)  ;
  
  abstract public void addJavascript(CharSequence s) ;
  abstract public void importJavascript(CharSequence s) ;
  abstract public void importJavascript(String s, String location) ;
  abstract public void addOnLoadJavascript(CharSequence s) ;
  abstract public void addOnResizeJavascript(CharSequence s) ;
  abstract public void addOnScrollJavascript(CharSequence s); 
  
  public  JavascriptManager getJavascriptManager() { 
    return getParentAppRequestContext().getJavascriptManager() ;
  }
 
  abstract public  boolean isAjaxRequest() ;
  abstract public String getBaseURL() ;
  abstract public boolean isLogon();
  abstract public String getRemoteUser()  ;
  
  abstract public boolean isUserInRole(String roleUser);
 
  
  public  boolean useAjax() {  return true ; }
  public boolean isForceFullUpdate() { return true; }
  
  public ApplicationSession getApplicationSession()  {
    throw  new RuntimeException("This method is not supported");
  }
  
  abstract  public Writer getWriter() throws Exception ;
  
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
  
  public int getApplicationMode() { throw new RuntimeException("Method is not supported") ; }
  
  @SuppressWarnings("unused")
  public void setApplicationMode(int mode) { throw new RuntimeException("Method is not supported") ; }
  
  public Throwable  getExecutionError()  { return executionError_ ; }
  
  @SuppressWarnings("unchecked")
  public static <T extends RequestContext> T getCurrentInstance()  { return (T)tlocal_.get() ; }
  public static void setCurrentInstance(RequestContext ctx) { tlocal_.set(ctx) ; }
  
}