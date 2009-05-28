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

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.webui.UIManagement;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.application.UIPortletOptions;
import org.exoplatform.portal.webui.container.UIContainerConfigOptions;
import org.exoplatform.portal.webui.page.UIPageBody;
import org.exoplatform.portal.webui.util.PortalDataMapper;
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
 * Author : liem_nguyen 
 *          ncliam@gmail.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(template = "system:/groovy/portal/webui/portal/UIPortalManagement2.gtmpl")
public class UIPortalManagement2 extends UIManagement {
  private UIPortal uiportal = Util.getUIPortal();
  
	public UIPortalManagement2() throws Exception {
		addChild(UIPortalManagementEditBar2.class, null, null);    
    addChild(UIDescription.class, null, "portalManagement").setRendered(false);
    addChild(UIWelcomeComponent.class,null, null);
    addChild(UIContainerConfigOptions.class, null, null).setRendered(false);
    addChild(UIPortletOptions.class, null, null).setRendered(false);
    addChild(UIPortalManagementControlBar.class, null, null);    
  }   
  
  public <T extends UIComponent> T setRendered(boolean b) { 
    getChild(UIPortalManagementEditBar2.class).setRendered(false);
    return super.<T>setRendered(b);
  }
  
  public void setMode(ManagementMode mode, Event<? extends UIComponent> event) throws Exception {
    //TODO: modify - dang.tung: config mode for uicomponent, getMode() always return right
    mode_ = mode ;
    //------------------------------------------------------------------------------------
    if(mode == ManagementMode.EDIT) {
      String portalName = event.getRequestContext().getRequestParameter(OBJECTID);
      if (uiportal.getName().equals(portalName)) {
        uiportal = Util.getUIPortal();
      } else {        
        this.uiportal.setChildren(null);
        PortalRequestContext prContext = Util.getPortalRequestContext();
        UserPortalConfigService service = event.getSource().getApplicationComponent(UserPortalConfigService.class);
        UserPortalConfig userConfig = service.getUserPortalConfig(portalName, prContext.getRemoteUser());
        PortalDataMapper.toUIPortal(this.uiportal, userConfig);
      }
      
      UIPageBody uiPageBody = this.uiportal.findFirstComponentOfType(UIPageBody.class);
      if(uiPageBody != null) {
        if(uiPageBody.getMaximizedUIComponent() != null) {
          UIPortlet uiMaximizedPortlet =  (UIPortlet) uiPageBody.getMaximizedUIComponent();
          uiMaximizedPortlet.setCurrentWindowState(WindowState.NORMAL);
          uiPageBody.setMaximizedUIComponent(null);
        }
      }
      UIPortalManagementEditBar2 uiEditBar = getChild(UIPortalManagementEditBar2.class);
      uiEditBar.createEvent("EditPortlet", Phase.PROCESS, event.getRequestContext()).broadcast();
      return;
    } 
    
    getChild(UIPortalManagementEditBar2.class).setRendered(false);
    getChild(UIPortalManagementControlBar.class).setRendered(false);
    getChild(UIDescription.class).setRendered(false); 
    
    UIWorkingWorkspace uiWorkingWS = Util.updateUIApplication(event);
    UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
    uiToolPanel.setShowMaskLayer(false);
    UISiteManagement UIPortalManager = uiToolPanel.createUIComponent(UISiteManagement.class, null, null);
    uiToolPanel.setUIComponent(UIPortalManager);
    uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
    
  }

  public UIPortal getUIPortal() {
    return uiportal;
  }

  public void setUIPortal(UIPortal uiportal) {
    this.uiportal = uiportal;
  }
  
  
//  private void clone(UIPortal portal1, UIPortal portal2) {
//    portal2.setChildren(portal1.getChildren());
//    portal2.setCreator(portal1.getCreator());
//    portal2.setDescription(portal1.getDescription());
//    portal2.setEditPermission(portal1.getEditPermission());
//    portal2.setFactoryId(portal1.getFactoryId());    
//    portal2.setHeight(portal1.getHeight());
//    portal2.setIcon(portal1.getIcon());
//    portal2.setId(portal1.getId());
//    portal2.setLocale(portal1.getLocale());
//    portal2.setMaximizedUIComponent(portal1.getMaximizedUIComponent());
//    portal2.setMode(portal1.getMode());    
//    portal2.setModifiable(portal1.isModifiable());
//    portal2.setModifier(portal1.getModifier());
//    portal2.setName(portal1.getName());
//    try {
//      portal2.setNavigation(portal1.getNavigations());
//    } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//    portal2.setOwner(portal1.getOwner());    
//    portal2.setParent(portal1.getParent());
//    portal2.setRendered(portal1.isRendered());
//    portal2.setTemplate(portal1.getTemplate());
//    portal2.setTitle(portal1.getTitle());
//    portal2.setWidth(portal1.getWidth());
//  }

}
