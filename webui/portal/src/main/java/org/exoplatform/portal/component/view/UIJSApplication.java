/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Feb 9, 2007  
 */
@ComponentConfig()
public class UIJSApplication extends UIPortalComponent {
  
  private String jsApplication ;
  
  public UIJSApplication(){
  }

  public String getJSApplication() { return jsApplication; }

  public void setJSApplication(String jsApplication) { this.jsApplication = jsApplication; }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    PortalRequestContext pcontext = (PortalRequestContext)context;
    if(jsApplication == null) return;
    pcontext.getJavascriptManager().addCustomizedOnLoadScript(jsApplication);
  }
  
}
