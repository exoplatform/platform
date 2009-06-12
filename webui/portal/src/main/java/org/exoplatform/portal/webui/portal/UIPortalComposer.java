/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.portal.webui.portal;

import org.exoplatform.portal.webui.application.UIApplicationList;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.container.UIContainerList;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UITabPane;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 10, 2009  
 */
@ComponentConfig(
                 template = "app:/groovy/portal/webui/portal/UIPortalComposer.gtmpl",
                 events = {
                     @EventConfig(listeners = UIPortalComposer.ViewPropertiesActionListener.class)
                 }
)
public class UIPortalComposer extends UIContainer {

  public UIPortalComposer() throws Exception {
    UITabPane uiTabPane = addChild(UITabPane.class, null, null);
    uiTabPane.addChild(UIApplicationList.class, null, null).setRendered(true);
    uiTabPane.addChild(UIContainerList.class, null, null);
    uiTabPane.setSelectedTab(1);
  }

  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    Util.showComponentLayoutMode(UIPortlet.class);
  }

  static public class ViewPropertiesActionListener extends EventListener<UIPortalComposer> {
    public void execute(Event<UIPortalComposer> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);

      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.createUIComponent(UIPortalForm.class, null, "UIPortalForm");
      uiMaskWS.setWindowSize(700, -1);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);

    }
  }
}
