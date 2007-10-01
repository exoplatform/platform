/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIDropDownItemSelector;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
@ComponentConfig(
  template = "app:/groovy/portal/webui/application/UIPortletOptions.gtmpl",
  events = @EventConfig(listeners = UIPortletOptions.ChangeOptionActionListener.class)
)
public class UIPortletOptions extends UIContainer {

  private List<PortletCategoryData> pCategoryDatas ; 
  private ApplicationCategory selectedPCategory;

  @SuppressWarnings("unchecked")
  public UIPortletOptions() throws Exception {
    setId("UIPortletOptions");
    pCategoryDatas = new ArrayList<PortletCategoryData>();
    UIDropDownItemSelector dropCategorys = addChild(UIDropDownItemSelector.class, null, null);
    dropCategorys.setTitle("PortletCategory");
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    dropCategorys.setOptions(options);
    ApplicationRegistryService service = getApplicationComponent(ApplicationRegistryService.class) ;
    dropCategorys.setOnServer(true);
    dropCategorys.setOnChange("ChangeOption");
    String remoteUser = RequestContext.<RequestContext>getCurrentInstance().getRemoteUser();
    List<ApplicationCategory> pCategories = service.getApplicationCategories(remoteUser, org.exoplatform.web.application.Application.EXO_PORTLET_TYPE) ; 
    Collections.sort(pCategories, new PortletCategoryComparator()) ;
    PortletComparator portletComparator = new PortletComparator() ;
    for(ApplicationCategory pCategory : pCategories) {
      List<Application> portlets = pCategory.getApplications();
      if(selectedPCategory == null) selectedPCategory = pCategory;
      Collections.sort(portlets, portletComparator) ;
      pCategoryDatas.add(new PortletCategoryData(pCategory, portlets)); 
    }    
    
    for(PortletCategoryData categoryData: pCategoryDatas) {
      categoryData.getPortlets();
      options.add(new SelectItemOption<String>(categoryData.getPortletCategory().getName()));
    }
  }

  public String removeStringPortlet(String name) {
    int index = name.lastIndexOf("Portlet") ;
    if(index != 0 && index != -1) return name.substring(0,index) ;
    return name ;
  }
  
  
  public Application getPortlet(String id) throws Exception {
    for(PortletCategoryData category : pCategoryDatas){
      List<Application> items = category.getPortlets();
      for(Application item : items){
        if(item.getId().equals(id)) return item;
      }      
    }
    return null;
  }

  public ApplicationCategory getSelectedPCategory() { return selectedPCategory; }
  public void setCategorySelected(String selectedContainerId) {
    for(PortletCategoryData categoryData: pCategoryDatas){
      if(categoryData.getPortletCategory().getName().equals(selectedContainerId)){
        selectedPCategory = categoryData.getPortletCategory();
      }
    }
  }
  public List<PortletCategoryData> getPortletCategorDatas() { return pCategoryDatas ; }
  
  public void processRender(WebuiRequestContext context) throws Exception {   
    super.processRender(context);    
    Util.showComponentLayoutMode(UIPortlet.class);   
  }
  
  static class PortletCategoryComparator implements Comparator<ApplicationCategory> {
    public int compare(ApplicationCategory cat1, ApplicationCategory cat2) {
      return cat1.getName().compareTo(cat2.getName()) ;
    }
  }

  static class PortletComparator implements Comparator<Application> {
    public int compare(Application p1, Application p2) {
      return p1.getApplicationName().compareTo(p2.getApplicationName()) ;
    }
  }

  static public class PortletCategoryData {

    private ApplicationCategory portletCategory;    
    private List<Application> portlets;

    public PortletCategoryData(ApplicationCategory portletCategory, List<Application> portlets) {
      this.portletCategory = portletCategory;
      this.portlets = portlets;
    }

    public ApplicationCategory getPortletCategory() { return portletCategory; }

    public List<Application> getPortlets() { return portlets; }
  }
  
  static  public class ChangeOptionActionListener extends EventListener<UIPortletOptions> {
    public void execute(Event<UIPortletOptions> event) throws Exception {
      String selectedContainerId  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPortletOptions uiPortletOptions = event.getSource();
      UIDropDownItemSelector uiDropDownItemSelector = uiPortletOptions.getChild(UIDropDownItemSelector.class);
      SelectItemOption<String> option = uiDropDownItemSelector.getOption(selectedContainerId);
      if(option != null) uiDropDownItemSelector.setSelectedItem(option);
      uiPortletOptions.setCategorySelected(selectedContainerId);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortletOptions.getParent());
    }
  }

  

}
