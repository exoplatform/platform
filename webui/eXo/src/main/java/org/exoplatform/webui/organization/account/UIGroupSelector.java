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
package org.exoplatform.webui.organization.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
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
 * Author : dang.tung
 *          tungcnw@gmail.com
 * Nov 22, 2008
 */
@ComponentConfigs({ 
  @ComponentConfig(
      template = "system:/groovy/webui/organization/account/UIGroupSelector.gtmpl",
      events = {
          @EventConfig(phase = Phase.DECODE, listeners = UIGroupSelector.ChangeNodeActionListener.class),
          @EventConfig(phase = Phase.DECODE, listeners = UIGroupSelector.SelectGroupActionListener.class),
          @EventConfig(phase = Phase.DECODE, listeners = UIGroupSelector.SelectPathActionListener.class)  
      }  
  ),
  @ComponentConfig(
      type = UITree.class, id = "UITreeGroupSelector",
      template = "system:/groovy/webui/core/UITree.gtmpl",
      events = @EventConfig(phase = Phase.DECODE, listeners = UITree.ChangeNodeActionListener.class)
  ),
  @ComponentConfig(
      type = UIBreadcumbs.class, id = "BreadcumbGroupSelector",
      template = "system:/groovy/webui/core/UIBreadcumbs.gtmpl",
      events = @EventConfig(phase = Phase.DECODE, listeners = UIBreadcumbs.SelectPathActionListener.class)
  )
})
public class UIGroupSelector extends UIContainer {

  private Group selectGroup_ ;
 
  public UIGroupSelector() throws Exception {
    UIBreadcumbs uiBreadcumbs = addChild(UIBreadcumbs.class, "BreadcumbGroupSelector", "BreadcumbGroupSelector") ;
    UITree tree = addChild(UITree.class, "UITreeGroupSelector", "TreeGroupSelector");
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    Collection<?> sibblingsGroup = service.getGroupHandler().findGroups(null); 
    
    tree.setSibbling((List)sibblingsGroup);
    tree.setIcon("GroupAdminIcon");
    tree.setSelectedIcon("PortalIcon");
    tree.setBeanIdField("id");
    tree.setBeanLabelField("groupName");
    uiBreadcumbs.setBreadcumbsStyle("UIExplorerHistoryPath") ;
  }

  public Group getCurrentGroup() { return selectGroup_ ; }

  @SuppressWarnings("unchecked")
  public void changeGroup(String groupId) throws Exception {    
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;    
    UIBreadcumbs uiBreadcumb = getChild(UIBreadcumbs.class);
    uiBreadcumb.setPath(getPath(null, groupId)) ;

    UITree tree = getChild(UITree.class);
    Collection<?> sibblingGroup;
    
    if(groupId == null) {      
      sibblingGroup = service.getGroupHandler().findGroups(null);
      tree.setSibbling((List)sibblingGroup);
      tree.setChildren(null);
      tree.setSelected(null);
      selectGroup_ = null;
      return;
    }

    selectGroup_ = service.getGroupHandler().findGroupById(groupId);
    String parentGroupId = null;
    if(selectGroup_ != null) parentGroupId = selectGroup_.getParentId();
    Group parentGroup = null ;
    if(parentGroupId != null) parentGroup = service.getGroupHandler().findGroupById(parentGroupId);

    Collection childrenGroup = service.getGroupHandler().findGroups(selectGroup_);
    sibblingGroup = service.getGroupHandler().findGroups(parentGroup);

    tree.setSibbling((List)sibblingGroup);
    tree.setChildren((List)childrenGroup);
    tree.setSelected(selectGroup_);
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

  @SuppressWarnings("unchecked")
  public List<String> getListGroup() throws Exception { 
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    List<String> listGroup = new ArrayList<String>();
    if(getCurrentGroup() == null) return null;
    Collection<Object> groups = service.getGroupHandler().findGroups(getCurrentGroup());
    if(groups.size() > 0) {
      for (Object child : groups) {
        Group childGroup = (Group)child;
        listGroup.add(childGroup.getId()) ;
      }
    }
    return listGroup; 
    
  }
  
  public String event(String name, String beanId) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    if(uiForm != null) return uiForm.event(name, getId(), beanId);
    return super.event(name, beanId);
  }

  static  public class ChangeNodeActionListener extends EventListener<UITree> {   
    public void execute(Event<UITree> event) throws Exception {
      UIGroupSelector uiGroupSelector = event.getSource().getAncestorOfType(UIGroupSelector.class) ;
      String groupId = event.getRequestContext().getRequestParameter(OBJECTID)  ;
      uiGroupSelector.changeGroup(groupId);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiGroupSelector) ;
    }
  }

  static  public class SelectGroupActionListener extends EventListener<UIGroupSelector> {   
    public void execute(Event<UIGroupSelector> event) throws Exception {
      UIGroupSelector uiSelector = event.getSource();
      UIComponent uiPermission = uiSelector.<UIComponent>getParent().getParent();
      //uiPermission.setRenderSibbling(uiPermission.getClass());
      WebuiRequestContext pcontext = event.getRequestContext();
      
      UIPopupWindow uiPopup = uiSelector.getParent();
      UIForm uiForm = event.getSource().getAncestorOfType(UIForm.class) ;
      if(uiForm != null) {
        event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()); 
      }else{
        event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);
      }
      if(uiSelector.getCurrentGroup() == null) {
        UIApplication uiApp = pcontext.getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIGroupMembershipSelector.msg.selectGroup", null)) ;
        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        uiPopup.setShow(true) ;
        return ;
      } 
      
      uiPermission.broadcast(event, event.getExecutionPhase());
      uiPopup.setShow(false);
      
    }
  }

  static  public class SelectPathActionListener extends EventListener<UIBreadcumbs> {
    public void execute(Event<UIBreadcumbs> event) throws Exception {
      UIBreadcumbs uiBreadcumbs = event.getSource();
      UIGroupSelector uiSelector = uiBreadcumbs.getParent() ; 
      String objectId =  event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiBreadcumbs.setSelectPath(objectId);     
      String selectGroupId = uiBreadcumbs.getSelectLocalPath().getId() ;
      uiSelector.changeGroup(selectGroupId) ;
      
      UIPopupWindow uiPopup = uiSelector.getParent();
      uiPopup.setShow(true);
      
      UIForm uiForm = event.getSource().getAncestorOfType(UIForm.class) ;
      if(uiForm != null) {
        event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()); 
      }else{
        event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);
      }
    }
  }
 
}
