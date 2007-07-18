/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
    if(componentId != null) {
      UIComponent uiTarget = uicomponent.findComponentById(componentId);
      if(uiTarget == uicomponent) super.processDecode(uicomponent, context) ; 
      else uiTarget.processDecode(context);
    }
  }
  
  public void processAction(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    String componentId =  context.getRequestParameter(context.getUIComponentIdParameterName()) ;   
    if(componentId != null) {
      UIComponent uiTarget =  uicomponent.findComponentById(componentId);      
      if(uiTarget == uicomponent) super.processAction(uicomponent, context) ;
      else if(uiTarget != null) uiTarget.processAction(context) ;
    }
  }
  
  //TODO TrongTT : check for config template of portlet
  public void processRender(UIComponent uicomponent, WebuiRequestContext context) throws Exception {
    if(uicomponent.getTemplate() != null) {
      super.processRender(uicomponent, context) ;
      return ;
    }
    UIPortletApplication uiApp = (UIPortletApplication) uicomponent;
    context.getWriter().append("<div id=\"").append(uicomponent.getId()).append("\" class=\"").append(uicomponent.getId()).
                        append("\" exo:minWidth=\"").append(String.valueOf(uiApp.getMinWidth())).
                        append("\" exo:minHeight=\"").append(String.valueOf(uiApp.getMinHeight())).append("\">");
    uiApp.renderChildren();
    context.getWriter().append("</div>");
  }
}