/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import java.util.ArrayList;
import java.util.Collection;
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
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.monitor.PortletContainerMonitor;
import org.exoplatform.services.portletcontainer.monitor.PortletRuntimeData;
import org.exoplatform.web.WebAppController;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInputInfo;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormPageIterator;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.UIFormTableInputSet;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jul 7, 2006
 * 11:43:12 AM 
 */
@ComponentConfigs({
    @ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template = "system:/groovy/webui/form/UIForm.gtmpl",
        events = {
          @EventConfig(listeners = UIAvailablePortletForm.SaveActionListener.class),
          @EventConfig(listeners = UIAvailablePortletForm.BackActionListener.class, phase = Phase.DECODE)
        }
      ),
    //TODO: Tung.Pham added
    @ComponentConfig(
        type = UIFormTableInputSet.class,
        id = "PortletFormTableInputSet",
        template = "system:/groovy/webui/form/UIFormTablePageIteratorInputSet.gtmpl"
    )

})
public class UIAvailablePortletForm extends UIFormTabPane {   

  final static String [] TABLE_COLUMNS = {"label", "description", "input"};
  List<Application> list_ = new ArrayList<Application>();
  
  public UIAvailablePortletForm() throws Exception {
    super("UIFormAvailablePortlet", false);
    setInfoBar(false);
    setRenderResourceTabName(false) ;
    //TODO: Tung Pham added
    //-------------------------------------------
    String tableName = getClass().getSimpleName();    
    UIFormTableInputSet uiTableInputSet = createUIComponent(UIFormTableInputSet.class, "PortletFormTableInputSet", null) ;
    uiTableInputSet.setName(tableName);
    uiTableInputSet.setColumns(TABLE_COLUMNS);
    UIFormPageIterator uiIterator = createUIComponent(UIFormPageIterator.class, null, null) ;
    uiTableInputSet.addChild(uiIterator) ;
    addChild(uiTableInputSet);
    //-------------------------------------------
  } 

    @SuppressWarnings("unchecked")
  public void setValue() throws Exception {
    PortletContainerService containerService = getApplicationComponent(PortletContainerService.class);
    Map map = containerService.getAllPortletMetaData();
    Iterator iter = map.keySet().iterator();
    ApplicationRegistryService registeryService = getApplicationComponent(ApplicationRegistryService.class) ;
    while(iter.hasNext()){
      String id = String.valueOf(iter.next());
      Application portlet = null; 
      try{
         portlet = registeryService.getApplication(id);
      }catch (Exception exp) {
        exp.printStackTrace();
      }
      if(portlet == null)  portlet = findPortletInDataRuntime(id);
      if(portlet == null ) continue;
      list_.add(portlet);
    } 
    findExoApplication();
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
    //TODO: Tun.Pham added
    UIFormPageIterator uiIterator = uiTableInputSet.getChild(UIFormPageIterator.class) ;
    PageList pageList = new ObjectPageList(uiInputSetList, 10) ;
    uiIterator.setPageList(pageList) ;
    //--------------------------------
    
  }

  private  void findExoApplication() throws Exception {
    PortalContainer container  = PortalContainer.getInstance() ;
    WebAppController appController = 
      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
    List<org.exoplatform.web.application.Application> eXoApplications = 
      appController.getApplicationByType(org.exoplatform.web.application.Application.EXO_APPLICATION_TYPE) ;
    List<org.exoplatform.web.application.Application> eXoWidgets = 
      appController.getApplicationByType(org.exoplatform.web.application.Application.EXO_WIDGET_TYPE) ;
    eXoApplications.addAll(eXoWidgets);     
    ApplicationRegistryService registeryService = getApplicationComponent(ApplicationRegistryService.class) ;
    for(org.exoplatform.web.application.Application app: eXoApplications) {
      String temp = app.getApplicationGroup()+ "/" + app.getApplicationName();
      Application portlet = registeryService.getApplication(temp);
      if(portlet == null) list_.add( cloneApplication(app));
      else list_.add(portlet);
     }
  }
  
  private Application cloneApplication(org.exoplatform.web.application.Application app) {
    Application newApplication = new Application() ;
    newApplication.setApplicationGroup(app.getApplicationGroup()) ;
    newApplication.setApplicationType(app.getApplicationType()) ;
    newApplication.setApplicationName(app.getApplicationName()) ;
    newApplication.setId(app.getApplicationId()) ;
    newApplication.setCategoryName(app.getApplicationGroup()) ;
    newApplication.setDisplayName(app.getApplicationName()) ;
    newApplication.setDescription(app.getDescription()) ;
    
    return newApplication ;
  }

  public List<Application> getListApplication() { return list_;}
  public void setListApplication(List<Application> list) {this.list_ = list; }

  @SuppressWarnings("unchecked")
  private Application findPortletInDataRuntime(String id) {
    PortalContainer manager  = PortalContainer.getInstance();
    PortletContainerMonitor monitor =
      (PortletContainerMonitor) manager.getComponentInstanceOfType(PortletContainerMonitor.class) ;
    Collection portletDatas = monitor.getPortletRuntimeDataMap().values();  
    Iterator iterator = portletDatas.iterator();
    while(iterator.hasNext()) {
      PortletRuntimeData portletRuntimeData = (PortletRuntimeData) iterator.next();
      String categoryName = portletRuntimeData.getPortletAppName();
      String portletName = portletRuntimeData.getPortletName();
      String fullName = categoryName + "/" + portletName;
      if(id.equals(fullName)){
        Application app = new Application();
        app.setId(fullName);
        app.setDisplayName(portletName) ;
        app.setApplicationName(portletName);
        app.setApplicationGroup(categoryName);
        app.setApplicationType("jsr168-portlet");
        app.setDescription("jsr168 portlet application");
        app.setAccessPermissions(new String[]{});
        return  app;
      }
    }
      return null;
    }

  public void processDecode(WebuiRequestContext context) throws Exception {
    super.processDecode(context);
    for(UIComponent child : getChildren())  {
      child.processDecode(context) ;
    }
  }

  @SuppressWarnings("unchecked")
  static public class SaveActionListener  extends EventListener<UIAvailablePortletForm> {
    public void execute(Event<UIAvailablePortletForm> event) throws Exception {     
      UIAvailablePortletForm uiForm = event.getSource() ;
      UIPopupWindow parent = uiForm.getParent();
      parent.setShow(false);
      List<UIFormCheckBoxInput> listCheckbox =  new ArrayList<UIFormCheckBoxInput>();
      event.getSource().findComponentOfType(listCheckbox, UIFormCheckBoxInput.class);
     
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
        Application portlet = event.getSource().getListApplication().get(ele.getValue());    
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

 
}

