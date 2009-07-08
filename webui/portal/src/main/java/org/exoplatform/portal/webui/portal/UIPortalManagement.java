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
package org.exoplatform.portal.webui.portal;


import javax.portlet.WindowState;

import org.exoplatform.portal.webui.UIManagement;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.application.UIPortletOptions;
import org.exoplatform.portal.webui.container.UIContainerConfigOptions;
import org.exoplatform.portal.webui.page.UIPageBody;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIDescription;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.Event.Phase;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(template = "system:/groovy/portal/webui/portal/UIPortalManagement.gtmpl")
public class UIPortalManagement extends UIManagement {
  
	public UIPortalManagement() throws Exception {
		addChild(UIPortalManagementEditBar.class, null, null);
    addChild(UIDescription.class, null, "portalManagement").setRendered(false);
    addChild(UIContainerConfigOptions.class, null, null).setRendered(false);
    addChild(UIPortletOptions.class, null, null).setRendered(false);
    addChild(UIPortalManagementControlBar.class, null, null);
  }   
  
  public <T extends UIComponent> T setRendered(boolean b) { 
    getChild(UIPortalManagementEditBar.class).setRendered(false);
    return super.<T>setRendered(b);
  }
  
  public void setMode(ManagementMode mode, Event<? extends UIComponent> event) throws Exception {
    //TODO: modify - dang.tung: config mode for uicomponent, getMode() always return right
    mode_ = mode ;
    //------------------------------------------------------------------------------------
    if(mode == ManagementMode.EDIT) {
      UIPageBody uiPageBody = Util.getUIPortal().findFirstComponentOfType(UIPageBody.class);
      if(uiPageBody != null) {
        if(uiPageBody.getMaximizedUIComponent() != null) {
          UIPortlet uiMaximizedPortlet =  (UIPortlet) uiPageBody.getMaximizedUIComponent();
          uiMaximizedPortlet.setCurrentWindowState(WindowState.NORMAL);
          uiPageBody.setMaximizedUIComponent(null);
        }
      }
      UIPortalManagementEditBar uiEditBar = getChild(UIPortalManagementEditBar.class);
      uiEditBar.createEvent("EditPortlet", Phase.PROCESS, event.getRequestContext()).broadcast();
      return;
    } 

    getChild(UIPortalManagementEditBar.class).setRendered(false);
    getChild(UIPortalManagementControlBar.class).setRendered(false);
    getChild(UIDescription.class).setRendered(true); 

    UIWorkingWorkspace uiWorkingWS = Util.updateUIApplication(event);
    UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
    uiToolPanel.setShowMaskLayer(false);
    UIPortalBrowser uiPortalBrowser = uiToolPanel.createUIComponent(UIPortalBrowser.class, null, null);
    uiToolPanel.setUIComponent(uiPortalBrowser);
    uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
  }
}
