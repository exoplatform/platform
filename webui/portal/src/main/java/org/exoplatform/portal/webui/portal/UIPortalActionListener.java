/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.portal;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 20, 2006
 */
public class UIPortalActionListener { 
  @SuppressWarnings("unchecked")
  static  public class LogoutActionListener extends EventListener {
    public void execute(Event event) throws Exception {
      PortalRequestContext prContext = Util.getPortalRequestContext();
      HttpServletRequest request = prContext.getRequest() ;
      UIPortal currentPortal = Util.getUIPortal() ;
      String portalName = currentPortal.getName() ;
      String redirect = request.getContextPath() + "/public/" + portalName + "/" ;
      prContext.getResponse().sendRedirect(redirect) ;
      prContext.setResponseComplete(true) ;
    }
  }    

  static public class ChangeWindowStateActionListener extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal  = event.getSource();
      String portletId = event.getRequestContext().getRequestParameter("portletId");
      UIPortlet uiPortlet = uiPortal.findComponentById(portletId);
      WebuiRequestContext context = event.getRequestContext();
      uiPortlet.createEvent("ChangeWindowState", event.getExecutionPhase(), context).broadcast();
    }
  }
  
}