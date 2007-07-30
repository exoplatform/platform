/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command.handler;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;

/**
 * Created by The eXo Platform SARL
 * Author : Tung.Pham
 *          tung.pham@exoplatform.com
 * Jul 27, 2007  
 */
public class UpdateWidgetContainerHandler extends Command {

  public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
    try {
      PortalContainer portalContainer = PortalContainer.getInstance() ;
      UserPortalConfigService configService = (UserPortalConfigService)portalContainer.getComponentInstanceOfType(UserPortalConfigService.class) ;
      String portalWidgetsId = PortalConfig.PORTAL_TYPE + "::site" ;
      Widgets widgets = configService.getWidgets(portalWidgetsId) ;
      if(widgets == null) return ;

      deleteContainer(widgets, req) ;
      saveContainer(widgets, req) ;
      
      configService.update(widgets) ;
    } catch(Exception e) {
      e.printStackTrace() ;
      throw new Exception(e.getMessage()) ;
    }
  }
  
  private void deleteContainer(Widgets widgets, HttpServletRequest req) {
    List<Container> existingContainers = widgets.getChildren() ;
    String[] deleteContainers = req.getParameterValues("deleted") ;
    if(deleteContainers == null) return ;
    for(String deletedItem :  deleteContainers) {
      Container foundContainer = getContainer(existingContainers, deletedItem) ;
      if(foundContainer != null) existingContainers.remove(foundContainer) ; 
    }    
  }
  
  private void saveContainer(Widgets widgets, HttpServletRequest req) {
    List<Container> existingContainers = widgets.getChildren() ;
    String[] cNames = req.getParameterValues("name") ;
    String[] cDesc = req.getParameterValues("desc") ;
    if(cNames == null || cDesc == null) return ;
    for(int i =0; i < cNames.length; i++) {
      Container foundContainer = getContainer(existingContainers, cNames[i]) ;
      if(foundContainer != null) {
        foundContainer.setDescription(cDesc[i]) ;
        continue ;
      }
      Container newContainer = createContainer(cNames[i], cDesc[i]) ;
      existingContainers.add(newContainer) ;
    }
  }
  
  private Container getContainer(List<Container> list, String name) {
    for(Container ele : list) {
      if (ele.getName().equals(name)) return ele ;
    }
    
    return null ;
  }
  
  private Container createContainer(String id, String desc) {
    Container container = new Container() ;
    container.setId(id) ;
    container.setName(id) ;
    container.setDescription(desc) ;
    
    return container ;
  }
  
}