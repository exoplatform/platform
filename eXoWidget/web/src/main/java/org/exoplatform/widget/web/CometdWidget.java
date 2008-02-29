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
 * Created by eXo Platform SARL
 * Author : Jérémi Joslin
 *          jeremi@exoplatform.com
 * May 25, 2007
 */
public class CometdWidget extends WidgetApplication<UIWidget> {

  public String getApplicationId() { return "eXoWidgetWeb/CometdWidget" ; }

  public String getApplicationName() { return "CometdWidget"; }

  public String getApplicationGroup() { return "eXoWidgetWeb"; }

  public void processRender(UIWidget uiWidget, Writer w) throws Exception {
    PortalRequestContext pContext = Util.getPortalRequestContext();
    MVCRequestContext appReqContext = new MVCRequestContext(this, pContext) ;
    String instanceId = uiWidget.getApplicationInstanceUniqueId() ;
    String userName = pContext.getRemoteUser() ;
    String token = (String) pContext.getRequest().getSession().getAttribute("cometdToken");
    if (token == null) {
        token = "" + Math.random();
        pContext.getRequest().getSession().setAttribute("cometdToken", token);
    }

    int posX = uiWidget.getProperties().getIntValue("locationX") ;
    int posY = uiWidget.getProperties().getIntValue("locationY") ;
    int zIndex = uiWidget.getProperties().getIntValue("zIndex") ;
    w.write("<div id = 'UICometdWidget' userName = '"+userName+"' token='" + token + "' applicationId = '"+instanceId+"' posX = '"+posX+"' posY = '"+posY+"' zIndex = '"+zIndex+"'><span></span></div>") ;
   
 	String script =
      "eXo.portal.UIPortal.createJSApplication('eXo.widget.web.cometd.UICometdWidget','UICometdWidget','"+instanceId+"','/eXoWidgetWeb/javascript/');";
    appReqContext.getJavascriptManager().addCustomizedOnLoadScript(script) ;
//    appReqContext.getJavascriptManager().importJavascript("eXo.webui.UIUpload") ;
//    appReqContext.getJavascriptManager().addCustomizedOnLoadScript("eXo.webui.UIUpload.initUploadEntry('"+instanceId+"');") ;
  }
}
