/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command.handler;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * Jun 6, 2007  
 */
public class GetWidgetContainerHandler extends Command {
  public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
    Writer writer = res.getWriter();
    try {
      writer.append(getWidgetContainers(req));
    } catch (Exception e) {
      e.printStackTrace() ;
      throw new IOException(e.getMessage());
    }
  }
  
  private StringBuilder getWidgetContainers(HttpServletRequest req) throws Exception {    
/*   
                         |-------portalWidgetContainer|----name
                         |                            |
    widgetContainers-----|                            |----containers|---con1
                         |                                           |---con2
                         |
                         |
                         |-------userWidgetContainer|----name
                                                    |      
                                                    |----containers|---con1
                                                                   |---con2
                                                                                                                                      
*/                                                                
    PortalContainer container = PortalContainer.getInstance();
    DataStorage dataService = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    String portal = req.getParameter("portal") ;
    String user = req.getRemoteUser() ;
    String[] widgetIds = {PortalConfig.PORTAL_TYPE + "::" + portal, PortalConfig.USER_TYPE + "::" + user} ;

    StringBuilder value = new StringBuilder();
    value.append("{\"widgetContainers\": {\n") ;
    for(int k = 0; k < widgetIds.length; k++) {
      Widgets widgets = dataService.getWidgets(widgetIds[k]) ;
      if(widgets == null) continue ; 
      ArrayList<Container> widgetContainers = widgets.getChildren() ;
      String owner = widgets.getOwnerType() ;
      
      value.append("\"").append(owner).append("WidgetContainer\": {")
           .append("\"name\": \"").append(getName(owner)).append("\",\n")
           .append("\"containers\": [") ;
      for(int i = 0; i < widgetContainers.size(); i ++) {
        value.append(" {");
        value.append("\n          \"cId\": \"").append(widgetContainers.get(i).getId()).append("\",") ;
        value.append("\n          \"cOwner\": \"").append(owner).append("\",") ;
        value.append("\n          \"cName\": \"").append(widgetContainers.get(i).getName()).append("\",") ;
        value.append("\n          \"cDescription\": \"").append(widgetContainers.get(i).getDescription()).append("\"\n") ;
        value.append("      }");
        if (i < (widgetContainers.size() - 1)) value.append(",\n") ;
        else value.append("\n") ;
      }
      value.append("    ]}\n") ;
      if(k <(widgetIds.length - 1)) value.append(",\n") ;
      else value.append("\n") ;   
    }
    
    value.append("}}") ;
    return value ;
  }
  
  private String getName(String key) {
    String title = "" ;
    if(key.equals(PortalConfig.PORTAL_TYPE)) {
      title = "PortalWidgetContainer" ;
    } else title = "UserWidgetContainer" ;

    return title ;
  }
  
}
