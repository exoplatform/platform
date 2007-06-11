/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 12, 2006
 * @version:: $Id$
 */
@ComponentConfig(
  lifecycle =UIApplicationLifecycle.class,
  template = "system:/groovy/webui/core/UIApplication.gtmpl" 
)
public class UIAddPortletPortlet extends UIPortletApplication {
  
  public UIAddPortletPortlet() throws Exception {
    addChild(UIAddPortletForm.class, null, null);
  }  
  
}
