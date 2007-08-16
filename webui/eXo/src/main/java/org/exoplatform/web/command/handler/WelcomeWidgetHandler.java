/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command.handler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.jcr.UserWidgetStorageImpl;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;
//import org.exoplatform.widget.service.UserWidgetDataService;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          thuy.le@exoplatform.com
 * Dec 9, 2006  
 */
public class WelcomeWidgetHandler extends Command {

  private String uploadId ;

  @SuppressWarnings("unused")
  public void execute(WebAppController controller,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    try{ 
      PortalContainer container  = PortalContainer.getInstance();
      UserWidgetStorageImpl service = 
        (UserWidgetStorageImpl)container.getComponentInstanceOfType(UserWidgetStorageImpl.class) ;    

      
      String instantId = "avatar";
      String widgetType = "WelcomeWidget";
      String userName = req.getRemoteUser();
      service.save(userName, widgetType, instantId, "aaa");
    } catch (Exception e) {
      e.printStackTrace();
    } catch (Throwable  e) {e.printStackTrace();
    }
  }
}