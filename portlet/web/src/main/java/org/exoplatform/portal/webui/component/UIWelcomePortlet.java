/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.widget.UILoginForm;
import org.exoplatform.portal.component.widget.UILoginForm.SigninActionListener;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.component.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 30, 2006
 */
@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/portal/webui/component/UIWelcomePortlet.gtmpl" ,
    events = @EventConfig(listeners = UIWelcomePortlet.LogoutActionListener.class)
  ),
  @ComponentConfig(
    type = UILoginForm.class,     
    lifecycle = UIFormLifecycle.class ,
    template = "app:/groovy/portal/webui/component/UILoginForm.gtmpl" ,
    events = @EventConfig(listeners = SigninActionListener.class)
  )    
})

public class UIWelcomePortlet extends UIPortletApplication {
  
  public int accessibility_ ;
  public String remoteUser_ ;
  
  public UIWelcomePortlet() throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    PortalRequestContext prcontext = (PortalRequestContext)context.getParentAppRequestContext() ;
    accessibility_ = prcontext.getAccessPath() ;
    if(accessibility_ == PortalRequestContext.PUBLIC_ACCESS) {
      addChild(UILoginForm.class, null, "UIWelcomePortletLogin") ;
    }else{
     
    }      
    remoteUser_ = context.getRemoteUser() ;
  }
  
  public int getAccessibility() {  return accessibility_ ;}
  
  public String getRemoteUser() { return remoteUser_ ; }
  
  static  public class LogoutActionListener extends EventListener {
    public void execute(Event event) throws Exception {
      WebuiRequestContext context =  event.getRequestContext() ;
      PortalRequestContext prcontext = (PortalRequestContext) context.getParentAppRequestContext() ;
      HttpServletRequest request = prcontext.getRequest() ;
      request.getSession().invalidate() ;
      context.setResponseComplete(true) ;
      prcontext.setResponseComplete(true) ;
      String redirect = request.getContextPath() ;
      prcontext.getResponse().sendRedirect(redirect) ;
    }
  }  
  
}
