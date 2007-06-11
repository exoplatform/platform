/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.crawler.webui.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 26, 2006  
 */
@ComponentConfig(
  template = "app:/groovy/crawler/webui/component/UIListArticles.gtmpl",
  events = {
    @EventConfig(name = "ReadNewsActionListener", listeners = UIListArticles.ReadNewsActionListener.class )
  }
)
public class UIListArticles extends UIContainer {
  
  public UIListArticles() throws Exception{
    
  }

  static public class ReadNewsActionListener extends EventListener<UIListArticles> {
    public void execute(Event<UIListArticles> event) throws Exception {
      UIListArticles uicom = event.getSource() ;
      String controlActionName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UICrawlerPortlet uiParent = uicom.getAncestorOfType(UICrawlerPortlet.class) ;
      UIContainer uiWorkingArea = uiParent.getChildById(UICrawlerPortlet.UI_WORKING_AREA_ID) ;

      if(controlActionName.equals("UICrawlerContentList")) {
        Class [] childrenToRender = {UICrawlerNavigationPage.class, UICrawlerContentList.class } ;
        uiWorkingArea.setRenderedChildrenOfTypes(childrenToRender) ;
      }
    }
  }
  
}
