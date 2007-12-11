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
package org.exoplatform.webui.event;

import org.exoplatform.webui.application.WebuiRequestContext;
/**
 * Created by The eXo Platform SAS
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