/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application.widget;

import java.io.Writer;
import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.web.application.Application;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 23, 2007  
 */
abstract public class WidgetApplication extends Application {
  
  public String getApplicationType() { return "eXoWidget" ; }
  
  abstract public void processRender(Writer w) throws Exception ;
  
  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {
    throw new Exception("This method is not supported") ;
  }

  public ResourceBundle getResourceBundle(Locale locale) throws Exception {
    throw new Exception("This method is not supported") ;
  }
}
