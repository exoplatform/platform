/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.applicationregistry.webui.component;

import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 24, 2008  
 */
@ComponentConfig(
    template = "app:/groovy/applicationregistry/webui/component/UIApplicationOrganizer.gtmpl",
    events = {
        @EventConfig(listeners = UIApplicationOrganizer.ShowCategoryActionListener.class),
        @EventConfig(listeners = UIApplicationOrganizer.ImportAllApplicationsActionListener.class),
        @EventConfig(listeners = UIApplicationOrganizer.SelectApplicationActionListener.class),
        @EventConfig(listeners = UIApplicationOrganizer.AddCategoryActionListener.class),
        @EventConfig(listeners = UIApplicationOrganizer.RemoveCategoryActionListener.class),
        @EventConfig(listeners = UIApplicationOrganizer.EditCategoryActionListener.class),
        @EventConfig(listeners = UIApplicationOrganizer.AddApplicationActionListener.class),
        @EventConfig(listeners = UIApplicationOrganizer.RemoveApplicationActionListener.class)
    }
)    
public class UIApplicationOrganizer extends UIContainer {
  
  private ApplicationCategory selectedCategory ;  
  private List<ApplicationCategory> categories ;
  private Application selectedApplication = null ;
  private List<Application> applications ;

  
  public UIApplicationOrganizer() throws Exception {
    initApplicationCategories() ;
  }
  
  public void initApplicationCategories() throws Exception {
    ApplicationRegistryService service = getApplicationComponent(ApplicationRegistryService.class);
    categories = service.getApplicationCategories();
    if(categories == null || categories.size() < 1) {
      setSelectedCategory((ApplicationCategory)null);
      return ;
    }
    setSelectedCategory(categories.get(0)) ;
  }

  public List<ApplicationCategory> getCategories() { return categories ;  }

  public ApplicationCategory getSelectedCategory() { return selectedCategory; }
  
  public void setSelectedCategory(String name) throws Exception {
    for(ApplicationCategory ele : categories) {
      if(ele.getName().equals(name)) {
        setSelectedCategory(ele) ;
        break ;
      }
    }
  }

  public void setSelectedCategory(ApplicationCategory category) throws Exception {
    selectedCategory = category;
    if(selectedCategory == null) {
      applications = null;
      setSelectedApplication(null);
      return;
    }
    ApplicationRegistryService service = getApplicationComponent(ApplicationRegistryService.class);
    applications = service.getApplications(selectedCategory, new String [] {});
    if(applications == null || applications.size() < 1) {
      setSelectedApplication(null);
      return ;
    }
    setSelectedApplication(applications.get(0));
  }
  
  public ApplicationCategory getCategory(String name) {
    for(ApplicationCategory category: categories){
      if(category.getName().equals(name))return category;
    }
    return null;
  }
  
  public Application getSelectedApplication() { return selectedApplication ; }
  
  public void setSelectedApplication(Application app) throws Exception {
    selectedApplication = app ;
    if(selectedApplication == null) {
      removeChild(UIApplicationInfo.class);
      return;
    }
    UIApplicationInfo uiAppInfo = getChild(UIApplicationInfo.class) ;
    if(uiAppInfo == null) {
      uiAppInfo = addChild(UIApplicationInfo.class, null, null) ;
    }
    uiAppInfo.setApplication(selectedApplication) ;
  }
  
  public List<Application> getApplications() { return applications ;  }
  
  public Application getApplication(String name) {
    for(Application ele : applications) {
      if(ele.getApplicationName().equals(name)) return ele ;
    }
    return null ;
  }
  
  public void selectApplication(String name) throws Exception {
    for(Application ele : applications) {
      if(ele.getApplicationName().equals(name)) {
        setSelectedApplication(ele) ;
        break ;
        
      }
    }
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
  }
  
  public static class ShowCategoryActionListener extends EventListener<UIApplicationOrganizer> {

