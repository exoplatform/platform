/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 11, 2009  
 */
@ComponentConfig(
                 template = "system:/groovy/portal/webui/application/UIApplicationList.gtmpl",
                 events = {
                     @EventConfig(listeners = UIApplicationList.SelectCategoryActionListener.class)
                 }
)
public class UIApplicationList extends UIContainer {
  private List<ApplicationCategory> categories ; 
  private ApplicationCategory selectedCategory;

  @SuppressWarnings("unchecked")
  public UIApplicationList() throws Exception {
    ApplicationRegistryService service = getApplicationComponent(ApplicationRegistryService.class) ;
    String remoteUser = Util.getPortalRequestContext().getRemoteUser();
    PortletComparator portletComparator = new PortletComparator() ;
    categories = service.getApplicationCategories(remoteUser, org.exoplatform.web.application.Application.EXO_PORTLET_TYPE) ; 
    Collections.sort(categories, new PortletCategoryComparator()) ;
    Iterator<ApplicationCategory> cateItr = categories.iterator() ;
    while(cateItr.hasNext()) {
      ApplicationCategory cate = cateItr.next() ;
      List<Application> applications = cate.getApplications() ;
      if(applications.size()<1) cateItr.remove() ;
      else Collections.sort(applications, portletComparator) ;
    }
    setSelectedCategory(categories.get(0).getName());
  }

  public Application getPortlet(String id) throws Exception {
    for(ApplicationCategory category : categories){
      List<Application> items = category.getApplications();
      for(Application item : items){
        if(item.getId().equals(id)) return item;
      }      
    }
    return null;
  }

  public ApplicationCategory getSelectedCategory() { return selectedCategory; }
  
  public void setSelectedCategory(String categoryName) {
    for(ApplicationCategory category: categories){
      if(category.getName().equals(categoryName)){
        selectedCategory = category;
      }
    }
  }
  public List<ApplicationCategory> getCategories() { return categories ; }
  
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
  
  static public class SelectCategoryActionListener extends EventListener<UIApplicationList> {
    public void execute(Event<UIApplicationList> event) throws Exception {
      String category = event.getRequestContext().getRequestParameter(OBJECTID);
      UIApplicationList uiApplicationList = event.getSource();
      uiApplicationList.setSelectedCategory(category);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiApplicationList);
    }
    
  }
}