/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.application.registery.Application;
import org.exoplatform.application.registery.ApplicationCategory;
import org.exoplatform.application.registery.ApplicationRegisteryService;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
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
  private ApplicationCategory selectedPCategory;

  @SuppressWarnings("unchecked")
  public UIPortletOptions() throws Exception {
    setId("UIPortletOptions");
    pCategoryDatas = new ArrayList<PortletCategoryData>();
    UIDropDownItemSelector dropCategorys = addChild(UIDropDownItemSelector.class, null, null);
    dropCategorys.setTitle("PortletCategory");
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    dropCategorys.setOptions(options);
    ApplicationRegisteryService service = getApplicationComponent(ApplicationRegisteryService.class) ;
    List<ApplicationCategory> pCategories = service.getApplicationCategories() ;    
    Collections.sort(pCategories, new PortletCategoryComparator()) ;
    
//    UserACL userACL = getApplicationComponent(UserACL.class) ;
//    String remoteUser = RequestContext.<RequestContext>getCurrentInstance().getRemoteUser();

    PortletComparator portletComparator = new PortletComparator() ;
    for(ApplicationCategory pCategory : pCategories) {
      List<Application> portlets = service.getApplications(pCategory) ;
      Iterator<Application> iterator = portlets.iterator();
      while (iterator.hasNext()) {
        Application portlet = iterator.next();
        String perm = null;//portlet.getViewPermission();
        if(perm == null) perm = "member:/user";
//        if(userACL.hasPermission(null, remoteUser, perm)) continue;
        iterator.remove();
      }
      if(portlets.size() < 1)  continue;
      if(selectedPCategory == null) selectedPCategory = pCategory;
      Collections.sort(portlets, portletComparator) ;
      pCategoryDatas.add(new PortletCategoryData(pCategory, portlets)); 
    }    
    for(PortletCategoryData categoryData: pCategoryDatas) {
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

}
