/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.organization.webui.component.UIPermissionSelector;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.services.portletcontainer.monitor.PortletContainerMonitor;
import org.exoplatform.services.portletregistery.Portlet;
import org.exoplatform.services.portletregistery.PortletCategory;
import org.exoplatform.services.portletregistery.PortletRegisteryService;
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

  private PortletCategory selectedCategory ;  
  private List<PortletCategory> portletCategories ;
  private Portlet selectedPortlet = null ;
  private List<Portlet> portlets ;

  public UIPortletRegistryCategory() throws Exception {
    initValues(null);
  }  

  @SuppressWarnings("unchecked")
  public void initValues(Collection portletData) throws Exception {
    PortletRegisteryService service = getApplicationComponent(PortletRegisteryService.class) ;
    if(portletData != null) service.importPortlets(portletData);
    portletCategories = service.getPortletCategories();   
    if(portletCategories == null) portletCategories = new ArrayList<PortletCategory>(0);
    if(portletCategories.size() > 0){
      setSelectedCategory(portletCategories.get(0));
      return;
    }
    setSelectedCategory((PortletCategory)null);
  }

  public PortletCategory getSelectedPortletCategory() { return selectedCategory; }

  @SuppressWarnings("unchecked")
  public void setSelectedCategory(Object category) throws Exception {
    selectedCategory = null;
    selectedPortlet = null;
    portlets = new ArrayList<Portlet>(0); 
    if(category instanceof PortletCategory) {
      selectedCategory = (PortletCategory)category;
    }else {
      for(PortletCategory portletCategory : portletCategories) {
        if(portletCategory.getPortletCategoryName().equals(category)) {
          selectedCategory = portletCategory ;
          break ;
        }
      }
    }
    if(selectedCategory == null) return;
    PortletRegisteryService service = getApplicationComponent(PortletRegisteryService.class) ;
    portlets = service.getPortlets(selectedCategory.getId()) ;
  }

  public List<PortletCategory> getPortletCategory() { return portletCategories ;  }

  public Portlet getSelectedPortlet() { return selectedPortlet ; }
  public void setSelectedPortlet(String portletId) { 
    selectedPortlet = null;
    for(Portlet portlet : portlets) {
      if(portlet.getId().equals(portletId)) {
        selectedPortlet = portlet ;
        break ;
      }
    }
  }

  public List<Portlet> getPortlets() { return portlets ;  }  
  public void setPortlets(List <Portlet> portlets) { this.portlets = portlets ; }
  
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
      PortletCategory selectedCategory = uiRegistryCategory.getSelectedPortletCategory() ;
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
      PortletRegisteryService service = uiSource.getApplicationComponent(PortletRegisteryService.class);
      List<PortletCategory> list = uiSource.getPortletCategory();
      for(PortletCategory ele : list){
        service.removePortletCategory(ele.getId()) ;
      }
      uiSource.initValues(null);
    }
  }

  static public class DeleteCategoryActionListener extends EventListener<UIPortletRegistryCategory> {
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception{
      UIPortletRegistryCategory uiComp = event.getSource();
      PortletRegisteryService service = uiComp.getApplicationComponent(PortletRegisteryService.class);            
      PortletCategory selectedCategory = uiComp.getSelectedPortletCategory();
      if(selectedCategory == null) return;      
      service.removePortletCategory(selectedCategory.getId()) ; 
      uiComp.initValues(null);
    }
  }

  static public class ShowCategoryActionListener extends EventListener<UIPortletRegistryCategory>{
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception{
      UIPortletRegistryCategory uiComp = event.getSource();
      String categoryName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      PortletCategory selectedCategory = uiComp.getSelectedPortletCategory();
      if(selectedCategory.getPortletCategoryName().equals(categoryName))  return;
      uiComp.setSelectedCategory(categoryName);      
    }
  }

  // Portlet Actions
  static public class ShowPortletActionListener extends EventListener<UIPortletRegistryCategory>{
    public void execute(Event<UIPortletRegistryCategory> event) throws Exception{
      UIPortletRegistryCategory uiComp = event.getSource();
      String portletId = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiComp.setSelectedPortlet(portletId) ;
      Portlet portletSelected = uiComp.getSelectedPortlet() ;
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
      Portlet portletSelected = uicomp.getSelectedPortlet() ;
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
      Portlet selectedPortlet = uicomp.getSelectedPortlet();
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
      Portlet selectedPortlet = uicomp.getSelectedPortlet() ;
      if(selectedPortlet == null) return ;

      PortletRegisteryService service = uicomp.getApplicationComponent(PortletRegisteryService.class) ;
      String portletSelectedId = selectedPortlet.getId() ;
      service.removePortlet(portletSelectedId) ;  
            
      uicomp.getPortlets().remove(uicomp.getSelectedPortlet()) ;
      uicomp.setSelectedPortlet(null);
    }
  }  

}
