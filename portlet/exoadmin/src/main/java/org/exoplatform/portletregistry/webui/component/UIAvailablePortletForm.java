/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletregistery.Portlet;
import org.exoplatform.services.portletregistery.PortletCategory;
import org.exoplatform.services.portletregistery.PortletRegisteryService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIDescription;
import org.exoplatform.webui.component.UIFormCheckBoxInput;
import org.exoplatform.webui.component.UIFormInputInfo;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.UIFormTableInputSet;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jul 7, 2006
 * 11:43:12 AM 
 */
@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/component/UIFormWithTitle.gtmpl",
  events = {
    @EventConfig(listeners = UIAvailablePortletForm.SaveActionListener.class),
    @EventConfig(listeners = UIAvailablePortletForm.BackActionListener.class, phase = Phase.DECODE)
  }
)
public class UIAvailablePortletForm extends UIFormTabPane {   

  final static String [] TABLE_COLUMNS = {"label", "description", "input"};
  
  public UIAvailablePortletForm() throws Exception {
    super("UIFormAvailablePortlet", false);
    super.setInfoBar(false);
    super.setRenderResourceTabName(false) ;
  } 

  @SuppressWarnings("unchecked")
  public void setValue() throws Exception {
    getChildren().clear();
    
    String tableName = getClass().getSimpleName();    
    UIFormTableInputSet uiTableInputSet = createUIComponent(UIFormTableInputSet.class, null, null) ;
    uiTableInputSet.setName(tableName);
    uiTableInputSet.setColumns(TABLE_COLUMNS);
    addChild(uiTableInputSet);
    
    PortletContainerService containerService = getApplicationComponent(PortletContainerService.class);
    Map map = containerService.getAllPortletMetaData();
    Iterator iter = map.keySet().iterator();
    PortletRegisteryService registeryService = getApplicationComponent(PortletRegisteryService.class) ;
    while(iter.hasNext()){
      String id = String.valueOf(iter.next());
      Portlet portlet = null; 
      try{
         portlet = registeryService.getPortlet(id);
      }catch (Exception exp) {
        exp.printStackTrace();
      }
      if(portlet == null) continue;  
      UIFormInputSet uiInputSet = new UIFormInputSet(portlet.getId()) ;
      UIFormInputInfo uiInfo = new UIFormInputInfo("label", null, portlet.getDisplayName());
      uiInputSet.addChild(uiInfo);
      uiInfo = new UIFormInputInfo("description", null, portlet.getDescription());
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
      List<UIFormCheckBoxInput> listCheckbox =  new ArrayList<UIFormCheckBoxInput>();
      event.getSource().findComponentOfType(listCheckbox, UIFormCheckBoxInput.class);
     
      UIPortletRegistryPortlet uiRegistryPortlet = event.getSource().getAncestorOfType(UIPortletRegistryPortlet.class);
      UIPortletRegistryCategory uiRegistryCategory =  uiRegistryPortlet.getChild(UIPortletRegistryCategory.class);
      PortletCategory selectedCategory = uiRegistryCategory.getSelectedPortletCategory();      
      PortletRegisteryService service = event.getSource().getApplicationComponent(PortletRegisteryService.class);
      
      Comparator portletComparator = new Comparator<Portlet>(){
        public int compare(Portlet portlet1, Portlet portlet2){  
          return portlet1.getId().compareTo(portlet2.getId());
        }
      };     
      
      List<Portlet> oldPortlets = uiRegistryCategory.getPortlets();      
      Collections.sort(oldPortlets, portletComparator);
      
      for(UIFormCheckBoxInput<String> ele : listCheckbox){
        if(!ele.isChecked())continue;    
        Portlet portlet = service.getPortlet(ele.getValue());       
        if(Collections.binarySearch(oldPortlets, portlet, portletComparator) > -1) continue;
        Portlet newPortlet = clonePortlet(service, portlet);
        service.addPortlet(selectedCategory, newPortlet);
      }      
      uiRegistryCategory.initValues(null);
      uiRegistryCategory.setSelectedCategory(selectedCategory);
      event.getSource().setRenderSibbling(UIDescription.class) ;
    }  
    
    private Portlet clonePortlet(PortletRegisteryService service, Portlet portlet){
      Portlet newPortlet = service.createPortletInstance() ;
      newPortlet.setPortletName(portlet.getPortletName()) ;
      newPortlet.setDescription(portlet.getDescription()) ;
      newPortlet.setDisplayName(portlet.getDisplayName()) ;
      newPortlet.setPortletApplicationName(portlet.getPortletApplicationName()) ;
      newPortlet.setCreatedDate(portlet.getCreatedDate()) ;
      return newPortlet;
    }
  }

  static public class BackActionListener  extends EventListener<UIAvailablePortletForm> {
    public void execute(Event<UIAvailablePortletForm> event) throws Exception {     
      UIAvailablePortletForm uiForm = event.getSource() ;
      uiForm.setRenderSibbling(UIDescription.class) ;
    }
  }

}

