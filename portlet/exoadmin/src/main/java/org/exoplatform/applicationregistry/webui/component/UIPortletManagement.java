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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.exoplatform.applicationregistry.webui.PortletExtra;
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
        @EventConfig(listeners = UIPortletManagement.SelectPortletActionListener.class),
        @EventConfig(listeners = UIPortletManagement.SelectPortletType.class)
    }
)

public class UIPortletManagement extends UIContainer {
  
  static final public String LOCAL = "local";
  static final public String REMOTE = "remote";
  private List<PortletExtra> portlets;
  private PortletExtra selectedPorlet;
  private String [] portletTypes;
  private String selectedType;
  
  public UIPortletManagement() throws Exception {
    portletTypes = new String [] {LOCAL, REMOTE};
    setSelectedType(LOCAL);
    UIPortletInfo uiPortletInfo = addChild(UIPortletInfo.class, null, null) ;
    uiPortletInfo.setPortlet(selectedPorlet) ;
  }
  
  private List<PortletExtra> getAllPortletData(String type) throws Exception {
    ExoContainer manager  = ExoContainerContext.getCurrentContainer();
    PortletContainerService pcService =
      (PortletContainerService) manager.getComponentInstanceOfType(PortletContainerService.class) ;
    Map<String, PortletData> allData = pcService.getAllPortletMetaData((type.equals(LOCAL)) ? true : false);
    Iterator<Entry<String, PortletData>> itr = allData.entrySet().iterator();
    List<PortletExtra> list = new ArrayList<PortletExtra>(5) ;
    while(itr.hasNext()) {
      Entry<String, PortletData> entry = itr.next();
      list.add(new PortletExtra(entry.getKey(), type, entry.getValue())) ;
    }
   return list; 
    
  }
  
  public List<PortletExtra> getPortlets() { return portlets; }
  public void setPortlets(List<PortletExtra> list) { portlets = list; }
  
  public String getSelectedType() { return selectedType; }
  public void setSelectedType(String type) throws Exception { 
    selectedType = type;
    portlets = getAllPortletData(selectedType) ;
    selectedPorlet = (portlets == null || portlets.size() < 1) ? null : portlets.get(0) ;
  }
  
  public String [] getPortletTypes() { return portletTypes; }
  
  public PortletExtra getSelectedPortlet() { return selectedPorlet; }  
  public void setSelectedPortlet(PortletExtra portlet) { selectedPorlet = portlet; }
  public void setSelectedPortlet(String id) {
    for(PortletExtra ele : portlets) {
      if(ele.getId().equals(id)) {
        setSelectedPortlet(ele) ;
        break ;
      }
    }
  }

  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
  }
  
  static public class SelectPortletType extends EventListener<UIPortletManagement> {

    public void execute(Event<UIPortletManagement> event) throws Exception {
      UIPortletManagement uiManagement = event.getSource() ;
      String type = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiManagement.setSelectedType(type) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
    
  }
  
  static public class SelectPortletActionListener extends EventListener<UIPortletManagement> {

    public void execute(Event<UIPortletManagement> event) throws Exception {
      UIPortletManagement uiManagement = event.getSource() ;
      String portletId = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiManagement.setSelectedPortlet(portletId) ;
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