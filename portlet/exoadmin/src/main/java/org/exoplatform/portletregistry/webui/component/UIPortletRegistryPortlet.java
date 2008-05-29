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
package org.exoplatform.portletregistry.webui.component;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPopupContainer;
import org.exoplatform.webui.core.UIPopupMessages;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/portletregistry/webui/component/UIPortletRegistryPortlet.gtmpl"
)
public class UIPortletRegistryPortlet extends UIPortletApplication {
  
  public UIPortletRegistryPortlet() throws Exception{
    addChild(ApplicationRegistryWorkingArea.class, null, null);  
    ApplicationRegistryControlArea uiControlArea = addChild(ApplicationRegistryControlArea.class, null, null);
    uiControlArea.initApplicationCategories();
    UIPopupContainer uiPopup =  addChild(UIPopupContainer.class, null, null) ;
    uiPopup.setId("UIPorletRegistryContainer") ;
  }
  
  public void renderPopupMessages() throws Exception {
    UIPopupMessages uiPopupMsg = getUIPopupMessages();
    if(uiPopupMsg == null)  return ;
    WebuiRequestContext  context =  WebuiRequestContext.getCurrentInstance() ;
    uiPopupMsg.processRender(context);
  }

}