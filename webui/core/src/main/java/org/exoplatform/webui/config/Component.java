/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.webui.Util;
import org.exoplatform.webui.component.lifecycle.Lifecycle;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 4, 2006
 */
public class Component {
  
  private String id ;
  private String type ;
  private String lifecycle ;
  private String template ;
  private String decorator ;
  
  private InitParams initParams ;
  
  private ArrayList<Validator> validators ;
  private ArrayList<Event>  events ;
  private ArrayList<EventInterceptor>  eventInterceptors ;
  
  transient private Map<String, Event> eventMap ;
  transient private Lifecycle componentLifecycle ;
  
  public String getId() {  return id; }
  public String getType() { return type; }
  public String getLifecycle() {  return lifecycle; }
  public String getTemplate() {  return template; }
  public String getDecorator() { return decorator; }
  
  public void setId(String id) {  this.id = id; }
  public void setType(String type) { this.type = type ; }
  public void setLifecycle(String lifecycle) { this.lifecycle =  lifecycle; }
  public void setTemplate(String template) {  this.template = template; }
  public void setDecorator(String decorator) { this.decorator = decorator; }
  
  
  public InitParams  getInitParams() { return initParams ; }
  public void setInitParams(InitParams initParams) { this.initParams = initParams ; }
  
  public ArrayList<Validator> getValidators() {  return validators; }
  public void setValidators(ArrayList<Validator> validators) {  this.validators = validators; }
  
  public ArrayList<Event> getEvents() {  return events; }
  public void setEvents(ArrayList<Event> events) {  this.events = events; }
  
  public ArrayList<EventInterceptor> getEventInterceptors() { return eventInterceptors; }
  public void setEventInterceptors(ArrayList<EventInterceptor>  events) {eventInterceptors = events; }
  
  public Event getUIComponentEventConfig(String eventName)  throws Exception {
    if(eventMap != null) return eventMap.get(eventName) ; 
    eventMap = new HashMap<String, Event>() ;
    if(events == null)  return null;
    for(Event event : events) {
      createCachedEventListeners(event) ;
      eventMap.put(event.getName(),  event) ;
    }
    return eventMap.get(eventName) ;
  }
  
  public List<EventListener> getUIComponentEventListeners(String eventName) throws Exception {
    Event event =  getUIComponentEventConfig(eventName) ;
    if(event == null)  return null ;
    List<EventListener> cachedListeners =  event.getCachedEventListeners() ;
    if(cachedListeners != null)  return cachedListeners;
    cachedListeners = new ArrayList<EventListener>() ;
    for(String listener :  event.getListeners()) {
      if(listener.indexOf(".") < 0) {
        listener =  type + "$" +  listener ;
      }
      EventListener eventListener = (EventListener) Util.createObject(listener, event.getInitParams()) ;
      cachedListeners.add(eventListener) ;
    }
    event.setCachedEventListeners(cachedListeners) ;
    return cachedListeners ;
  }

  private void createCachedEventListeners(Event event) throws Exception {
    List<EventListener> cachedListeners = new ArrayList<EventListener>() ;
    for(String listener :  event.getListeners()) {
      if(listener.indexOf(".") < 0) listener =  type + "$" +  listener ;      
      EventListener eventListener = (EventListener) Util.createObject(listener, event.getInitParams()) ;
      cachedListeners.add(eventListener) ;
    }
    event.setCachedEventListeners(cachedListeners) ;
  }

  public Lifecycle getUIComponentLifecycle() throws Exception {
    if(componentLifecycle != null)  return componentLifecycle;
    if(lifecycle != null) {
      componentLifecycle = (Lifecycle)Util.createObject(lifecycle, null) ;
    } else {
      componentLifecycle = Util.createObject(Lifecycle.class, null) ;
    }
    return componentLifecycle ;
  }
 
}