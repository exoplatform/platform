/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 27, 2007  
 */
public class JavascriptManager {
  private StringBuilder javascript = new StringBuilder(1000) ;
  private StringBuilder customizedOnloadJavascript ;

  public void addJavascript(CharSequence s) { javascript.append(s).append(" \n") ; }

  public void importJavascript(CharSequence s) {
    javascript.append("eXo.require('").append(s).append("'); \n") ;
  }

  public void importJavascript(String s, String location) {
    if(!location.endsWith("/")) location =  location + '/' ;
    javascript.append("eXo.require('").append(s).append("', '").append(location).append("'); \n") ;
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
