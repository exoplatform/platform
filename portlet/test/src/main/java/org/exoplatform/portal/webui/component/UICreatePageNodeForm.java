/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.portal.webui.component;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Feb 18, 2008  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    events = {
      @EventConfig(listeners = UICreatePageNodeForm.CreatePageActionListener.class)
    }
)

public class UICreatePageNodeForm extends UIForm {
  
  public UICreatePageNodeForm() {
    addUIFormInput(new UIFormStringInput("PageId", null, null)) ;
    addUIFormInput(new UIFormStringInput("PageNodeName", null, null)) ;
  }
  
  public static class CreatePageActionListener extends EventListener<UICreatePageNodeForm> {

    public void execute(Event<UICreatePageNodeForm> event) throws Exception {
      UICreatePageNodeForm uiForm = event.getSource() ;
      PortalRequestContext pContext = Util.getPortalRequestContext()  ;
      UIPortal uiPortal = Util.getUIPortal();
      
      //create PageNode
      String givenName = uiForm.getUIStringInput("PageNodeName").getValue() ;
      String pageId = uiForm.getUIStringInput("PageId").getValue() ;
      Map<String, String[]> map = new HashMap<String, String[]>()  ;
      map.put("nameA", new String[] {"valueA1", "valueA2"}) ;
      map.put("nameB", new String[] {"valueB1", "valueB2"}) ;
      //PageNavigation userNavi = uiPortal.getPageNavigation(PortalConfig.USER_TYPE + "::" + pContext.getRemoteUser()) ; 
      //PageUtils.createNodeFromPageTemplate(givenName, givenName, pageId, map, userNavi) ;
      
      //Hide Popup
      UIPopupWindow uiPopup = uiForm.getParent() ;
      uiPopup.setShow(false) ;
      
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      //Update UIWorkspace to refresh PageNavigation in navigation bar 
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      pContext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
      pContext.setFullRender(true);
    }
  }
}
