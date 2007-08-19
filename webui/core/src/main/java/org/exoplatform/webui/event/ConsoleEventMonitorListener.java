/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.event;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;

/**
 * Created by The eXo Platform SAS
 * Jun 10, 2006
 * 
 * This class is listening to the events:
 *  - portal.application.lifecycle.event
 *  - portal.execution.lifecycle.event
 *  
 * The events are sent by the portal platform from the MonitorApplicationLifecycle class
 * 
 * Here we simply put in the log some response time information
 */
public class ConsoleEventMonitorListener extends EventListener {
  
  protected static Log log = ExoLogger.getLogger("portal:ConsoleEventMonitorListener");

  public void execute(Event event) throws Exception {
    MonitorEvent mevent = (MonitorEvent) event ;
    StringBuilder b = new StringBuilder() ;
    b.append("\nComponent ").append(event.getSource().getClass().getName()).
      append(", phase " + event.getExecutionPhase()).
      append("\n  Start event ").append(mevent.getName()).append(" at ").
      append(new Date(mevent.getStartExecutionTime())) ;
    if(mevent.getEndExecutionTime() > 0) {
      b.append("\n  End event ").append(mevent.getName()).append(" at ").
      append(new Date(mevent.getEndExecutionTime())) ;
      b.append("\n  Execute the event in ").append(mevent.getEndExecutionTime() - mevent.getStartExecutionTime()) ;
    }
    b.append("\n") ;
    log.debug(b.toString());
  }

}