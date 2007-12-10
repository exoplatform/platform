/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import javax.portlet.PortletRequest;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Ba Phu
 *          phului@gmail.com
 * Nov 7, 2007  
 */

@ComponentConfig (
    lifecycle = UIApplicationLifecycle.class, 
    template = "app:/groovy/portal/webui/component/UIHomePagePortlet.gtmpl"    
)

public class UIHomePagePortlet extends UIPortletApplication {

  private static String DEFAULT_TEMPLATE = "app:/groovy/portal/webui/component/UIHomePagePortlet.gtmpl" ;  
  
  public UIHomePagePortlet () throws  Exception {
  
  } 
  
  @Override
  public String getTemplate() {
    PortletRequestContext context = (PortletRequestContext) WebuiRequestContext.getCurrentInstance() ;
    PortletRequest prequest = context.getRequest() ;    
    String template =  prequest.getPreferences().getValue("template", DEFAULT_TEMPLATE) ; 
    
    return template;
  }
}
