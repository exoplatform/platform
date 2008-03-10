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

import org.exoplatform.portal.config.UserACL.Permission;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputContainer;
import org.exoplatform.webui.form.UIFormPopupWindow;
import org.exoplatform.webui.form.validator.Validator;
@ComponentConfig(
  template = "system:/groovy/organization/webui/component/UIPermissionSelector.gtmpl",
  events = {
      @EventConfig (phase = Phase.DECODE, listeners = UIPermissionSelector.SelectMembershipActionListener.class),
      @EventConfig (phase = Phase.DECODE, listeners = UIPermissionSelector.DeletePermissionActionListener.class)
  }
)
public class UIPermissionSelector extends UISelector<String> {
  
  private Permission permission_;
  private boolean isEditable = true;
	public UIPermissionSelector() throws Exception {
    super(null, null) ;		
    isEditable = true;
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
      uiPermissionSelector.setRendered(true);
      UIForm uiForm = uiPermissionSelector.getAncestorOfType(UIForm.class) ;
      uiForm.findFirstComponentOfType(UIListPermissionSelector.class).setRendered(false);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()) ;
    }
  }
  //TODO: Tung.Pham added
  static public class MandatoryValidator implements Validator {

    public void validate(UIFormInput uiInput) throws Exception {
      UIFormInputContainer uiInputContainer = (UIFormInputContainer) uiInput ;
      String value = (String)uiInputContainer.getValue() ; 
      if(value == null || value.trim().length() < 1) {
        String[] args =  {uiInputContainer.getBindingField()} ;
        throw new MessageException(new ApplicationMessage("MandatoryValidator.msg.empty", args, ApplicationMessage.INFO)) ;
      }
    }
    
  }
  public boolean isEditable() {
    return isEditable;
  }

  public void setEditable(boolean isEditable) {
    this.isEditable = isEditable;
  }

}
