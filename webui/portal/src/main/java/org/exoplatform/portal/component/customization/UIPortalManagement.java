/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;


import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIDescription;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.Event.Phase;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(
    template = "app:/groovy/portal/webui/component/customization/UIPortalManagement.gtmpl"
)
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
    if(mode == ManagementMode.EDIT) {
      UIPortalManagementEditBar uiEditBar = getChild(UIPortalManagementEditBar.class);
      uiEditBar.createEvent("EditPortlet", Phase.PROCESS, event.getRequestContext()).broadcast();
      return;
    } 
    getChild(UIPortalManagementEditBar.class).setRendered(false);
    getChild(UIPortalManagementControlBar.class).setRendered(false);
    getChild(UIDescription.class).setRendered(true); 
    
    UIWorkspace uiWorkingWS = Util.updateUIApplication(event);
    UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);   
    UIPortalBrowser uiPortalBrowser = uiToolPanel.createUIComponent(UIPortalBrowser.class, null, null);
    uiToolPanel.setUIComponent(uiPortalBrowser);
    uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
  }
}
