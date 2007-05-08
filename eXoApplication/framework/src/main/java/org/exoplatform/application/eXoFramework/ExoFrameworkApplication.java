/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.eXoFramework;

import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.web.application.Application ;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 23, 2007  
 */
abstract public class ExoFrameworkApplication extends Application {

  public String getApplicationType() { return EXO_APPLICATION_TYPE; }

  
  public ExoContainer getApplicationServiceContainer() {
    return null;
  }

  public ResourceBundle getOwnerResourceBundle(String arg0, Locale arg1) throws Exception {
    return null;
  }

  public ResourceBundle getResourceBundle(Locale arg0) throws Exception {
    return null;
  }
  
  public void processAction(ExoFrameworkRequestContext context) throws Exception {
    
  }
  
  public void processRender(ExoFrameworkRequestContext context) throws Exception {
    context.getAttribute(JSContext.class) ;
  }

}
