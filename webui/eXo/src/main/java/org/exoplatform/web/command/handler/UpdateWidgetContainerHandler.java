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
      String widgetsId = "" ; 
      String owner = req.getParameter("owner") ;
      if(owner.equals(PortalConfig.PORTAL_TYPE)) {
        widgetsId = PortalConfig.PORTAL_TYPE + "::site" ;
      } else {
        widgetsId = PortalConfig.USER_TYPE + "::" + req.getRemoteUser() ;
      }
      
      Widgets widgets = configService.getWidgets(widgetsId) ;
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
    String[] cIds = req.getParameterValues("id") ;
    String[] cNames = req.getParameterValues("name") ;
    String[] cDesc = req.getParameterValues("desc") ;
    if(cIds == null || cDesc == null) return ;
    for(int i = 0; i < cIds.length; i++) {
      Container foundContainer = getContainer(existingContainers, cIds[i]) ;
      if(foundContainer != null) {
        foundContainer.setName(cNames[i]) ;
        foundContainer.setDescription(cDesc[i]) ;
        continue ;
      }
      Container newContainer = createContainer(cIds[i], cDesc[i]) ;
      existingContainers.add(newContainer) ;
    }
  }
  
  private Container getContainer(List<Container> list, String id) {
    for(Container ele : list) {
      if (ele.getId().equals(id)) return ele ;
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