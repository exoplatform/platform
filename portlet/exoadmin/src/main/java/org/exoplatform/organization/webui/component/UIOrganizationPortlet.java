/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.ViewChildActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */

@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class
)
public class UIOrganizationPortlet extends UIPortletApplication {
 
  public UIOrganizationPortlet() throws Exception {
    setMinWidth(730) ;
  	addChild(UIViewMode.class, null, UIPortletApplication.VIEW_MODE);
  }

  @ComponentConfig(
      template = "app:/groovy/organization/webui/component/UIViewMode.gtmpl",
      events = @EventConfig (listeners = ViewChildActionListener.class)
  )
  static public class UIViewMode extends UIContainer {
    public UIViewMode() throws Exception {
      addChild(UIUserManagement.class, null, null);
      addChild(UIGroupManagement.class, null, null).setRendered(false);
      addChild(UIMembershipManagement.class, null, null).setRendered(false);
    }
  } 
  
}