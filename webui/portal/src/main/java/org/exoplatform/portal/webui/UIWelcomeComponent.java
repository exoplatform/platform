/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.webui;

import java.util.ArrayList;

import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Gadgets;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.application.UIGadgets;
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
    UIPortalApplication uiPortalApplication = (UIPortalApplication)rcontext.getUIApplication();
    UserPortalConfig userPortalConfig = uiPortalApplication.getUserPortalConfig();
    if(userPortalConfig == null) return;
    Gadgets gadgets = userPortalConfig.getGadgets() ;
    if(gadgets == null) {
      gadgets = new Gadgets() ;
      gadgets.setOwnerType(PortalConfig.USER_TYPE) ;
      gadgets.setOwnerId(rcontext.getRemoteUser()) ;
      gadgets.setChildren(new ArrayList<Container>()) ;
      UserPortalConfigService configService = getApplicationComponent(UserPortalConfigService.class) ;
      configService.create(gadgets) ;
      userPortalConfig.setGadgets(gadgets) ;
    }
    UIGadgets uiGadgets = addChild(UIGadgets.class, null, null) ;
    PortalDataMapper.toUIGadgets(uiGadgets, gadgets) ;
  }  
}
