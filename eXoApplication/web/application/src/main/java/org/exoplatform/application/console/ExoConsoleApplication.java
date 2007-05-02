/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.console;

import org.exoplatform.application.eXoFramework.ExoFrameworkApplication;
import org.exoplatform.application.eXoFramework.ExoFrameworkRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 23, 2007  
 */
public class ExoConsoleApplication extends ExoFrameworkApplication {
  public String getApplicationId() { return "exo.app.web/eXoConsole"; }

  public String getApplicationName() { return "eXoConsole"; }
  
  public void processAction(ExoFrameworkRequestContext context) throws Exception {
    
  }
  
  public void processRender(ExoFrameworkRequestContext context) throws Exception {
    String script = 
      "eXo.desktop.UIDesktop.createJSApplication('eXo.application.console.UIConsoleApplication','eXoConsole','eXoConsole','/exo.app.web/javascript/');";
    context.getJavascriptManager().addCustomizedOnLoadScript(script) ;
  }
}