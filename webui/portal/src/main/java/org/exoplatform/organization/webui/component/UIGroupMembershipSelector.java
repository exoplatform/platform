/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
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
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jun 27, 2006
 */
@ComponentConfigs({ 
  @ComponentConfig(
      template = "system:/groovy/organization/webui/component/UIGroupMembershipSelector.gtmpl",
      events = {
          @EventConfig(phase = Phase.DECODE, listeners = UIGroupMembershipSelector.ChangeNodeActionListener.class),
          @EventConfig(phase = Phase.DECODE, listeners = UIGroupMembershipSelector.SelectMembershipActionListener.class),
          @EventConfig(phase = Phase.DECODE, listeners = UIGroupMembershipSelector.SelectPathActionListener.class)  
      }  
  ),
  @ComponentConfig(
      type = UITree.class, id = "UITreeGroupSelector",
      template = "system:/groovy/webui/component/UITree.gtmpl",
      events = @EventConfig(phase = Phase.DECODE, listeners = UITree.ChangeNodeActionListener.class)
  ),
  @ComponentConfig(
      type = UIBreadcumbs.class, id = "BreadcumbGroupSelector",
      template = "system:/groovy/webui/component/UIBreadcumbs.gtmpl",
      events = @EventConfig(phase = Phase.DECODE, listeners = UIBreadcumbs.SelectPathActionListener.class)
  )
})
public class UIGroupMembershipSelector extends UIContainer {

  private Group selectGroup_ ;
  private List<String> listMemberhip;
 
  public UIGroupMembershipSelector() throws Exception {
    UIBreadcumbs uiBreadcumbs = addChild(UIBreadcumbs.class, "BreadcumbGroupSelector", "BreadcumbGroupSelector") ;
    UITree tree = addChild(UITree.class, "UITreeGroupSelector", "TreeGroupSelector");
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    Collection sibblingsGroup = service.getGroupHandler().findGroups(null);
    
    Collection collection = service.getMembershipTypeHandler().findMembershipTypes();
    listMemberhip  = new ArrayList<String>(5);
    for(Object obj : collection){
      listMemberhip.add(((MembershipType)obj).getName());
    }
    listMemberhip.add("any"); 
    
    tree.setSibbling((List)sibblingsGroup);
    tree.setIcon("Icon GroupAdminIcon");
    tree.setSelectedIcon("Icon PortalIcon");
    tree.setBeanIdField("id");
    tree.setBeanLabelField("groupName");
    uiBreadcumbs.setBreadcumbsStyle("UIExplorerHistoryPath") ;    
  }
  
  /*public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    UIForm uiForm  = getAncestorOfType(UIForm.class);
    String action =  null;
    if(uiForm != null){
      action =  uiForm.getSubmitAction();
    }else {
      action = context.getRequestParameter(UIForm.ACTION);
    }    
    if(action == null)  return;    
    
    String componentId =  context.getRequestParameter("selectorId") ;
    System.out.println("\n\n\n\n == > tai day ta co "+componentId +"\n\n\n");
    if(componentId != null && componentId.trim().length() > 0 && componentId.equals(getId())) {
      Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;   
      if(event != null) event.broadcast()  ;  
    }
  }*/

  public Group getCurrentGroup() { return selectGroup_ ; }

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

  public List<String> getListMemberhip() { return listMemberhip; }
  
  public String event(String name, String beanId) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    if(uiForm != null) return uiForm.event(name, getId(), beanId);
    return super.event(name, beanId);
  }

  static  public class ChangeNodeActionListener extends EventListener<UITree> {   
    public void execute(Event<UITree> event) throws Exception {     
      String groupId = event.getRequestContext().getRequestParameter(OBJECTID)  ;
      UITree uiTree = event.getSource();
      UIGroupMembershipSelector uiSelector = uiTree.getParent() ;    
      uiSelector.changeGroup(groupId) ;
      UIComponent uiParent = uiSelector.<UIComponent>getParent().getParent();
      uiParent.setRenderSibbling(uiParent.getClass()); 
      uiParent.broadcast(event, Event.Phase.PROCESS) ;      
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

  static  public class SelectMembershipActionListener extends EventListener<UIGroupMembershipSelector> {   
    public void execute(Event<UIGroupMembershipSelector> event) throws Exception {
      UIGroupMembershipSelector uiSelector = event.getSource();
      UIComponent uiParent = uiSelector.<UIComponent>getParent().getParent();
      uiParent.setRenderSibbling(uiParent.getClass());      
      if(uiSelector.getCurrentGroup() == null) return;
      uiParent.broadcast(event, event.getExecutionPhase()) ;
      
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

  static  public class SelectPathActionListener extends EventListener<UIBreadcumbs> {
    public void execute(Event<UIBreadcumbs> event) throws Exception {
      UIBreadcumbs uiBreadcumbs = event.getSource();
      UIGroupMembershipSelector uiSelector = uiBreadcumbs.getParent() ; 
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
