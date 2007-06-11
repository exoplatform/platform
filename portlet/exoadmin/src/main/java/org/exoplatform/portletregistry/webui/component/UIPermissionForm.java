/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import java.util.Calendar;
import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.organization.webui.component.UIListPermissionSelector;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Thi Hoa
 *          hoa.nguyen@exoplatform.com
 * Sep 26, 2006  
 */

@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    events = @EventConfig(listeners = UIPermissionForm.SaveActionListener.class)
)
public class UIPermissionForm extends UIForm {
 
  private Application portlet_;
  
  public UIPermissionForm() throws Exception{
    UIListPermissionSelector selector = addChild(UIListPermissionSelector.class, null, "UIListPermissionSelector") ;
    selector.setName("UIListPermissionSelector") ;
  }
  
  public void setValue(Application portlet) throws Exception {
    portlet_ = portlet;
    String[] accessPermissions = portlet_.getAccessPermissions() ;
    if (accessPermissions != null && accessPermissions.length > 0) {
      getChild(UIListPermissionSelector.class).setValue(accessPermissions) ;
    }
  }
  
  public Application getPortlet() { return portlet_; }
  
  static public class SaveActionListener extends EventListener<UIPermissionForm> {    
    public void execute(Event<UIPermissionForm> event) throws Exception {
      UIPermissionForm  uiPermissionForm = event.getSource();
      Application portlet = uiPermissionForm.getPortlet() ;
      UIListPermissionSelector listPermissionSelector = uiPermissionForm.getChild(UIListPermissionSelector.class) ;
      portlet.setAccessPermissions(listPermissionSelector.getValue()) ;
      ApplicationRegistryService service = uiPermissionForm.getApplicationComponent(ApplicationRegistryService.class) ;
      portlet.setModifiedDate(Calendar.getInstance().getTime());
      service.update(portlet) ;      
      UIPopupWindow popupWindow = uiPermissionForm.getParent();
      popupWindow.setShow(false);
    }
  }

}
