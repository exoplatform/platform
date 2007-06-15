/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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