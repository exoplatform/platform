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
package org.exoplatform.portal.webui.workspace;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.Lifecycle;

/**
 * Created by The eXo Platform SAS
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
  
  /**
   * The processAction() method of the UIPortalApplication is called, as there is no 
   * method in the object itself it will call the processAction() of the 
   * UIPortalApplicationLifecycle bound to the UI component
   * 
   * If no uicomponent object is targeted, which is the case the first time 
   * (unless a bookmarked link is used) then nothing is done. Otherwise, the 
   * targeted component is extracted and a call of its processAction() method is executed.
   * 
   */
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