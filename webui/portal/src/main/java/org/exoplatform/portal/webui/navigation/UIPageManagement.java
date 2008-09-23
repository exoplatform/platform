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
package org.exoplatform.portal.webui.navigation;

import org.exoplatform.portal.webui.UIManagement;
import org.exoplatform.portal.webui.application.UIPortletOptions;
import org.exoplatform.portal.webui.container.UIContainerConfigOptions;
import org.exoplatform.portal.webui.page.UIPageBrowseControlBar;
import org.exoplatform.portal.webui.page.UIPageBrowser;
import org.exoplatform.portal.webui.page.UIPageEditBar;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIDescription;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;

@ComponentConfig(
  template = "app:/groovy/portal/webui/navigation/UIPageManagement.gtmpl"
)
public class UIPageManagement extends UIManagement {

  @SuppressWarnings("unused")
  public UIPageManagement() throws Exception {
    addChild(UIPageNodeSelector.class, null, null);
    addChild(UIPageEditBar.class, null, null).setRendered(false);
    addChild(UIDescription.class, null, "pageManagement").setRendered(false);
    addChild(UIContainerConfigOptions.class, null, null).setRendered(false);
    addChild(UIPortletOptions.class, null, null).setRendered(false);
    addChild(UIPageBrowseControlBar.class, null, null).setRendered(false);
    addChild(UIPageNavigationControlBar.class, null, null);
  }

  public <T extends UIComponent> T setRendered(boolean b) {
    getChild(UIPageEditBar.class).setRendered(false);
    return super.<T> setRendered(b);
  }

  public void setMode(ManagementMode mode, Event<? extends UIComponent> event) throws Exception {
    mode_ = mode ;
    if (mode == ManagementMode.EDIT) {
      UIPageNodeSelector uiNodeSelector = getChild(UIPageNodeSelector.class);
      UITree uiTree = uiNodeSelector.getChild(UITree.class);
      getChild(UIDescription.class).setRendered(false);
      uiTree.createEvent("ChangeNode", event.getExecutionPhase(), event.getRequestContext()).broadcast();
      return;
    }
    UIWorkingWorkspace uiWorkingWS = Util.updateUIApplication(event);
    getChild(UIPageNodeSelector.class).setRendered(false);
    getChild(UIPageNavigationControlBar.class).setRendered(false);
    getChild(UIDescription.class).setRendered(true);
    
    UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
    uiToolPanel.setShowMaskLayer(false);
    
    UIPageBrowser uiPageBrowser = uiToolPanel.findFirstComponentOfType(UIPageBrowser.class) ; 
    if(uiPageBrowser == null) uiPageBrowser = uiToolPanel.createUIComponent(UIPageBrowser.class, null, null) ;
    uiToolPanel.setUIComponent(uiPageBrowser);
    uiPageBrowser.setShowAddNewPage(true);    
    uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
  }

}