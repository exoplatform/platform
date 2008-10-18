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
package org.exoplatform.webui.application.portlet;

import org.exoplatform.web.application.Parameter;
import org.exoplatform.web.application.URLBuilder;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;

/**
 * Created by The eXo Platform SAS
 * Apr 3, 2007  
 */
public class PortletURLBuilder extends URLBuilder<UIComponent> {
  
  public PortletURLBuilder() {
    super(null);
  }
  
  public PortletURLBuilder(String baseURL) {
    super(baseURL);
  }
  
  public String createURL(String action, Parameter[] params) { 
    return null; 
  }
 
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
