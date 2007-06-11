/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
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
import org.exoplatform.webui.form.UIFormTabPane;
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
public class UIAvailablePortletForm extends UIFormTabPane {   

  final static String [] TABLE_COLUMNS = {"label", "description", "input"};
  
  public UIAvailablePortletForm() throws Exception {
    super("UIFormAvailablePortlet", false);
    setInfoBar(false);
    setRenderResourceTabName(false) ;
  } 

  @SuppressWarnings("unchecked")
  public void setValue() throws Exception {
    getChildren().clear();
    
    String tableName = getClass().getSimpleName();    
    UIFormTableInputSet uiTableInputSet = createUIComponent(UIFormTableInputSet.class, null, null) ;
    uiTableInputSet.setName(tableName);
    uiTableInputSet.setColumns(TABLE_COLUMNS);
    addChild(uiTableInputSet);
    
    ApplicationRegistryService registeryService = getApplicationComponent(ApplicationRegistryService.class) ;
    List<Application> applications = registeryService.getAllApplications();
//    while(iter.hasNext()){
//      String id = String.valueOf(iter.next());
//      Application portlet = null; 
//      try{
//         portlet = registeryService.getApplication(id);
//      }catch (Exception exp) {
//        exp.printStackTrace();
//      }
//      if(portlet == null) continue;  
    for(Application portlet: applications) {
      String id = portlet.getId();
      UIFormInputSet uiInputSet = new UIFormInputSet(portlet.getId()) ;
      UIFormInputInfo uiInfo = new UIFormInputInfo("label", null, portlet.getDisplayName());
      uiInputSet.addChild(uiInfo);
      uiInfo = new UIFormInputInfo("description", null, portlet.getApplicationType());
      uiInputSet.addChild(uiInfo);
      UIFormCheckBoxInput<String> uiCheckbox = new UIFormCheckBoxInput<String>(id, null, id);       
      uiCheckbox.setValue(id);
      uiInputSet.addChild(uiCheckbox);
      uiTableInputSet.addChild(uiInputSet);
    }
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
      
      for(UIFormCheckBoxInput<String> ele : listCheckbox){
        if(!ele.isChecked())continue;    
        Application portlet = service.getApplication(ele.getValue());       
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

