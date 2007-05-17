/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIBreadcumbs;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.component.UITree;
import org.exoplatform.webui.component.UIBreadcumbs.LocalPath;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

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
      template = "system:/groovy/webui/component/UITree.gtmpl",
      events = @EventConfig(listeners = UITree.ChangeNodeActionListener.class)
  ),
  @ComponentConfig(
      type = UIBreadcumbs.class, id = "BreadcumbGroupSelector",
      template = "system:/groovy/webui/component/UIBreadcumbs.gtmpl",
      events = @EventConfig(listeners = UIBreadcumbs.SelectPathActionListener.class)
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
    tree.setIcon("Icon GroupAdminIcon");
    tree.setSelectedIcon("Icon PortalIcon");
    tree.setBeanIdField("id");
    tree.setBeanLabelField("groupName");
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
  
  public void processDecode(WebuiRequestContext context) throws Exception {
    super.processDecode(context);
    UIForm uiForm  = getAncestorOfType(UIForm.class);
    String action = uiForm.getSubmitAction(); 
    if(action == null) return;    
    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;
    if(event != null) event.broadcast() ;
  }
  
  public String event(String name) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    if(uiForm != null) return uiForm.event(name, getId());
    return super.event(name);
  }
  
  static  public class ChangeNodeActionListener extends EventListener<UIGroupSelector> {   
    public void execute(Event<UIGroupSelector> event) throws Exception {
      String groupId = event.getRequestContext().getRequestParameter(OBJECTID)  ;    
      UIGroupSelector uiSelector = event.getSource() ;
      uiSelector.changeGroup(groupId);
      
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
      UIComponent uiParent = uiSelector.<UIComponent>getParent().getParent();
      uiParent.setRenderSibbling(uiParent.getClass());      
      uiParent.broadcast(event, Event.Phase.PROCESS) ;
      
      UIPopupWindow uiPopup = uiSelector.getParent();
      uiPopup.setShow(false);
      UIForm uiForm = event.getSource().getAncestorOfType(UIForm.class) ;
      if(uiForm != null) {
        event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()); 
      }else{
        event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);
      }
           
    }
  }
 
}
