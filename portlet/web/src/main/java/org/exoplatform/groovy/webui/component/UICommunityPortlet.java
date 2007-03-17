/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * Sep 24, 2006  
 */

package org.exoplatform.groovy.webui.component;

import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.component.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * September 24, 2006
 */

@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/community/template/UICommunityPortlet.gtmpl"
)

public class UICommunityPortlet extends UIPortletApplication {
    
  public UICommunityPortlet() throws Exception {
          
  }
}
