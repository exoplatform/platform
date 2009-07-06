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
package org.exoplatform.samples.virtuallist;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.UIRepeater;
import org.exoplatform.webui.core.UIVirtualList;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by The eXo Platform SAS Author : LiemNC ncliam@gmail.com Aug 2, 2009
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class)
public class UIVirtualListPortlet extends UIPortletApplication {

  public UIVirtualListPortlet() throws Exception {
    addChild(UISampleResourcesBrowser.class, null, "UISampleResourcesBrowser");
  }

  @ComponentConfig(template = "app:/groovy/webui/component/UISampleResourcesBrowser.gtmpl")
  static public class UISampleResourcesBrowser extends UIContainer {   
    
    private static String[] RESOURCE_LIST = {"name", "language"} ;
    private static String[] RESOURCE_ACTION = {"View", "Delete"} ;

    public UISampleResourcesBrowser() throws Exception {      
      UIRepeater uiRepeater = createUIComponent(UIRepeater.class, null, "ScrollableResourceList");
      uiRepeater.configure("name", RESOURCE_LIST, RESOURCE_ACTION);
      uiRepeater.setRendered(true);
      
      UIVirtualList virtualList = addChild(UIVirtualList.class, null, "UIVirtualList1");
      virtualList.setRendered(true);
      virtualList.setHeight(200);
      virtualList.setPageSize(10);
      virtualList.setDataFeed(uiRepeater);
    }
    
    public String event(String name, String beanId) throws Exception {
      if(Util.getUIPortal().getName().equals(beanId)) return super.url(name, beanId); 
      return super.event(name, beanId);
    }

    public void loadPortalConfigs() throws Exception {      
      try{
        ResourceBundleService resBundleServ = getApplicationComponent(ResourceBundleService.class);
        org.exoplatform.services.resources.Query lastQuery_ = new org.exoplatform.services.resources.Query(null, null) ;
        PageList pageList = resBundleServ.findResourceDescriptions(lastQuery_) ;
        UIVirtualList virtualList = getChild(UIVirtualList.class);
        virtualList.attachDataSource(pageList);
        UIPageIterator pageIterator = virtualList.getChild(UIRepeater.class).getUIPageIterator();
        if(pageIterator.getAvailable() == 0 ) {
          throw new Exception("No results") ;
        }
      } catch (Exception e) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UISearchForm.msg.empty", null)) ;
      }
    }
  } 
}
