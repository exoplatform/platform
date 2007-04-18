/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.SharedConfigDAO;
import org.exoplatform.portal.config.SharedNavigation;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTextAreaInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.component.validator.IdentifierValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
/**
 * Author : lxchiati  
 *          lebienthuy@gmail.com
 * Jun 19, 2006
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,    
    template =  "system:/groovy/webui/component/UIForm.gtmpl",
    events = {
      @EventConfig(phase = Phase.DECODE, listeners = UISharedNavigationForm.RemoveActionListener.class),
      @EventConfig(listeners = UISharedNavigationForm.SaveActionListener.class)
    }
)
public class UISharedNavigationForm  extends UIForm {

  private static final String NAVIGATION = "navigation";
  private static final String TEXT_DESCRIPTION = "description";
  private static final String SELECT_MEMBERSHIP = "membership";

  private static String SELECT_PRIORITY = "priority";

  private SharedNavigation sharedNavigation_;

  @SuppressWarnings("unchecked")
  public UISharedNavigationForm() throws Exception {
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;    
    List<MembershipType> memberships = (List<MembershipType>)service.getMembershipTypeHandler().findMembershipTypes();  
    List<SelectItemOption<String>> listMembership = new ArrayList<SelectItemOption<String>>() ;
    for(int i = 0; i < memberships.size(); i ++){
      String membershipName  = memberships.get(i).getName();
      listMembership.add(new SelectItemOption<String>(membershipName, membershipName));
    }

    UIFormSelectBox uiSelectBoxMemberShip = new UIFormSelectBox(SELECT_MEMBERSHIP, SELECT_MEMBERSHIP, listMembership) ;
    addUIFormInput(uiSelectBoxMemberShip);
    addUIFormInput(new UIFormStringInput(NAVIGATION, NAVIGATION, null).
                   addValidator(EmptyFieldValidator.class).
                   addValidator(IdentifierValidator.class)) ;
    
    List<SelectItemOption<String>> listPriority = new ArrayList<SelectItemOption<String>>() ;
    for(int i = 1; i< 11; i++ ) {
      String priority = new Integer(i).toString();
      listPriority.add(new SelectItemOption<String>(priority, priority));
    }
    UIFormSelectBox uiSelectBoxPriority = new UIFormSelectBox(SELECT_PRIORITY, null, listPriority) ;
    uiSelectBoxPriority.setValue("5");
    addUIFormInput(uiSelectBoxPriority);

    addUIFormInput(new UIFormTextAreaInput(TEXT_DESCRIPTION,TEXT_DESCRIPTION, null));   
  }

  public void setValues(String groupId) throws Exception {    
    reset();
    if(groupId == null) return;
    SharedConfigDAO configService = getApplicationComponent(SharedConfigDAO.class);    
    sharedNavigation_ = configService.getSharedNavigation(groupId);    
    if (sharedNavigation_ == null){
      sharedNavigation_ = new SharedNavigation();
      sharedNavigation_.setGroupId(groupId);
    }
    invokeGetBindingBean(sharedNavigation_);
  }

  public SharedNavigation getSharedNavigation() { return sharedNavigation_; }

  static public class SaveActionListener  extends EventListener<UISharedNavigationForm> {
    public void execute(Event<UISharedNavigationForm> event) throws Exception {
      UISharedNavigationForm uiForm  = event.getSource();      
      SharedNavigation sharedNavigation = uiForm.getSharedNavigation();
      if(sharedNavigation == null) return;
      uiForm.invokeSetBindingBean(sharedNavigation);
      uiForm.<UIContainer>getParent().setRenderedChild(UISharedNavigationForm.class);
      
      OrganizationService service = uiForm.getApplicationComponent(OrganizationService.class);
      User user = service.getUserHandler().findUserByName(sharedNavigation.getNavigation());
      if(user == null){
        UIApplication uiApp = uiForm.getAncestorOfType(UIApplication.class) ;
        uiApp.addMessage(new ApplicationMessage("UISharedPortalForm.msg.user-nonexist", 
                            new String[]{sharedNavigation.getNavigation()}, ApplicationMessage.ERROR));
        return;
      }  

      UIFormSelectBox uiSelectPriority  = uiForm.getUIInput(SELECT_PRIORITY);
      sharedNavigation.setPriority(Integer.parseInt(uiSelectPriority.getValue()));

      SharedConfigDAO configService = uiForm.getApplicationComponent(SharedConfigDAO.class);
      configService.addSharedNavigation(sharedNavigation);
    }
  }

  static public class RemoveActionListener  extends EventListener<UISharedNavigationForm> {
    public void execute(Event<UISharedNavigationForm> event) throws Exception {
      UISharedNavigationForm uiForm  = event.getSource();      
      SharedNavigation sharedNavigation = uiForm.getSharedNavigation();
      if(sharedNavigation == null) return;      
      SharedConfigDAO configService = uiForm.getApplicationComponent(SharedConfigDAO.class);
      configService.removeSharedNavigation(sharedNavigation);
      uiForm.reset();
    }
  }

}
