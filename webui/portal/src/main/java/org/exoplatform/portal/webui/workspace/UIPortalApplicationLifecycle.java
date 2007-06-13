/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.workspace;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.Lifecycle;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 8, 2006
 */
public class UIPortalApplicationLifecycle extends Lifecycle {  
  
  public void processDecode(UIComponent uicomponent , WebuiRequestContext context) throws Exception {  
    UIPortalApplication uiApp = (UIPortalApplication) uicomponent ; 
    String componentId =  context.getRequestParameter(context.getUIComponentIdParameterName()) ;    
    if(componentId == null)  return ;
    UIComponent uiTarget =  uiApp.findComponentById(componentId);
    if(uiTarget == null)  return ;
    if(uiTarget == uicomponent)  super.processDecode(uicomponent, context) ;
    uiTarget.processDecode(context);
  }
  
  public void processAction(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    UIPortalApplication uiApp = (UIPortalApplication) uicomponent ;
    String componentId =  context.getRequestParameter(context.getUIComponentIdParameterName()) ;
    if(componentId == null)  return;
    UIComponent uiTarget =  uiApp.findComponentById(componentId);  
    if(uiTarget == null)  return ;
    if(uiTarget == uicomponent)  super.processAction(uicomponent, context) ;
    uiTarget.processAction(context) ;
  }
  
}