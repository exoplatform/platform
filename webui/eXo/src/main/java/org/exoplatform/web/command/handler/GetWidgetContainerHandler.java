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
  //{widgetContainers: [{"cId" : "testid", "cName" : "testname", "cDescription" : "testDes"}]}
  private StringBuilder getWidgetContainers(HttpServletRequest req) throws Exception {    
    PortalContainer container = PortalContainer.getInstance();
    DataStorage dataService = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    String userWidgetsId = PortalConfig.USER_TYPE + "::" + req.getRemoteUser() ;
    
    StringBuilder value = new StringBuilder();
    value.append("{\"widgetContainers\": [\n") ;
    Widgets widgets = dataService.getWidgets(userWidgetsId) ;
    if(widgets != null) {
      ArrayList<Container> widgetContainers = widgets.getChildren() ;
      for(int i = 0; i < widgetContainers.size(); i ++) {
        value.append(" {");
        value.append("\n          \"cId\": \"").append(widgetContainers.get(i).getId()).append("\",") ;
        value.append("\n          \"cName\": \"").append(widgetContainers.get(i).getName()).append("\",") ;
        value.append("\n          \"cDescription\": \"").append(widgetContainers.get(i).getDescription()).append("\"\n") ;
        value.append("      }");
        if (i < (widgetContainers.size() - 1)) value.append(",\n") ;
        else value.append("\n") ;
      }      
    }
    value.append("]}\n") ;
    return value ;
  }

}
