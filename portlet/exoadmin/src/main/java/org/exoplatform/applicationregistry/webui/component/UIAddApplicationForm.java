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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.applicationregistry.webui.Util;
import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.PortletData;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInputInfo;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormPageIterator;
import org.exoplatform.webui.form.UIFormRadioBoxInput;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTableInputSet;
import org.exoplatform.webui.form.validator.StringLengthValidator;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jul 10, 2008  
 */
@ComponentConfig(
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    lifecycle = UIFormLifecycle.class,
    events = {
      @EventConfig(listeners = UIAddApplicationForm.ChangeTypeActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIAddApplicationForm.AddActionListener.class),
      @EventConfig(listeners = UIAddApplicationForm.CancelActionListener.class, phase = Phase.DECODE)
    }
)
public class UIAddApplicationForm extends UIForm {
  
  final static public String FIELD_NAME = "displayName" ;
  final static public String FIELD_TYPE = "type" ;
  final static public String FIELD_APPLICATION = "application" ;
  final static String [] TABLE_COLUMNS = {"input", "label", "description"};
  
  private List<Application> applications_ = new ArrayList<Application>() ;
  
  public UIAddApplicationForm() throws Exception {
	  
	WebuiRequestContext contextres = WebuiRequestContext.getCurrentInstance();
	ResourceBundle res = contextres.getApplicationResourceBundle();  
	
    addUIFormInput(new UIFormStringInput(FIELD_NAME, null, null).
                   addValidator(StringLengthValidator.class, 3, 30) ) ;
    List<SelectItemOption<String>> types = new ArrayList<SelectItemOption<String>>(2) ;
    types.add(new SelectItemOption<String>(org.exoplatform.web.application.Application.EXO_PORTLET_TYPE)) ;
    types.add(new SelectItemOption<String>(org.exoplatform.web.application.Application.EXO_GAGGET_TYPE)) ;
    UIFormSelectBox uiSelectBox = new UIFormSelectBox(FIELD_TYPE, null, types) ;
    uiSelectBox.setOnChange("ChangeType") ;
    addUIFormInput(uiSelectBox) ;
    String tableName = getClass().getSimpleName();    
    UIFormTableIteratorInputSet uiTableInputSet = createUIComponent(UIFormTableIteratorInputSet.class, null, null) ;
    uiTableInputSet.setName(tableName);
    uiTableInputSet.setColumns(TABLE_COLUMNS);
    addChild(uiTableInputSet);
    setApplicationList(org.exoplatform.web.application.Application.EXO_PORTLET_TYPE) ;
    setActions(new String[]{"Add", "Cancel"}) ;
  }
  
  public List<Application> getApplications() { return applications_ ; }
  
  public void setApplicationList(String type) throws Exception {
    applications_.clear() ;
    applications_ = getApplcationByType(type) ;
    setup() ;   
  }
  
  private void setup() throws Exception {
    List<UIFormInputSet> uiInputSetList = new ArrayList<UIFormInputSet>() ;
    UIFormTableInputSet uiTableInputSet = getChild(UIFormTableInputSet.class) ;
    int i = 0 ;
    for(Application app: applications_) {
      UIFormInputSet uiInputSet = new UIFormInputSet(app.getId()) ;
      ArrayList<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>(5) ;
      options.add(new SelectItemOption<String>("", String.valueOf(i))) ;
      UIFormRadioBoxInput uiRadioInput = new UIFormRadioBoxInput(FIELD_APPLICATION, "", options) ;
      //TODO review
      if(i == 0) {
        uiRadioInput.setValue(options.get(0).getValue());
      }
      //----------------------------------------------
      uiInputSet.addChild(uiRadioInput);
      UIFormInputInfo uiInfo = new UIFormInputInfo("label", null, app.getDisplayName());
      uiInputSet.addChild(uiInfo);
      uiInfo = new UIFormInputInfo("description", null, app.getDescription());
      uiInputSet.addChild(uiInfo);
      uiTableInputSet.addChild(uiInputSet);
      uiInputSetList.add(uiInputSet) ;
      i++ ;
    }
    UIFormPageIterator uiIterator = uiTableInputSet.getChild(UIFormPageIterator.class) ;
    LazyPageList<UIFormInputSet> pageList = new LazyPageList<UIFormInputSet>(new FormInputSetListAccess(uiInputSetList), 10);
    uiIterator.setPageList(pageList) ;    
  }
  
