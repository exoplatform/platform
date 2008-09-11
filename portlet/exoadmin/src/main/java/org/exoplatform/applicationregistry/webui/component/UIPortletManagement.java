/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.applicationregistry.webui.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.PortletData;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 24, 2008  
 */

@ComponentConfig(
    template = "app:/groovy/applicationregistry/webui/component/UIPortletManagement.gtmpl",
    events = {
        @EventConfig(listeners = UIPortletManagement.SelectPortletActionListener.class)
    }
)

public class UIPortletManagement extends UIContainer {
  
  static final public String LOCAL = "local";
  static final public String REMOTE = "remote";
  private List<PortletData> localPortlets;
  private List<PortletData> remotePortlets;
  private PortletData selectedPorlet ;
  
  public UIPortletManagement() throws Exception {
    init() ;
    UIPortletInfo uiPortletInfo = addChild(UIPortletInfo.class, null, null) ;
    uiPortletInfo.setPortlet(localPortlets.get(0)) ;
  }
  
  public void init() throws Exception {
    ExoContainer manager  = ExoContainerContext.getCurrentContainer();
    PortletContainerService pcService =
      (PortletContainerService) manager.getComponentInstanceOfType(PortletContainerService.class) ;
    Map<String, PortletData> allPortletMetaData = pcService.getAllPortletMetaData();
    localPortlets = new ArrayList<PortletData>(allPortletMetaData.values()) ;
  }
  
  public List<PortletData> getLocalPortlets() {
    return localPortlets;
  }
  
  public List<PortletData> getRemotePortlets() {
    return remotePortlets;
  }
  
  public PortletData getSelectedPortlet() { return selectedPorlet; }
  
  public void setSelectedPortlet(PortletData portlet) { selectedPorlet = portlet; }
  
  public void setSelectedPortlet(String name) {
    for(PortletData ele : localPortlets) {
      if(ele.getPortletName().equals(name)) {
        setSelectedPortlet(ele) ;
        break ;
      }
    }
  }

  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
  }
  
  static public class SelectPortletActionListener extends EventListener<UIPortletManagement> {

    public void execute(Event<UIPortletManagement> event) throws Exception {
      UIPortletManagement uiManagement = event.getSource() ;
      String portletName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiManagement.setSelectedPortlet(portletName) ;
      UIPortletInfo uiPortletInfo = uiManagement.getChild(UIPortletInfo.class) ;
      if(uiPortletInfo == null) {
        uiManagement.getChildren().clear() ;
        uiPortletInfo = uiManagement.addChild(UIPortletInfo.class, null, null) ;
      }
      uiPortletInfo.setPortlet(uiManagement.getSelectedPortlet()) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
    
  }

}