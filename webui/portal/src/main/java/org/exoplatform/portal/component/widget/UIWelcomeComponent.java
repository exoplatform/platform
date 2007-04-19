/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.widget;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.lifecycle.UIContainerLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 11, 2006  
 */

@ComponentConfig(lifecycle = UIContainerLifecycle.class)
public class UIWelcomeComponent extends UIContainer {

  public UIWelcomeComponent() throws Exception {
    PortalRequestContext prContext = Util.getPortalRequestContext();
    int accessibility = prContext.getAccessPath() ;
    if(accessibility == PortalRequestContext.PUBLIC_ACCESS) {
      addChild(UILoginForm.class, null, "LoginWelcomeComponent");
      return ;
    }
    addChild(UIUserSpace.class, null, null) ;
  }  

}
