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
