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