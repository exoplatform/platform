/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy, 
 *          lebienthuy@gmail.com
 * Jul 11, 2006  
 */
@ComponentConfig(
  template = "system:/groovy/portal/webui/component/view/UISystemWidgets.gtmpl" ,
    events = {
      @EventConfig(listeners = UISystemWidgets.EditActionListener.class ),
      @EventConfig(listeners = UISystemWidgets.LogoutActionListener.class)
  }
      
)
public class UISystemWidgets extends UIContainer {
  
  public int accessibility_;
  public String remoteUser_;
  
  public UISystemWidgets() throws Exception {
    PortalRequestContext prContext = Util.getPortalRequestContext();
    accessibility_ = prContext.getAccessPath() ;
    remoteUser_ = prContext.getRemoteUser();
  }
  
  public int getAccessibility() {  return accessibility_ ;}
  
  public String getRemoteUser() { return remoteUser_ ; }
  
  @SuppressWarnings("unused")
  static  public class LogoutActionListener extends EventListener {
    public void execute(Event event) throws Exception {
      PortalRequestContext prContext = Util.getPortalRequestContext();
      HttpServletRequest request = prContext.getRequest() ;
      request.getSession().invalidate() ;
      prContext.setResponseComplete(true) ;
      String redirect = request.getContextPath() ;
      prContext.getResponse().sendRedirect(redirect) ;
    }
  }
  
  static  public class EditActionListener extends EventListener {
    public void execute(Event event) throws Exception {
      System.out.println("\n\n\n\n==============================Edit duoc roi");
      
    }
  }
}
