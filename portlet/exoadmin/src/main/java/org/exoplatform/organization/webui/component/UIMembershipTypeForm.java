package org.exoplatform.organization.webui.component;

import java.util.Date;

import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
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
  
  public UIMembershipTypeForm() throws Exception {
    addUIFormInput(new UIFormStringInput(MEMBERSHIP_TYPE_NAME, MEMBERSHIP_TYPE_NAME, null).
                   setEditable(UIFormStringInput.ENABLE).
                   addValidator(EmptyFieldValidator.class).
                   addValidator(NameValidator.class)) ;
    
    addUIFormInput(new UIFormTextAreaInput(DESCRIPTION, DESCRIPTION, null)) ; 
  } 
  
  public void setMembershipType(MembershipType membershipType) throws Exception {    
    getUIStringInput(MEMBERSHIP_TYPE_NAME).setValue(membershipType.getName());
    getUIStringInput(DESCRIPTION).setValue(membershipType.getDescription());
    getUIStringInput(MEMBERSHIP_TYPE_NAME).setEditable(UIFormStringInput.DISABLE);   
  }
  
  static  public class SaveActionListener extends EventListener<UIMembershipTypeForm> {
    public void execute(Event<UIMembershipTypeForm> event) throws Exception {
      UIMembershipTypeForm uiForm = event.getSource();
      UIMembershipManagement membership = uiForm.getParent() ;
      OrganizationService service = uiForm.getApplicationComponent(OrganizationService.class);
      
      String name = uiForm.getUIStringInput(MEMBERSHIP_TYPE_NAME).getValue();
      String description = uiForm.getUIStringInput(DESCRIPTION).getValue();
      MembershipType mt = service.getMembershipTypeHandler().findMembershipType(name);
      
      if(mt == null){
        mt =   service.getMembershipTypeHandler().createMembershipTypeInstance();
        uiForm.invokeSetBindingBean(mt) ;
        mt.setModifiedDate(new Date());
        mt.setCreatedDate(new Date());
        service.getMembershipTypeHandler().createMembershipType(mt, true);
        membership.addOptions(mt) ;
      }else if(description != mt.getDescription()) {
        mt.setDescription(description);
        mt.setModifiedDate(new Date());
        service.getMembershipTypeHandler().saveMembershipType(mt, true);
      }
      
      uiForm.getUIStringInput(MEMBERSHIP_TYPE_NAME).setEditable(UIFormStringInput.ENABLE);
      
      UIMembershipManagement uiMembershipManager = uiForm.getParent();
      UIListMembershipType uiMembershipList = uiMembershipManager.getChild(UIListMembershipType.class);
      UIGrid uiGrid = uiMembershipList.findComponentById("UIGrid"); 
      uiMembershipList.update(uiGrid) ;
      uiForm.reset();      
    }
  }
  
}
