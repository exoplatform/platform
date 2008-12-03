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
package org.exoplatform.portal.webui.login;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.mail.MailService;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmailAddressValidator;
import org.exoplatform.webui.form.validator.MandatoryValidator;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * Jul 09, 2008
 */
@ComponentConfig(  
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
  events = {
    @EventConfig(listeners = UIForgetPassword.SendActionListener.class),
    @EventConfig(phase = Phase.DECODE, listeners = UIForgetPassword.BackActionListener.class)
  }
)
public class UIForgetPassword extends UIForm {
  static final String Username = "username";
  static final String Email = "email";
  public UIForgetPassword() throws Exception{
    addUIFormInput(new UIFormStringInput(Username,null).addValidator(MandatoryValidator.class)).
    addUIFormInput(new UIFormStringInput(Email,null).addValidator(MandatoryValidator.class).addValidator(EmailAddressValidator.class));
  }
  
  static public class SendActionListener  extends EventListener<UIForgetPassword> {
    public void execute(Event<UIForgetPassword> event) throws Exception {
      UIForgetPassword uiForm = event.getSource();
      UILogin uilogin = uiForm.getParent();
      WebuiRequestContext requestContext = event.getRequestContext();
      PortalRequestContext portalContext = PortalRequestContext.getCurrentInstance();
      String url = portalContext.getRequest().getRequestURL().toString();
      MailService mailSrc = uiForm.getApplicationComponent(MailService.class);
      OrganizationService orgSrc = uiForm.getApplicationComponent(OrganizationService.class);
      String userName = uiForm.getUIStringInput(Username).getValue();
      String email = uiForm.getUIStringInput(Email).getValue();
      uiForm.reset();
      if(userName != null) {
        if (orgSrc.getUserHandler().findUserByName(userName)==null) {
          requestContext.getUIApplication().addMessage(new ApplicationMessage("UIForgetPassword.msg.user-not-exist", null));
          requestContext.addUIComponentToUpdateByAjax(requestContext.getUIApplication().getUIPopupMessages());
          return;
        }
        email = orgSrc.getUserHandler().findUserByName(userName).getEmail();
      }
      //get user
      PageList userPageList = orgSrc.getUserHandler().findUsers(new Query());
      List userList = userPageList.currentPage();
      User user=null;
      for(int i=0; i<userList.size(); i++) {
        User tmpUser = (User)userList.get(i);
        if(tmpUser.getEmail().equals(email)) {
         user = tmpUser;
         break;
        }
      }
      if(user==null) {
        requestContext.getUIApplication().addMessage(new ApplicationMessage("UIForgetPassword.msg.email-not-exist", null));
        requestContext.addUIComponentToUpdateByAjax(requestContext.getUIApplication().getUIPopupMessages());
        return;
      }
      String portalName = URLEncoder.encode(Util.getUIPortal().getName(), "UTF-8");
      Long now = new Date().getTime();
      user.setPassword(now.toString());
      orgSrc.getUserHandler().saveUser(user,true);
      ResourceBundle res = requestContext.getApplicationResourceBundle() ;
      String headerMail = "headermail";
      String footerMail = "footer";
      try {
        headerMail = res.getString(uiForm.getId()+ ".mail.header") + "\n\n" +
                     res.getString(uiForm.getId()+ ".mail.user") + user.getUserName()+ "\n" +
                     res.getString(uiForm.getId()+ ".mail.password") + user.getPassword() + "\n\n\n"+
                     res.getString(uiForm.getId()+ ".mail.link");
        footerMail = "\n\n\n" + res.getString(uiForm.getId() + ".mail.footer");
      } catch (MissingResourceException e) {
        e.printStackTrace();
      }
      String host = url.substring(0, url.indexOf(requestContext.getRequestContextPath()));
      String activeLink = host + requestContext.getRequestContextPath() + "/public/" + portalName;
      activeLink += "?portal:componentId=UIPortal&portal:action=RecoveryPasswordAndUsername&datesend="+now.toString()+"&email="+email;
      activeLink = headerMail + activeLink + footerMail;
      mailSrc.sendMessage("exoservice@gmail.com",email, "Remind password and username", activeLink);
      uilogin.getChild(UILoginForm.class).setRendered(true);
      uilogin.getChild(UIForgetPasswordWizard.class).setRendered(false);
      uilogin.getChild(UIForgetPassword.class).setRendered(false);
      requestContext.addUIComponentToUpdateByAjax(uilogin);
    }
  }
  
  static public class BackActionListener  extends EventListener<UIForgetPassword> {
    public void execute(Event<UIForgetPassword> event) throws Exception {
      UILogin uilogin = event.getSource().getParent();
      uilogin.getChild(UILoginForm.class).setRendered(false);
      uilogin.getChild(UIForgetPasswordWizard.class).setRendered(true);
      uilogin.getChild(UIForgetPassword.class).setRendered(false);
      event.getSource().reset();
      event.getRequestContext().addUIComponentToUpdateByAjax(uilogin);
    }   
  }
}
