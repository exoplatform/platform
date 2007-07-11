/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 1:55:22 PM 
 */
@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
  events = @EventConfig(listeners = UIGroupMembershipForm.SaveActionListener.class)
)
public class UIGroupMembershipForm extends UIForm {  
    
  List<SelectItemOption<String>> listOption = new ArrayList<SelectItemOption<String>>();
  
  @SuppressWarnings("unchecked")
  public UIGroupMembershipForm() throws Exception {
    listOption.clear(); 
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    List collection = (List) service.getMembershipTypeHandler().findMembershipTypes();
    for(Object ele : collection){
      MembershipType mt = (MembershipType) ele;
      listOption.add(new SelectItemOption<String>(mt.getName(), mt.getName(), mt.getDescription()));
    }
    
    addUIFormInput(new UIFormStringInput("username", "username", null).
                   addValidator(EmptyFieldValidator.class));
    addUIFormInput(new UIFormSelectBox("membership","membership", listOption).setSize(1));
  } 
  
  public String getUserName() { return getUIStringInput("username").getValue(); }
  public String getMembership() { return getUIStringInput("membership").getValue(); }
  
  @SuppressWarnings("unchecked")
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
        Object[]  args = {"UserName", username } ;
        uiApp.addMessage(new ApplicationMessage("UIGroupMembershipForm.msg.user-doesn't-exist", args)) ;
        return ;
      }
      Group group = userInGroup.getSelectedGroup() ;
      if(group == null) {
        uiApp.addMessage(new ApplicationMessage("UIGroupMembershipForm.msg.group-doesn't-select", null)) ;
        return ;
      }
      MembershipType membershipType = 
        service.getMembershipTypeHandler().findMembershipType(uiForm.getMembership());
      //TODO: Tung.Pham added
      //-----------------------------------------
      Membership membership = service.getMembershipHandler().findMembershipByUserGroupAndType(username, group.getId(), membershipType.getName());
      if(membership != null){
        uiApp.addMessage(new ApplicationMessage("UIGroupMembershipForm.msg.membership-exist", new String[]{group.getGroupName()})) ;
        return ;
      }
      //-----------------------------------------
      service.getMembershipHandler().linkMembership(user,group,membershipType,true);               
      userInGroup.setValues(); 
      uiForm.reset();
    }
  }

}

