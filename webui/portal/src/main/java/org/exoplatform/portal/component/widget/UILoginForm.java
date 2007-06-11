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
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 11, 2006  
 */
@ComponentConfig(  
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/portal/webui/component/widget/UILoginForm.gtmpl" ,
  events = {
    @EventConfig(listeners = UILoginForm.SigninActionListener.class),
//      @EventConfig(listeners = UILoginForm.SignUpActionListener.class),
    @EventConfig(phase = Phase.DECODE, listeners = UIMaskWorkspace.CloseActionListener.class)
  }
)
public class UILoginForm extends UIForm {
  
  public UILoginForm() throws Exception{    
    addUIFormInput(new UIFormStringInput("username", "username", null)).
    addUIFormInput(new UIFormStringInput("password", "password", null).
                   setType(UIFormStringInput.PASSWORD_TYPE)).
    addUIFormInput(new UIFormCheckBoxInput<Boolean>("remember", "remember", null));
//    PortalRequestContext prContext = Util.getPortalRequestContext();
//    HttpServletRequest request = prContext.getRequest();
//    Cookie userName = loadCookie(request, "authentication.username", null, false);
//    Cookie pass = loadCookie(request, "authentication.password", null, false);
//    if( userName == null || pass == null) return;
//    getUIStringInput("username").setValue(userName.getValue());
//    getUIStringInput("password").setValue(pass.getValue());
    /*
         addUIFormInput(new UIFormStringInput("username", "username", null).
                   addValidator(NameValidator.class)).
    addUIFormInput(new UIFormStringInput("password", "password", null).
                   setType(UIFormStringInput.PASSWORD_TYPE).
                   addValidator(EmptyFieldValidator.class)).
    addUIFormInput(new UIFormCheckBoxInput<Boolean>("remember", "remember", null));
     */
  }
  
//  protected Cookie loadCookie(HttpServletRequest request, String name, String value, boolean autoCreate){
//    Cookie[] cookies = request.getCookies();
//    Cookie cookie = null;
//    if (cookies != null) {
//      for (Cookie ele : cookies) {
//        if(ele.getName().equals(name)) cookie = ele;
//      }
//    }
//    if(cookie == null && autoCreate) cookie = new Cookie(name, value);
//    if(cookie == null) return null;
//    cookie.setDomain(request.getRemoteHost());
//    cookie.setSecure(true);
//    return cookie;
//  }

  //TODO: Tung.Pham Modified
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
      //String redirect = request.getContextPath() + "/private/site:/";
      UIPortal currentPortal = Util.getUIPortal() ;
      String portalName = currentPortal.getName() ;
      String redirect = request.getContextPath() + "/private/" + portalName + "/";
      prContext.getResponse().sendRedirect(redirect);      
    }   
    
    protected Cookie loadCookie(HttpServletRequest request, String name, String value){
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
  
//  static public class SignUpActionListener  extends EventListener<UILoginForm> {
//    public void execute(Event<UILoginForm> event) throws Exception {
//      System.out.println("\n\n\n\n^^^^^^^^^^^^^^##############################################");
//      UIPortal uiPortal = Util.getUIPortal();
//      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);   
//      UIComponent uicom = event.getSource().getParent();
//      UIComponent superParen = uicom.getParent();
//      UIAccountPortlet accountPortlet = event.getSource().getAncestorOfType(UIAccountPortlet.class);//uiApp.findFirstComponentOfType(UIAccountPortlet.class);
//      System.out.println("\n>>>>>>>>>>>AccountPortlet: " + superParen);
//      UIAccountForm accountForm = uiApp.findFirstComponentOfType(UIAccountForm.class);
//      System.out.println("\n>>>>>>>>>>>AccountForm: " + accountForm);
//      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;     
      
//      UIAccountPortlet uiAccountPortlet = uiMaskWS.createUIComponent(UIAccountPortlet.class, null, null);    
//      uiMaskWS.setUIComponent(uiAccountPortlet);
//      uiMaskWS.setWindowSize(640, 400);
//      uiMaskWS.setShow(true);
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
//      Util.updateUIApplication(event);  
  
      // TODO BUG! exception in phase 'parsing' in source unit 'Script1.groovy' null
      // Ko hieu cach su dung Param nhu ben duoi thi co gi sai.
     /* System.out.println("\n\n\n\n^^^^^^^^^^^^^^##############################################");

      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      Param param = new Param();
      param.setName("AccountTemplateConfigOption");
      param.setValue("app:/WEB-INF/conf/uiconf/account/webui/component/model/AccountTemplateConfigOption.groovy");
      ArrayList<Param> params = new ArrayList<Param>();
      InitParams initParam = new InitParams();
      initParam.setParams(params);
      params.add(param);
      UIAccountForm accountForm = new UIAccountForm(initParam);
      uiMaskWS.setUIComponent(accountForm);
      uiMaskWS.setWindowSize(630, -1);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      */
//    }
//  }
}
