/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.util.List;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.templates.groovy.ResourceResolver;
import org.exoplatform.webui.application.Application;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.config.Component;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.MonitorEvent;
import org.exoplatform.webui.event.Event.Phase;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
abstract public class UIComponent {
  
  final static public String OBJECTID = "objectId" ;
  final static public String UICOMPONENT   = "uicomponent";
  
  private String id ;
  private boolean rendered = true;
  
  transient protected UIComponent uiparent ;
  transient protected Component config ;
  
  public String getId() { return this.id ; }  
  public UIComponent   setId(String id) {
    if(id == null) this.id =  Integer.toString(hashCode()) ;
    else this.id = id ;
    return this ;
  }
  
  public String getName(){ return getClass().getSimpleName(); }  
  
  @SuppressWarnings("unchecked")
  public <T extends UIComponent> T getParent()  { 
    return (T)this.uiparent ; 
  }  
  public void  setParent(UIComponent uicomponent) {  this.uiparent = uicomponent ; }
  
  public boolean isRendered() { return this.rendered ; }
  
  @SuppressWarnings("unchecked")
  public <T extends UIComponent> T  setRendered(boolean b) { 
    this.rendered =  b ;
    return (T)this ;
  } 
  
  public void processInit(RequestContext context) throws Exception {
    MonitorEvent<UIComponent> mevent = createMonitorEvent(Event.Phase.INIT, context);
    config.getUIComponentLifecycle().init(this, context) ;
    if(mevent != null) {
      mevent.setEndExecutionTime(System.currentTimeMillis()) ;
      mevent.broadcast()  ;
    }
  }
  
  public void processDecode(RequestContext context) throws Exception {
    MonitorEvent<UIComponent> mevent = createMonitorEvent(Event.Phase.DECODE, context);
    config.getUIComponentLifecycle().processDecode(this, context) ;
    if(mevent != null) {
      mevent.setEndExecutionTime(System.currentTimeMillis()) ;
      mevent.broadcast()  ;
    }
  }
  
  public void processAction(RequestContext context) throws Exception {
    MonitorEvent<UIComponent> mevent =  createMonitorEvent(Event.Phase.PROCESS, context);
    config.getUIComponentLifecycle().processAction(this, context) ;
    if(mevent != null) {
      mevent.setEndExecutionTime(System.currentTimeMillis()) ;
      mevent.broadcast()  ;
    }
  }
  
  public void processRender(RequestContext context) throws Exception {
    MonitorEvent<UIComponent> mevent = createMonitorEvent(Event.Phase.RENDER, context);
    config.getUIComponentLifecycle().processRender(this, context) ;
    if(mevent != null) {
      mevent.setEndExecutionTime(System.currentTimeMillis()) ;
      mevent.broadcast()  ;
    }
  }
  
  public void processDestroy(RequestContext context) throws Exception {
    MonitorEvent<UIComponent> mevent = createMonitorEvent(Event.Phase.DESTROY, context);
    config.getUIComponentLifecycle().init(this, context) ;
    if(mevent != null) {
      mevent.setEndExecutionTime(System.currentTimeMillis()) ;
      mevent.broadcast()  ;
    }
  }
  
  public  Component getComponentConfig() { return this.config ; }
  
  public  void setComponentConfig(String componentId, Component config) throws Exception {
    this.config =  config ;
    if(componentId == null || componentId.length() == 0) componentId = config.getId() ;
    if(componentId == null) {
      String type = config.getType() ;
      componentId = type.substring(type.lastIndexOf('.') + 1) ;
    }
    setId(componentId) ;
  }
  
  public  void setComponentConfig(Class clazz, String id) {
    RequestContext context =  RequestContext.getCurrentInstance() ;
    this.config = context.getApplication().getConfigurationManager().getComponentConfig(clazz, id) ;
  }
  
  public String getTemplate() {  return config.getTemplate() ; }
  
  public ResourceResolver getTemplateResourceResolver(RequestContext context, String template) {
    return  context.getResourceResolver(template) ;
  }
  
  public <T extends UIComponent> T getAncestorOfType(Class<T> classType) {
    UIComponent parent =  getParent() ;
    while(parent != null ) {      
      if(classType.isInstance(parent)) return classType.cast(parent) ;
      parent = parent.getParent() ;
    }
    return null ;
  }
  
  
  public String event(String name) throws Exception { return event(name, null); }   
  
  public String event(String name, String beanId) throws Exception {
    org.exoplatform.webui.config.Event event = config.getUIComponentEventConfig(name) ;
    if(event == null) return "??config??" ;
    RequestContext context = RequestContext.getCurrentInstance();
    return context.createURL(this, event, context.useAjax(), beanId).toString();
  }
  
