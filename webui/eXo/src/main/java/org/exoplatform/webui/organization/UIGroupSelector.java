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
package org.exoplatform.webui.organization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.core.UIBreadcumbs.LocalPath;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 8, 2007  
 */

@ComponentConfigs({ 
  @ComponentConfig(
      template = "system:/groovy/organization/webui/component/UIGroupSelector.gtmpl",
      events = {
          @EventConfig(phase = Phase.DECODE, listeners = UIGroupSelector.ChangeNodeActionListener.class),
          @EventConfig(phase = Phase.DECODE, listeners = UIGroupSelector.SelectPathActionListener.class),  
          @EventConfig(phase = Phase.DECODE, listeners = UIGroupSelector.SelectGroupActionListener.class)
      }  
  ),
  @ComponentConfig(
      type = UITree.class, id = "UITreeGroupSelector",
      template = "system:/groovy/webui/core/UITree.gtmpl",
      events = @EventConfig(listeners = UITree.ChangeNodeActionListener.class, phase = Phase.DECODE)
  ),
  @ComponentConfig(
      type = UIBreadcumbs.class, id = "BreadcumbGroupSelector",
      template = "system:/groovy/webui/core/UIBreadcumbs.gtmpl",
      events = @EventConfig(listeners = UIBreadcumbs.SelectPathActionListener.class, phase = Phase.DECODE)
  )
})

public class UIGroupSelector extends UIContainer {
  
  private Group selectedGroup;
  
  public UIGroupSelector() throws Exception {
    UIBreadcumbs uiBreadcumbs = addChild(UIBreadcumbs.class, "BreadcumbGroupSelector", "BreadcumbGroupSelector") ;
    UITree tree = addChild(UITree.class, "UITreeGroupSelector", "TreeGroupSelector");
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    Collection sibblingsGroup = service.getGroupHandler().findGroups(null);
    
    tree.setSibbling((List)sibblingsGroup);
    tree.setIcon("GroupAdminIcon");
    tree.setSelectedIcon("PortalIcon");
    tree.setBeanIdField("id");
    //tree.setBeanLabelField("groupName");
    tree.setBeanLabelField("label");
    uiBreadcumbs.setBreadcumbsStyle("UIExplorerHistoryPath") ;
  }

  public Group getSelectedGroup() { return selectedGroup; }
  public void setSelectedGroup(Group group) { selectedGroup = group; }
  
  public void changeGroup(String groupId) throws Exception {    
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;    
    UIBreadcumbs uiBreadcumb = getChild(UIBreadcumbs.class);
    uiBreadcumb.setPath(getPath(null, groupId)) ;

    UITree tree = getChild(UITree.class);
    Collection sibblingGroup;
    
    if(groupId == null) {      
      sibblingGroup = service.getGroupHandler().findGroups(null);
      tree.setSibbling((List)sibblingGroup);
      tree.setChildren(null);
      tree.setSelected(null);
      selectedGroup = null;
      return;
    }

    selectedGroup = service.getGroupHandler().findGroupById(groupId);
    String parentGroupId = null;
    if(selectedGroup != null) parentGroupId = selectedGroup.getParentId();
    Group parentGroup = null ;
    if(parentGroupId != null) parentGroup = service.getGroupHandler().findGroupById(parentGroupId);

    Collection childrenGroup = service.getGroupHandler().findGroups(selectedGroup);
    sibblingGroup = service.getGroupHandler().findGroups(parentGroup);

    tree.setSibbling((List)sibblingGroup);
    tree.setChildren((List)childrenGroup);
    tree.setSelected(selectedGroup);
    tree.setParentSelected(parentGroup);
  }
  
  private List<LocalPath> getPath(List<LocalPath> list, String id) throws Exception {
    if(list == null) list = new ArrayList<LocalPath>(5);
    if(id == null) return list;
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    Group group = service.getGroupHandler().findGroupById(id);
    if(group == null) return list;
    list.add(0, new LocalPath(group.getId(), group.getGroupName())); 
    getPath(list, group.getParentId());
    return list ;
  }
  
//  public void processDecode(WebuiRequestContext context) throws Exception {
//    super.processDecode(context);
//    UIForm uiForm  = getAncestorOfType(UIForm.class);
//    String action = uiForm.getSubmitAction(); 
//    if(action == null) return;    
//    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;
//    if(event != null) event.broadcast() ;
//  }
  
  public String event(String name) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    if(uiForm != null) return uiForm.event(name, getId(), "");
    return super.event(name);
  }
  
  static  public class ChangeNodeActionListener extends EventListener<UITree> {   
    public void execute(Event<UITree> event) throws Exception {
      String groupId = event.getRequestContext().getRequestParameter(OBJECTID)  ;   
      UIGroupSelector uiSelector = null;
      UITree uicom = event.getSource() ;
      uiSelector = uicom.getParent();
      uiSelector.changeGroup(groupId);
      
      UIForm uiForm = uiSelector.getAncestorOfType(UIForm.class) ;
      UIPopupWindow uiPopup = uiSelector.getParent();
      uiPopup.setShow(true);
      if(uiForm != null) {
        event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()); 
      }else{
        event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);
      }
    }
  }
  
  static  public class SelectPathActionListener extends EventListener<UIGroupSelector> {
    public void execute(Event<UIGroupSelector> event) throws Exception {
      UIGroupSelector uiSelector = event.getSource();
      UIBreadcumbs uiBreadcumbs = uiSelector.getChild(UIBreadcumbs.class);
      String objectId =  event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiBreadcumbs.setSelectPath(objectId);     
      String selectGroupId = uiBreadcumbs.getSelectLocalPath().getId() ;
      uiSelector.changeGroup(selectGroupId) ;
      
      UIForm uiForm = event.getSource().getAncestorOfType(UIForm.class) ;
      UIPopupWindow uiPopup = uiSelector.getParent();
      uiPopup.setShow(true);
      if(uiForm != null) {
        event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()); 
      }else{
        event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);
      }
    }
  }
  
  static  public class SelectGroupActionListener extends EventListener<UIGroupSelector> {   
    public void execute(Event<UIGroupSelector> event) throws Exception {
      UIGroupSelector uiSelector = event.getSource();
      
      UIPopupWindow uiPopup = uiSelector.getParent();
      uiPopup.setShow(false);
      UIForm uiForm = event.getSource().getAncestorOfType(UIForm.class) ;
      if(uiForm != null) {
        event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()); 
        uiForm.broadcast(event, event.getExecutionPhase()) ;
      }else{
        event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);
        UIComponent uiParent = uiPopup.getParent();
        uiParent.broadcast(event, event.getExecutionPhase()) ;
      }
           
    }
  }
 
}
