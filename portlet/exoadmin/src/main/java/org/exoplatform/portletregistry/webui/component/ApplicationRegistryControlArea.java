/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.organization.webui.component.UIPermissionSelector;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.services.portletcontainer.monitor.PortletContainerMonitor;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(    
    template = "app:/groovy/portletregistry/webui/component/ApplicationRegistryControlArea.gtmpl",
    events = {
        @EventConfig(listeners = ApplicationRegistryControlArea.AddCategoryActionListener.class),
        @EventConfig(listeners = ApplicationRegistryControlArea.EditCategoryActionListener.class),
        @EventConfig(listeners = ApplicationRegistryControlArea.ImportCategoryActionListener.class),
        @EventConfig(listeners = ApplicationRegistryControlArea.DeleteCategoryActionListener.class),
        @EventConfig(listeners = ApplicationRegistryControlArea.DeleteAllActionListener.class),
        @EventConfig(listeners = ApplicationRegistryControlArea.ShowCategoryActionListener.class),
        @EventConfig(listeners = ApplicationRegistryControlArea.ShowPortletActionListener.class),
        @EventConfig(listeners = ApplicationRegistryControlArea.AddPortletActionListener.class),
        @EventConfig(listeners = ApplicationRegistryControlArea.DeletePortletActionListener.class),
        @EventConfig(listeners = ApplicationRegistryControlArea.PermissionPortletActionListener.class),
        @EventConfig(listeners = ApplicationRegistryControlArea.EditPortletActionListener.class)
    }
)
public class ApplicationRegistryControlArea extends UIContainer {

  private ApplicationCategory selectedCategory ;  
  private List<ApplicationCategory> portletCategories ;
  private Application selectedPortlet = null ;
  private List<Application> portlets ;

  public ApplicationRegistryControlArea() throws Exception {
    initValues(null);
  }  

  @SuppressWarnings("unchecked")
  public void initValues(Collection portletData) throws Exception {
    ApplicationRegistryService service = getApplicationComponent(ApplicationRegistryService.class) ;
    if(portletData != null) service.importJSR168Portlets();
    portletCategories = service.getApplicationCategories(); 
    if(portletCategories == null) portletCategories = new ArrayList<ApplicationCategory>(0);
    if(portletCategories.size() > 0){
      setSelectedCategory(portletCategories.get(0));
      return;
    }
    setSelectedCategory((ApplicationCategory)null);
  }

  public ApplicationCategory getSelectedPortletCategory() { return selectedCategory; }

  @SuppressWarnings("unchecked")
  public void setSelectedCategory(Object category) throws Exception {
    selectedCategory = null;
    selectedPortlet = null;
    portlets = new ArrayList<Application>(0); 
    if(category instanceof ApplicationCategory) {
      selectedCategory = (ApplicationCategory)category;
    }else {
      for(ApplicationCategory portletCategory : portletCategories) {
        if(portletCategory.getName().equals(category)) {
          selectedCategory = portletCategory ;
          break ;
        }
      }
    }
    if(selectedCategory == null) return;
    ApplicationRegistryService service = getApplicationComponent(ApplicationRegistryService.class) ;
    portlets = service.getApplications(selectedCategory) ;
  }

  public List<ApplicationCategory> getPortletCategory() { return portletCategories ;  }

  public Application getSelectedPortlet() { return selectedPortlet ; }
  public void setSelectedPortlet(String portletId) { 
    selectedPortlet = null;
    for(Application portlet : portlets) {
      if(portlet.getId().equals(portletId)) {
        selectedPortlet = portlet ;
        break ;
      }
    }
  }

  public List<Application> getPortlets() { return portlets ;  }  
  public void setPortlets(List <Application> portlets) { this.portlets = portlets ; }
  
