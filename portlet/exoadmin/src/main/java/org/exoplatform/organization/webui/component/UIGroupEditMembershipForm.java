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
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * Dec 2, 2008          
 */

@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    events = {
        @EventConfig(listeners = UIGroupEditMembershipForm.SaveActionListener.class),
        @EventConfig(listeners = UIGroupEditMembershipForm.CancelActionListener.class)
      }
)
public class UIGroupEditMembershipForm extends UIForm {

  private List<SelectItemOption<String>> listOption = new ArrayList<SelectItemOption<String>>();
  private final static String USER_NAME = "username";
  private final static String MEMBER_SHIP = "membership";
  private Membership membership;
  private Group group;
  
  public UIGroupEditMembershipForm() throws Exception {
    addUIFormInput(new UIFormStringInput(USER_NAME, USER_NAME, null).setEditable(false));
    addUIFormInput(new UIFormSelectBox(MEMBER_SHIP,MEMBER_SHIP, listOption).setSize(1));
  }
  
  public void setValue(Membership memberShip, Group selectedGroup) throws Exception {
    this.membership = memberShip;
    this.group = selectedGroup;
    getUIStringInput(USER_NAME).setValue(memberShip.getUserName());
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    List<?> collection = (List<?>) service.getMembershipTypeHandler().findMembershipTypes();
    for(Object ele : collection){
      MembershipType mt = (MembershipType) ele;
      SelectItemOption<String> option = new SelectItemOption<String>(mt.getName(), mt.getName(), mt.getDescription());
      if(mt.getName().equals(memberShip.getMembershipType())) option.setSelected(true);
      listOption.add(option);
    }
  }
  
  static public class SaveActionListener extends EventListener<UIGroupEditMembershipForm> {
    public void execute(Event<UIGroupEditMembershipForm> event) throws Exception {
      UIGroupEditMembershipForm uiForm = event.getSource();
      UIApplication uiApp = event.getRequestContext().getUIApplication() ;
      OrganizationService service = uiForm.getApplicationComponent(OrganizationService.class);
      User user = service.getUserHandler().findUserByName(uiForm.membership.getUserName()) ;
      MembershipHandler memberShipHandler = service.getMembershipHandler();
      String memberShipType = uiForm.getUIFormSelectBox(MEMBER_SHIP).getValue();
      MembershipType membershipType = service.getMembershipTypeHandler().findMembershipType(memberShipType);
      try {
        memberShipHandler.removeMembership(uiForm.membership.getId(), true);
        memberShipHandler.linkMembership(user,uiForm.group,membershipType,true);
      } catch (Exception e) {
        // membership removed
        uiApp.addMessage(new ApplicationMessage("UIGroupEditMembershipForm.msg.membership-delete", null)) ;
      }
      UIPopupWindow uiPopup = uiForm.getParent();
      uiPopup.setUIComponent(null);
      uiPopup.setShow(false);
    }
  }
  
  static public class CancelActionListener extends EventListener<UIGroupEditMembershipForm> {
    public void execute(Event<UIGroupEditMembershipForm> event) throws Exception {
      UIGroupEditMembershipForm uiForm = event.getSource();
      UIPopupWindow uiPopup = uiForm.getParent();
      uiPopup.setUIComponent(null);
      uiPopup.setShow(false);
    }
  }
}
