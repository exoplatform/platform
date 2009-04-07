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
package org.exoplatform.organization.webui.component;

import java.util.Collection;
import java.util.List;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIBreadcumbs.LocalPath;
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
  template = "app:/groovy/organization/webui/component/UIGroupManagement.gtmpl",
  events = {
    @EventConfig(listeners = UIGroupManagement.AddGroupActionListener.class),
    @EventConfig(listeners = UIGroupManagement.DeleteGroupActionListener.class, confirm = "UIGroupManagement.deleteGroup"),
    @EventConfig(listeners = UIGroupManagement.SelectPathActionListener.class),
    @EventConfig(listeners = UIGroupManagement.SelectGroupMessageActionListener.class),
    @EventConfig(listeners = UIGroupManagement.EditGroupActionListener.class)
    
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
    
  static  public class SelectGroupMessageActionListener extends EventListener<UIGroupManagement> {
    public void execute(Event<UIGroupManagement> event) throws Exception {
      UIGroupManagement uiGroupManagement = event.getSource() ;
      WebuiRequestContext context = event.getRequestContext() ;
      UIApplication uiApp = context.getUIApplication() ;
      uiApp.addMessage(new ApplicationMessage("UIGroupManagement.msg.Edit", null)) ;
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
      UIGroupForm groupForm = uiGroupManagement.findFirstComponentOfType(UIGroupForm.class);
      if(groupForm.getGroup()!= null ){
        uiApp.addMessage(new ApplicationMessage("UIGroupManagement.msg.Delete", null)) ;
        return;
      }
      OrganizationService service = uiGroupManagement.getApplicationComponent(OrganizationService.class) ;
      UserACL acl = uiGroupManagement.getApplicationComponent(UserACL.class);
      List<String> mandatories = acl.getMandatoryGroups();
      if(!mandatories.isEmpty() && isMandatory(service.getGroupHandler(), currentGroup, mandatories)) {
        uiApp.addMessage(new ApplicationMessage("UIGroupManagement.msg.DeleteMandatory", null)) ;
        return;
      }
      String parentId = currentGroup.getParentId();
      service.getGroupHandler().removeGroup(currentGroup, true);
      uiGroupExplorer.changeGroup(parentId);
    }    
    
    private boolean isMandatory(GroupHandler dao, Group group, List<String> mandatories) throws Exception {
      if(mandatories.contains(group.getId())) return true;
      Collection<Group> children = dao.findGroups(group);
      for(Group g : children){
        if(isMandatory(dao, g, mandatories)) return true;
      }
      return false;
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
