/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.portal;


import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.UIManagement;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.application.UIPortletOptions;
import org.exoplatform.portal.webui.container.UIContainerConfigOptions;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.web.application.ApplicationMessage;
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
@ComponentConfig(template = "app:/groovy/portal/webui/portal/UIPortalManagement.gtmpl")
public class UIPortalManagement extends UIManagement {
  
	public UIPortalManagement() throws Exception {
		addChild(UIPortalManagementEditBar.class, null, null);
    addChild(UIDescription.class, null, "portalManagement").setRendered(false);
    addChild(UIWelcomeComponent.class,null, null);
    addChild(UIContainerConfigOptions.class, null, null).setRendered(false);
    addChild(UIPortletOptions.class, null, null).setRendered(false);
    addChild(UIPortalManagementControlBar.class, null, null);
  }   
  
  public <T extends UIComponent> T setRendered(boolean b) { 
    getChild(UIPortalManagementEditBar.class).setRendered(false);
    return super.<T>setRendered(b);
  }
  
  public void setMode(ManagementMode mode, Event<? extends UIComponent> event) throws Exception {
    mode_ = mode;    
    PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext() ;
    if(mode == ManagementMode.EDIT) {
      UIPortal uiPortal = Util.getUIPortal();
      if(uiPortal.isModifiable()) {
        UIPortalManagementEditBar uiEditBar = getChild(UIPortalManagementEditBar.class);
        uiEditBar.createEvent("EditPortlet", Phase.PROCESS, event.getRequestContext()).broadcast();
        return;
      }
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      uiPortalApp.addMessage(new ApplicationMessage("UIPortalManagement.msg.Invalid-editPermission", new String[]{uiPortal.getName()})) ;;
      pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
      return;
    } 
    getChild(UIPortalManagementEditBar.class).setRendered(false);
    getChild(UIPortalManagementControlBar.class).setRendered(false);
    getChild(UIDescription.class).setRendered(true); 
    
    UIWorkspace uiWorkingWS = Util.updateUIApplication(event);
    UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
    uiToolPanel.setShowMaskLayer(false);
    UIPortalBrowser uiPortalBrowser = uiToolPanel.createUIComponent(UIPortalBrowser.class, null, null);
    uiToolPanel.setUIComponent(uiPortalBrowser);
    uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
  }
}
