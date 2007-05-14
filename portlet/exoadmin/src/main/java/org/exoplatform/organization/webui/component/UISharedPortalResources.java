/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.organization.webui.component.UIGroupMembershipForm.SaveActionListener;
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
  template = "system:/groovy/webui/component/UIFormWithTitle.gtmpl",
  events = @EventConfig(listeners = SaveActionListener.class)
)

public class UISharedPortalResources extends UIForm {
  List<SelectItemOption<String>> listOption = new ArrayList<SelectItemOption<String>>();
  @SuppressWarnings("unchecked")
  public UISharedPortalResources() throws Exception {
    for(int i = 1; i <= 10; i++ ) {
      listOption.add(new SelectItemOption(new Integer(i).toString()));
    }
    
    addUIFormInput(new UIFormStringInput("portalResources", "portalResources", null).
                   addValidator(EmptyFieldValidator.class));
    addUIFormInput(new UIFormSelectBox("priority","priority", listOption));
  }
  
  public String getPortalResources() { return getUIStringInput("portalResources").getValue(); }
  public String getPriority() { return getUIStringInput("priority").getValue(); }
  
  static  public class SaveActionListener extends EventListener<UISharedPortalResources> {
    public void execute(Event<UISharedPortalResources> event) throws Exception {
      // get UserPortalConfigService
      // get portalresource as portalName
      // get selected group id as groupId 
      // get all page by, review UIPageSelect, query set ownerType as DataStore.user_type and ownerId  as portalName 
      // List<Page> list =  userPortalConfig.getPage(String portalName);
      //for(Page page : list) {
      //  page.setOnwnerType(DataStore.Group_type);
      //  page.setOwnerId(groupId);
      //  userPortalConfig.savePage(page);
      //}
      //PageNavigation nav = userPortalConfig.getPageNavigation(String portalName);
      //  nav.setOnwnerType(DataStore.Group_type);
      //  nav.setOwnerId(groupId);
      //  userPortalConfig.saveNav(nav);      
      System.out.println("\n\n\n\n\n\n\n\n UISharedPortalResources \n\n\n\n\n\n\n\n");
    }
  }
}
