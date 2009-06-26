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
package org.exoplatform.portal.account ;

import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.PasswordStringLengthValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;
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
           addValidator(MandatoryValidator.class)) ;
    addUIFormInput(new UIFormStringInput("newpass", "password", null).
           setType(UIFormStringInput.PASSWORD_TYPE).
           addValidator(PasswordStringLengthValidator.class, 6, 30).
           addValidator(MandatoryValidator.class)) ;
    addUIFormInput(new UIFormStringInput("confirmnewpass", "password", null).
          setType(UIFormStringInput.PASSWORD_TYPE).
          addValidator(PasswordStringLengthValidator.class, 6, 30).
          addValidator(MandatoryValidator.class)) ;
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
        String username = Util.getPortalRequestContext().getRemoteUser() ;
        User user = service.getUserHandler().findUserByName(username) ; 
        String currentPass = uiForm.getUIStringInput("currentpass").getValue() ;
        String newPass = uiForm.getUIStringInput("newpass").getValue() ;
        String confirmnewPass = uiForm.getUIStringInput("confirmnewpass").getValue() ;
        
        
        if(!currentPass.equals(user.getPassword())) {
          uiApp.addMessage(new ApplicationMessage("UIAccountChangePass.msg.currentpassword-is-not-match", null,1)) ;
          uiForm.reset();
          event.getRequestContext().addUIComponentToUpdateByAjax(uiForm);
          return ;
        }
        
        if(!newPass.equals(confirmnewPass)) {
          uiApp.addMessage(new ApplicationMessage("UIAccountChangePass.msg.password-is-not-match", null,1)) ;
          uiForm.reset();
          event.getRequestContext().addUIComponentToUpdateByAjax(uiForm);
          return ;
        }
        user.setPassword(newPass) ;
        uiApp.addMessage(new ApplicationMessage("UIAccountChangePass.msg.change.pass.success", null)) ;
        service.getUserHandler().saveUser(user, true) ;
        uiForm.reset() ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiForm) ;
        UIAccountSetting ui = uiForm.getParent() ;
        ui.getChild(UIAccountProfiles.class).setRendered(true) ;
        ui.getChild(UIAccountChangePass.class).setRendered(false) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(ui) ;
        return ;
    }
  }
}
