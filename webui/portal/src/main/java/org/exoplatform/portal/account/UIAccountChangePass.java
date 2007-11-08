package org.exoplatform.portal.account ;

import org.exoplatform.portal.account.UIAccountSetting;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIContainerLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmailAddressValidator;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.IdentifierValidator;
/**
 * Created by The eXo Platform SARL
 * Author : tung.dang
 *          tungcnw@gmail.com
 */         

@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "app:/groovy/webui/form/UIForm.gtmpl",
    events = {
        @EventConfig(listeners = UIAccountChangePass.SaveActionListener.class),
        @EventConfig(listeners = UIAccountChangePass.ResetActionListener.class,phase = Phase.DECODE)
      }
)

public class UIAccountChangePass extends UIForm {
  
  // constructor
  public UIAccountChangePass() throws Exception {
    super();
    addUIFormInput(new UIFormStringInput("currentpass", "password", null).
           setType(UIFormStringInput.PASSWORD_TYPE).
           addValidator(EmptyFieldValidator.class)) ;
    addUIFormInput(new UIFormStringInput("newpass", "password", null).
           setType(UIFormStringInput.PASSWORD_TYPE).
           addValidator(EmptyFieldValidator.class)) ;
    addUIFormInput(new UIFormStringInput("confirmnewpass", "password", null).
        setType(UIFormStringInput.PASSWORD_TYPE).
        addValidator(EmptyFieldValidator.class)) ;
  }
  
  static  public class ResetActionListener extends EventListener<UIAccountChangePass> {
    public void execute(Event<UIAccountChangePass> event) throws Exception {
      UIAccountChangePass uiForm = event.getSource() ;
      uiForm.reset() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm) ;
    }
  }
  
  static  public class SaveActionListener extends EventListener<UIAccountChangePass> {
    public void execute(Event<UIAccountChangePass> event) throws Exception {
        UIAccountChangePass uiForm = event.getSource();
        OrganizationService service =  uiForm.getApplicationComponent(OrganizationService.class) ;
        WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
        UIApplication uiApp = context.getUIApplication() ;
        // get userName input
        String username = Util.getPortalRequestContext().getRemoteUser() ;
        User user = service.getUserHandler().findUserByName(username) ; 
        String currentPass = uiForm.getUIStringInput("currentpass").getValue() ;
        String newPass = uiForm.getUIStringInput("newpass").getValue() ;
        String confirmnewPass = uiForm.getUIStringInput("confirmnewpass").getValue() ;
        
        
        if(!currentPass.equals(user.getPassword())) {
          uiApp.addMessage(new ApplicationMessage("UIAccountChangePass.msg.currentpassword-is-not-match", null,1)) ;
          uiForm.reset();
          event.getRequestContext().addUIComponentToUpdateByAjax(uiForm);
          context.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
          return ;
        }
        
        if(!newPass.equals(confirmnewPass)) {
          uiApp.addMessage(new ApplicationMessage("UIAccountChangePass.msg.password-is-not-match", null,1)) ;
          uiForm.reset();
          event.getRequestContext().addUIComponentToUpdateByAjax(uiForm);
          context.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
          return ;
        }
        user.setPassword(newPass) ;
        uiApp.addMessage(new ApplicationMessage("UIAccountChangePass.msg.change.pass.success", null)) ;
        service.getUserHandler().saveUser(user, true) ;
        uiForm.reset() ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiForm) ;
        context.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        UIAccountSetting ui = uiForm.getParent() ;
        ui.getChild(UIAccountProfiles.class).setRendered(true) ;
        ui.getChild(UIAccountChangePass.class).setRendered(false) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(ui) ;
        return ;
    }
  }
}
