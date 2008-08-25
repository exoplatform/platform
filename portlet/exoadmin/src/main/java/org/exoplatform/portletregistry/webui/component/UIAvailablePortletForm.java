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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.PortletData;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPopupComponent;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInputInfo;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormPageIterator;
import org.exoplatform.webui.form.UIFormTableInputSet;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jul 7, 2006
 * 11:43:12 AM 
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    events = {
      @EventConfig(listeners = UIAvailablePortletForm.SaveActionListener.class),
      @EventConfig(listeners = UIAvailablePortletForm.BackActionListener.class, phase = Phase.DECODE)
    }
)
public class UIAvailablePortletForm extends UIForm implements UIPopupComponent {   

  final static String [] TABLE_COLUMNS = {"label", "description", "input"};
  List<Application> list_ = new ArrayList<Application>();
  
  public UIAvailablePortletForm() throws Exception {
    String tableName = getClass().getSimpleName();    
    UIFormTableIteratorInputSet uiTableInputSet = createUIComponent(UIFormTableIteratorInputSet.class, null, null) ;
    uiTableInputSet.setName(tableName);
    uiTableInputSet.setColumns(TABLE_COLUMNS);
    addChild(uiTableInputSet);
  } 

  @SuppressWarnings("unchecked")
  public void setValue() throws Exception {
      list_.clear() ;
      PortletContainerService containerService = getApplicationComponent(PortletContainerService.class);
      ApplicationRegistryService service = getApplicationComponent(ApplicationRegistryService.class) ;
      List<Application> existedApps = service.getAllApplications() ;
      Map map = containerService.getAllPortletMetaData();
      Iterator iter = map.keySet().iterator();
      while(iter.hasNext()){
        String id = String.valueOf(iter.next());
        Application application = null; 
        application = findPortletInDataRuntime(id);
        if(application == null ) continue;
        list_.add(application);
      } 
      Comparator application = new Comparator<Application>(){
        public int compare(Application portlet1, Application portlet2){  
          return portlet1.getDisplayName().compareTo(portlet2.getDisplayName());
        }
      };    
      Collections.sort(list_, application);
      setup();
  }
  
  private void setup() throws Exception {
    List<UIFormInputSet> uiInputSetList = new ArrayList<UIFormInputSet>() ;
    UIFormTableInputSet uiTableInputSet = getChild(UIFormTableInputSet.class) ;
    int i = 0;
    for(Application portlet: list_) {
      UIFormInputSet uiInputSet = new UIFormInputSet(portlet.getId()) ;
      UIFormInputInfo uiInfo = new UIFormInputInfo("label", null, portlet.getDisplayName());
      uiInputSet.addChild(uiInfo);
      String description = portlet.getDescription();
      if(description == null || description.length() < 1) description = portlet.getApplicationType();
      uiInfo = new UIFormInputInfo("description", null, description);
      uiInputSet.addChild(uiInfo);
      UIFormCheckBoxInput<Integer> uiCheckbox = new UIFormCheckBoxInput<Integer>(portlet.getId(), null, i);    
      i++;
      uiInputSet.addChild(uiCheckbox);
      uiTableInputSet.addChild(uiInputSet);
      uiInputSetList.add(uiInputSet) ;
    }
    UIFormPageIterator uiIterator = uiTableInputSet.getChild(UIFormPageIterator.class) ;
    PageList pageList = new ObjectPageList(uiInputSetList, 10) ;
    uiIterator.setPageList(pageList) ;    
  }

  public List<Application> getListApplication() { return list_;}

  public void setListApplication(List<Application> list) {this.list_ = list; }

  @SuppressWarnings("unchecked")
  private Application findPortletInDataRuntime(String id) {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    PortletContainerService pcService =
      (PortletContainerService) container.getComponentInstanceOfType(PortletContainerService.class) ;
    Map<String, PortletData> allPortletMetaData = pcService.getAllPortletMetaData();
    Iterator<String> iterator = allPortletMetaData.keySet().iterator();
    
    while(iterator.hasNext()) {
      String fullName = iterator.next();
      String categoryName = fullName.split("/")[0];
      String portletName = fullName.split("/")[1];
      
      if(id.equals(fullName)){
        Application app = new Application();
        app.setDisplayName(portletName) ;
        app.setApplicationName(portletName);
        app.setApplicationGroup(categoryName);
        app.setCategoryName(categoryName);
        app.setApplicationType(org.exoplatform.web.application.Application.EXO_PORTLET_TYPE);
        app.setDescription("A portlet application");
        app.setAccessPermissions(new ArrayList<String>());
        return  app;
      }
    }
      return null;
    }

