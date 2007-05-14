/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIFormPopupWindow;
//import org.exoplatform.webui.component.UIPopupDialog;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(
  template = "system:/groovy/organization/webui/component/UIPermissionSelector.gtmpl",
  events = @EventConfig (listeners = UIPermissionSelector.SelectMembershipActionListener.class)
)
public class UIPermissionSelector extends UISelector {
  
//  private List<Permission> permissions_ ;
  
	public UIPermissionSelector() throws Exception {
    super(null, null) ;		
    UIFormPopupWindow uiPopup = addChild(UIFormPopupWindow.class, null, "PopupPermissionSelector");
    uiPopup.setWindowSize(540, 0);  
//    UIPopupDialog dialog = createUIComponent(UIPopupDialog.class, null, null);
//    dialog.setComponent(this);
//    dialog.setHanderEvent("SelectMembership");
    UIGroupMembershipSelector uiMembershipSelector = createUIComponent(UIGroupMembershipSelector.class, null, null) ;
    uiPopup.setUIComponent(uiMembershipSelector);  
  }
	
//	public void configure(String iname, String bfield, List<Permission> permissions) {  
//    setName(iname) ;
//    setBindingField(bfield) ; 
//    permissions_ = permissions;
//  } 
  
  public void createPermission(String label, String exp) throws Exception {
//    Permission permission = new Permission();
//    permission.setPermissionExpression(exp) ;
//    permission.setName(label);
//    if(permissions_ == null) permissions_ = new ArrayList<Permission>();
//    if(permissions_.size() < 1)  permission.setSelected(true);
//    permissions_.add(permission);
//    if(permission.isSelected()) {
//      UIGroupMembershipSelector uiSelector = findFirstComponentOfType(UIGroupMembershipSelector.class);
//      uiSelector.changeGroup(permission.getGroupId());
//    }
  }
  
//  public void setPermission(Permission permission){
//    if(permissions_ == null) permissions_ = new ArrayList<Permission>(5);
//    for(int i = 0; i< permissions_.size(); i++){      
//      if(!permissions_.get(i).getName().equals(permission.getName())) continue;
//      permissions_.set(i, permission);    
//      return;
//    }
//  }
  
  public void processDecode(WebuiRequestContext context) throws Exception {   
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
  }
  
//  @SuppressWarnings("hiding")
//  public Permission getPermission(String name){
//    if(permissions_ == null) return null;
//    for(Permission ele : permissions_) {
//      if(ele.getName().equals(name)) return ele;
//    }
//    return null;
//  }
//  
//  public List<Permission> getPermissions(){ return permissions_; }
//  
//  public Permission getSelectedPermission(){
//    if(permissions_ == null) return null;
//    for(Permission ele : permissions_){
//      if(ele.isSelected()) return ele;
//    }
//    return null;
//  }
//  
  void setMembership(String groupId, String membershipType){
//    Permission permission = getSelectedPermission();
//    if(permission == null) return ;
//    permission.setGroupId(groupId);
//    permission.setMembership(membershipType);
  } 
  
}
