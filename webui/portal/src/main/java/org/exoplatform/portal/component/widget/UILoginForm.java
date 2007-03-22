/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.widget;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.application.ApplicationMessage;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormCheckBoxInput;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.component.validator.NameValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.exception.MessageException;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 11, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(  
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/portal/webui/component/widget/UILoginForm.gtmpl" ,
    events = {
      @EventConfig(listeners = UILoginForm.SigninActionListener.class),
      @EventConfig(phase = Phase.DECODE, listeners = UIMaskWorkspace.CloseActionListener.class)
    }
  )
})
public class UILoginForm extends UIForm {
  
  public UILoginForm() throws Exception{    
    addUIFormInput(new UIFormStringInput("username", "username", null).
                   addValidator(NameValidator.class)).
    addUIFormInput(new UIFormStringInput("password", "password", null).
                   setType(UIFormStringInput.PASSWORD_TYPE).
                   addValidator(EmptyFieldValidator.class)).
    addUIFormInput(new UIFormCheckBoxInput<Boolean>("remember", "remember", null));
  }

  static public class SigninActionListener  extends EventListener<UILoginForm> {
    public void execute(Event<UILoginForm> event) throws Exception {
      UILoginForm uiForm = event.getSource();
      String username = uiForm.getUIStringInput("username").getValue();
      String password = uiForm.getUIStringInput("password").getValue();
      boolean remember = uiForm.<UIFormCheckBoxInput >getUIInput("remember").isChecked();
      
      OrganizationService orgService = uiForm.getApplicationComponent(OrganizationService.class);
      boolean authentication = orgService.getUserHandler().authenticate(username, password);
      if(!authentication){
        throw new MessageException(new ApplicationMessage("UILoginForm.msg.Invalid-account", null));
      }
        
      PortalRequestContext prContext = Util.getPortalRequestContext();
      HttpServletRequest request = prContext.getRequest();
      request.getSession().invalidate();
      HttpSession session = request.getSession();
      session.setAttribute("authentication.username", username);
      session.setAttribute("authentication.password", password);
      if(remember && authentication){
        HttpServletResponse response = prContext.getResponse();
        response.addCookie(loadCookie(request, "authentication.username", username));
        response.addCookie(loadCookie(request, "authentication.password", password));
      }
      prContext.setResponseComplete(true);
      String redirect = request.getContextPath() + "/private/" + username + ":/";
      prContext.getResponse().sendRedirect(redirect);      
    }   
    
    private Cookie loadCookie(HttpServletRequest request, String name, String value){
      Cookie[] cookies = request.getCookies();
      Cookie cookie = null;
      if (cookies != null) {
        for (Cookie ele : cookies) {
          if(ele.getName().equals(name)) cookie = ele;
        }
      }
      if(cookie == null) cookie = new Cookie(name, value);
      cookie.setDomain(request.getRemoteHost());
      cookie.setSecure(true);
      return cookie;
    }
  }
  
//  static public class SignupActionListener  extends EventListener<UILoginForm> {
//    public void execute(Event<UILoginForm> event) throws Exception {
//      UILoginForm uiSource = event.getSource();
//      UIMaskWorkspace uiMaskWorkspace = uiSource.getAncestorOfType(UIMaskWorkspace.class);
//    }
//  }
}
