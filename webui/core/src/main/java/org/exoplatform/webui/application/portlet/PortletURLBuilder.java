/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application.portlet;

import org.exoplatform.web.application.Parameter;
import org.exoplatform.web.application.URLBuilder;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Apr 3, 2007  
 */
public class PortletURLBuilder extends URLBuilder<UIComponent> {
  
  public PortletURLBuilder() {
    super(null);
  }
  
  public PortletURLBuilder(String baseURL) {
    super(baseURL);
  }
  
  @SuppressWarnings("unused")
  public String createURL(String action, Parameter[] params) { 
    return null; 
  }
 
  @SuppressWarnings("unused")
  public String createURL(String action, String objectId, Parameter[] params) {
    return null;
  }
  
  protected void createURL(StringBuilder builder, UIComponent targetComponent, String action, String targetBeanId, Parameter[] params) {
    builder.append(getBaseURL()).append("&amp;").
    append(UIComponent.UICOMPONENT).append('=').append(targetComponent.getId()) ;

    if(action != null && action.trim().length() > 0) {
      builder.append("&amp;").append(WebuiRequestContext.ACTION).append('=').append(action) ;
    }

    if(targetBeanId != null && targetBeanId.trim().length() > 0) {
      builder.append("&amp;").append(UIComponent.OBJECTID).append('=').append(targetBeanId) ;
    }

    if(params == null || params.length < 1) return;
    for(Parameter param : params){
      builder.append("&amp;").append(param.getName()).append('=').append(param.getValue()) ;
    }
    
  }

}
