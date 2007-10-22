/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui;

import java.util.ArrayList;

import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.webui.application.UIWidgets;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
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
    WebuiRequestContext rcontext = Util.getPortalRequestContext();
//    int accessibility = prContext.getAccessPath() ;
//    if(accessibility == PortalRequestContext.PUBLIC_ACCESS) {
//      addChild(UILoginForm.class, null, "LoginWelcomeComponent");
//      return ;
//    }
    
    UIPortalApplication uiPortalApplication = (UIPortalApplication)rcontext.getUIApplication();
    UserPortalConfig userPortalConfig = uiPortalApplication.getUserPortalConfig();
    if(userPortalConfig == null) return;
    UIWidgets uiWidgets = addChild(UIWidgets.class, null, null) ;
    Widgets widgets = userPortalConfig.getWidgets();
    if(widgets == null) {
      widgets = new Widgets() ;
      widgets.setOwnerType(PortalConfig.USER_TYPE) ;
      widgets.setOwnerId(rcontext.getRemoteUser()) ;
      widgets.setChildren(new ArrayList<Container>()) ;
      UserPortalConfigService configService = getApplicationComponent(UserPortalConfigService.class) ;
      configService.create(widgets) ;
    }
    PortalDataMapper.toUIWidgets(uiWidgets, widgets);
  }  

}
