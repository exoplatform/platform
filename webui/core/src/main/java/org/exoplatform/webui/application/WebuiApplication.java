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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.Util;
import org.exoplatform.webui.config.Component;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;


/**
 *Created by The eXo Platform SAS
 * May 7, 2006
 * 
 * This abstract class defines several methods to abstract the differnt type of web application the
 * eXo web framework can provide such as portal or portlet.
 */
abstract public class WebuiApplication extends Application {
  
  private ConfigurationManager configManager_ ;
  private StateManager stateManager_ ;
  
  /**
   * This initialisation goals is to first extract and parse the webui configuration XML file 
   * defined inside the web.xml of the web application.
   * 
   * The ConfigurationManager class is responsible of the parsing and then wrap all the information 
   * about the UI configuration.
   * 
   * One of the information is the real implementation of the StateManager object. That object is 
   * extracted from the configuration and stored as a field in that class.
   * 
   * Lifecycle phases are also extracted from the XML file, referenced in this WebuiApplication class
   * and initialized at the same time.
   * 
   */
  public void onInit() throws Exception {
    String configPath = getApplicationInitParam("webui.configuration") ;
    InputStream is = getResourceResolver().getInputStream(configPath) ;
    configManager_ = new ConfigurationManager(is) ;
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
    if(config == null) {
      throw new Exception("Cannot find the configuration for the component " + type.getName() + ", configId " +configId) ;  
    }
    T uicomponent =   Util.createObject(type, config.getInitParams());
    uicomponent.setComponentConfig(id, config) ;
    return type.cast(uicomponent) ;
  }
  
  public List<UIComponent> getDefaultUIComponentToUpdateByAjax(WebuiRequestContext context) {
    List<UIComponent> list = new ArrayList<UIComponent>(3) ;
    list.add(context.getUIApplication()) ;
    return list ;
  }  
}