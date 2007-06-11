/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.lifecycle;

import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.Lifecycle;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 8, 2006
 */
public class UIPortalLifecycle extends Lifecycle { 
  
  public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception { 
    UIPortal uiPortal = (UIPortal) uicomponent;
    if(uiPortal.getMaximizedUIComponent() != null){
      UIComponent uiComponent = uiPortal.getMaximizedUIComponent();
      uiComponent.processRender(context);
      return;
    }
    super.processRender(uicomponent, context);
  }
  
}