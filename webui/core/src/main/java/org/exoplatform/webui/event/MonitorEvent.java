/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.event;

import org.exoplatform.webui.application.WebuiRequestContext;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 10, 2006
 * 
 * An event object used to monitor the lifecycle of a component
 */
public class MonitorEvent<T> extends Event<T> {

  final static public String PORTAL_APPLICATION_LIFECYCLE_EVENT = "portal.application.lifecycle.event" ;

  final static public String PORTAL_EXECUTION_LIFECYCLE_EVENT   = "portal.execution.lifecycle.event" ;

  final static public String PORTLET_APPLICATION_LIFECYCLE_EVENT = "portlet.application.lifecycle.event" ;

  final static public String PORTLET_ACTION_LIFECYCLE_EVENT = "portlet.action.lifecycle.event" ;

  final static public String PORTLET_RENDER_LIFECYCLE_EVENT = "portlet.render.lifecycle.event" ;
  
  final static public String UICOMPONENT_LIFECYCLE_MONITOR_EVENT = "uicomponent.lifecycle.monitor.event" ;
  
  private long startExecutionTime_  ;
  private long endExecutionTime_  ;
  private Throwable  error_ ;
  
  public MonitorEvent(T source, String name, WebuiRequestContext context) {
    super(source, name, context);
  }
  
  public  long getStartExecutionTime()  { return startExecutionTime_ ; }
  public  void setStartExecutionTime(long t)  { startExecutionTime_ = t ; }
  
  public  long getEndExecutionTime()  { return endExecutionTime_ ; }
  public  void setEndExecutionTime(long t)  { endExecutionTime_ = t ; }
  
  public Throwable getError()  { return error_ ; }
  public void setError(Throwable t) { error_ = t ; }
}