/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.webui.component.UIBannerPortlet;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.application.ApplicationMessage;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.component.validator.NameValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.exception.MessageException;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 11, 2006  
 */
@ComponentConfig(  
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/portal/webui/component/widget/UILoginForm.gtmpl" ,
  events = @EventConfig(listeners = UILoginForm.LoginActionListener.class)
)
public class UILoginForm extends UIForm {
  
  public UILoginForm() throws Exception{    
    addUIFormInput(new UIFormStringInput("username","username",null).
                   addValidator(NameValidator.class)).
    addUIFormInput(new UIFormStringInput("password","password",null).
                   setType(UIFormStringInput.PASSWORD_TYPE).
                   addValidator(EmptyFieldValidator.class));
  }

  static public class LoginActionListener  extends EventListener<UILoginForm> {
    public void execute(Event<UILoginForm> event) throws Exception {
      UILoginForm uiForm = event.getSource();
      String username = uiForm.getUIStringInput("username").getValue();
      String password = uiForm.getUIStringInput("password").getValue();
      OrganizationService orgService = uiForm.getApplicationComponent(OrganizationService.class);
      boolean authentication = orgService.getUserHandler().authenticate(username, password);
      if(!authentication){
        throw new MessageException(new ApplicationMessage("UILoginForm.msg.Invalid-account", null));
      }
        
      PortalRequestContext prContext = Util.getPortalRequestContext();
      HttpServletRequest request = prContext.getRequest();
      request.getSession().invalidate();
      HttpSession session = request.getSession();
      session.setAttribute("authentication.username",username);
      session.setAttribute("authentication.password",password);
      prContext.setResponseComplete(true);
      String redirect = request.getContextPath() + "/private/" + username + ":/";
      prContext.getResponse().sendRedirect(redirect);      
    }    
  }
  
  static  public class SigninActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      System.out.println(" \n\n\n == > "+event.getSource() +"\n\n\n");
//      @SuppressWarnings("unused")
//      UIBannerPortlet uicom = event.getSource();
//      UIPortal uiPortal = Util.getUIPortal();
//      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
//      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
//      UILoginForm uiForm = uiMaskWS.createUIComponent(UILoginForm.class, null, null);
//      uiMaskWS.setUIComponent(uiForm) ;
//      uiMaskWS.setShow(true) ;
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
  
}
