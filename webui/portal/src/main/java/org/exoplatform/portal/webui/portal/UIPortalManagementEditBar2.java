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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.application.UIPortletOptions;
import org.exoplatform.portal.webui.container.UIContainerConfigOptions;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIToolbar;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(    
  template = "system:/groovy/webui/core/UIToolbar.gtmpl",
  events = {   
    @EventConfig(listeners = UIPortalManagementEditBar2.PreviewActionListener.class),
    @EventConfig(listeners = UIPortalManagementEditBar2.EditPortalActionListener.class),
    @EventConfig(listeners = UIPortalManagementEditBar2.EditContainerActionListener.class),
    @EventConfig(listeners = UIPortalManagementEditBar2.EditPortletActionListener.class)
  }
)
public class UIPortalManagementEditBar2 extends UIToolbar { 
    
  public UIPortalManagementEditBar2() throws Exception {
    setToolbarStyle("EditToolbar") ;
    setJavascript("Preview","onclick='eXo.portal.UIPortal.switchMode(this);'") ;
  }
  
  public <T extends UIComponent> T setRendered(boolean b) { 
    List<UIPortlet> uiPortlets = new ArrayList<UIPortlet>();
    UIPortalManagement2 manager = this.getParent();
    manager.getUIPortal().findComponentOfType(uiPortlets, UIPortlet.class);
    for (UIPortlet uiPortlet : uiPortlets) {
      uiPortlet.setShowEditControl(b);
    }
    return super.<T>setRendered(b) ;    
  } 
  
  @SuppressWarnings("unused")
  static public class PreviewActionListener  extends EventListener<UIPortalManagementEditBar2> {
    public void execute(Event<UIPortalManagementEditBar2> event) throws Exception {
    }
  }
  
  static public class EditPortalActionListener  extends EventListener<UIPortalManagementEditBar2> {
    public void execute(Event<UIPortalManagementEditBar2> event) throws Exception {
      UIPortalManagement2 manager = event.getSource().getParent();      
      UIPortal uiPortal = manager.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.createUIComponent(UIPortalForm.class, null, "UIPortalForm");
      uiMaskWS.setWindowSize(700, -1);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
  
  static public class EditContainerActionListener  extends EventListener<UIPortalManagementEditBar2> {
    public void execute(Event<UIPortalManagementEditBar2> event) throws Exception {     
      UIPortalManagementEditBar2 uiEditBar = event.getSource();
      UIPortalManagement2 manager = uiEditBar.getParent();
      UIPortal uiPortal = manager.getUIPortal();
      uiPortal.setRenderSibbling(UIPortal.class);   
      
      UIPortalManagement2 uiPManagement = uiEditBar.getParent();
      Class<?> [] childrenToRender = {UIPortalManagementEditBar2.class,
                                      UIContainerConfigOptions.class, UIPortalManagementControlBar.class};
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
      
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext() ;
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      
      UIControlWorkspace uiControl = uiPortalApp.getChildById(UIPortalApplication.UI_CONTROL_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiControl);
      
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);    
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
      pcontext.setFullRender(true);
    }
  }
  
  static public class EditPortletActionListener  extends EventListener<UIPortalManagementEditBar2> {
    public void execute(Event<UIPortalManagementEditBar2> event) throws Exception {
      UIPortalManagementEditBar2 uiEditBar = event.getSource();
      UIPortalManagement2 manager = uiEditBar.getParent();
      UIPortal uiPortal = manager.getUIPortal();
      uiPortal.setRenderSibbling(UIPortal.class);
      
      UIPortalManagement2 uiPManagement = uiEditBar.getParent();
      Class<?> [] childrenToRender = {UIPortalManagementEditBar2.class, 
                                   UIPortletOptions.class, UIPortalManagementControlBar.class};
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
      
      UIWorkingWorkspace uiWorkingWS = Util.updateUIApplication(event) ;
      UIPortalToolPanel toolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class);
      if(toolPanel != null ) toolPanel.setShowMaskLayer(false);
    }
  }
}