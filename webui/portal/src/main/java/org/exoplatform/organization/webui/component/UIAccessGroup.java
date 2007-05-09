/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.component.UIFormInputContainer;
import org.exoplatform.webui.component.UIFormPopupWindow;
import org.exoplatform.webui.component.UIGrid;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 7, 2007
 */

@ComponentConfig(
  template = "system:/groovy/organization/webui/component/UIAccessGroup.gtmpl",
  events = @EventConfig(listeners = UIAccessGroup.RemoveActionListener.class)
)

public class UIAccessGroup extends UIFormInputContainer<String> { 

  private static String[] USER_BEAN_FIELD = {"groupId", "description"} ;
  private static String[] USER_ACTION = {"Remove"} ;
  
  private List<Group> groups;
  
  public UIAccessGroup() throws Exception {
    super(null, null);
    UIGrid uiGrid = addChild(UIGrid.class, null, "TableGroup") ;
    uiGrid.configure("groupId", USER_BEAN_FIELD, USER_ACTION) ;
    groups = new ArrayList<Group>();
    uiGrid.getUIPageIterator().setPageList(new ObjectPageList(groups, 10));
    
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    GroupHandler groupHandler = service.getGroupHandler();
    Group group = groupHandler.createGroupInstance();
    group.setDescription("group1");
    group.setGroupName("thuan");
    groups.add(group);
    
    UIFormPopupWindow uiPopup = addChild(UIFormPopupWindow.class, null, "UIGroupSelector");
    uiPopup.setWindowSize(540, -1);    
    UIGroupSelector uiGroupSelector = createUIComponent(UIGroupSelector.class, null, null) ;
    uiPopup.setUIComponent(uiGroupSelector);
  }
  
  public void configure(String iname, String bfield) {  
    setName(iname) ;
    setBindingField(bfield) ; 
  }
  
  static  public class RemoveActionListener extends EventListener<UIAccessGroup> {   
    public void execute(Event<UIAccessGroup> event) throws Exception {
      
    }
  }
}