/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.application.widget.WidgetApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.Lifecycle;
/**
 * Created by The eXo Platform SAS
 * May 8, 2006
 */
public class UIWidgetLifecycle extends Lifecycle {
  
  @SuppressWarnings("unused")
  public void processAction(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    
  }
  
  @SuppressWarnings("unchecked")
  public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    UIWidget uiWidget = (UIWidget)  uicomponent ;
    PortalContainer container = PortalContainer.getInstance() ;
    WebAppController controller = 
      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
    WidgetApplication application =
      (WidgetApplication) controller.getApplication(uiWidget.getApplicationId()) ;
    if(application != null) application.processRender(uiWidget, context.getWriter()) ;
  }
}
