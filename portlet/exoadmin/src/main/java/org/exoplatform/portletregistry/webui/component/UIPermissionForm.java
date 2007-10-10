/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import java.util.ArrayList;
import java.util.Calendar;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.organization.UIListPermissionSelector;

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
    ArrayList<String> accessPermissions = portlet_.getAccessPermissions() ;
    String[] per = new String[accessPermissions.size()];
    if (accessPermissions != null && accessPermissions.size() > 0) {
      getChild(UIListPermissionSelector.class).setValue(accessPermissions.toArray(per)) ;
    }
  }
  
  public Application getPortlet() { return portlet_; }
  
  static public class SaveActionListener extends EventListener<UIPermissionForm> {    
    public void execute(Event<UIPermissionForm> event) throws Exception {
      UIPermissionForm  uiPermissionForm = event.getSource();
      Application portlet = uiPermissionForm.getPortlet() ;
      UIListPermissionSelector uiListPermissionSelector = uiPermissionForm.getChild(UIListPermissionSelector.class) ;
      ArrayList<String> pers = new ArrayList<String>();
      if(uiListPermissionSelector.getValue()!= null)
      for(String per: uiListPermissionSelector.getValue()) pers.add(per);
      portlet.setAccessPermissions(pers) ;
      ApplicationRegistryService service = uiPermissionForm.getApplicationComponent(ApplicationRegistryService.class) ;
      portlet.setModifiedDate(Calendar.getInstance().getTime());
      service.update(portlet) ;      
      UIPopupWindow uiPopupWindow = uiPermissionForm.getParent();
      uiPopupWindow.setShow(false);
    }
  }

}
