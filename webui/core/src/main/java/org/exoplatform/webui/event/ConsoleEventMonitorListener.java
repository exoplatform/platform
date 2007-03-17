/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.event;

import java.util.Date;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 10, 2006
 */
public class ConsoleEventMonitorListener extends EventListener {

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
    System.out.print(b.toString()) ;
  }

}