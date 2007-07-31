/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan, 
 *          nhudinhthuan@exoplatform.com
 * Jul 11, 2006  
 */
@ComponentConfig(
  template = "system:/groovy/portal/webui/UILogged.gtmpl" ,
  events = @EventConfig(listeners = UILogged.LogoutActionListener.class)
)
public class UILogged extends UIContainer {
  
    public int accessibility_;
    public String remoteUser_;
    
    public UILogged() throws Exception{
      PortalRequestContext prContext = Util.getPortalRequestContext();
      accessibility_ = prContext.getAccessPath() ;
      remoteUser_ = prContext.getRemoteUser();
    }
    
    public int getAccessibility() {  return accessibility_ ;}
    
    public String getRemoteUser() { return remoteUser_ ; }
    
    @SuppressWarnings("unused")
    //TODO: Tung.Pham Modified
    static  public class LogoutActionListener extends EventListener {
      public void execute(Event event) throws Exception {
        PortalRequestContext prContext = Util.getPortalRequestContext();
        HttpServletRequest request = prContext.getRequest() ;
        UIPortal currentPortal = Util.getUIPortal() ;
        String portalName = currentPortal.getName() ;
        String redirect = request.getContextPath() + "/public/" + portalName + "/" ;
        prContext.getResponse().sendRedirect(redirect) ;
//        request.getSession().invalidate() ;
//        prContext.setResponseComplete(true) ;
      }
    }    
}
