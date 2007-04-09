/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 9, 2006
 */
public interface ApplicationLifecycle<E extends RequestContext> {
  
  public void onInit(Application app) throws Exception  ;
  public void onStartRequest(Application app, E context) throws Exception  ;
  public void onEndRequest(Application app, E context) throws Exception  ;
  public void onDestroy(Application app) throws Exception  ;
  
}
