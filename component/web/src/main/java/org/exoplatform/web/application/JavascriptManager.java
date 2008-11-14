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
package org.exoplatform.web.application;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.web.application.javascript.JavascriptConfigService;
import org.exoplatform.commons.utils.PropertyManager;

/**
 * Created by The eXo Platform SAS
 * Mar 27, 2007  
 */
public class JavascriptManager {

  private StringBuilder javascript = new StringBuilder(1000) ;
  private StringBuilder customizedOnloadJavascript ;
  private JavascriptConfigService jsSrevice_ ;
  
  public JavascriptManager() {
    jsSrevice_ = (JavascriptConfigService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(JavascriptConfigService.class);
  }
  
  public void addJavascript(CharSequence s) { javascript.append(s).append(" \n") ; }

  public void importJavascript(CharSequence s) {
    if(!jsSrevice_.isModuleLoaded(s) || PropertyManager.isDevelopping()) {
      javascript.append("eXo.require('").append(s).append("'); \n") ;      
    }
  }

  public void importJavascript(String s, String location) {
    if(!location.endsWith("/")) location =  location + '/' ;
    if(!jsSrevice_.isModuleLoaded(s) || PropertyManager.isDevelopping()) {
      javascript.append("eXo.require('").append(s).append("', '").append(location).append("'); \n") ;
    }
  }


  public void addOnLoadJavascript(CharSequence s) {
    String id = Integer.toString(Math.abs(s.hashCode())) ;
    javascript.
    append("eXo.core.Browser.addOnLoadCallback('mid").append(id).
    append("',").append(s).append("); \n") ;
  }

  public void addOnResizeJavascript(CharSequence s) {
    String id = Integer.toString(Math.abs(s.hashCode())) ;
    javascript.
    append("eXo.core.Browser.addOnResizeCallback('mid").append(id).
    append("',").append(s).append("); \n") ;
  }

  public void addOnScrollJavascript(CharSequence s) {
    String id = Integer.toString(Math.abs(s.hashCode())) ;
    javascript.
    append("eXo.core.Browser.addOnScrollCallback('mid").append(id).
    append("',").append(s).append("); \n") ;
  }

  public String getJavascript() { return javascript.toString() ; }

  public void addCustomizedOnLoadScript(CharSequence s) {
    if(customizedOnloadJavascript == null) customizedOnloadJavascript = new StringBuilder() ;
    customizedOnloadJavascript.append(s).append("\n") ;
  }

  public String getCustomizedOnLoadScript() { 
    if(customizedOnloadJavascript == null)  return "" ;
    return customizedOnloadJavascript.toString() ; 
  }
}
