/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.List;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIBreadcumbs;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIFormPopupWindow;
import org.exoplatform.webui.component.UIPopupWindow;

import org.exoplatform.webui.component.UIBreadcumbs.LocalPath;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.organization.webui.component.UIGroupManagement.*;
import org.exoplatform.portal.component.customization.UIPopupDialog;

/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(
  template = "app:/groovy/organization/webui/component/UIGroupManagement.gtmpl",
  events = {
    @EventConfig(listeners = AddGroupActionListener.class),
    @EventConfig(listeners = DeleteGroupActionListener.class, confirm = "UIGroupManagement.deleteGroup"),
    @EventConfig(listeners = SelectPathActionListener.class),
    @EventConfig(listeners = EditGroupActionListener.class)
    
  }
)
public class UIGroupManagement extends UIContainer {

  public UIGroupManagement() throws Exception {
    
    UIBreadcumbs uiBreadcum = addChild(UIBreadcumbs.class, null, "BreadcumbsGroupManagement") ;
    addChild(UIGroupExplorer.class, null, null);
    addChild(UIGroupDetail.class, null, null) ;
    uiBreadcum.setBreadcumbsStyle("UIExplorerHistoryPath") ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context); 
    List<UIComponent> children = this.getChildren();
    for(UIComponent com: children){
      if(com instanceof UIPopupWindow) com.processRender(context);
    }
  }
    
  static  public class AddGroupActionListener extends EventListener<UIGroupManagement> {
    public void execute(Event<UIGroupManagement> event) throws Exception {
      UIGroupManagement uiGroupManagement = event.getSource() ;
      UIGroupDetail uiGroupDetail = uiGroupManagement.getChild(UIGroupDetail.class) ;
      uiGroupDetail.setRenderedChild(UIGroupForm.class) ;
      UIGroupForm uiGroupForm = uiGroupDetail.getChild(UIGroupForm.class);
      uiGroupForm.setName("AddGroup");
      uiGroupForm.setGroup(null);
    }
  }
  
  
  static  public class EditGroupActionListener extends EventListener<UIGroupManagement> {
    public void execute(Event<UIGroupManagement> event) throws Exception {
      UIGroupManagement uiGroupManagement = event.getSource() ;
      WebuiRequestContext context = event.getRequestContext() ;
      UIApplication uiApp = context.getUIApplication() ;
      
      UIGroupDetail uiGroupDetail = uiGroupManagement.getChild(UIGroupDetail.class) ;
      UIGroupExplorer uiGroupExplorer = uiGroupManagement.getChild(UIGroupExplorer.class) ;
//            
      Group currentGroup = uiGroupExplorer.getCurrentGroup();
      if (currentGroup == null) {
        uiApp.addMessage(new ApplicationMessage("UIGroupManagement.msg.Edit", null)) ;
        return;
      }
      uiGroupDetail.setRenderedChild(UIGroupForm.class) ;
      UIGroupForm uiGroupForm = uiGroupDetail.getChild(UIGroupForm.class);
      uiGroupForm.setName("EditGroup");
      uiGroupForm.setGroup(currentGroup);
    }
  }
  
  static  public class DeleteGroupActionListener extends EventListener<UIGroupManagement> {
    public void execute(Event<UIGroupManagement> event) throws Exception {
      UIGroupManagement uiGroupManagement = event.getSource() ;
      WebuiRequestContext context = event.getRequestContext() ;
      UIApplication uiApp = context.getUIApplication() ;
      UIGroupExplorer uiGroupExplorer = uiGroupManagement.getChild(UIGroupExplorer.class) ;
      Group currentGroup = uiGroupExplorer.getCurrentGroup();
      if(currentGroup == null) {
        uiApp.addMessage(new ApplicationMessage("UIGroupManagement.msg.Edit", null)) ;
        return;
      }
      String parentId = currentGroup.getParentId();
      OrganizationService service = uiGroupManagement.getApplicationComponent(OrganizationService.class) ;
      service.getGroupHandler().removeGroup(currentGroup, true);
      uiGroupExplorer.changeGroup(parentId);
    }    
  }
  
  static  public class SelectPathActionListener extends EventListener<UIBreadcumbs> {
    public void execute(Event<UIBreadcumbs> event) throws Exception {
      UIBreadcumbs uiBreadcumbs = event.getSource();
      UIGroupManagement uiGroupManagement = uiBreadcumbs.getAncestorOfType(UIGroupManagement.class);
      UIGroupExplorer uiGroupExplorer = uiGroupManagement.getChild(UIGroupExplorer.class);
      LocalPath localPath = uiBreadcumbs.getSelectLocalPath() ;
      if(localPath != null) {
        String selectGroupId = uiBreadcumbs.getSelectLocalPath().getId() ;
        uiGroupExplorer.changeGroup(selectGroupId) ;
      } else {
        uiGroupExplorer.changeGroup(null) ;
      }
      
    }
  }
      
}
