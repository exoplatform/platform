/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.console;

import org.exoplatform.web.application.mvc.MVCApplication;
import org.exoplatform.web.application.mvc.MVCRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 23, 2007  
 */
public class ExoConsoleApplication extends MVCApplication {
  
  public String getApplicationId() { return "exo.app.web/eXoConsole"; }

  public String getApplicationName() { return "eXoConsole"; }
  public String getApplicationGroup() { return "exo.app.web"; }
  public String getApplicationType() { return EXO_APPLICATION_TYPE; }
  
  public void processAction(MVCRequestContext context) throws Exception {
    
  }
  
  public void processRender(MVCRequestContext context) throws Exception {
    String script = 
      "eXo.desktop.UIDesktop.createJSApplication('eXo.application.console.UIConsoleApplication','eXoConsole','eXoConsole','/exo.app.web/javascript/');";
    context.getJavascriptManager().addCustomizedOnLoadScript(script) ;
  }
}