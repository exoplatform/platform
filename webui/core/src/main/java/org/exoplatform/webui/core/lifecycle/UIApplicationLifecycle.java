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
package org.exoplatform.webui.core.lifecycle;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPortletApplication;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 1, 2006
 */
public class UIApplicationLifecycle  extends Lifecycle {  

  public void processDecode(UIComponent uicomponent , WebuiRequestContext context) throws Exception { 
    String componentId =  context.getRequestParameter(context.getUIComponentIdParameterName()) ;
    if(componentId == null ||componentId.length() == 0) return ;    
    UIComponent uiTarget = uicomponent.findComponentById(componentId);
    //TODO to avoid exception
    if(uiTarget == null) return ;       
    else if(uiTarget == uicomponent) super.processDecode(uicomponent, context) ; 
    else uiTarget.processDecode(context);    
  }

  public void processAction(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    String componentId =  context.getRequestParameter(context.getUIComponentIdParameterName()) ;   
    if(componentId != null) {
      UIComponent uiTarget =  uicomponent.findComponentById(componentId);      
      if(uiTarget == uicomponent) super.processAction(uicomponent, context) ;
      else if(uiTarget != null) uiTarget.processAction(context) ;
    }
  }

  public void processRender(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    if(uicomponent.getTemplate() != null) {
      super.processRender(uicomponent, context) ;
      return ;
    }
    UIPortletApplication uiApp = (UIPortletApplication) uicomponent;
    context.getWriter().append("<div id=\"").append(uicomponent.getId()).append("\"").
    append(" style=\"min-width:").append(String.valueOf(uiApp.getMinWidth())).
    append("px\" class=\"").append(uicomponent.getId()).append("\">");
    uiApp.renderChildren();
    context.getWriter().append("</div>");
  }
}