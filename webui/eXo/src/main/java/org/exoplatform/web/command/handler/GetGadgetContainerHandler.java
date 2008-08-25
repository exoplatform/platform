/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.web.command.handler;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Gadgets;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * Modified: tung.dang
 *           tungcnw@gmail.com          
 * Jun 6, 2007  
 */
public class GetGadgetContainerHandler extends Command {
  public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
    Writer writer = res.getWriter();
    try {
      writer.append(getGadgetContainers(req));
    } catch (Exception e) {
      e.printStackTrace() ;
      throw new IOException(e.getMessage());
    }
  }
  //{gadgetContainers: [{"cId" : "testid", "cName" : "testname", "cDescription" : "testDes"}]}
  private StringBuilder getGadgetContainers(HttpServletRequest req) throws Exception {    
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    DataStorage dataService = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    String userGadgetsId = PortalConfig.USER_TYPE + "::" + req.getRemoteUser() ;
    
    StringBuilder value = new StringBuilder();
    value.append("{\"gadgetContainers\": [\n") ;
    Gadgets gadgets = dataService.getGadgets(userGadgetsId) ;
    if(gadgets != null) {
      ArrayList<Container> gadgetContainers = gadgets.getChildren() ;
      for(int i = 0; i < gadgetContainers.size(); i ++) {
        value.append(" {");
        value.append("\n          \"cId\": \"").append(gadgetContainers.get(i).getId()).append("\",") ;
        value.append("\n          \"cName\": \"").append(gadgetContainers.get(i).getName()).append("\",") ;
        value.append("\n          \"cDescription\": \"").append(gadgetContainers.get(i).getDescription()).append("\"\n") ;
        value.append("      }");
        if (i < (gadgetContainers.size() - 1)) value.append(",\n") ;
        else value.append("\n") ;
      }      
    }
    value.append("]}\n") ;
    return value ;
  }

}
