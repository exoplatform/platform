/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.application.registery.Application;
import org.exoplatform.application.registery.ApplicationCategory;
import org.exoplatform.application.registery.ApplicationRegisteryService;
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
    template = "app:/groovy/portletregistry/webui/component/UIPortletRegistryCategory.gtmpl",
    events = {
        @EventConfig(listeners = UIPortletRegistryCategory.AddCategoryActionListener.class),
        @EventConfig(listeners = UIPortletRegistryCategory.EditCategoryActionListener.class),
        @EventConfig(listeners = UIPortletRegistryCategory.ImportCategoryActionListener.class),
        @EventConfig(listeners = UIPortletRegistryCategory.DeleteCategoryActionListener.class),
        @EventConfig(listeners = UIPortletRegistryCategory.DeleteAllActionListener.class),
        @EventConfig(listeners = UIPortletRegistryCategory.ShowCategoryActionListener.class),
        @EventConfig(listeners = UIPortletRegistryCategory.ShowPortletActionListener.class),
        @EventConfig(listeners = UIPortletRegistryCategory.AddPortletActionListener.class),
        @EventConfig(listeners = UIPortletRegistryCategory.DeletePortletActionListener.class),
        @EventConfig(listeners = UIPortletRegistryCategory.PermissionPortletActionListener.class),
        @EventConfig(listeners = UIPortletRegistryCategory.EditPortletActionListener.class)
    }
)
public class UIPortletRegistryCategory extends UIContainer {

  private ApplicationCategory selectedCategory ;  
  private List<ApplicationCategory> portletCategories ;
  private Application selectedPortlet = null ;
  private List<Application> portlets ;

  public UIPortletRegistryCategory() throws Exception {
    initValues(null);
  }  

  @SuppressWarnings("unchecked")
  public void initValues(Collection portletData) throws Exception {
    ApplicationRegisteryService service = getApplicationComponent(ApplicationRegisteryService.class) ;
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
    ApplicationRegisteryService service = getApplicationComponent(ApplicationRegisteryService.class) ;
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
  
  static public class AddCategoryActionListener extends EventListener<UIPortletRegistryCategory>{
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception{
      UIPortletRegistryCategory uiRegistryCategory = event.getSource();
      UIPortletRegistryPortlet uiParent = uiRegistryCategory.getParent();
      UIWorkingArea uiWorkingArea = uiParent.getChild(UIWorkingArea.class);
      UICategoryForm uiForm = uiWorkingArea.getChild(UICategoryForm.class) ;
      uiWorkingArea.setRenderedChild(UICategoryForm.class);
      uiForm.setValue(null);
    }
  }

  static public class EditCategoryActionListener extends EventListener<UIPortletRegistryCategory>{
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception{
      UIPortletRegistryCategory uiRegistryCategory = event.getSource();
      UIPortletRegistryPortlet uiParent = uiRegistryCategory.getParent();
      UIWorkingArea uiWorkingArea = uiParent.getChild(UIWorkingArea.class);
      UICategoryForm uiForm = uiWorkingArea.getChild(UICategoryForm.class) ;
      uiWorkingArea.setRenderedChild(UICategoryForm.class);
      ApplicationCategory selectedCategory = uiRegistryCategory.getSelectedPortletCategory() ;
      if(selectedCategory == null) return ;
      uiForm.setValue(selectedCategory);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiParent) ;
    }
  }

  static public class ImportCategoryActionListener extends EventListener<UIPortletRegistryCategory> {
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception {
      UIPortletRegistryCategory uiSource = event.getSource();
      PortletContainerMonitor monitor = uiSource.getApplicationComponent(PortletContainerMonitor.class);
      Collection portletDatas = monitor.getPortletRuntimeDataMap().values();       
      uiSource.initValues(portletDatas) ;     
    }
  }
  
  static public class DeleteAllActionListener extends EventListener<UIPortletRegistryCategory> {
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception{
      UIPortletRegistryCategory uiSource = event.getSource();
      ApplicationRegisteryService service = uiSource.getApplicationComponent(ApplicationRegisteryService.class);
      List<ApplicationCategory> list = uiSource.getPortletCategory();
      for(ApplicationCategory ele : list){
        service.remove(ele) ;
      }
      uiSource.initValues(null);
    }
  }

  static public class DeleteCategoryActionListener extends EventListener<UIPortletRegistryCategory> {
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception{
      UIPortletRegistryCategory uiComp = event.getSource();
      ApplicationRegisteryService service = uiComp.getApplicationComponent(ApplicationRegisteryService.class);            
      ApplicationCategory selectedCategory = uiComp.getSelectedPortletCategory();
      if(selectedCategory == null) return;      
      service.remove(selectedCategory) ; 
      uiComp.initValues(null);
    }
  }

