/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.widget.web;

import java.io.Writer;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.application.UIWidget;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.mvc.MVCRequestContext;
import org.exoplatform.web.application.widget.WidgetApplication;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 25, 2007
 */
public class WelcomeWidget extends WidgetApplication<UIWidget> {
  
  public String getApplicationId() { return "eXoWidgetWeb/WelcomeWidget" ; }

  public String getApplicationName() { return "WelcomeWidget"; }

  public String getApplicationGroup() { return "eXoWidgetWeb"; }
  
  public void processRender(UIWidget uiWidget, Writer w) throws Exception {
    PortalRequestContext pContext = Util.getPortalRequestContext();
    MVCRequestContext appReqContext = new MVCRequestContext(this, pContext) ;
    String instanceId = uiWidget.getApplicationInstanceUniqueId() ;
    String userName = pContext.getRemoteUser() ;
    int posX = uiWidget.getProperties().getIntValue("locationX") ;
    int posY = uiWidget.getProperties().getIntValue("locationY") ;
    int zIndex = uiWidget.getProperties().getIntValue("zIndex") ;
    
//    PortalContainer container  = PortalContainer.getInstance();
//    UserWidgetStorageImpl service = 
//      (UserWidgetStorageImpl)container.getComponentInstanceOfType(UserWidgetStorageImpl.class) ;    
//    byte[] bytes = (byte[]) service.get(pContext.getRemoteUser(), "WelcomeWidget", "avatar");
//    if(bytes != null) {
//      System.out.println("\n\n\n-----------IN WELCOME: " + new String(bytes));
//      
//    }
    w.write("<div id = 'UIWelcomeWidget' userName = '"+userName+"' applicationId = '"+instanceId+"' posX = '"+posX+"' posY = '"+posY+"' zIndex = '"+zIndex+"'><span></span></div>") ;
    String script = 
      "eXo.portal.UIPortal.createJSApplication('eXo.widget.web.welcome.UIWelcomeWidget','UIWelcomeWidget','"+instanceId+"','/eXoWidgetWeb/javascript/');";
    appReqContext.getJavascriptManager().addCustomizedOnLoadScript(script) ;
//    appReqContext.getJavascriptManager().importJavascript("eXo.webui.UIUpload") ;
//    appReqContext.getJavascriptManager().addCustomizedOnLoadScript("eXo.webui.UIUpload.initUploadEntry('"+instanceId+"');") ;
  }
}
