/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application;

import org.exoplatform.web.application.Parameter;
import org.exoplatform.web.application.URLBuilder;
import org.exoplatform.webui.core.UIComponent;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Apr 3, 2007  
 */
public class PortalURLBuilder extends URLBuilder<UIComponent> {
  
  public PortalURLBuilder(String baseURL) {
    super(baseURL);
  }

  @SuppressWarnings("unused")
  public String createURL(String action, Parameter[] params) {
    return null;
  }

  @SuppressWarnings("unused")
  public String createURL(String action, String objectId, Parameter[] params){
    return null;
  }

  protected void createURL(StringBuilder builder, UIComponent targetComponent, String action, String targetBeanId, Parameter[] params) {
    builder.append(getBaseURL()).append("?").
            append(PortalRequestContext.UI_COMPONENT_ID).append('=').append(targetComponent.getId()) ;
    if(action != null && action.trim().length() > 0) {
      builder.append("&amp;").append(PortalRequestContext.UI_COMPONENT_ACTION).append('=').append(action) ;
    }
    
    if(targetBeanId != null && targetBeanId.trim().length() > 0) {
     /* try {
        targetBeanId = URLEncoder.encode(targetBeanId, "utf-8");
      }catch (Exception e) {
        System.err.println(e.toString());
      }*/
      builder.append("&amp;").append(UIComponent.OBJECTID).append('=').append(targetBeanId) ;
    }
    
    if(params == null || params.length < 1)  return;
    for(Parameter param : params) {
      /*try {
        param.setValue(URLEncoder.encode(param.getValue(), "utf-8"));
      }catch (Exception e) {
        System.err.println(e.toString());
      }*/
      builder.append("&amp;").append(param.getName()).append('=').append(param.getValue()) ;
    }
    
  }
  
}