  public void processDecode(WebuiRequestContext context) throws Exception {
    setSubmitAction(context.getRequestParameter(UIForm.ACTION)) ;
    List<UIComponent>  children = getChildren() ;
    for(UIComponent uiChild :  children) {
      uiChild.processDecode(context) ;     
    }
    String action =  getSubmitAction();
    String subComponentId = context.getRequestParameter(UIForm.SUBCOMPONENT_ID);
    if(subComponentId == null || subComponentId.trim().length() < 1) {
      Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;
      if(event != null) event.broadcast() ;
      return;
    }
    UIComponent uiSubComponent = findComponentById(subComponentId);
    Event<UIComponent> event = uiSubComponent.createEvent(action, Event.Phase.DECODE, context) ;
    if(event != null)  event.broadcast() ;
  }

  @SuppressWarnings("unchecked")
  static public class SaveActionListener  extends EventListener<UIAvailablePortletForm> {
    public void execute(Event<UIAvailablePortletForm> event) throws Exception {
      UIAvailablePortletForm uiForm = event.getSource() ;
      UIPopupWindow parent = uiForm.getParent();
      parent.setShow(false);
      
      List<UIFormCheckBoxInput> listCheckbox =  new ArrayList<UIFormCheckBoxInput>();
      uiForm.findComponentOfType(listCheckbox, UIFormCheckBoxInput.class);
     
      UIPortletRegistryPortlet uiRegistryPortlet = event.getSource().getAncestorOfType(UIPortletRegistryPortlet.class);
      ApplicationRegistryControlArea uiRegistryCategory =  uiRegistryPortlet.getChild(ApplicationRegistryControlArea.class);
      ApplicationCategory selectedCategory = uiRegistryCategory.getSelectedPortletCategory();      
      ApplicationRegistryService service = event.getSource().getApplicationComponent(ApplicationRegistryService.class);
      
      Comparator portletComparator = new Comparator<Application>(){
        public int compare(Application portlet1, Application portlet2){  
          return portlet1.getId().compareTo(portlet2.getId());
        }
      };     
      
      List<Application> oldPortlets = uiRegistryCategory.getPortlets();      
      Collections.sort(oldPortlets, portletComparator);
      
      for(UIFormCheckBoxInput<Integer> ele : listCheckbox){
        if(!ele.isChecked())continue;    
        Application portlet = uiForm.getListApplication().get(ele.getValue());    
        if(Collections.binarySearch(oldPortlets, portlet, portletComparator) > -1) continue;
        Application newPortlet = clonePortlet(portlet);
        service.save(selectedCategory, newPortlet);
      }      
      uiRegistryCategory.initApplicationCategories() ;
      uiRegistryCategory.setSelectedCategory(selectedCategory);
    }  
    
    private Application clonePortlet(Application portlet){
      Application newPortlet = new Application();
      newPortlet.setAccessPermissions(portlet.getAccessPermissions()) ;
      newPortlet.setApplicationGroup(portlet.getApplicationGroup());
      newPortlet.setApplicationType(portlet.getApplicationType());
      newPortlet.setApplicationName(portlet.getApplicationName()) ;
      newPortlet.setDescription(portlet.getDescription()) ;
      newPortlet.setDisplayName(portlet.getDisplayName()) ;
      return newPortlet;
    }
  }

  static public class BackActionListener  extends EventListener<UIAvailablePortletForm> {
    public void execute(Event<UIAvailablePortletForm> event) throws Exception {     
      UIAvailablePortletForm uiForm = event.getSource() ;
      UIPopupWindow parent = uiForm.getParent();
      parent.setShow(false);
    }
  }
  
  public void activate() throws Exception {}
  public void deActivate() throws Exception {} 
}
