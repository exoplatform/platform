/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.organization.Group;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 14, 2007  
 */

@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/component/UIForm.gtmpl",
  events = @EventConfig(listeners = UISharedNavigation.SaveActionListener.class)
)
public class UISharedNavigation extends UIForm {
  
  public UISharedNavigation() throws Exception {
    addUIFormInput(new UIFormStringInput("userNavigation", null, null).addValidator(EmptyFieldValidator.class));
    
    List<SelectItemOption<String>> priorities = new ArrayList<SelectItemOption<String>>();
    for(int i = 1; i <= 10; i++ ) {
      priorities.add(new SelectItemOption<String>(new Integer(i).toString()));
    }
    addUIFormInput(new UIFormSelectBox("priority", null, priorities));
  }
  
  public String getUserNavigationName() { return getUIStringInput("userNavigation").getValue(); }
  
  public String getPriority() { return getUIFormSelectBox("priority").getValue(); }
  
  static  public class SaveActionListener extends EventListener<UISharedNavigation> {
    public void execute(Event<UISharedNavigation> event) throws Exception {
      UISharedNavigation uiForm = event.getSource() ;
      PortalRequestContext pcontext = Util.getPortalRequestContext();
      
      UIGroupInfo uiGroupInfo = uiForm.getParent();
      UIUserInGroup uiUserInGroup = uiGroupInfo.getChild(UIUserInGroup.class);
      Group selectedGroup = uiUserInGroup.getSelectedGroup();
      
      if(selectedGroup == null) {
        UIApplication uiApp = pcontext.getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UISharedNavigation.msg.notSelected", null)) ;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return ;
      } //UISharedPortalResources
      String selectedGroupId = selectedGroup.getId() ;
      String accessUser = pcontext.getRemoteUser();
      String resourceName = uiForm.getUserNavigationName();
      UserPortalConfigService pcService = uiForm.getApplicationComponent(UserPortalConfigService.class);
      
      String userNavId = PortalConfig.USER_TYPE + "::" + resourceName;
      String groupNavId = PortalConfig.GROUP_TYPE + "::" + selectedGroupId;
      PageNavigation userNav = pcService.getPageNavigation(userNavId, accessUser);
      PageNavigation groupNav = pcService.getPageNavigation(groupNavId, accessUser);
      if(groupNav == null) {
        groupNav = new PageNavigation();
        groupNav.setOwnerType(PortalConfig.GROUP_TYPE);
        groupNav.setOwnerId(selectedGroupId);
        groupNav.setAccessPermission(new String[]{selectedGroupId});

        pcService.computeModifiable(groupNav, accessUser);
        if(!groupNav.isModifiable()) return;
        groupNav.setPriority(new Integer(uiForm.getPriority()));
        groupNav.setNodes(userNav.getNodes());
        groupNav.setCreator(accessUser);
        pcService.create(groupNav);
        return ;
      } 
      if(!groupNav.isModifiable()) return;
      groupNav.setPriority(new Integer(uiForm.getPriority()));
      groupNav.setNodes(userNav.getNodes());
      groupNav.setCreator(accessUser);
      pcService.update(userNav);
    }
  }
}
