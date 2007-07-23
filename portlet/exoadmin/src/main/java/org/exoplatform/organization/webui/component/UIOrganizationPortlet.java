/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.ViewChildActionListener;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupMessages;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */

@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class,
  events = @EventConfig(listeners = UIOrganizationPortlet.ClosePopupActionListener.class)
)
public class UIOrganizationPortlet extends UIPortletApplication {
 
  public UIOrganizationPortlet() throws Exception {
    setMinWidth(730) ;
  	addChild(UIViewMode.class, null, UIPortletApplication.VIEW_MODE);
    addChild(UIPopupMessages.class, null , UIPortletApplication.HELP_MODE) ;
  }
  
  public void  processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
    PortletRequestContext portletReqContext = (PortletRequestContext)  context ;
    List<UIComponent>  children = getChildren() ;
    UIComponent view = null , edit = null, help = null ;
    for(UIComponent child : children) {
//      if(child instanceof  UIPopupMessages) continue ;
      if(child.getId() == VIEW_MODE)  view = child  ;
      else if(child.getId() == EDIT_MODE) edit = child ;
      else if(child.getId() == HELP_MODE) help = child ;
    } 
    
    if (portletReqContext.getApplicationMode() == PortletRequestContext.VIEW_MODE) {
      setRenderedComponent(view,  true) ;
      setRenderedComponent(edit,  false) ;
      setRenderedComponent(help,  false) ;
    } else if(portletReqContext.getApplicationMode() == PortletRequestContext.EDIT_MODE) {
      setRenderedComponent(view,  false) ;
      setRenderedComponent(edit,  true) ;
      setRenderedComponent(help,  false) ;
    } else if(portletReqContext.getApplicationMode() == PortletRequestContext.HELP_MODE) {
      UIPopupMessages uiPopup = (UIPopupMessages)help;
      ApplicationMessage message = new ApplicationMessage("Help document", new Object[]{});
      List<ApplicationMessage> infors = new ArrayList<ApplicationMessage>();
      infors.add(message);
      uiPopup.setInfos(infors);
      setRenderedComponent(help,  true) ;
    }
    super.processRender(app, context) ;
  }

  private void setRenderedComponent(UIComponent component, boolean  rendered) {
    if(component != null)  component.setRendered(rendered) ;
  } 
  
  @ComponentConfig(
      template = "app:/groovy/organization/webui/component/UIViewMode.gtmpl",
      events = @EventConfig (listeners = ViewChildActionListener.class)
  )
  static public class UIViewMode extends UIContainer {
    public UIViewMode() throws Exception {
      addChild(UIUserManagement.class, null, null);
      addChild(UIGroupManagement.class, null, null).setRendered(false);
      addChild(UIMembershipManagement.class, null, null).setRendered(false);
    }
  } 
  
  static  public class ClosePopupActionListener extends EventListener<UIOrganizationPortlet> {
    public void execute(Event<UIOrganizationPortlet> event) throws Exception {
      PortletRequestContext portletReqContext = (PortletRequestContext) event.getRequestContext() ;
      portletReqContext.setApplicationMode(PortletRequestContext.VIEW_MODE);
    }
  }
}