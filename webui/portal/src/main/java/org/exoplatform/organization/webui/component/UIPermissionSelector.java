/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import org.exoplatform.portal.config.UserACL.Permission;
import org.exoplatform.webui.component.UIBreadcumbs;
import org.exoplatform.webui.component.UIFormPopupWindow;
import org.exoplatform.webui.component.UITree;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
import org.exoplatform.webui.event.Event.Phase;
@ComponentConfig(
  template = "system:/groovy/organization/webui/component/UIPermissionSelector.gtmpl",
  events = @EventConfig (phase = Phase.DECODE, listeners = UIPermissionSelector.SelectMembershipActionListener.class)
)
public class UIPermissionSelector extends UISelector<String> {
  
  private Permission permission_;
  
	public UIPermissionSelector() throws Exception {
    super(null, null) ;		
    
    UIFormPopupWindow uiPopup = addChild(UIFormPopupWindow.class, null, "PopupPermissionSelector");
    uiPopup.setWindowSize(540, 0);  
    
    UIGroupMembershipSelector uiMembershipSelector = createUIComponent(UIGroupMembershipSelector.class, null, "SelectEditPermission") ;
    uiPopup.setUIComponent(uiMembershipSelector);
    uiMembershipSelector.setId("PermissionSelector");
    uiMembershipSelector.getChild(UITree.class).setId("TreePermissionSelector");
    uiMembershipSelector.getChild(UIBreadcumbs.class).setId("BreadcumbsPermissionSelector");
    
    permission_ = new Permission();
  }
	
	public void configure(String iname, String bfield) {  
    setName(iname) ;
    setBindingField(bfield) ; 
  } 
  
  /*public void createPermission(String label, String exp) throws Exception {
    permission_.setName(label);
    permission_.setPermissionExpression(exp) ;
    UIGroupMembershipSelector uiSelector = findFirstComponentOfType(UIGroupMembershipSelector.class);
    uiSelector.changeGroup(permission_.getGroupId());
  }*/
  
  public UIPermissionSelector setValue(String exp){
    System.out.println("\n\n= d da cap nhat vao day "+exp+"\n\n");
    permission_.setExpression(exp);
    return this;
  }
  
  public Class<String> getTypeValue() { return String.class; }
  
//  public void processDecode(WebuiRequestContext context) throws Exception {   
//    super.processDecode(context);
//    String selectedName =  context.getRequestParameter("SelectedPermission") ;
//    if(permissions_ == null) permissions_ = new ArrayList<Permission>() ;
//    for(int i = 0; i< permissions_.size(); i++){      
//      if(permissions_.get(i).getName().equals(selectedName)) {
//        permissions_.get(i).setSelected(true);        
//      }else{
//        permissions_.get(i).setSelected(false);
//      }
//    }
//  }
//  
//  @SuppressWarnings("hiding")
//  public Permission getPermission(String name){
//    if(permissions_ == null) return null;
//    for(Permission ele : permissions_) {
//      if(ele.getName().equals(name)) return ele;
//    }
//    return null;
//  }
//  
  public Permission getPermission(){ return permission_; }
  
  public String getValue(){ return permission_.getExpression(); }
  
  void setMembership(String groupId, String membershipType){
    if(permission_ == null) return ;
    permission_.setGroupId(groupId);
    permission_.setMembership(membershipType);
    permission_.setExpression(membershipType+":"+groupId);
  }
  
}
