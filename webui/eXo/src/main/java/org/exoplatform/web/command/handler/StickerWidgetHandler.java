/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command.handler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.widget.jcr.UserWidgetDataService;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;
//import org.exoplatform.widget.service.UserWidgetDataService;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          thuy.le@exoplatform.com
 * Dec 9, 2006  
 */
public class StickerWidgetHandler extends Command {
  
  private String componentId ;
  
  @SuppressWarnings("unused")
  public void execute(WebAppController controller,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    try{ 
    PortalContainer container  = PortalContainer.getInstance();
    UserWidgetDataService service = 
      (UserWidgetDataService)container.getComponentInstanceOfType(UserWidgetDataService.class) ;    

//    System.out.println("\n\n\n-------------------->service " + service.getClass().getName());
    String value = req.getParameter("content");
    String objectId = req.getParameter("objectId");
    String[] split = objectId.split("/");
    String instantId = split[3];
    String widgetType = split[2];
    String userName = req.getRemoteUser();
    service.save(userName, widgetType, instantId, value );
    String hehe = service.getWidgetData(userName, widgetType, instantId);
//    System.out.println("\n\n\n\n------d-----> " + instantId +  " - " + hehe +  " - " +userName);
    } catch (Exception e) {
      e.printStackTrace();
    } catch (Throwable  e) {e.printStackTrace();
    }
  }
}