  static public class ShowCategoryActionListener extends EventListener<UIPortletRegistryCategory>{
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception{
      UIPortletRegistryCategory uiComp = event.getSource();
      String categoryName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      ApplicationCategory selectedCategory = uiComp.getSelectedPortletCategory();
      if(selectedCategory.getName().equals(categoryName))  return;
      uiComp.setSelectedCategory(categoryName);      
    }
  }

  // Application Actions
  static public class ShowPortletActionListener extends EventListener<UIPortletRegistryCategory>{
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception{
      UIPortletRegistryCategory uiComp = event.getSource();
      String portletId = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiComp.setSelectedPortlet(portletId) ;
      Application portletSelected = uiComp.getSelectedPortlet() ;
      if(portletSelected == null) return;
      UIPortletRegistryPortlet uiParent = uiComp.getParent();
      UIWorkingArea uiWorkingArea = uiParent.getChild(UIWorkingArea.class);
      UIInfoPortletForm uiPortletForm = uiWorkingArea.getChild(UIInfoPortletForm.class) ;
      String[] action = new String[0];
      uiPortletForm.setActions(action);
      uiPortletForm.setName("UIShowPortletForm");
      uiPortletForm.setValues(portletSelected) ;
      uiWorkingArea.setRenderedChild(UIInfoPortletForm.class) ;
    }
  }

  static public class AddPortletActionListener extends EventListener<UIPortletRegistryCategory> {
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception {
      UIPortletRegistryCategory uiComp = event.getSource();
      UIPortletRegistryPortlet uiParent = uiComp.getParent();
      UIWorkingArea uiWorkingArea = uiParent.getChild(UIWorkingArea.class);
      UIAvailablePortletForm uiFormPortlet = uiWorkingArea.getChild(UIAvailablePortletForm.class);
      uiFormPortlet.setValue() ;    
      uiWorkingArea.setRenderedChild(UIAvailablePortletForm.class) ;      
    }
  }

  static public class EditPortletActionListener extends EventListener<UIPortletRegistryCategory> {
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception {
      UIPortletRegistryCategory uicomp = event.getSource();
      Application portletSelected = uicomp.getSelectedPortlet() ;
      if(portletSelected == null) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPortletRegistryCategory.msg.editPortlet", null)) ;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return;
      }
      UIPortletRegistryPortlet uiParent = uicomp.getParent();
      UIWorkingArea uiWorkingArea = uiParent.getChild(UIWorkingArea.class);
      UIInfoPortletForm uiPortletForm = uiWorkingArea.getChild(UIInfoPortletForm.class) ;
      String[] actions = {"Back", "Save"};
      uiPortletForm.setName("UIEditPortletForm");
      uiPortletForm.setActions(actions);
      uiPortletForm.setValues(portletSelected) ;
      uiWorkingArea.setRenderedChild(UIInfoPortletForm.class) ;
    }
  }

  static public class PermissionPortletActionListener extends EventListener<UIPortletRegistryCategory> {
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception {
      UIPortletRegistryCategory uicomp = event.getSource();      
      UIPortletRegistryPortlet uiParent = uicomp.getParent();
      Application selectedPortlet = uicomp.getSelectedPortlet();
      if(selectedPortlet == null) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPortletRegistryCategory.msg.editPortlet", null)) ;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return;
      }
      
      UIWorkingArea uiWorkingArea = uiParent.getChild(UIWorkingArea.class);      
      UIPermissionForm uiPermissionForm = uiWorkingArea.getChild(UIPermissionForm.class);
//      uiPermissionForm.setWithRenderTab(false);
      if(selectedPortlet!=null) uiPermissionForm.setValue(selectedPortlet);      
      uiPermissionForm.setRenderedChild(UIPermissionSelector.class);      
      uiWorkingArea.setRenderedChild(UIPermissionForm.class);
    }
  }

  static public class DeletePortletActionListener extends EventListener<UIPortletRegistryCategory> {
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception {
      UIPortletRegistryCategory uicomp = event.getSource();
      Application selectedPortlet = uicomp.getSelectedPortlet() ;
      if(selectedPortlet == null) return ;

      ApplicationRegisteryService service = uicomp.getApplicationComponent(ApplicationRegisteryService.class) ;
      String portletSelectedId = selectedPortlet.getId() ;
      Application portlet = service.getApplication(portletSelectedId);
      service.remove(portlet) ;  
            
      uicomp.getPortlets().remove(uicomp.getSelectedPortlet()) ;
      uicomp.setSelectedPortlet(null);
    }
  }  

}
