/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.lifecycle;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIContainer;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 10, 2006
 */
public class UIContainerLifecycle extends Lifecycle {
  
  @SuppressWarnings("unused")
  public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    context.getWriter().append("<div class=\"").append(uicomponent.getId()).append("\" id=\"").append(uicomponent.getId()).append("\">");
    UIContainer uiContainer = (UIContainer) uicomponent;
    uiContainer.renderChildren(context);
    context.getWriter().append("</div>");
  }
}
