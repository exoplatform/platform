/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application.mvc;

import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.web.application.Application;

/**
 * Created by The eXo Platform SAS
 * Apr 23, 2007  
 */
abstract public class MVCApplication extends Application {
  
  public ResourceBundle getOwnerResourceBundle(String arg0, Locale arg1) throws Exception {
    return null;
  }

  public ResourceBundle getResourceBundle(Locale arg0) throws Exception {
    return null;
  }
  
  public void processAction(MVCRequestContext context) throws Exception {
    
  }
  
  public void processRender(MVCRequestContext context) throws Exception {
    context.getAttribute(JSContext.class) ;
  }

}
