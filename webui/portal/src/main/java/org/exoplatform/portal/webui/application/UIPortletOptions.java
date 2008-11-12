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
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIDropDownControl;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfigs( {
  @ComponentConfig (
      template = "app:/groovy/portal/webui/application/UIPortletOptions.gtmpl"
    ),
  
  @ComponentConfig (
      type = UIDropDownControl.class ,
      id = "UIDropDownPorletOptions",
      template = "system:/groovy/webui/core/UIDropDownControl.gtmpl",
      events = {
        @EventConfig(listeners = UIPortletOptions.ChangeOptionActionListener.class)
      }
    )
})
public class UIPortletOptions extends UIContainer {

  private List<PortletCategoryData> pCategoryDatas ; 
  private ApplicationCategory selectedPCategory;

  @SuppressWarnings("unchecked")
  public UIPortletOptions() throws Exception {
    setId("UIPortletOptions");
    pCategoryDatas = new ArrayList<PortletCategoryData>();
    UIDropDownControl dropCategorys = addChild(UIDropDownControl.class, "UIDropDownPorletOptions", "UIDropDownPorletOptions");
    dropCategorys.setParent(this);
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    dropCategorys.setOptions(options);
    ApplicationRegistryService service = getApplicationComponent(ApplicationRegistryService.class) ;
    String remoteUser = RequestContext.<RequestContext>getCurrentInstance().getRemoteUser();
    List<ApplicationCategory> pCategories = service.getApplicationCategories(remoteUser, org.exoplatform.web.application.Application.EXO_PORTLET_TYPE) ; 
    Iterator<ApplicationCategory> cateItr = pCategories.iterator() ;
    while(cateItr.hasNext()) {
      ApplicationCategory cate = cateItr.next() ;
      List<Application> applications = cate.getApplications() ;
      if(applications.size()<1) cateItr.remove() ;
    }
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
      options.add(new SelectItemOption<String>(categoryData.getPortletCategory().getDisplayName(),
                                               categoryData.getPortletCategory().getName()));
    }
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
      return cat1.getDisplayName().compareTo(cat2.getDisplayName()) ;
    }
  }

  static class PortletComparator implements Comparator<Application> {
    public int compare(Application p1, Application p2) {
      return p1.getDisplayName().compareTo(p2.getDisplayName()) ;
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
  
  static  public class ChangeOptionActionListener extends EventListener<UIDropDownControl> {
    public void execute(Event<UIDropDownControl> event) throws Exception {
      String selectedContainerId  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIDropDownControl uiDropDown = event.getSource();
      UIPortletOptions uiPortletOptions = uiDropDown.getParent();
      uiDropDown.setValue(selectedContainerId);
      uiPortletOptions.setCategorySelected(selectedContainerId);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortletOptions.getParent());
    }
  }

  

}
