/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.webui.config.Component;
import org.exoplatform.webui.config.Event;
import org.exoplatform.webui.config.EventInterceptor;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.Validator;
import org.exoplatform.webui.config.WebuiConfiguration;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.EventInterceptorConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.config.annotation.ValidatorConfig;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 10, 2006
 */
public class ConfigurationManager {

  private Map<String, Component> configs_ = new HashMap<String, Component>() ;
  private org.exoplatform.webui.config.Application application_ ;

  @SuppressWarnings("unused")
  public ConfigurationManager(InputStream inputStream, WebuiApplication app) throws Exception {   
    IBindingFactory bfact = BindingDirectory.getFactory(WebuiConfiguration.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    WebuiConfiguration config = (WebuiConfiguration)uctx.unmarshalDocument(inputStream, null) ;  
    ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
    if(config.getAnnotationClasses() != null) {
      for(String annotationClass : config.getAnnotationClasses()) {
        //process annotation and get the Component
        Component[] components = annotationToComponents(cl, annotationClass) ;
        setComponentConfigs(components);
      }
    }    
    if(config.getComponents() != null){
      for(Component component :  config.getComponents()) {
        String key = component.getType() ;
        if(component.getId() != null) key = key + ":" + component.getId() ;
        configs_.put(key, component) ;
      }
    }

    application_ = config.getApplication() ;
  }

  void setComponentConfigs(Component [] configs){
    for(Component component : configs){
      String key = component.getType() ;
      if(component.getId() != null) key = key + ":" + component.getId() ;
      configs_.put(key, component) ;
    }
  }
  
  public List<Component> getComponentConfig(Class clazz) {
    List<Component> configs = new ArrayList<Component>();
    Collection<Component> values = configs_.values();
    String type = clazz.getName();
    for(Component comp : values){
      if(comp.getType().equals(type)) configs.add(comp);
    }
    return configs;
  }

  public Component getComponentConfig(Class type, String id) {
    String key =  type.getName() ;   
    if(id != null )  key = key + ":" + id  ;    
    Component config = configs_.get(key) ;
    if(config != null) return config;    
    try{
      Component[] components = annotationToComponents(type) ;
      setComponentConfigs(components);
      return  configs_.get(key) ;
    }catch (Exception e) {
      return null;
    }    
  }

  public org.exoplatform.webui.config.Application getApplication() { return application_ ; }

  Component [] annotationToComponents(ClassLoader cl, String annClass) throws Exception {
    Class<?> clazz = cl.loadClass(annClass) ;
    return annotationToComponents(clazz);
  }   

  Component [] annotationToComponents(Class<?> clazz) throws Exception {
    ComponentConfig annotation = clazz.getAnnotation(ComponentConfig.class);
    if(annotation != null){
      return new Component [] { toComponentConfig(annotation, clazz)};
    }

    ComponentConfigs annotations =  clazz.getAnnotation(ComponentConfigs.class);
    if(annotations != null){
      ComponentConfig [] listAnnotations = annotations.value();
      Component [] componentConfigs = new Component[listAnnotations.length];
      for(int i=0; i < componentConfigs.length; i++){
        componentConfigs[i] = toComponentConfig(listAnnotations[i], clazz);
      }
      return componentConfigs;
    }   

    return new Component [] {};   
  }  

  private Component toComponentConfig(ComponentConfig annotation, Class clazz) throws Exception {
    Component config = new Component();    
    if(annotation.id().length() > 0) config.setId(annotation.id());

    Class type = annotation.type() == void.class ? clazz : annotation.type();
    config.setType(type.getName());
    if(annotation.template().length() > 0) config.setTemplate(annotation.template());    
    if(annotation.lifecycle() != void.class) config.setLifecycle(annotation.lifecycle().getName());
    if(annotation.decorator().length() > 0) config.setDecorator(annotation.decorator());
    config.setInitParams(toInitParams(annotation.initParams()));

    EventConfig [] eventAnnotations = annotation.events();
    ArrayList<Event> events = new ArrayList<Event>(); 
    for(EventConfig eventAnnotation : eventAnnotations){
      events.add(toEventConfig(eventAnnotation));
    }
    config.setEvents(events);

    EventInterceptorConfig [] eventInterceptorAnnotations = annotation.eventInterceptors();
    ArrayList<EventInterceptor> eventInterceptors = new ArrayList<EventInterceptor>(); 
    for(EventInterceptorConfig eventAnnotation : eventInterceptorAnnotations){
      eventInterceptors .add(toEventInterceptorConfig(eventAnnotation));
    }
    config.setEventInterceptors(eventInterceptors);

    ValidatorConfig [] validatorAnnotations = annotation.validators();
    ArrayList<Validator> validators = new ArrayList<Validator>();
    for(ValidatorConfig ele : validatorAnnotations){
      validators.add(toValidator(ele));
    }
    config.setValidators(validators);  

    return config;
  }

  private Event toEventConfig(EventConfig annotation) throws Exception {
    Event event  = new Event();      
    event.setExecutionPhase(annotation.phase()); 
    event.setConfirm(annotation.confirm());
    event.setInitParams(toInitParams(annotation.initParams()));
    ArrayList<String> listeners = new ArrayList<String>();    
    for(Class clazz : annotation.listeners()){
      listeners.add(clazz.getName());
    }
    if(annotation.name().length() > 0){
      event.setName(annotation.name());
    }else if(annotation.listeners().length > 0){
      String name  = annotation.listeners()[0].getSimpleName();
      int idx = name.indexOf("ActionListener");
      if(idx > -1) name = name.substring(0, idx);
      event.setName(name);
    }
    event.setListeners(listeners);
    return event;
  }

  private EventInterceptor toEventInterceptorConfig(EventInterceptorConfig annotation) throws Exception {
    EventInterceptor eventInterceptor  = new EventInterceptor();
    eventInterceptor.setType(annotation.type().getName());
    ArrayList<String> list = new ArrayList<String>();
    Collections.addAll(list, annotation.interceptors());
    eventInterceptor.setInterceptors(list);
    eventInterceptor.setInitParams(toInitParams(annotation.initParams()));
    return eventInterceptor;
  }

  private Validator toValidator(ValidatorConfig annotation) throws Exception {
    Validator validator = new Validator();
    validator.setType(annotation.type().getName());
    validator.setInitParams(toInitParams(annotation.initParams()));
    return validator;
  }

  private InitParams  toInitParams(ParamConfig [] annotations){
    if(annotations == null || annotations.length < 1) return null;
    ArrayList<Param> listParam  = new ArrayList<Param>();
    for(ParamConfig  ele : annotations){
      Param param = new Param();
      param.setName(ele.name());
      param.setValue(ele.value());
      listParam.add(param);
    }
    InitParams initParams = new InitParams();
    initParams.setParams(listParam);
    return initParams;
  }
}