  public String url(String name) throws Exception { return url(name, null); }  
  
  public String url(String name, String beanId) throws Exception {
    org.exoplatform.webui.config.Event event = config.getUIComponentEventConfig(name) ;
    if(event == null) return "??config??" ;
    RequestContext context = RequestContext.getCurrentInstance();
    return context.createURL(this, event, false, beanId).toString();
  }
  
  public <T> void broadcast(Event<T> event, Phase phase) throws Exception {
    if(config == null)  return;
    org.exoplatform.webui.config.Event econfig = config.getUIComponentEventConfig(event.getName());
    if(econfig == null)  return ;
    Phase executionPhase = econfig.getExecutionPhase() ;
    if(executionPhase == phase ||  executionPhase ==  Event.Phase.ANY) {
      for(EventListener<T> listener :  econfig.getCachedEventListeners()) listener.execute(event) ;
    }
  }  
  
  public  <T extends UIComponent> T createUIComponent(Class<T> type, String configId, 
                                        String componentId, UIComponent parent) throws Exception  {
    T uicomp = createUIComponent(type, configId, componentId)  ;
    uicomp.setParent(parent) ;
    return uicomp ;
  }
  
  public  <T extends UIComponent> T createUIComponent(
      Class<T> type, String configId, String componentId) throws Exception  {
    RequestContext  context =  RequestContext.getCurrentInstance() ;
    return createUIComponent(context, type, configId, componentId)  ;
  }
  
  public <T extends UIComponent> T createUIComponent(
      RequestContext  context, Class<T> type, String configId, String componentId) throws Exception {
    Application app  = context.getApplication() ;    
    T comp =  app.createUIComponent(type, configId, componentId, context) ;
    return comp ;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends UIComponent> T findComponentById(String lookupId) {    
    if(getId().equals(lookupId)) return (T)this ;
    return null ;
  }
    
  public <T extends UIComponent> T findFirstComponentOfType(Class<T> type) {
    if (type.isInstance(this)) return type.cast(this);
    return null;
  }
  
  public <T> void findComponentOfType(List<T> list, Class<T> type) {
    if (type.isInstance(this)) list.add(type.cast(this));
  }
  
  public <T extends UIComponent> void setRenderSibbling(Class<T> type) {
    UIContainer uicontainer = (UIContainer) uiparent ;
  	List<UIComponent> children = uicontainer.getChildren() ;
  	for(UIComponent child : children) {
  		if(type.isInstance(child))  child.setRendered(true);
  		else   child.setRendered(false) ;
  	}
  }
  
  public String getUIComponentName() { return "uicomponent" ; }
  
  public <T> T getApplicationComponent(Class<T> type) {
  	RequestContext context = RequestContext.getCurrentInstance() ;
  	ExoContainer container = context.getApplication().getApplicationServiceContainer() ;
  	return  type.cast(container.getComponentInstanceOfType(type)) ;
  }
  
  public Event<UIComponent> createEvent(String name, Phase phase, RequestContext context) throws Exception {
    if(config == null)  return null ; 
    org.exoplatform.webui.config.Event econfig = config.getUIComponentEventConfig(name);
    if(econfig == null) return null ;
    Phase executionPhase = econfig.getExecutionPhase() ;   
    if(executionPhase == phase ||  executionPhase ==  Event.Phase.ANY) { 
      Event<UIComponent>  event =  new Event<UIComponent>(this, name, context) ;
      event.setExecutionPhase(phase) ;
      event.setEventListeners(econfig.getCachedEventListeners()) ;
      return event ;
    }
    return null ;
  }
  
  private MonitorEvent<UIComponent> createMonitorEvent(Phase phase, RequestContext context) throws Exception {
    if(config == null)  return null ; 
    org.exoplatform.webui.config.Event econfig = 
      config.getUIComponentEventConfig(MonitorEvent.UICOMPONENT_LIFECYCLE_MONITOR_EVENT);
    if(econfig == null) return null ;
    Phase executionPhase = econfig.getExecutionPhase() ;
    if(executionPhase == phase ||  executionPhase ==  Event.Phase.ANY) {
      MonitorEvent<UIComponent>  mevent = 
        new MonitorEvent<UIComponent>(this, MonitorEvent.UICOMPONENT_LIFECYCLE_MONITOR_EVENT, context) ;
      mevent.setEventListeners(econfig.getCachedEventListeners()) ;
      mevent.setStartExecutionTime(System.currentTimeMillis()) ;
      mevent.setExecutionPhase(phase) ;
      return mevent ;
    }
    return null ;
  }
}