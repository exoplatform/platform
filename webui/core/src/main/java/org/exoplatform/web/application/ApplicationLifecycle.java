/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application;

import org.exoplatform.webui.application.WebuiRequestContext;


/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 9, 2006
 */
public interface ApplicationLifecycle {
  
  public void init(Application app) throws Exception  ;
  public void beginExecution(Application app, WebuiRequestContext context) throws Exception  ;
  public void endExecution(Application app, WebuiRequestContext context) throws Exception  ;
  public void destroy(Application app) throws Exception  ;
  
}
