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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Gadgets;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;

/**
 * Created by The eXo Platform SARL
 * Author : Tung.Pham
 *          tung.pham@exoplatform.com
 * Modified: dang.tung
 *           tungcnw@gmail.com         
 * Jul 27, 2007  
 */
public class UpdateGadgetContainerHandler extends Command {

  public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
    try {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      UserPortalConfigService configService = (UserPortalConfigService)container.getComponentInstanceOfType(UserPortalConfigService.class) ;
      String ownerType = PortalConfig.USER_TYPE ;
      String ownerId = req.getRemoteUser() ;
      String gadgetsId = ownerType + "::" + ownerId ; 
      Gadgets gadgets = configService.getGadgets(gadgetsId) ;
      if(gadgets == null) {
        gadgets = new Gadgets() ;
        gadgets.setOwnerType(ownerType) ;
        gadgets.setOwnerId(ownerId) ;
        gadgets.setChildren(new ArrayList<Container>()) ;
        configService.create(gadgets) ;
      }

      deleteContainer(gadgets, req) ;
      saveContainer(gadgets, req) ;
      
      configService.update(gadgets) ;
    } catch(Exception e) {
      e.printStackTrace() ;
      throw new Exception(e.getMessage()) ;
    }
  }
  
  private void deleteContainer(Gadgets gadgets, HttpServletRequest req) {
    List<Container> existingContainers = gadgets.getChildren() ;
    String[] deleteContainers = req.getParameterValues("deleted") ;
    if(deleteContainers == null) return ;
    for(String deletedItem :  deleteContainers) {
      Container foundContainer = getContainer(existingContainers, deletedItem) ;
      if(foundContainer != null) existingContainers.remove(foundContainer) ; 
    }    
  }
  
  private void saveContainer(Gadgets gadgets, HttpServletRequest req) {
    List<Container> existingContainers = gadgets.getChildren() ;
    String[] cIds = req.getParameterValues("id") ;
    String[] cNames = req.getParameterValues("name") ;
    String[] cDesc = req.getParameterValues("desc") ;
    if(cIds == null) return ;
    for(int i = 0; i < cIds.length; i++) {
      Container foundContainer = getContainer(existingContainers, cIds[i]) ;
      if(foundContainer != null) {
        foundContainer.setName(cNames[i]) ;
        foundContainer.setDescription(cDesc[i]) ;
        continue ;
      }
      Container newContainer = createContainer(cIds[i], cNames[i], cDesc[i]) ;
      existingContainers.add(newContainer) ;
    }
  }
  
  private Container getContainer(List<Container> list, String id) {
    for(Container ele : list) {
      if (ele.getId().equals(id)) return ele ;
    }
    
    return null ;
  }
  
  private Container createContainer(String id, String name, String desc) {
    Container container = new Container() ;
    container.setId(id) ;
    container.setName(name) ;
    container.setDescription(desc) ;
    
    return container ;
  }
  
}