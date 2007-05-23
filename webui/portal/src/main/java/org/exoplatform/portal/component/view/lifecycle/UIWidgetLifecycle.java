/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.lifecycle;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.component.view.UIWidget;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.application.widget.WidgetApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.lifecycle.Lifecycle;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 8, 2006
 */
public class UIWidgetLifecycle extends Lifecycle {
  
  public void processAction(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    
  }
  
  public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    UIWidget uiWidget = (UIWidget)  uicomponent ;
    PortalContainer container = PortalContainer.getInstance() ;
    WebAppController controller = 
      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
    WidgetApplication application =
      (WidgetApplication) controller.getApplication(uiWidget.getApplicationId()) ;
    application.processRender(context.getWriter()) ;
  }
}
