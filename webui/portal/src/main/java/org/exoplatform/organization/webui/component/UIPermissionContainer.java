/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import org.exoplatform.webui.component.UITabPane;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 18, 2007  
 */
 
@ComponentConfig( template = "system:/groovy/webui/component/UITabPane.gtmpl" )

public class UIPermissionContainer extends UITabPane {

  public UIPermissionContainer() throws Exception {
    addChild(UIListPermissionSelector.class, null, null) ;
    addChild(UIPermissionSelector.class, null, null).setRendered(false) ;
  }
}
