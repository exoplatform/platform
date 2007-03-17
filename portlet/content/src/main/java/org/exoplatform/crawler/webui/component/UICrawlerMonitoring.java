/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.crawler.webui.component;

import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 26, 2006  
 */
@ComponentConfig(
  template = "app:/groovy/crawler/webui/component/UICrawlerMonitoring.gtmpl",
  events = {
      @EventConfig(name = "ConfigActionListener", listeners = UICrawlerMonitoring.ConfigActionListener.class )
    }
)
public class UICrawlerMonitoring extends UIContainer {
  
  public UICrawlerMonitoring() throws Exception {
    
  }
  
  static public class ConfigActionListener extends EventListener<UICrawlerMonitoring> {
    public void execute(Event<UICrawlerMonitoring> event) throws Exception {
      UICrawlerMonitoring uicom = event.getSource() ;
      String controlActionName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UICrawlerPortlet uiParent = uicom.getAncestorOfType(UICrawlerPortlet.class) ;
      UIContainer uiWorkingArea = uiParent.getChildById(UICrawlerPortlet.UI_WORKING_AREA_ID) ;

      if(controlActionName.equals("UICrawlerMonitoring")) {
        Class [] childrenToRender = {UICrawlerLoadingConfig.class, UICrawlerLoadingInfo.class, UICrawlerLoadedNews.class } ;
        uiWorkingArea.setRenderedChildrenOfTypes(childrenToRender) ;
      }
    }
  }

}
