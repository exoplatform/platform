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
package org.exoplatform.i18n.webui.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * Nov 01, 2007
 */

@ComponentConfigs ( {
  @ComponentConfig(
      lifecycle = UIApplicationLifecycle.class,
      events = {
        @EventConfig (listeners = UII18nPortlet.ViewActionListener.class),
        @EventConfig (listeners = UII18nPortlet.DeleteActionListener.class, confirm="UII18n.deleteResource")
      }
  ),
  
  @ComponentConfig(
      id = "UISearchI18n",
      type = UIForm.class,
      lifecycle = UIFormLifecycle.class,
      template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
      events = {
        @EventConfig (listeners = UII18nPortlet.SearchActionListener.class),
        @EventConfig (listeners = UII18nPortlet.NewResourceActionListener.class)
      }
  )
})
public class UII18nPortlet extends UIPortletApplication {
  private static String[] RESOURCE_LIST = {"name", "language"} ;
  private static String[] RESOURCE_ACTION = {"View", "Delete"} ;
  
  private Query lastQuery_ ;

  public UII18nPortlet() throws Exception {
    
    UIGrid grid_ = addChild(UIGrid.class, null, "ResourceList") ;
    grid_.configure("id", RESOURCE_LIST, RESOURCE_ACTION) ;
    grid_.setRendered(true) ;
    
    addChild(UIEditResource.class,null,null).setRendered(false) ;
    
    UIForm uiSearchResource = addChild(UIForm.class,"UISearchI18n", null);
    uiSearchResource.addUIFormInput(new UIFormStringInput("name","name",null));
    
    LocaleConfigService service = getApplicationComponent(LocaleConfigService.class) ;
    Iterator i = service.getLocalConfigs().iterator() ;
    
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    options.add(new SelectItemOption<String>("All", "All"));

    while (i.hasNext()) {
      LocaleConfig config = (LocaleConfig) i.next() ;
      options.add(new SelectItemOption<String>(config.getLocaleName(), config.getLanguage()))  ;
    }
    
    uiSearchResource.addUIFormInput(new UIFormSelectBox("language","language",options));
    uiSearchResource.setRendered(true) ;
    // update grid
    update(null,null);
  } 
  
  public Query getLastQuery() { return lastQuery_; }
  
  static public class DeleteActionListener extends EventListener<UII18nPortlet> {
    public void execute(Event<UII18nPortlet> event) throws Exception {
      UII18nPortlet uiI18n = event.getSource() ;
      UIPageIterator pageIterator = uiI18n.getChild(UIGrid.class).getUIPageIterator() ;
      int currentPage = pageIterator.getCurrentPage() ;
      ResourceBundleService serv = uiI18n.getApplicationComponent(ResourceBundleService.class);
      serv.removeResourceBundleData(event.getRequestContext().getRequestParameter(OBJECTID)) ;
      Query lastQuery = uiI18n.getLastQuery() ;
      uiI18n.update(lastQuery.getName(), lastQuery.getLanguage()) ;
      while(currentPage > pageIterator.getAvailablePage()) currentPage-- ;
      pageIterator.setCurrentPage(currentPage) ;
    }
  }
  
  static public class ViewActionListener extends EventListener<UII18nPortlet> {
    public void execute(Event<UII18nPortlet> event) throws Exception {
      UII18nPortlet uiI18n = event.getSource() ;
      
      UIEditResource uiEditResource = uiI18n.getChild(UIEditResource.class) ;
      uiEditResource.setRendered(true) ;
      uiEditResource.getChild(UIFormTextAreaInput.class).setEditable(false) ;
      uiEditResource.setActions(new String[]{"Edit", "Cancel"});
      String paramID = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiEditResource.setResource(paramID) ;
      
      uiI18n.getChild(UIGrid.class).setRendered(false) ;
      UIForm uiSearch = uiI18n.getChildById("UISearchI18n") ;
      uiSearch.setRendered(false) ;
    }
  }

  static public class SearchActionListener  extends EventListener<UIForm> {
    public void execute(Event<UIForm> event) throws Exception {
      UIForm uiSearch = event.getSource() ;
      UII18nPortlet uiI18n = uiSearch.getParent() ;
      String language = uiSearch.getChild(UIFormSelectBox.class).getValue() ;
      if ("All".equals(language)) language = null ;
      uiI18n.update(uiSearch.getChild(UIFormStringInput.class).getValue(), language);
    }
  }
  static public class NewResourceActionListener  extends EventListener<UIForm> {
    public void execute(Event<UIForm> event) throws Exception {
      UII18nPortlet uiI18n = event.getSource().getParent() ;
      
      UIEditResource uiEditResource = uiI18n.getChild(UIEditResource.class) ;
      uiEditResource.setRendered(true) ;
      uiEditResource.setResource(null) ;
      uiEditResource.setActions(new String[]{"Save", "Cancel"});
      
      uiI18n.getChild(UIGrid.class).setRendered(false) ;
      UIForm uiSearch = uiI18n.getChildById("UISearchI18n") ;
      uiSearch.setRendered(false) ;
      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiI18n) ;
    }
  }
  
  public void update(String name , String lang) throws Exception {
    if(name!=null && name.trim().length()>0 && name.indexOf("*")<0){ 
      if(name.charAt(0)!='*') name = "*"+name ;
      if(name.charAt(name.length()-1)!='*') name += "*" ;
    }
    ResourceBundleService resBundleServ = getApplicationComponent(ResourceBundleService.class);
    lastQuery_ = new Query(name, lang) ;
    PageList pageList = resBundleServ.findResourceDescriptions(lastQuery_) ;
    pageList.setPageSize(10) ;
    getChild(UIGrid.class).getUIPageIterator().setPageList(pageList) ;
    UIPageIterator pageIterator = getChild(UIGrid.class).getUIPageIterator();
    if(pageIterator.getAvailable() == 0 ) {
      UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
      uiApp.addMessage(new ApplicationMessage("UISearchForm.msg.empty", null)) ;
    }
  }
}

