/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.web;

import java.io.Writer;

import org.exoplatform.portal.webui.application.UIExoApplication;
import org.exoplatform.web.application.mvc.MVCApplication;
import org.exoplatform.web.application.mvc.MVCRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 23, 2007  
 */
public class WebBrowserApplication extends MVCApplication {
  public String getApplicationId() { return "eXoAppWeb/eXoBrowser"; }

  public String getApplicationName() { return "eXoBrowser"; }
  public String getApplicationGroup() { return "eXoAppWeb"; }
  public String getApplicationType() { return EXO_APPLICATION_TYPE; }
  
  public void processAction(MVCRequestContext context) throws Exception {
    
  }
  
  public void processRender(MVCRequestContext context) throws Exception {
    //Application.init() ;
    
    UIExoApplication uiExoApplication = (UIExoApplication)context.getAttribute(UIExoApplication.class) ;
    String instanceId = uiExoApplication.getApplicationInstanceId() ;
    
    String posX = uiExoApplication.getProperties().get("locationX") ;
    String posY = uiExoApplication.getProperties().get("locationY") ;
    String zIndex = uiExoApplication.getProperties().get("zIndex") ;
    String windowWidth = uiExoApplication.getProperties().get("windowWidth") ;
    String windowHeight = uiExoApplication.getProperties().get("windowHeight") ;
    
    String display = "" ;
    String appStatus = uiExoApplication.getProperties().get("appStatus") ;
    
    if("SHOW".equals(appStatus)) display = "block" ;
    
    else display = "" ;
    
    String cssStyle = "style=\"";
//    if(posX != null || posY != null || zIndex != null || appWidth != null || appHeight != null) {
//      cssStyle = "style=\"";
//    }
        
    if(posX != null) cssStyle += "left: "+ posX +"px; " ;
    if(posY != null)  cssStyle += "top: "+ posY +"px; " ;
    if(zIndex != null) cssStyle += "z-index: "+ zIndex +"; " ;
    if(windowWidth != null) cssStyle += "width: "+ windowWidth +"px; " ;
    if(windowHeight != null) cssStyle += "height: "+ windowHeight +"px; " ;
    cssStyle += "display: "+ display +"; " ;
    cssStyle += "visibility: hidden; " ;
    
    if(cssStyle.length() > 0) cssStyle += "\"";
    
    Writer w =  context.getWriter() ;
    w.write("<div id='WebBrowserApplicationDetector' cssStyle='"+cssStyle+"'><span></span></div>") ;
    
    String script = 
      "eXo.portal.UIPortal.createJSApplication('eXo.application.browser.UIBrowserApplication','eXoBrowser','"+instanceId+"','/eXoAppWeb/javascript/');";
    context.getJavascriptManager().addCustomizedOnLoadScript(script) ;
  }
}
