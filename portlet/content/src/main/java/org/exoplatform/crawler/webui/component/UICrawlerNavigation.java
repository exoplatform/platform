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
import org.exoplatform.crawler.webui.component.UICrawlerNavigation.*;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 26, 2006  
 */
@ComponentConfig(
  template = "app:/groovy/crawler/webui/component/UICrawlerNavigation.gtmpl",
  events = {
    @EventConfig(name = "controlTab", listeners = ControlTabListener.class)
  }
)
public class UICrawlerNavigation  extends UIContainer {

  private String tabName_ ;
  public UICrawlerNavigation() throws Exception {
  	addChild(UIListArticles.class, null, null);
  	addChild(UICrawlerManageSources.class, null, null).setRendered(false);
  	addChild(UICrawlerMonitoring.class, null, null).setRendered(false);
  }
  
  public String getTabName() { return tabName_ ; }
  public void setTabName(String tabName) { tabName_ = tabName ; }
  
  static public class ControlTabListener extends EventListener<UICrawlerNavigation> {
    public void execute(Event<UICrawlerNavigation> event) throws Exception {
      UICrawlerNavigation uicom = event.getSource() ;
      String tabname = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uicom.setTabName(tabname) ;
      if(tabname == null) {
        uicom.getChild(UIListArticles.class).setRendered(true) ;
        uicom.getChild(UICrawlerManageSources.class).setRendered(false) ;
        uicom.getChild(UICrawlerMonitoring.class).setRendered(false) ;
      } else if(tabname.equals("UICrawlerManageSources")) {
        uicom.getChild(UIListArticles.class).setRendered(false) ;
        uicom.getChild(UICrawlerManageSources.class).setRendered(true) ;
        uicom.getChild(UICrawlerMonitoring.class).setRendered(false) ;
      } else {
        uicom.getChild(UIListArticles.class).setRendered(false) ;
        uicom.getChild(UICrawlerManageSources.class).setRendered(false) ;
        uicom.getChild(UICrawlerMonitoring.class).setRendered(true) ;
      }
    }
  }
}
