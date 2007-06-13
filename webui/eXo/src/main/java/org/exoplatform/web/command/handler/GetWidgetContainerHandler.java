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
      writer.append(getWidgetContainers(req.getRemoteUser()));
    } catch (Exception e) {
      e.printStackTrace() ;
      throw new IOException(e.getMessage());
    }
  }
  
  private StringBuilder getWidgetContainers(String remoteUser) throws Exception {
    PortalContainer container = PortalContainer.getInstance();
    DataStorage dataService = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    /*Anh Thuan cho dung hashcode : site , co loi gi kien anh Thuan!!!  :D */
    String portalWidgetId = PortalConfig.PORTAL_TYPE + "::site" ;
    Widgets widgets = dataService.getWidgets(portalWidgetId) ;
    
    StringBuilder value = new StringBuilder();
    
    ArrayList<Container> widgetContainers = widgets.getChildren() ;
    
    value.append("{\n").append("widgetContainer : {\n");
    for(int i = 0; i < widgetContainers.size(); i ++) {
      value.append("      \"container"+i+"\" : ").append(" {");
      value.append("\n          \"name\" : \"").append(widgetContainers.get(i).getName()).append("\",") ;
      value.append("\n          \"description\" : \"").append(widgetContainers.get(i).getDescription()).append("\"\n") ;
      value.append("      }");
      if (i < (widgetContainers.size() - 1)) value.append(",\n") ;
      else value.append("\n") ;
    }
    value.append("    }\n").append("}\n") ;
    
    return value ;
  }
  
}
