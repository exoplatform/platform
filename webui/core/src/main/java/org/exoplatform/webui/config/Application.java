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
package org.exoplatform.webui.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.webui.Util;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
public class Application {
  
  private InitParams initParams ;
  private String uiroot ;
  private String stateManager ;
  
  public  InitParams getInitParams() { return initParams ; }
  private ArrayList<String> lifecycleListeners ;
  private ArrayList<Event>  events ;
  
  transient private Map<String, Event> eventMap ;
  
  public  String getUIRootComponent() {  return uiroot ; }
  
  public String getStateManager() { return stateManager ; }
  
  public ArrayList<String> getLifecyleListeners() { return lifecycleListeners ; }
  
  public ArrayList<Event>  getEvents() { return events ; }  
  
  public Event getApplicationEventConfig(String eventName)  {
    if(eventMap != null) return eventMap.get(eventName) ; 
    eventMap = new HashMap<String, Event>() ;
    if(events == null)  return null;
    for(Event event : events) {
      eventMap.put(event.getName(),  event) ;
    }
    return eventMap.get(eventName) ;
  }
  
  public List<EventListener> getApplicationEventListeners(String eventName) throws Exception {
    Event event =  getApplicationEventConfig(eventName) ;
    if(event == null)  return null ;
    List<EventListener> cachedListeners =  event.getCachedEventListeners() ;
    if(cachedListeners != null)  return cachedListeners;
    cachedListeners = new ArrayList<EventListener>() ;
    for(String listener :  event.getListeners()) {
      EventListener eventListener = (EventListener) Util.createObject(listener, null) ;
      cachedListeners.add(eventListener) ;
    }
    event.setCachedEventListeners(cachedListeners) ;
    return cachedListeners ;
  }
  
  public List<ApplicationLifecycle> getApplicationLifecycleListeners() throws Exception {
    List<ApplicationLifecycle> appLifecycles = new ArrayList<ApplicationLifecycle>() ;
    if(lifecycleListeners == null)  return appLifecycles ;
    for(String type : lifecycleListeners) {
      ApplicationLifecycle instance = (ApplicationLifecycle)Util.createObject(type , null) ;
      appLifecycles.add(instance) ;
    }    
    return appLifecycles ;
  }
}