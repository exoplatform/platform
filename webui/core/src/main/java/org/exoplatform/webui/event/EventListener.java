/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.event;
/**
 * Created by The eXo Platform SAS
 * May 10, 2006
 */
abstract public class EventListener<T> {
  abstract public void execute(Event<T> event) throws Exception;
}