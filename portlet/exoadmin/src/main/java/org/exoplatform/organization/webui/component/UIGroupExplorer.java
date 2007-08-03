/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UITree;
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
@ComponentConfig(events = @EventConfig(listeners = UIGroupExplorer.ChangeNodeActionListener.class) )
public class UIGroupExplorer extends UIContainer {
  
	private Group selectedGroup_ ;
	@SuppressWarnings("unchecked")
	private Collection sibblingsGroup_ ;
	@SuppressWarnings("unchecked")
	private Collection childrenGroup_ ;
  
	@SuppressWarnings("unchecked")
	public UIGroupExplorer() throws Exception {
	  UITree tree = addChild(UITree.class, null, "TreeGroupExplorer");
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    sibblingsGroup_ = service.getGroupHandler().findGroups(null);
    
    tree.setSibbling((List)sibblingsGroup_);
    tree.setIcon("Icon GroupAdminIcon");
    tree.setSelectedIcon("Icon PortalIcon");
    tree.setBeanIdField("id");
    //tree.setBeanLabelField("groupName");
    tree.setBeanLabelField("label");
	}
	
	@SuppressWarnings("unchecked")
	public void changeGroup(String groupId) throws Exception {	  
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    
    UIGroupManagement uiGroupManagement = this.getParent() ;
    UIBreadcumbs uiBreadcumb = uiGroupManagement.getChild(UIBreadcumbs.class);
    uiBreadcumb.setPath(getPath(null, groupId)) ;
    
    UITree uiTree = getChild(UITree.class);
    UIGroupDetail uiGroupDetail = uiGroupManagement.getChild(UIGroupDetail.class);     
    UIGroupInfo uiGroupInfo = uiGroupDetail.getChild(UIGroupInfo.class) ;    
    
    if(groupId == null){
      sibblingsGroup_ = service.getGroupHandler().findGroups(null);
      uiTree.setSibbling((List)sibblingsGroup_);
      uiTree.setSelected(null) ;
      uiTree.setChildren(null) ;
      uiTree.setParentSelected(null) ;
      selectedGroup_ = null;
      uiGroupInfo.setGroup(null); 
      return ;  
    }
    
    if(groupId != null){
      selectedGroup_ = service.getGroupHandler().findGroupById(groupId);
    } else {
      selectedGroup_ = null;
    }
    String parentGroupId = null ;
    if(selectedGroup_ != null) parentGroupId = selectedGroup_.getParentId(); 
	  Group parentGroup = null ;
	  if(parentGroupId != null)	parentGroup = service.getGroupHandler().findGroupById(parentGroupId);
    childrenGroup_ = service.getGroupHandler().findGroups(selectedGroup_); 
    sibblingsGroup_ = service.getGroupHandler().findGroups(parentGroup);  
    for(Object group: sibblingsGroup_) {
      if(((Group)group).getId().equals(selectedGroup_.getId())){
        selectedGroup_ = (Group) group;
        break;
      }
    }
    uiGroupInfo.setGroup(selectedGroup_); 
    
    uiTree.setSibbling((List)sibblingsGroup_);
    uiTree.setChildren((List)childrenGroup_);
    uiTree.setSelected(selectedGroup_);
    uiTree.setParentSelected(parentGroup);
	}
	
	public List<LocalPath> getPath(List<LocalPath> list, String id) throws Exception {
    if(list == null) list = new ArrayList<LocalPath>(5);
    if(id == null) return list;
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    Group group = service.getGroupHandler().findGroupById(id);
    if(group == null) return list;
    //list.add(0, new LocalPath(group.getId(), group.getGroupName())); 
    list.add(0, new LocalPath(group.getId(), group.getLabel()));
		getPath(list, group.getParentId());
		return list ;
	}
	
	public Group getCurrentGroup() { return selectedGroup_ ; }
  public void setCurrentGroup(Group g) { selectedGroup_ = g; }
  
  @SuppressWarnings("unchecked")
	public Collection getChildrenGroup() { return childrenGroup_ ;}
  
  @SuppressWarnings("unchecked")
	public Collection getSibblingGroups() { return sibblingsGroup_ ; }
  
  @SuppressWarnings("unchecked")
	public void setChildGroup(Collection childrendGroup) { childrenGroup_ = childrendGroup ;	}
  
  @SuppressWarnings("unused")
  public void processRender(WebuiRequestContext context) throws Exception {
    renderChildren();
  }
	
	static  public class ChangeNodeActionListener extends EventListener<UITree> {
		public void execute(Event<UITree> event) throws Exception {      
      UIGroupExplorer uiGroupExplorer = event.getSource().getParent() ;      
			String groupId = event.getRequestContext().getRequestParameter(OBJECTID)  ;
      uiGroupExplorer.changeGroup(groupId) ;
      UIGroupManagement uiGroupManagement = uiGroupExplorer.getParent();
      UIGroupDetail uiGroupDetail = uiGroupManagement.getChild(UIGroupDetail.class);
      uiGroupDetail.getChild(UIGroupForm.class).setGroup(null) ;
      uiGroupDetail.setRenderedChild(UIGroupInfo.class);
		}
	}
  
}
