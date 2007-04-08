/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.services.portletregistery.Portlet;
import org.exoplatform.services.portletregistery.PortletCategory;
import org.exoplatform.services.portletregistery.PortletRegisteryService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIDropDownItemSelector;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
@ComponentConfig(
  template = "app:/groovy/portal/webui/component/customization/UIPortletOptions.gtmpl"
)
public class UIPortletOptions extends UIContainer {

  private List<PortletCategoryData> pCategoryDatas ; 
  private PortletCategory selectedPCategory;

  @SuppressWarnings("unchecked")
  public UIPortletOptions() throws Exception {
    setId("UIPortletOptions");
    pCategoryDatas = new ArrayList<PortletCategoryData>();
    UIDropDownItemSelector dropCategorys = addChild(UIDropDownItemSelector.class, null, null);
    dropCategorys.setTitle("PortletCategory");
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    dropCategorys.setOptions(options);
    PortletRegisteryService service = getApplicationComponent(PortletRegisteryService.class) ;
    List<PortletCategory> pCategories = service.getPortletCategories() ;    
    Collections.sort(pCategories, new PortletCategoryComparator()) ;

    PortletComparator portletComparator = new PortletComparator() ;
    for(PortletCategory pCategory : pCategories) {
      List<Portlet> portlets = service.getPortlets(pCategory.getId()) ;
      if(portlets.size() < 1)  continue;
      if(selectedPCategory == null) selectedPCategory = pCategory;
      Collections.sort(portlets, portletComparator) ;
      pCategoryDatas.add(new PortletCategoryData(pCategory, portlets)); 
    }    
    for(PortletCategoryData categoryData: pCategoryDatas) {
      options.add(new SelectItemOption<String>(categoryData.getPortletCategory().getPortletCategoryName()));
    }
  }

  public String removeStringPortlet(String name) {
    int index = name.lastIndexOf("Portlet") ;
    if(index != 0 && index != -1) return name.substring(0,index) ;
    return name ;
  }
  
  
  public Portlet getPortlet(String id) throws Exception {
    for(PortletCategoryData category : pCategoryDatas){
      List<Portlet> items = category.getPortlets();
      for(Portlet item : items){
        if(item.getId().equals(id)) return item;
      }      
    }
    return null;
  }

  public PortletCategory getSelectedPCategory() { return selectedPCategory; }

  public List<PortletCategoryData> getPortletCategorDatas() { return pCategoryDatas ; }
  
  public void processRender(WebuiRequestContext context) throws Exception {   
    super.processRender(context);    
    Util.showComponentLayoutMode(UIPortlet.class);   
  }
  
  static class PortletCategoryComparator implements Comparator<PortletCategory> {
    public int compare(PortletCategory cat1, PortletCategory cat2) {
      return cat1.getPortletCategoryName().compareTo(cat2.getPortletCategoryName()) ;
    }
  }

  static class PortletComparator implements Comparator<Portlet> {
    public int compare(Portlet p1, Portlet p2) {
      return p1.getPortletName().compareTo(p2.getPortletName()) ;
    }
  }

  static public class PortletCategoryData {

    private PortletCategory portletCategory;    
    private List<Portlet> portlets;

    public PortletCategoryData(PortletCategory portletCategory, List<Portlet> portlets) {
      this.portletCategory = portletCategory;
      this.portlets = portlets;
    }

    public PortletCategory getPortletCategory() { return portletCategory; }

    public List<Portlet> getPortlets() { return portlets; }
  }

}
