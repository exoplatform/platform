/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIFormInputContainer;
import org.exoplatform.webui.component.UIFormPopupWindow;
import org.exoplatform.webui.component.UIGrid;
import org.exoplatform.webui.component.UIPopupDialog;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 7, 2007
 */

@ComponentConfig(
  template = "system:/groovy/organization/webui/component/UIAccessGroup.gtmpl"
)

public class UIAccessGroup extends UIFormInputContainer<String> { 

  private static String[] USER_BEAN_FIELD = {"groupId", "description"} ;
  private static String[] USER_ACTION = {} ;
  
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
//    uiPopup.setShow(false);
    uiPopup.setWindowSize(540, 0);
    
    UIGroupSelector uiGroupSelector = createUIComponent(UIGroupSelector.class, null, null) ;
    uiPopup.setUIComponent(uiGroupSelector);
  }
  
//  public void processRender(WebuiRequestContext context) throws Exception {
//    super.processRender(context);
//    Writer w =  context.getWriter() ;
//    w.write("<div class=\"asdf\">");
//      for(UIComponent child: getChildren()){
//        if(child instanceof UIPopupWindow){
//          child.processRender(context);
//        }
//      }
//    w.write("</div>");
//  }
  
  public void configure(String iname, String bfield) {  
    setName(iname) ;
    setBindingField(bfield) ; 
  }
}