/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
/**
 * Author : Uoc Nguyen Ba
 *          uoc.nguyen@exoplatform.com
 * Oct 12, 2007
 */
@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class,
  template = "app:/groovy/portal/webui/component/UIConsolePortlet.gtmpl"
)

public class UIConsolePortlet extends UIPortletApplication {
  
  public UIConsolePortlet() throws Exception {
   
  }
}