    public void execute(Event<UIApplicationOrganizer> event) throws Exception {
      String categoryName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIApplicationOrganizer uiOrganizer = event.getSource() ;
      uiOrganizer.setSelectedCategory(categoryName) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer) ;
    }
    
  }
  
  public static class ImportAllApplicationsActionListener extends EventListener<UIApplicationOrganizer> {

    public void execute(Event<UIApplicationOrganizer> event) throws Exception {
      UIApplicationOrganizer uiOrganizer = event.getSource() ;
      ApplicationRegistryService service = uiOrganizer.getApplicationComponent(ApplicationRegistryService.class) ;
      service.importAllPortlets() ;
      service.importExoGadgets() ;
      uiOrganizer.initApplicationCategories() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer) ;
    }
    
  }
  
  public static class SelectApplicationActionListener extends EventListener<UIApplicationOrganizer> {

    public void execute(Event<UIApplicationOrganizer> event) throws Exception {
      String appName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIApplicationOrganizer uiOrganizer = event.getSource() ;
      uiOrganizer.selectApplication(appName) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer) ;
    }
    
  }
  
  public static class AddCategoryActionListener extends EventListener<UIApplicationOrganizer> {

    public void execute(Event<UIApplicationOrganizer> event) throws Exception {
     UIApplicationOrganizer uiOrganizer = event.getSource() ;
     uiOrganizer.getChildren().clear() ;
     uiOrganizer.addChild(UICategoryForm.class, null, null) ;
     event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer) ;
    }
    
  }
  
  public static class RemoveCategoryActionListener extends EventListener<UIApplicationOrganizer> {

    public void execute(Event<UIApplicationOrganizer> event) throws Exception {
      UIApplicationOrganizer uiOrganizer = event.getSource() ;
      String name = event.getRequestContext().getRequestParameter(OBJECTID) ;
      ApplicationRegistryService service = uiOrganizer.getApplicationComponent(ApplicationRegistryService.class) ;
      service.remove(uiOrganizer.getCategory(name)) ;
      uiOrganizer.initApplicationCategories() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer) ;
    }
    
  }
  
  public static class EditCategoryActionListener extends EventListener<UIApplicationOrganizer> {

    public void execute(Event<UIApplicationOrganizer> event) throws Exception {
      UIApplicationOrganizer uiOrganizer = event.getSource() ;
      String name = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UICategoryForm uiCategoryForm = uiOrganizer.createUIComponent(UICategoryForm.class, null, null) ;
      uiCategoryForm.setValue(uiOrganizer.getCategory(name))  ;
      uiOrganizer.getChildren().clear() ;
      uiOrganizer.addChild(uiCategoryForm) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer) ;
    }
    
  }
  
  public static class AddApplicationActionListener extends EventListener<UIApplicationOrganizer> {

    public void execute(Event<UIApplicationOrganizer> event) throws Exception {
      UIApplicationOrganizer uiOrganizer = event.getSource() ;
      uiOrganizer.getChildren().clear() ;
      uiOrganizer.addChild(UIAddApplicationForm.class, null, null) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer) ;      
    }
    
  }
  
  public static class RemoveApplicationActionListener extends EventListener<UIApplicationOrganizer> {

    public void execute(Event<UIApplicationOrganizer> event) throws Exception {
     UIApplicationOrganizer uiOrganizer = event.getSource() ;
     String appName = event.getRequestContext().getRequestParameter(OBJECTID) ;
     ApplicationRegistryService service = uiOrganizer.getApplicationComponent(ApplicationRegistryService.class) ;
     Application app = uiOrganizer.getApplication(appName) ;
     service.remove(app) ;
     String cateName = uiOrganizer.getSelectedCategory().getName() ;
     uiOrganizer.initApplicationCategories() ;
     uiOrganizer.setSelectedCategory(cateName) ;
     event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer) ;
    }
    
  }

}
