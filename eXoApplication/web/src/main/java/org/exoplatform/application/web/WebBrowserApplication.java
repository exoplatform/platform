/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.web;

import org.exoplatform.web.application.mvc.MVCApplication;
import org.exoplatform.web.application.mvc.MVCRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 23, 2007  
 */
public class WebBrowserApplication extends MVCApplication {
  public String getApplicationId() { return "exo.app.web/eXoBrowser"; }

  public String getApplicationName() { return "eXoBrowser"; }
  public String getApplicationGroup() { return "exo.app.web"; }
  public String getApplicationType() { return EXO_APPLICATION_TYPE; }
  
  public void processAction(MVCRequestContext context) throws Exception {
    
  }
  
  public void processRender(MVCRequestContext context) throws Exception {
    //Application.init() ;
    String script = 
      "eXo.portal.UIPortal.createJSApplication('eXo.application.browser.UIBrowserApplication','eXoBrowser','eXoBrowser','/exo.app.web/javascript/');";
    context.getJavascriptManager().addCustomizedOnLoadScript(script) ;
  }
}
