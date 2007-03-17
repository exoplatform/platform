/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.exoplatform.templates.groovy.ApplicationResourceResolver;
import org.exoplatform.templates.groovy.ResourceResolver;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.config.Event ;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
abstract public class RequestContext {
  static public int VIEW_MODE =  0 ;
  static public int EDIT_MODE =  1 ;
  static public int HELP_MODE =  2 ;
  static public int CONFIG_MODE = 3 ;
  
  final static public String ACTION   = "op"; 
  
  private  static ThreadLocal<RequestContext> tlocal_ = new ThreadLocal<RequestContext>()  ;
  
  private Application app_ ;
  protected UIApplication  uiApplication_ ;
  protected String sessionId_ ;
  protected ResourceBundle appRes_ ;
  protected RequestContext parentAppRequestContext_ ;
  private StateManager stateManager_ ;
  private boolean  responseComplete_ = false ;
  private boolean  processRender_ =  false ;
  private Map<String, Object> attributes ;
  private Throwable executionError_ ;
  private ArrayList<UIComponent>  uicomponentToUpdateByAjax ;
  protected StringBuilder builderURL = new StringBuilder(300);
  
  public RequestContext(Application app) {
    app_ =  app ;
  }
  
  public String getSessionId() {  return sessionId_  ; }  
  protected void setSessionId(String id) { sessionId_ = id ;}
  
  @SuppressWarnings("unchecked")
  public UIApplication getUIApplication() { return uiApplication_ ; }  
  
  public void  setUIApplication(UIApplication uiApplication) throws Exception { 
    uiApplication_ = uiApplication ;
    appRes_ = app_.getResourceBundle(uiApplication.getLocale()) ;   
  }
  
  public Application getApplication() { return  app_ ; }
  
  public ResourceBundle getApplicationResourceBundle() {  return appRes_ ; }
  
  abstract  public String getRequestParameter(String name)  ;
  
  abstract  public String[] getRequestParameterValues(String name)  ;
  
  abstract  public Object getRequestAttribute(String name)  ;
  
  abstract  public void setRequestAttribute(String name, Object value)  ;
  
  abstract  public Object getSessionAttribute(String name)  ;
  
  abstract  public void setSessionAttribute(String name, Object value)  ;
  
  public  String getActionParameterName() {  return RequestContext.ACTION ; }
  
  public  String getUIComponentIdParameterName() {  return UIComponent.UICOMPONENT; }
  
  abstract public String getRequestContextPath() ;
  
  abstract public String getRemoteUser()  ;
  
  abstract  public Writer getWriter() throws Exception ;
  
  abstract  public <T> T getRequest() throws Exception ;
  
  abstract  public <T> T getResponse() throws Exception ;
  
  public Object  getAttribute(String name) { 
    if(attributes == null) return null ;
    return attributes.get(name) ; 
  }
  
  public void    setAttribute(String name, Object value) {
    if(attributes == null) attributes = new HashMap<String, Object>() ;
    attributes.put(name, value) ; 
  }
  
  public Object  getAttribute(Class type) { return getAttribute(type.getName()) ; }
  
  public void    setAttribute(Class type, Object value) {
    setAttribute(type.getName(), value) ;
  }
  
  public RequestContext getParentAppRequestContext() { return parentAppRequestContext_ ; }
  public void setParentAppRequestContext(RequestContext context) { parentAppRequestContext_ = context ; }
  
  public int getApplicationMode() { throw new RuntimeException("Method is not supported") ; }
  
  @SuppressWarnings("unused")
  public void setApplicationMode(int mode) { throw new RuntimeException("Method is not supported") ; }
  
  public Throwable  getExecutionError()  { return executionError_ ; }
  
  abstract public  boolean isAjaxRequest() ;
  
  public  boolean useAjax() {  return true ; }
  
  public boolean isForceFullUpdate(){ return true; }
  
  public List<UIComponent>  getUIComponentToUpdateByAjax() {  return uicomponentToUpdateByAjax ; }
  
  public boolean isResponseComplete() { return responseComplete_ ;}
  
  public void    setResponseComplete(boolean b) { responseComplete_ = b ; }
  
  public boolean getProcessRender() { return processRender_ ;}
  
  public void    setProcessRender(boolean b) { processRender_ = b; }
  
  public void addUIComponentToUpdateByAjax(UIComponent uicomponent) {   
    if(uicomponentToUpdateByAjax == null)  {
      uicomponentToUpdateByAjax =  new ArrayList<UIComponent>() ;
    }
    uicomponentToUpdateByAjax.add(uicomponent) ;
  }
  
  abstract public void addJavascript(CharSequence s) ;
  
  abstract public void importJavascript(CharSequence s) ;
  abstract public void importJavascript(String s, String location) ;
  
  
  abstract public void addOnLoadJavascript(CharSequence s) ;
  
  abstract public void addOnResizeJavascript(CharSequence s) ;
  
  abstract public void addOnScrollJavascript(CharSequence s); 
  
  abstract public String getBaseURL() ;
  
  abstract public boolean isUserInRole(String roleUser);
  
  abstract public boolean isLogon();
    
  abstract public StringBuilder createURL(UIComponent uicomponent, Event event, 
                                          boolean supportAjax, String beanId, Parameter ... params) ;
  
  public ResourceResolver getResourceResolver(String uri) {
    Application app = app_ ;
    while(app != null) {
      ApplicationResourceResolver appResolver = app.getResourceResolver() ;
      ResourceResolver resolver =  appResolver.getResourceResolver(uri) ;
      if(resolver  != null)  return resolver ;  
      RequestContext pcontext = getParentAppRequestContext() ;
      if(pcontext != null) app = pcontext.getApplication() ;
      else app =null ;
    }
    return null ;
  }
  
  public StateManager  getStateManager() { return stateManager_; }
  public void  setStateManager(StateManager manager) { stateManager_ =  manager ; }
  
  @SuppressWarnings("unchecked")
  public static <T extends RequestContext> T getCurrentInstance()  { return (T)tlocal_.get() ; }
  
  public static void setCurrentInstance(RequestContext ctx) { tlocal_.set(ctx) ; }
  
}