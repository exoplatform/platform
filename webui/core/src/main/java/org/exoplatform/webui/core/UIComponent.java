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
package org.exoplatform.webui.core;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.resolver.ResourceResolver;
import org.exoplatform.web.application.Parameter;
import org.exoplatform.web.application.URLBuilder;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.Component;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.MonitorEvent;
import org.exoplatform.webui.event.Event.Phase;
/**
 * Created by The eXo Platform SAS
 * May 7, 2006
 */
abstract public class UIComponent {
  
  final static public String OBJECTID = "objectId" ;
  final static public String UICOMPONENT   = "uicomponent";
  final static public String AJAX_ASYNC   = "ajax_async";
  
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
//  
//  public void processInit(WebuiRequestContext context) throws Exception {
//    MonitorEvent<UIComponent> mevent = createMonitorEvent(Event.Phase.INIT, context);
//    config.getUIComponentLifecycle().init(this, context) ;
//    if(mevent != null) {
//      mevent.setEndExecutionTime(System.currentTimeMillis()) ;
//      mevent.broadcast()  ;
//    }
//  }
//  
  public void processDecode(WebuiRequestContext context) throws Exception {
    MonitorEvent<UIComponent> mevent = createMonitorEvent(Event.Phase.DECODE, context);
    config.getUIComponentLifecycle().processDecode(this, context) ;
    if(mevent != null) {
      mevent.setEndExecutionTime(System.currentTimeMillis()) ;
      mevent.broadcast()  ;
    }
  }
  
  public void processAction(WebuiRequestContext context) throws Exception {
    MonitorEvent<UIComponent> mevent =  createMonitorEvent(Event.Phase.PROCESS, context);
    config.getUIComponentLifecycle().processAction(this, context) ;
    if(mevent != null) {
      mevent.setEndExecutionTime(System.currentTimeMillis()) ;
      mevent.broadcast()  ;
    }
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    MonitorEvent<UIComponent> mevent = createMonitorEvent(Event.Phase.RENDER, context);
    config.getUIComponentLifecycle().processRender(this, context) ;
    if(mevent != null) {
      mevent.setEndExecutionTime(System.currentTimeMillis()) ;
      mevent.broadcast()  ;
    }
  }
//  
//  public void processDestroy(WebuiRequestContext context) throws Exception {
//    MonitorEvent<UIComponent> mevent = createMonitorEvent(Event.Phase.DESTROY, context);
//    config.getUIComponentLifecycle().init(this, context) ;
//    if(mevent != null) {
//      mevent.setEndExecutionTime(System.currentTimeMillis()) ;
//      mevent.broadcast()  ;
//    }
//  }
  
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
  
  public  void setComponentConfig(Class<?> clazz, String id) {
    WebuiRequestContext context =  WebuiRequestContext.getCurrentInstance() ;
    WebuiApplication app = (WebuiApplication) context.getApplication() ;
    this.config = app.getConfigurationManager().getComponentConfig(clazz, id) ;
  }
  
  public String getTemplate() {  return config.getTemplate() ; }
  
  public ResourceResolver getTemplateResourceResolver(WebuiRequestContext context, String template) {
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
  
  public String event(String name, String beanId) throws Exception { return event(name, beanId, (Parameter[])null); }
  
  @SuppressWarnings("unchecked")
  public String event(String name, String beanId, Parameter[] params) throws Exception {
    org.exoplatform.webui.config.Event event = config.getUIComponentEventConfig(name) ;
    if(event == null) return "??config??";
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    URLBuilder urlBuilder = context.getURLBuilder();
    if(urlBuilder == null)  return "??builder??";
    String confirm = loadConfirmMesssage(event, context, beanId);
    return urlBuilder.createAjaxURL(this, event.getName(), confirm, beanId, params).toString();
  } 
  
  protected String loadConfirmMesssage(org.exoplatform.webui.config.Event event, WebuiRequestContext context, String beanId) {
    String confirmKey  = event.getConfirm();
    if(confirmKey.length() < 1) return confirmKey;  
    try {
      String confirm = context.getApplicationResourceBundle().getString(confirmKey);
      return confirm.replaceAll("\\{0\\}", beanId);
    }catch (Exception e) {}
    return confirmKey;
  }
  
  public String url(String name) throws Exception { return url(name, null); }  
 
  public String url(String name, String beanId) throws Exception { return url(name, beanId, null); }
  
  @SuppressWarnings("unchecked")
  public String url(String name, String beanId, Parameter[] params) throws Exception {
    org.exoplatform.webui.config.Event event = config.getUIComponentEventConfig(name) ;
    if(event == null) return "??config??" ;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    String confirm = loadConfirmMesssage(event, context, beanId);
    try {
      return context.getURLBuilder().createURL(this, event.getName(), confirm, beanId, params).toString();
    }catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }
  
  public String doAsync(String name, String beanId, Parameter[] params) throws Exception {
    Parameter async_param = new Parameter(AJAX_ASYNC,"true"); 
    if (params == null) {
      params = new Parameter[] {async_param};
    } else {
      ArrayList<Parameter> paramList = new ArrayList<Parameter>();
      for (int i=0;i<params.length;i++) {
          if (params[i].getName().equals(AJAX_ASYNC)) {
            params[i]=async_param;
          }
          paramList.add(params[i]);
      }
      if (!paramList.contains(async_param)) {
        paramList.add(async_param);
      }
      params = (Parameter[])paramList.toArray();
    }
    
    return event(name, beanId, new Parameter[] {async_param});
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
    WebuiRequestContext  context =  WebuiRequestContext.getCurrentInstance() ;
    return createUIComponent(context, type, configId, componentId)  ;
  }
  
  public <T extends UIComponent> T createUIComponent(
    WebuiRequestContext  context, Class<T> type, String configId, String componentId) throws Exception {
    WebuiApplication app  = (WebuiApplication)context.getApplication() ;    
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
  	WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
  	ExoContainer container = context.getApplication().getApplicationServiceContainer() ;
  	return  type.cast(container.getComponentInstanceOfType(type)) ;
  }
  
  public Event<UIComponent> createEvent(String name, Phase phase, WebuiRequestContext context) throws Exception {
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
  
  private MonitorEvent<UIComponent> createMonitorEvent(Phase phase, WebuiRequestContext context) throws Exception {
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