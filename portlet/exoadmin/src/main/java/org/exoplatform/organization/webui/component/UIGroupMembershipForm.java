/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.organization.webui.component.UIGroupMembershipForm.*;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 1:55:22 PM 
 */
@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/component/UIFormWithTitle.gtmpl",
  events = @EventConfig(listeners = SaveActionListener.class)
)
public class UIGroupMembershipForm extends UIForm {  
    
  List<SelectItemOption<String>> listOption = new ArrayList<SelectItemOption<String>>();
  public UIGroupMembershipForm() throws Exception {
    listOption.clear(); 
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    List collection = (List) service.getMembershipTypeHandler().findMembershipTypes();
    for(Object ele : collection){
      MembershipType membershipType = (MembershipType) ele;
      listOption.add(new SelectItemOption<String>(
          membershipType.getName(), membershipType.getName(), membershipType.getDescription()));
    }
    
    addUIFormInput(new UIFormStringInput("username", "username", null).
                   addValidator(EmptyFieldValidator.class));
    addUIFormInput(new UIFormSelectBox("membership","membership", listOption).setSize(1));
  } 
  
  public String getUserName() { return getUIStringInput("username").getValue(); }
  public String getMembership() { return getUIStringInput("membership").getValue(); }
  
  public void removeOptionMembershipType(MembershipType membership) {
    for(SelectItemOption op : listOption) {
      if(op.getLabel().equals(membership.getName())) {
        listOption.remove(op) ;
        break ;
      }
    }
  }
  
  public void addOptionMembershipType(MembershipType membership) {
    SelectItemOption<String> option = 
      new SelectItemOption<String>(membership.getName(),membership.getName(),membership.getDescription()) ;
    listOption.add(option) ;
  }
  
  static  public class SaveActionListener extends EventListener<UIGroupMembershipForm> {
    public void execute(Event<UIGroupMembershipForm> event) throws Exception {
      UIGroupMembershipForm uiForm = event.getSource() ;
      UIUserInGroup userInGroup = uiForm.getParent() ;
      OrganizationService service = uiForm.getApplicationComponent(OrganizationService.class) ;
      UIApplication uiApp = event.getRequestContext().getUIApplication() ;
      String username = uiForm.getUserName();
      User user = service.getUserHandler().findUserByName(username) ;
      if(user==null) {
        Object[]  args = { "UserName", username } ;
        uiApp.addMessage(
            new ApplicationMessage("UIGroupMembershipForm.msg.user-doesn't-exist", args)) ;
        return ;
      }
      Group group = userInGroup.getSelectedGroup() ;
      if(group == null) {
        uiApp.addMessage(
            new ApplicationMessage("UIGroupMembershipForm.msg.group-doesn't-select", null)) ;
        return ;
      }
      MembershipType membershipType = 
        service.getMembershipTypeHandler().findMembershipType(uiForm.getMembership());
      service.getMembershipHandler().linkMembership(user,group,membershipType,true);               
      userInGroup.setValues(); 
    }
  }

}

