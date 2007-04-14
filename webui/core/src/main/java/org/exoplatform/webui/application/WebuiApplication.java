/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
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
abstract public class WebuiApplication extends Application {
  
  private ConfigurationManager configManager_ ;
  private StateManager stateManager_ ;  
  
  public void onInit() throws Exception {        
    String configPath = getApplicationInitParam("webui.configuration") ;
    InputStream is = getResourceResolver().getInputStream(configPath) ;
    configManager_ = new ConfigurationManager(is, this) ;
    String stateManagerClass = configManager_.getApplication().getStateManager() ;
    StateManager stManager = (StateManager) Util.createObject(stateManagerClass, null) ;
    setStateManager(stManager) ;
    List<ApplicationLifecycle> lifecycleListeners = 
      configManager_.getApplication().getApplicationLifecycleListeners() ;
    setApplicationLifecycle(lifecycleListeners) ;
    for(ApplicationLifecycle lifecycle :  lifecycleListeners) lifecycle.onInit(this) ;
  }

  public ConfigurationManager  getConfigurationManager() { return configManager_ ;}  
  
  public StateManager  getStateManager() { return stateManager_ ;}  
  public void setStateManager(StateManager sm) { stateManager_ =  sm ; }
  
  abstract public String getApplicationInitParam(String name) ;
  
  public <T> void   broadcast(Event<T> event) throws Exception {
    List<EventListener> listeners = 
      configManager_.getApplication().getApplicationEventListeners(event.getName()) ;
    if(listeners == null)  return;
    for(EventListener<T> listener : listeners) listener.execute(event) ;
  }
  
  
  public <T extends UIComponent> T createUIComponent(Class<T> type, String configId, String id, WebuiRequestContext context)  throws Exception{
    Component config = configManager_.getComponentConfig(type, configId) ;
    //TODO Le Bien Thuy modified
    if(config == null) {      
      config = configManager_.getComponentConfig(type, "Default") ;
      if(config == null) {
        throw new Exception("Cannot find the configuration for the component " + type.getName() + ", configId " +configId) ;  
      }
    }
    T uicomponent =   Util.createObject(type, config.getInitParams());
    uicomponent.setComponentConfig(id, config) ;
    config.getUIComponentLifecycle().init(uicomponent, context) ;
    return type.cast(uicomponent) ;
  }
  
  public void  processDecode(UIApplication uiApp, WebuiRequestContext context) throws Exception {
    context.setUIApplication(uiApp) ;
    uiApp.processDecode(context) ;
  }
  
  public void  processAction(UIApplication uiApp, WebuiRequestContext context) throws Exception {
    context.setUIApplication(uiApp) ;
    uiApp.processAction(context) ;
  }
  
  public List<UIComponent> getDefaultUIComponentToUpdateByAjax(WebuiRequestContext context) {
    List<UIComponent> list = new ArrayList<UIComponent>(3) ;
    list.add(context.getUIApplication()) ;
    return list ;
  }  
}