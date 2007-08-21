/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application.mvc;


/**
 * Created by The eXo Platform SAS
 * Apr 23, 2007  
 */
abstract public class ActionHandler {
  abstract public void processAction(MVCRequestContext context) throws Exception ;
}