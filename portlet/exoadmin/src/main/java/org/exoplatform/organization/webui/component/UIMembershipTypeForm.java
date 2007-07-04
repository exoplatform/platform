package org.exoplatform.organization.webui.component;

import java.util.Date;

import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.NameValidator;
@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
  events = @EventConfig(listeners = UIMembershipTypeForm.SaveActionListener.class)  
)
public class UIMembershipTypeForm extends UIForm {  
    
  private static String MEMBERSHIP_TYPE_NAME = "name",
                        DESCRIPTION = "description";
  //TODO: Tung.Pham added
  private MembershipType membershipType_ ;
  //-------------------------
  public UIMembershipTypeForm() throws Exception {
    addUIFormInput(new UIFormStringInput(MEMBERSHIP_TYPE_NAME, MEMBERSHIP_TYPE_NAME, null).
                   setEditable(UIFormStringInput.ENABLE).
                   addValidator(EmptyFieldValidator.class).
                   addValidator(NameValidator.class)) ;
    
    addUIFormInput(new UIFormTextAreaInput(DESCRIPTION, DESCRIPTION, null)) ; 
  } 
  
  public void setMembershipType(MembershipType membershipType) throws Exception {    
    //TODO: Tung.Pham replaced
    membershipType_ = membershipType ;
    if(membershipType_ == null) {
      getUIStringInput(MEMBERSHIP_TYPE_NAME).setEditable(UIFormStringInput.ENABLE) ;
      return ;
    }
    getUIStringInput(MEMBERSHIP_TYPE_NAME).setEditable(UIFormStringInput.DISABLE);
    invokeGetBindingBean(membershipType_) ;
//    getUIStringInput(MEMBERSHIP_TYPE_NAME).setValue(membershipType.getName());
//    getUIStringInput(DESCRIPTION).setValue(membershipType.getDescription());
//    getUIStringInput(MEMBERSHIP_TYPE_NAME).setEditable(UIFormStringInput.DISABLE);
  }
  
  //TODO
  public MembershipType getMembershipType() {return membershipType_ ;} ;
  
  static  public class SaveActionListener extends EventListener<UIMembershipTypeForm> {
    public void execute(Event<UIMembershipTypeForm> event) throws Exception {
      //TODO: Tung.Pham replaced
      UIMembershipTypeForm uiForm = event.getSource();
      UIMembershipManagement membership = uiForm.getParent() ;
      OrganizationService service = uiForm.getApplicationComponent(OrganizationService.class);
      
      MembershipType mt = uiForm.getMembershipType() ;
      if(mt == null) mt = service.getMembershipTypeHandler().createMembershipTypeInstance();
      uiForm.invokeSetBindingBean(mt) ;

      if(mt == uiForm.getMembershipType())
      {
        mt.setModifiedDate(new Date());
        service.getMembershipTypeHandler().saveMembershipType(mt, true);
      } else {
        MembershipType existMembershipType = service.getMembershipTypeHandler().findMembershipType(mt.getName()) ;
        if(existMembershipType != null) {
          UIApplication uiApp = event.getRequestContext().getUIApplication() ;
          uiApp.addMessage(new ApplicationMessage("UIMembershipTypeForm.msg.SameName", null)) ;
          return ;
        }
        mt.setModifiedDate(new Date());
        mt.setCreatedDate(new Date());
        service.getMembershipTypeHandler().createMembershipType(mt, true);
        membership.addOptions(mt) ;
      }

      UIListMembershipType uiMembershipList = membership.getChild(UIListMembershipType.class);
      UIGrid uiGrid = uiMembershipList.findComponentById("UIGrid"); 
      uiMembershipList.update(uiGrid) ;
      uiForm.setMembershipType(null) ;
      uiForm.reset();
      //---------------------------------------------------
//      UIMembershipTypeForm uiForm = event.getSource();
//      UIMembershipManagement membership = uiForm.getParent() ;
//      OrganizationService service = uiForm.getApplicationComponent(OrganizationService.class);
//      
//      String name = uiForm.getUIStringInput(MEMBERSHIP_TYPE_NAME).getValue();
//      String description = uiForm.getUIStringInput(DESCRIPTION).getValue();
//      MembershipType mt = service.getMembershipTypeHandler().findMembershipType(name);
//      
//      if(mt == null){
//        mt =   service.getMembershipTypeHandler().createMembershipTypeInstance();
//        uiForm.invokeSetBindingBean(mt) ;
//        mt.setModifiedDate(new Date());
//        mt.setCreatedDate(new Date());
//        service.getMembershipTypeHandler().createMembershipType(mt, true);
//        membership.addOptions(mt) ;
//      }else if(description != mt.getDescription()) {
//        mt.setDescription(description);
//        mt.setModifiedDate(new Date());
//        service.getMembershipTypeHandler().saveMembershipType(mt, true);
//      }
//      
//      uiForm.getUIStringInput(MEMBERSHIP_TYPE_NAME).setEditable(UIFormStringInput.ENABLE);
//      
//      UIMembershipManagement uiMembershipManager = uiForm.getParent();
//      UIListMembershipType uiMembershipList = uiMembershipManager.getChild(UIListMembershipType.class);
//      UIGrid uiGrid = uiMembershipList.findComponentById("UIGrid"); 
//      uiMembershipList.update(uiGrid) ;
//      uiForm.reset();      
    }
  }
  
}
