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
package org.exoplatform.portletregistry.webui.component;

import java.util.Calendar;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupComponent;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.NameValidator;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Viet Chung
 *          nguyenchung136@yahoo.com
 * Jul 28, 2006  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/form/UIForm.gtmpl",
    events = {
      @EventConfig(listeners = UIInfoPortletForm.SaveActionListener.class),
      @EventConfig(phase = Phase.DECODE, listeners = UIInfoPortletForm.BackActionListener.class)
    }
)
public class UIInfoPortletForm extends UIForm implements UIPopupComponent {  

  private Application portlet_ ;
  
  private String name_;
  
  public UIInfoPortletForm() throws Exception {
    addUIFormInput(new UIFormStringInput("applicationName", "applicationName", null).
                   addValidator(MandatoryValidator.class).addValidator(NameValidator.class)) ;
    
    addUIFormInput(new UIFormStringInput("displayName", "displayName", null));
    addUIFormInput(new UIFormTextAreaInput("description", "description", null));
  }
  
  public String getName() {return name_;}
  public void setName(String name) {name_ = name;}

  public void setValues(Application portlet) throws Exception {
    portlet_ = portlet;
    if(portlet_ == null) {
      getUIStringInput("applicationName").setEditable(true);
      return ;
    }
    getUIStringInput("applicationName").setEditable(false);
    invokeGetBindingBean(portlet) ;
  }

  public Application getPortlet() { return portlet_ ; }

  static public class SaveActionListener extends EventListener<UIInfoPortletForm> {
    public void execute(Event<UIInfoPortletForm> event) throws Exception{
      UIInfoPortletForm uiForm = event.getSource() ;
      UIPopupWindow uiParent = uiForm.getParent();
      uiParent.setShow(false);
      ApplicationRegistryService service = uiForm.getApplicationComponent(ApplicationRegistryService.class) ;
      Application portlet = uiForm.getPortlet() ;
      uiForm.invokeSetBindingBean(portlet);
      portlet.setModifiedDate(Calendar.getInstance().getTime());
      service.update(portlet) ;
      uiForm.setValues(null) ;
    }
  }

  static public class BackActionListener extends EventListener<UIInfoPortletForm>{
    public void execute(Event<UIInfoPortletForm> event) throws Exception{
      UIInfoPortletForm uiForm = event.getSource() ;
      uiForm.setValues(null) ;
      UIPopupWindow uiParent = uiForm.getParent();
      uiParent.setShow(false);
    }
  }
  
  public void activate() throws Exception {}
  public void deActivate() throws Exception {} 
}