  private List<Application> getApplcationByType(String type) throws Exception {
    List<Application> list = new ArrayList<Application>(10) ;
    if(org.exoplatform.web.application.Application.EXO_PORTLET_TYPE.equals(type)) {
      ExoContainer manager  = ExoContainerContext.getCurrentContainer();
      PortletContainerService pcService =
        (PortletContainerService) manager.getComponentInstanceOfType(PortletContainerService.class) ;
      Map<String, PortletData> allPortletMetaData = pcService.getAllPortletMetaData();
       Iterator<Entry<String, PortletData>> iterator = allPortletMetaData.entrySet().iterator();

      while(iterator.hasNext()) {
        Entry<String, PortletData> entry = iterator.next() ;
        String fullName = entry.getKey();
        String categoryName = fullName.split("/")[0];
        String portletName = fullName.split("/")[1];
        PortletData portlet = entry.getValue();
        Application app = new Application();
        app.setApplicationName(portletName);
        app.setApplicationGroup(categoryName);
        app.setApplicationType(org.exoplatform.web.application.Application.EXO_PORTLET_TYPE);
        app.setDisplayName(Util.getDisplayNameValue(portlet.getDisplayName(), portletName)) ;
        app.setDescription(Util.getDescriptionValue(portlet.getDescription(), portletName));
        app.setAccessPermissions(new ArrayList<String>());
        list.add(app) ;
      }
    } else if(org.exoplatform.web.application.Application.EXO_GAGGET_TYPE.equals(type)) {
      GadgetRegistryService gadgetService = getApplicationComponent(GadgetRegistryService.class) ;
      Iterator<Gadget> iterator = gadgetService.getAllGadgets().iterator() ;
      while(iterator.hasNext()) {
        Gadget tmp = iterator.next() ;
        Application app = new Application() ;
        app.setApplicationName(tmp.getName()) ;
        app.setApplicationGroup(GadgetApplication.EXO_GADGET_GROUP) ;
        app.setApplicationType(org.exoplatform.web.application.Application.EXO_GAGGET_TYPE) ;
        app.setDisplayName(tmp.getTitle()) ;
        String description = (tmp.getDescription() == null || tmp.getDescription().length() < 1) ? tmp.getName() : tmp.getDescription() ; 
        app.setDescription(description) ;
        app.setAccessPermissions(new ArrayList<String>()) ;
        list.add(app) ;
      }
    }
    return list ;
  }
  
  public static class ChangeTypeActionListener extends EventListener<UIAddApplicationForm> {

    public void execute(Event<UIAddApplicationForm> event) throws Exception {
      UIAddApplicationForm uiForm = event.getSource() ;
      String type = uiForm.getUIFormSelectBox(UIAddApplicationForm.FIELD_TYPE).getValue() ;
      uiForm.setApplicationList(type) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm) ; 
    }
    
  }
  
  public static class AddActionListener extends EventListener<UIAddApplicationForm> {

    public void execute(Event<UIAddApplicationForm> event) throws Exception {
      UIAddApplicationForm uiForm = event.getSource() ;
      UIApplicationOrganizer uiOrganizer = uiForm.getParent() ;
      WebuiRequestContext ctx = event.getRequestContext();
      ApplicationRegistryService appRegService = uiForm.getApplicationComponent(ApplicationRegistryService.class) ;
      ApplicationCategory selectedCate = uiOrganizer.getSelectedCategory() ;
      if(appRegService.getApplicationCategory(selectedCate.getName()) == null) {
        uiOrganizer.reload();
        UIApplication uiApp = ctx.getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("category.msg.changeNotExist", null)) ;
        ctx.addUIComponentToUpdateByAjax(uiOrganizer) ;
        return ;
      }
      
      UIFormRadioBoxInput uiRadio = uiForm.getUIInput("application") ;
      String displayName = uiForm.getUIStringInput(FIELD_NAME).getValue() ;
      Application tmp = uiForm.getApplications().get(Integer.parseInt(uiRadio.getValue()));
      
      // check portet name is exist
      if (appRegService.getApplication(selectedCate.getName(), tmp.getApplicationName()) != null) {
        WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
        UIApplication uiApp = context.getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIAddApplicationForm.msg.PortletExist", null)) ;
        return;
      }
      
      Application app = cloneApplication(tmp) ;      
      if(displayName != null && displayName.trim().length() > 0) {
        app.setDisplayName(displayName) ;
      }
      
      appRegService.save(selectedCate, app) ;
      uiOrganizer.setSelectedCategory(selectedCate);
      uiOrganizer.selectApplication(app.getApplicationName()) ;
      ctx.addUIComponentToUpdateByAjax(uiOrganizer) ;
    }
    
    private Application cloneApplication(Application app){
      Application newApp = new Application();
      newApp.setApplicationName(app.getApplicationName()) ;
      newApp.setDisplayName(app.getDisplayName()) ;
      newApp.setApplicationType(app.getApplicationType());
      newApp.setApplicationGroup(app.getApplicationGroup());
      newApp.setDescription(app.getDescription()) ;
      newApp.setAccessPermissions(app.getAccessPermissions()) ;
      return newApp;
    }
    
  }
  
  public static class CancelActionListener extends EventListener<UIAddApplicationForm> {

    public void execute(Event<UIAddApplicationForm> event) throws Exception {
      UIApplicationOrganizer uiOrganizer = event.getSource().getParent() ;
      uiOrganizer.setSelectedApplication(uiOrganizer.getSelectedApplication()) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer) ;

    }
    
  }

  
}
