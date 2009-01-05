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
package org.exoplatform.webui.application;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.exoplatform.resolver.ApplicationResourceResolver;
import org.exoplatform.resolver.ResourceResolver;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
/**
 * Created by The eXo Platform SAS
 * May 7, 2006
 * 
 * The main class to manage the request context in a webui environment
 * 
 * It adds:
 * - some access to the root UI component (UIApplication)
 * - access to the request and response objects
 * - information about the current state of the request
 * - the list of object to be updated in an AJAX way
 * - an access to the ResourceResolver bound to an uri scheme
 * - the reference on the StateManager object
 */
abstract public class WebuiRequestContext extends RequestContext {
  
  protected UIApplication  uiApplication_ ;
  protected String sessionId_ ;
  protected ResourceBundle appRes_ ;
  private StateManager stateManager_ ;
  private boolean  responseComplete_ = false ;
  private boolean  processRender_ =  false ;
  private Throwable executionError_ ;
  private Set<UIComponent>  uicomponentToUpdateByAjax ;
  
  public WebuiRequestContext(Application app) {
    super(app) ;
  }
  
  public String getSessionId() {  return sessionId_  ; }  
  protected void setSessionId(String id) { sessionId_ = id ;}
  
  public UIApplication getUIApplication() { return uiApplication_ ; }  
  public void  setUIApplication(UIApplication uiApplication) throws Exception { 
    uiApplication_ = uiApplication ;
    appRes_ = null;
  }

  public ResourceBundle getApplicationResourceBundle() {
    if (appRes_ == null)
    {
      try {
        Locale locale = getLocale();
        appRes_ = getApplication().getResourceBundle(locale) ;
      }
      catch (Exception e) {
        throw new UndeclaredThrowableException(e);
      }
    }
    return appRes_ ;
  }
  
  public  String getActionParameterName() {  return WebuiRequestContext.ACTION ; }
  
  public  String getUIComponentIdParameterName() {  return UIComponent.UICOMPONENT; }
  
  abstract public String getRequestContextPath() ;
  
  abstract  public <T> T getRequest() throws Exception ;
  
  abstract  public <T> T getResponse() throws Exception ;
  
  public Throwable  getExecutionError()  { return executionError_ ; }
  
  public boolean isResponseComplete() { return responseComplete_ ;}
  
  public void    setResponseComplete(boolean b) { responseComplete_ = b ; }
  
  abstract public void sendRedirect(String url) throws Exception ;
  
  public boolean getProcessRender() { return processRender_ ;}
  
  public void    setProcessRender(boolean b) { processRender_ = b; }

  public Set<UIComponent>  getUIComponentToUpdateByAjax() {  return uicomponentToUpdateByAjax ; }
  
  public void addUIComponentToUpdateByAjax(UIComponent uicomponent) {   
    if(uicomponentToUpdateByAjax == null) {
      uicomponentToUpdateByAjax =  new LinkedHashSet<UIComponent>() ;
    }
    uicomponentToUpdateByAjax.add(uicomponent) ;
  }
 
  public ResourceResolver getResourceResolver(String uri) {
    Application app = getApplication() ;
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
}