  static public class AddCategoryActionListener extends EventListener<ApplicationRegistryControlArea>{
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception{
      System.out.println("\n\n\n===>>>ApplicationRegistryControlArea.AddCategory");
      ApplicationRegistryControlArea uiRegistryCategory = event.getSource();
      UIPortletRegistryPortlet uiParent = uiRegistryCategory.getParent();
      ApplicationRegistryWorkingArea uiWorkingArea = uiParent.getChild(ApplicationRegistryWorkingArea.class);
      UICategoryForm uiForm = uiWorkingArea.getChild(UICategoryForm.class) ;
      uiWorkingArea.setRenderedChild(UICategoryForm.class);
      uiForm.setValue(null);
    }
  }

  static public class EditCategoryActionListener extends EventListener<ApplicationRegistryControlArea>{
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception{
      System.out.println("\n\n\n===>>>ApplicationRegistryControlArea.EditCategory");
      ApplicationRegistryControlArea uiRegistryCategory = event.getSource();
      UIPortletRegistryPortlet uiParent = uiRegistryCategory.getParent();
      ApplicationRegistryWorkingArea uiWorkingArea = uiParent.getChild(ApplicationRegistryWorkingArea.class);
      UICategoryForm uiForm = uiWorkingArea.getChild(UICategoryForm.class) ;
      uiWorkingArea.setRenderedChild(UICategoryForm.class);
      ApplicationCategory selectedCategory = uiRegistryCategory.getSelectedPortletCategory() ;
      if(selectedCategory == null) return ;
      uiForm.setValue(selectedCategory);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiParent) ;
    }
  }

  static public class ImportCategoryActionListener extends EventListener<ApplicationRegistryControlArea> {
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception {
      System.out.println("\n\n\n===>>>ApplicationRegistryControlArea.ImportCategory");
      ApplicationRegistryControlArea uiSource = event.getSource();
      PortletContainerMonitor monitor = uiSource.getApplicationComponent(PortletContainerMonitor.class);
      Collection portletDatas = monitor.getPortletRuntimeDataMap().values();       
      uiSource.initValues(portletDatas) ;     
    }
  }
  
  static public class DeleteAllActionListener extends EventListener<ApplicationRegistryControlArea> {
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception{
      ApplicationRegistryControlArea uiSource = event.getSource();
      ApplicationRegistryService service = uiSource.getApplicationComponent(ApplicationRegistryService.class);
      List<ApplicationCategory> list = uiSource.getPortletCategory();
      for(ApplicationCategory ele : list){
        service.remove(ele) ;
      }
      uiSource.initValues(null);
    }
  }

  static public class DeleteCategoryActionListener extends EventListener<ApplicationRegistryControlArea> {
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception{
      System.out.println("\n\n\n===>>>ApplicationRegistryControlArea.DeleteCategory");
      ApplicationRegistryControlArea uiComp = event.getSource();
      ApplicationRegistryService service = uiComp.getApplicationComponent(ApplicationRegistryService.class);            
      ApplicationCategory selectedCategory = uiComp.getSelectedPortletCategory();
      if(selectedCategory == null) return;      
      service.remove(selectedCategory) ; 
      uiComp.initValues(null);
    }
  }

  static public class ShowCategoryActionListener extends EventListener<ApplicationRegistryControlArea>{
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception{
      ApplicationRegistryControlArea uiComp = event.getSource();
      String categoryName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      ApplicationCategory selectedCategory = uiComp.getSelectedPortletCategory();
      if(selectedCategory.getName().equals(categoryName))  return;
      uiComp.setSelectedCategory(categoryName);      
    }
  }

  // Application Actions
  static public class ShowPortletActionListener extends EventListener<ApplicationRegistryControlArea>{
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception{
      ApplicationRegistryControlArea uiComp = event.getSource();
      String portletId = event.getRequestContext().getRequestParameter(OBJECTID) ;
      System.out.println("\n\n\n===>>>ApplicationRegistryControlArea.ShowPortlet: " + portletId);
//      uiComp.setSelectedPortlet(portletId) ;
//      Application portletSelected = uiComp.getSelectedPortlet() ;
//      if(portletSelected == null) return;
//      UIPortletRegistryPortlet uiParent = uiComp.getParent();
//      ApplicationRegistryWorkingArea uiWorkingArea = uiParent.getChild(ApplicationRegistryWorkingArea.class);
//      UIInfoPortletForm uiPortletForm = uiWorkingArea.getChild(UIInfoPortletForm.class) ;
//      String[] action = new String[0];
//      uiPortletForm.setActions(action);
//      uiPortletForm.setName("UIShowPortletForm");
//      uiPortletForm.setValues(portletSelected) ;
//      uiWorkingArea.setRenderedChild(UIInfoPortletForm.class) ;
    }
  }

  static public class AddPortletActionListener extends EventListener<ApplicationRegistryControlArea> {
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception {
      ApplicationRegistryControlArea uiComp = event.getSource();
      UIPortletRegistryPortlet uiParent = uiComp.getParent();
      ApplicationRegistryWorkingArea uiWorkingArea = uiParent.getChild(ApplicationRegistryWorkingArea.class);
      UIAvailablePortletForm uiFormPortlet = uiWorkingArea.getChild(UIAvailablePortletForm.class);
      uiFormPortlet.setValue() ;    
      uiWorkingArea.setRenderedChild(UIAvailablePortletForm.class) ;      
    }
  }

  static public class EditPortletActionListener extends EventListener<ApplicationRegistryControlArea> {
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception {
      ApplicationRegistryControlArea uicomp = event.getSource();
      Application portletSelected = uicomp.getSelectedPortlet() ;
      if(portletSelected == null) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPortletRegistryCategory.msg.editPortlet", null)) ;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return;
      }
      UIPortletRegistryPortlet uiParent = uicomp.getParent();
      ApplicationRegistryWorkingArea uiWorkingArea = uiParent.getChild(ApplicationRegistryWorkingArea.class);
      UIInfoPortletForm uiPortletForm = uiWorkingArea.getChild(UIInfoPortletForm.class) ;
      String[] actions = {"Back", "Save"};
      uiPortletForm.setName("UIEditPortletForm");
      uiPortletForm.setActions(actions);
      uiPortletForm.setValues(portletSelected) ;
      uiWorkingArea.setRenderedChild(UIInfoPortletForm.class) ;
    }
  }

  static public class PermissionPortletActionListener extends EventListener<ApplicationRegistryControlArea> {
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception {
      ApplicationRegistryControlArea uicomp = event.getSource();      
      UIPortletRegistryPortlet uiParent = uicomp.getParent();
      Application selectedPortlet = uicomp.getSelectedPortlet();
      if(selectedPortlet == null) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPortletRegistryCategory.msg.editPortlet", null)) ;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return;
      }
      
      ApplicationRegistryWorkingArea uiWorkingArea = uiParent.getChild(ApplicationRegistryWorkingArea.class);      
      UIPermissionForm uiPermissionForm = uiWorkingArea.getChild(UIPermissionForm.class);
//      uiPermissionForm.setWithRenderTab(false);
      if(selectedPortlet!=null) uiPermissionForm.setValue(selectedPortlet);      
      uiPermissionForm.setRenderedChild(UIPermissionSelector.class);      
      uiWorkingArea.setRenderedChild(UIPermissionForm.class);
    }
  }

  static public class DeletePortletActionListener extends EventListener<ApplicationRegistryControlArea> {
    public void execute(Event<ApplicationRegistryControlArea> event) throws Exception {
      ApplicationRegistryControlArea uicomp = event.getSource();
      Application selectedPortlet = uicomp.getSelectedPortlet() ;
      if(selectedPortlet == null) return ;

      ApplicationRegistryService service = uicomp.getApplicationComponent(ApplicationRegistryService.class) ;
      String portletSelectedId = selectedPortlet.getId() ;
      Application portlet = service.getApplication(portletSelectedId);
      service.remove(portlet) ;  
            
      uicomp.getPortlets().remove(uicomp.getSelectedPortlet()) ;
      uicomp.setSelectedPortlet(null);
    }
  }  

}
