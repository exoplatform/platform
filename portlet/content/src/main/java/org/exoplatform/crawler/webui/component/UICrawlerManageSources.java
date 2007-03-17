/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.crawler.webui.component;

import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * Jul 26, 2006  
 */
@ComponentConfig(
  template = "app:/groovy/crawler/webui/component/UICrawlerManageSources.gtmpl",
  events = {
    @EventConfig(name = "ControlBarActionListener", listeners = UICrawlerManageSources.ManageSourcesControlBarActionListener.class ),
    @EventConfig(name = "SourceFormAction", listeners = UICrawlerManageSources.AddSourceActionListener.class)
  }
)

public class UICrawlerManageSources extends UIComponent {
  
  public UICrawlerManageSources() throws Exception {
    
  }
  
  static public class ManageSourcesControlBarActionListener extends EventListener<UICrawlerManageSources> {
    public void execute(Event<UICrawlerManageSources> event) throws Exception {
      UICrawlerManageSources uicom = event.getSource() ;
      String controlActionName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UICrawlerPortlet uiParent = uicom.getAncestorOfType(UICrawlerPortlet.class) ;
      UIContainer uiWorkingArea = uiParent.getChildById(UICrawlerPortlet.UI_WORKING_AREA_ID) ;

      if(controlActionName.equals("UICategoryForm")) {
        Class [] childrenToRender = {UICategoryForm.class } ;
        uiWorkingArea.setRenderedChildrenOfTypes(childrenToRender) ;
      } else if(controlActionName.equals("UIDescription")) {
        Class [] childrenToRender = {UICrawlerDescription.class} ;
        uiWorkingArea.setRenderedChildrenOfTypes(childrenToRender) ;
      } else if (controlActionName.equals("NewsList")) {
        Class [] childrenToRender = {UICrawlerNavigationPage.class } ;
        uiWorkingArea.setRenderedChildrenOfTypes(childrenToRender) ;
      }
      
    }
  }
  
  static public class AddSourceActionListener extends EventListener<UICrawlerManageSources> {
    public void execute(Event<UICrawlerManageSources> event) throws Exception {
      UICrawlerManageSources uiCrawlerManageSources = event.getSource();      
      UICrawlerPortlet uiParent = uiCrawlerManageSources.getAncestorOfType(UICrawlerPortlet.class);
      Class [] childrenToRender = {UICrawlerNavigation.class, UISourceForm.class};
      uiParent.setRenderedChildrenOfTypes(childrenToRender);
    }
  }
}
