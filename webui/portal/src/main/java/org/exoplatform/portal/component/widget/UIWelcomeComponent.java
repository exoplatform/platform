/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.widget;

import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.view.PortalDataMapper;
import org.exoplatform.portal.component.view.UIWidgets;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.Widgets;
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
    
    UIPortalApplication uiPortalApplication = (UIPortalApplication)prContext.getUIApplication();
    UserPortalConfig userPortalConfig = uiPortalApplication.getUserPortalConfig();
    if(userPortalConfig == null) return;
    List<Widgets> list = userPortalConfig.getWidgets();
    for(Widgets widgets : list) {
      if(widgets.getChildren() == null || widgets.getChildren().size() < 1) continue;
      UIWidgets uiWidgets = addChild(UIWidgets.class, null, null) ;
      PortalDataMapper.toUIWidgets(uiWidgets, widgets);
    }
  }  

}
