/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.config;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 9, 2006
 */
public class Event {
  
  private String name ;
  private String confirm;
  private InitParams initParams  ;
  
  private ArrayList<String> listeners ;
  
  transient private List<EventListener> eventListeners_ ;
  private String phase = "process" ;
  transient private Phase executionPhase_ = null;
  
  public String getName() { return name ; }
  public void setName(String name){ this.name = name; }
  
  public String getPhase() { return phase ; }
  public void   setPhase(String s){phase = s; }
  
  public List<String> getListeners() {  return listeners ; }
  public void setListeners(ArrayList<String> listeners ){ this.listeners = listeners; }
  
  public InitParams  getInitParams()  { return initParams ; }
  public void setInitParams(InitParams initParams){ this.initParams = initParams; }
  
  public String getConfirm() { return confirm; }
  public void setConfirm(String confirm) { this.confirm = confirm;}
  
  public Phase  getExecutionPhase() {
    if(executionPhase_ != null) return executionPhase_;
    try{
      executionPhase_ = Phase.valueOf(phase.toUpperCase());
    }catch (Exception e) {
      executionPhase_ = Phase.PROCESS;
    }
    return executionPhase_; 
  }  
  public void setExecutionPhase(Phase executionPhase) {
    this.executionPhase_ = executionPhase;
  }
  
  public List<EventListener> getCachedEventListeners() throws Exception {
    return eventListeners_ ;
  }
  
  public void setCachedEventListeners(List<EventListener> list) {
    eventListeners_ = list ;
  }
  
}
