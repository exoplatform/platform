/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Hoa Nguyen
 *          hoa.nguyen@exoplatform.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(    
    template = "app:/groovy/portletregistry/webui/component/ApplicationRegistryWorkingArea.gtmpl",
    events = {
        @EventConfig(listeners = ApplicationRegistryWorkingArea.AddPortletActionListener.class),
        @EventConfig(listeners = ApplicationRegistryWorkingArea.DeletePortletActionListener.class, confirm = "ApplicationRegistryWorkingAreaNew.deleteApplication"),
        @EventConfig(listeners = ApplicationRegistryWorkingArea.EditPermissionActionListener.class),
        @EventConfig(listeners = ApplicationRegistryWorkingArea.EditPortletActionListener.class)
    }
)
public class ApplicationRegistryWorkingArea extends UIContainer {
   
  private List<Application> portlets_  = new ArrayList<Application>();
  private Application select_;
  
  public ApplicationRegistryWorkingArea() throws Exception {
    UIPopupWindow addCategoryPopup = addChild(UIPopupWindow.class, null, "WorkingPopup");
    addCategoryPopup.setWindowSize(640, 0); 
  }  
    
  public List<Application> getPortlets() {return portlets_;}
  public void setPortlets(List<Application> p) { portlets_ = p; }
  
  public Application getPortlet() {return select_;}
  public void setPortlet(Application s) { select_ = s; }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    renderChildren(context) ;
  }

  public Application getSelectApplication() { return select_; }
  
  public void setSeletcApplication(Application select){ select_ = select; }
  
  public void setSeletcApplication(String appName){
    for(Application app: portlets_){
      if(app.getDisplayName().equals(appName)){
        select_ = app;
      }
    }
  }
  static public class AddPortletActionListener extends EventListener<ApplicationRegistryWorkingArea> {
    public void execute(Event<ApplicationRegistryWorkingArea> event) throws Exception {
      ApplicationRegistryWorkingArea workingArea = event.getSource();
      
      UIPopupWindow popupWindow = workingArea.getChild(UIPopupWindow.class);
      UIAvailablePortletForm availablePortletForm = workingArea.createUIComponent(UIAvailablePortletForm.class, null, null);
      availablePortletForm.setValue();
      popupWindow.setUIComponent(availablePortletForm);
      popupWindow.setShow(true);
    }
  }

  static public class EditPortletActionListener extends EventListener<ApplicationRegistryWorkingArea> {
    public void execute(Event<ApplicationRegistryWorkingArea> event) throws Exception {
      String appName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      ApplicationRegistryWorkingArea workingArea = event.getSource();
      workingArea.setSeletcApplication(appName);
      UIPopupWindow popupWindow = workingArea.getChild(UIPopupWindow.class);
      UIInfoPortletForm availablePortletForm= workingArea.createUIComponent(UIInfoPortletForm.class, null, null);
      availablePortletForm.setName("UIInfoPortletForm");
      availablePortletForm.setValues(workingArea.getSelectApplication());
      popupWindow.setUIComponent(availablePortletForm);
      popupWindow.setShow(true);
    }
  }

  static public class EditPermissionActionListener extends EventListener<ApplicationRegistryWorkingArea> {
    public void execute(Event<ApplicationRegistryWorkingArea> event) throws Exception {
      String appName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      ApplicationRegistryWorkingArea workingArea = event.getSource();
      workingArea.setSeletcApplication(appName);
      UIPopupWindow popupWindow = workingArea.getChild(UIPopupWindow.class);
      UIPermissionForm accessGroupForm= workingArea.createUIComponent(UIPermissionForm.class, null, null);
      accessGroupForm.setValue(workingArea.getSelectApplication());
      popupWindow.setUIComponent(accessGroupForm);
      popupWindow.setShow(true);
    }
  }


  static public class DeletePortletActionListener extends EventListener<ApplicationRegistryWorkingArea> {
    public void execute(Event<ApplicationRegistryWorkingArea> event) throws Exception {
      String categoryName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      ApplicationRegistryWorkingArea uicomp = event.getSource();
      uicomp.setSeletcApplication(categoryName);
      Application selectedPortlet = uicomp.getSelectApplication() ;
      if(selectedPortlet == null) return ;

      ApplicationRegistryService service = uicomp.getApplicationComponent(ApplicationRegistryService.class) ;
      String portletSelectedId = selectedPortlet.getId() ;
      Application portlet = service.getApplication(portletSelectedId);
      service.remove(portlet) ;  
      uicomp.getPortlets().remove(selectedPortlet) ;
      uicomp.setSeletcApplication((Application)null);
    }
  }

}

