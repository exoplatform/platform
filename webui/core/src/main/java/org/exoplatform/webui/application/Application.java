/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.templates.groovy.ApplicationResourceResolver;
import org.exoplatform.webui.Util;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.config.Component;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
abstract public class Application {
  
  private ConfigurationManager configManager_ ;
  private StateManager stateManager_ ;  
  private List<ApplicationLifecycle>  lifecycleListeners_ ;
  private ApplicationResourceResolver resourceResolver_ ;
  private Hashtable<String, Object> attributes_ =  new Hashtable<String, Object>() ;
  
  public void init() throws Exception {        
    String configPath = getApplicationInitParam("webui.configuration") ;
    InputStream is = getResourceResolver().getInputStream(configPath) ;
    configManager_ = new ConfigurationManager(is, this) ;
    String stateManagerClass = configManager_.getApplication().getStateManager() ;
    StateManager stManager = (StateManager) Util.createObject(stateManagerClass, null) ;
    setStateManager(stManager) ;
    lifecycleListeners_ = configManager_.getApplication().getApplicationLifecycleListeners() ;
    for(ApplicationLifecycle lifecycle :  lifecycleListeners_) {
      lifecycle.init(this) ;
    }
  }

  abstract public String getApplicationId() ;
  
  public ApplicationResourceResolver getResourceResolver() { return resourceResolver_ ; }  
  public void setResourceResolver(ApplicationResourceResolver resolver) { resourceResolver_ = resolver ; }
  
  public ConfigurationManager  getConfigurationManager() { return configManager_ ;}  
  
  public StateManager  getStateManager() { return stateManager_ ;}  
  public void setStateManager(StateManager sm) { stateManager_ =  sm ; }
  
  public Object  getAttribute(String name) { return attributes_.get(name) ; }  
  public void    setAttribute(String name, Object value) { attributes_.put(name, value) ; }
  
  public void destroy() throws Exception {
    for(ApplicationLifecycle lifecycle :  lifecycleListeners_) lifecycle.destroy(this) ;
  }
  
  abstract public String getApplicationName() ;
  
  abstract public ResourceBundle  getResourceBundle(Locale locale) throws Exception ;
  
  abstract public ResourceBundle  getOwnerResourceBundle(String username, Locale locale) throws Exception ;
  
  abstract public ExoContainer getApplicationServiceContainer() ;
  
  abstract public String getApplicationInitParam(String name) ;
  
  public <T> void   broadcast(Event<T> event) throws Exception {
    List<EventListener> listeners = 
      configManager_.getApplication().getApplicationEventListeners(event.getName()) ;
    if(listeners == null)  return;
    for(EventListener<T> listener : listeners) listener.execute(event) ;
  }
  
  
  public <T extends UIComponent> T createUIComponent(Class<T> type, String configId, String id, RequestContext context)  throws Exception{
    Component config = configManager_.getComponentConfig(type, configId) ;
    
    if(config == null) {      
      throw new Exception("Cannot find the configuration for the component " + type.getName() + ", configId " +configId) ;
    }
    
    T uicomponent =   Util.createObject(type, config.getInitParams());
    uicomponent.setComponentConfig(id, config) ;
    config.getUIComponentLifecycle().init(uicomponent, context) ;
    return type.cast(uicomponent) ;
  }
  
  public void  processDecode(UIApplication uiApp, RequestContext context) throws Exception {
    context.setUIApplication(uiApp) ;
    uiApp.processDecode(context) ;
  }
  
  public void  processAction(UIApplication uiApp, RequestContext context) throws Exception {
    context.setUIApplication(uiApp) ;
    uiApp.processAction(context) ;
  }
  
  public List<UIComponent> getDefaultUIComponentToUpdateByAjax(RequestContext context) {
    List<UIComponent> list = new ArrayList<UIComponent>(3) ;
    list.add(context.getUIApplication()) ;
    return list ;
  }  
  
  public List<ApplicationLifecycle> getApplicationLifecycle(){ return lifecycleListeners_; }
  
}