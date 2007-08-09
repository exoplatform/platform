/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.organization;

import org.exoplatform.portal.config.UserACL.Permission;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormPopupWindow;
@ComponentConfig(
  template = "system:/groovy/organization/webui/component/UIPermissionSelector.gtmpl",
  events = {
      @EventConfig (phase = Phase.DECODE, listeners = UIPermissionSelector.SelectMembershipActionListener.class),
      @EventConfig (phase = Phase.DECODE, listeners = UIPermissionSelector.DeletePermissionActionListener.class)
  }
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
  
  public UIPermissionSelector setValue(String exp){
    //TODO: Tung.Pham modified
    //---------------------
    //permission_.setPermissionExpression(exp);
    permission_ = new Permission() ;
    permission_.setPermissionExpression(exp);
    //---------------------
    return this;
  }
  
  public Class<String> getTypeValue() { return String.class; }
  

  public Permission getPermission(){ return permission_; }
  
  public String getValue(){ return permission_.getExpression(); }
  
  void setMembership(String groupId, String membershipType){
    if(permission_ == null) return ;
    permission_.setGroupId(groupId);
    permission_.setMembership(membershipType);
    permission_.setExpression(membershipType+":"+groupId);
  }
  
  //TODO: Tung.Pham added
  static public class DeletePermissionActionListener extends EventListener<UIPermissionSelector> {
    public void execute(Event<UIPermissionSelector> event) throws Exception {
      UIPermissionSelector uiPermissionSelector = event.getSource() ;
      uiPermissionSelector.setValue(null) ;
      UIForm uiForm = uiPermissionSelector.getAncestorOfType(UIForm.class) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()) ;
    }
  }
}
