/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.widget.web;

import org.exoplatform.web.application.mvc.MVCApplication;
import org.exoplatform.web.application.mvc.MVCRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 23, 2007  
 */
public class InfoWidget extends MVCApplication {
  public String getApplicationId() { return "exo.widget.web/InfoWidget"; }

  public String getApplicationName() { return "InfoWidget"; }

  public String getApplicationGroup() { return "exo.widget.web"; }
  
  public String getApplicationType() { return "eXoWidget" ; }
  
  public void processAction(MVCRequestContext context) throws Exception {

  }
  
  public void processRender(MVCRequestContext context) throws Exception {
    String script = 
      "eXo.desktop.UIDesktop.createJSApplication('eXo.widget.info.UIInfoWidget','Info','Info','/exo.widget.web/javascript/');";
    context.getJavascriptManager().addCustomizedOnLoadScript(script) ;
  }
}