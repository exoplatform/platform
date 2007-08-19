/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.event;

import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;

public class Event<T>  {
  
  private String name_ ;
  private T source_ ;
  private Phase executionPhase_ =  Phase.PROCESS ;
  
  private WebuiRequestContext context_;
  private List<EventListener> listeners_ ;

  public Event(T source, String name, WebuiRequestContext context) {
    name_ = name  ;
    source_ =  source ;
    context_ = context;
  }
  
  public String getName() { return name_ ; }
  
  public T getSource() { return source_ ; }
  
  public Phase  getExecutionPhase() { return executionPhase_ ; }
  public void setExecutionPhase(Phase phase) { executionPhase_ =  phase ; }
  
  public WebuiRequestContext getRequestContext( ) { return context_ ; }
  public void  setRequestContext(WebuiRequestContext context) { context_ = context ; }

  public List<EventListener>  getEventListeners() { return listeners_ ; }
  public void  setEventListeners(List<EventListener> listeners) { listeners_ = listeners ; }
  
  final public void broadcast() throws Exception {
    for(EventListener<T> listener : listeners_) listener.execute(this) ; 
  }
  
  static public enum Phase {
    ANY, INIT, RESTORE, DECODE, PROCESS, RENDER, DESTROY
  }
  
}