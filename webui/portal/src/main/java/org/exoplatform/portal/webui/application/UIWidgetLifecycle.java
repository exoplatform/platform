/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.webui.application;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
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
  
  /**
   * The processRender method of the UIWidget component is implemented in its bound Lifecycle object.
   * 
   * The Lifecycle itself simply delegates the call to the WidgetApplication stored in the IoC WebAppController
   * component
   */
  @SuppressWarnings("unchecked")
  public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    UIWidget uiWidget = (UIWidget)  uicomponent ;
    ExoContainer container  = ExoContainerContext.getCurrentContainer();
    WebAppController controller = 
      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
    WidgetApplication application =
      (WidgetApplication) controller.getApplication(uiWidget.getApplicationId()) ;
    if(application != null) application.processRender(uiWidget, context.getWriter()) ;
  }
}
