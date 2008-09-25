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
package org.exoplatform.portal.webui.page;

import org.exoplatform.portal.webui.UIManagement.ManagementMode;
import org.exoplatform.portal.webui.navigation.UIPageManagement;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIDescription;
import org.exoplatform.webui.core.UIToolbar;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfigs({
  @ComponentConfig(
      template = "system:/groovy/webui/core/UIToolbar.gtmpl",
      events = { 
          @EventConfig(listeners = UIPageBrowseControlBar.BackActionListener.class),
          @EventConfig(listeners = UIPageBrowseControlBar.FinishActionListener.class)
      }
  ),
  @ComponentConfig(
      id = "PagePreviewControlBar",
      template = "system:/groovy/webui/core/UIToolbar.gtmpl",
      events = @EventConfig(listeners = UIPageBrowseControlBar.BackActionListener.class)   
  )
})
public class UIPageBrowseControlBar extends UIToolbar {

  private UIComponent uiBackComponent ;

  public UIComponent getBackComponent() { return uiBackComponent ; }
  public void setBackComponent(UIComponent uiComp) { uiBackComponent = uiComp ; }

  public boolean hasBackEvent(){ return uiBackComponent != null; }


  public UIPageBrowseControlBar() throws Exception { setToolbarStyle("ControlToolbar") ; }

  static public class BackActionListener extends EventListener<UIPageBrowseControlBar> {
    public void execute(Event<UIPageBrowseControlBar> event) throws Exception {
      UIPageBrowseControlBar uiBrowseControlBar = event.getSource();

      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel(); 
      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class);
      UIPageBrowser uiPageBrowser = (UIPageBrowser) uiBrowseControlBar.getBackComponent() ;
      uiPageBrowser.defaultValue(uiPageBrowser.getLastQuery());
      uiToolPanel.setUIComponent(uiPageBrowser) ;
      uiToolPanel.setShowMaskLayer(false);
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);    
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;

      UIPageManagement uiManagement = uiBrowseControlBar.getParent();
      uiManagement.setRenderedChild(UIDescription.class);
      uiManagement.setMode(ManagementMode.BROWSE, event);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
  }

  static public class FinishActionListener extends EventListener<UIPageBrowseControlBar> {
    public void execute(Event<UIPageBrowseControlBar> event) throws Exception {
      UIPageBrowseControlBar uiBrowseControlBar = event.getSource();
      UIPageManagement pageManagement = uiBrowseControlBar.getParent();
      UIPageEditBar uiEditBar = pageManagement.getChild(UIPageEditBar.class);
      uiEditBar.savePage();

      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();      
      uiToolPanel.setShowMaskLayer(false);
      UIPageBrowser uiPageBrowser = (UIPageBrowser) uiBrowseControlBar.getBackComponent() ;
      uiPageBrowser.defaultValue(uiPageBrowser.getLastQuery());
      uiToolPanel.setUIComponent(uiPageBrowser) ;

      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);    
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;

      UIPageManagement uiManagement = uiBrowseControlBar.getParent();
      uiManagement.setRenderedChild(UIDescription.class);
      uiManagement.setMode(ManagementMode.BROWSE, event);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
  }
}
