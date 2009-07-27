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
import org.exoplatform.services.organization.MembershipType;
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
			template = "system:/groovy/webui/core/UITree.gtmpl",
			events = @EventConfig(phase = Phase.DECODE, listeners = UITree.ChangeNodeActionListener.class)
	),
	@ComponentConfig(
			type = UIBreadcumbs.class, id = "BreadcumbGroupSelector",
			template = "system:/groovy/webui/core/UIBreadcumbs.gtmpl",
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
		Collection<?> sibblingsGroup = service.getGroupHandler().findGroups(null);

		Collection<?> collection = service.getMembershipTypeHandler().findMembershipTypes();
		listMemberhip  = new ArrayList<String>(5);
		for(Object obj : collection){
			listMemberhip.add(((MembershipType)obj).getName());
		}
		listMemberhip.add("*");

		tree.setSibbling((List)sibblingsGroup);
		tree.setIcon("GroupAdminIcon");
		tree.setSelectedIcon("PortalIcon");
		tree.setBeanIdField("id");
		//tree.setBeanLabelField("groupName");
		tree.setBeanLabelField("label");
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

	public List<String> getListMemberhip() { return listMemberhip; }

	public String event(String name, String beanId) throws Exception {
		UIForm uiForm = getAncestorOfType(UIForm.class) ;
		if(uiForm != null) return uiForm.event(name, getId(), beanId);
		return super.event(name, beanId);
	}

	static  public class ChangeNodeActionListener extends EventListener<UIComponent> {   
		public void execute(Event<UIComponent> event) throws Exception {
			String groupId = event.getRequestContext().getRequestParameter(OBJECTID)  ;
			UIComponent uiComp = event.getSource();
			UIGroupMembershipSelector uiSelector = uiComp.getParent();    
			uiSelector.changeGroup(groupId);
			UIComponent uiPermission = uiSelector.<UIComponent>getParent().getParent();
			uiPermission.setRenderSibbling(uiPermission.getClass());
			uiPermission.broadcast(event, Event.Phase.PROCESS);
			UIPopupWindow uiPopup = uiSelector.getParent();
			uiPopup.setShow(true); 
			event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);//TODO: Update relevant tab panes
			/*
      UIForm uiForm = event.getSource().getAncestorOfType(UIForm.class) ;
      if(uiForm != null) {
        event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()); 
      }else{
        event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);
      }
			 */
		}
	}

	static  public class SelectMembershipActionListener extends EventListener<UIGroupMembershipSelector> {   
		public void execute(Event<UIGroupMembershipSelector> event) throws Exception {
			UIGroupMembershipSelector uiSelector = event.getSource();
			UIComponent uiPermission = uiSelector.<UIComponent>getParent().getParent();
			uiPermission.setRenderSibbling(uiPermission.getClass());
			WebuiRequestContext pcontext = event.getRequestContext();

			UIPopupWindow uiPopup = uiSelector.getParent();
			//TODO: Tung.Pham modified
			//-----------------------------------
			//UIForm uiForm = event.getSource().getAncestorOfType(UIForm.class);//TODO: Remove duplicated call to event.getSource()
			UIForm uiForm=uiSelector.getAncestorOfType(UIForm.class);
			//-----------------------------------

			//TODO: by Minh Hoang TO, retrieve the UIPermissionSelector and update this component
			//UIPermissionSelector pSelector=uiPopup.getAncestorOfType(UIPermissionSelector.class);
			//if(pSelector!=null){
			//	event.getRequestContext().addUIComponentToUpdateByAjax(pSelector);
			//}
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPermission);
			
			if(uiSelector.getCurrentGroup() == null) {
				UIApplication uiApp = pcontext.getUIApplication() ;
				uiApp.addMessage(new ApplicationMessage("UIGroupMembershipSelector.msg.selectGroup", null)) ;
				pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
				uiPopup.setShow(true) ;
				return ;
			} 

			uiPermission.broadcast(event, event.getExecutionPhase());
			uiPopup.setShow(false);

//			UIForm uiForm = event.getSource().getAncestorOfType(UIForm.class) ;
//			if(uiForm != null) {
//			event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()); 
//			}else{
//			event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);
//			}
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

			event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);//TODO: Update relevant tab panes
			/*
      UIForm uiForm = event.getSource().getAncestorOfType(UIForm.class) ;
      if(uiForm != null) {
        event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()); 
      }else{
        event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);
      }
			 */
		}
	}

}
