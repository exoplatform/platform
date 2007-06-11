/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.lifecycle;

import org.exoplatform.portal.component.view.UIExoApplication;
import org.exoplatform.web.application.mvc.MVCApplication;
import org.exoplatform.web.application.mvc.MVCRequestContext;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.Lifecycle;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 8, 2006
 */
public class UIExoApplicationLifecycle extends Lifecycle {
  
  public void processAction(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    
  }
  
  public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    UIExoApplication uiExoApp = (UIExoApplication)  uicomponent ;
    MVCApplication application =  uiExoApp.getApplication() ;
    MVCRequestContext appReqContext = new MVCRequestContext(application, context) ;
    try {
      WebuiRequestContext.setCurrentInstance(appReqContext) ;
      uiExoApp.getApplication().processRender(appReqContext) ;
    } finally {
      WebuiRequestContext.setCurrentInstance(context) ;
     }
  }
}
