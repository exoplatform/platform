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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormRadioBoxInput;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * Jul 09, 2008
 */
@ComponentConfig(  
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/portal/webui/UIForgetPasswordWizard.gtmpl",
  events = {
    @EventConfig(listeners = UIForgetPasswordWizard.NextActionListener.class),
    @EventConfig(phase = Phase.DECODE, listeners = UIForgetPasswordWizard.BackActionListener.class)
  }
)
public class UIForgetPasswordWizard extends UIForm {
  final static String Password_Radio = "forgotpassword";
  final static String Username_Radio = "forgotusername";
  final static String Forgot = "UIForgetPasswordWizard";
  public UIForgetPasswordWizard() throws Exception{
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>(2);
    options.add(new SelectItemOption<String>(Password_Radio,"password"));
    options.add(new SelectItemOption<String>(Username_Radio,"username"));
    addUIFormInput(new UIFormRadioBoxInput(Forgot,null,options).setAlign(UIFormRadioBoxInput.VERTICAL_ALIGN));
  }
  
  static public class NextActionListener  extends EventListener<UIForgetPasswordWizard> {
    public void execute(Event<UIForgetPasswordWizard> event) throws Exception {
      UILogin uilogin = event.getSource().getParent();
      uilogin.getChild(UILoginForm.class).setRendered(false);
      uilogin.getChild(UIForgetPasswordWizard.class).setRendered(false);
      UIForgetPassword uiForgetpassword = (UIForgetPassword)uilogin.getChild(UIForgetPassword.class).setRendered(true);
      String value = event.getSource().getChild(UIFormRadioBoxInput.class).getValue();
      UIFormStringInput uiEmail = uiForgetpassword.getUIStringInput(UIForgetPassword.Email);
      UIFormStringInput uiUser = uiForgetpassword.getUIStringInput(UIForgetPassword.Username);
      if(value.equals("password")){
        uiEmail.setRendered(false);
        uiUser.setRendered(true);
      }
      else {
        uiEmail.setRendered(true);
        uiUser.setRendered(false);
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uilogin);
    }   
  }
  
  static public class BackActionListener  extends EventListener<UIForgetPasswordWizard> {
    public void execute(Event<UIForgetPasswordWizard> event) throws Exception {
      UILogin uilogin = event.getSource().getParent();
      uilogin.getChild(UILoginForm.class).setRendered(true);
      uilogin.getChild(UIForgetPasswordWizard.class).setRendered(false);
      uilogin.getChild(UIForgetPassword.class).setRendered(false);
      event.getRequestContext().addUIComponentToUpdateByAjax(uilogin);
    }   
  }
}
