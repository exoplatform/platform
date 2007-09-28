/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.webui.application.UIWidgets;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.lifecycle.UIContainerLifecycle;

/**
 * Created by The eXo Platform SAS
 * Jul 11, 2006  
 */

@ComponentConfig(lifecycle = UIContainerLifecycle.class)
public class UIWelcomeComponent extends UIContainer {

  public UIWelcomeComponent() throws Exception {
    PortalRequestContext prContext = Util.getPortalRequestContext();
//    int accessibility = prContext.getAccessPath() ;
//    if(accessibility == PortalRequestContext.PUBLIC_ACCESS) {
//      addChild(UILoginForm.class, null, "LoginWelcomeComponent");
//      return ;
//    }
    
    UIPortalApplication uiPortalApplication = (UIPortalApplication)prContext.getUIApplication();
    UserPortalConfig userPortalConfig = uiPortalApplication.getUserPortalConfig();
    if(userPortalConfig == null) return;
    UIWidgets uiWidgets = addChild(UIWidgets.class, null, null) ;
    Widgets widgets = userPortalConfig.getWidgets();
    PortalDataMapper.toUIWidgets(uiWidgets, widgets);
  }  

}
