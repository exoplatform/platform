/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.widget.web;

import java.io.Writer;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.view.UIWidget;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.web.application.mvc.MVCRequestContext;
import org.exoplatform.web.application.widget.WidgetApplication;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 23, 2007  
 */
public class InfoWidget extends WidgetApplication<UIWidget> {
  
  public String getApplicationId() { return "exo.widget.web/InfoWidget"; }

  public String getApplicationName() { return "InfoWidget"; }

  public String getApplicationGroup() { return "exo.widget.web"; }
  
  public void processRender(UIWidget uiWidget, Writer w) throws Exception {
    PortalRequestContext pContext = Util.getPortalRequestContext();
    MVCRequestContext appReqContext = new MVCRequestContext(this, pContext) ;
    
    String instanceId = uiWidget.getApplicationInstanceId() ;
    int posX = uiWidget.getLocationX() ;
    int posY = uiWidget.getLocationY() ;
    
    w.write("<div id = 'UIInfoWidget' applicationId = '"+instanceId+"' posX = '"+posX+"' posY = '"+posY+"'><span></span></div>") ;
    
    String script = 
      "eXo.portal.UIPortal.createJSApplication('eXo.widget.web.info.UIInfoWidget','UIInfoWidget','"+instanceId+"','/exo.widget.web/javascript/');";
    appReqContext.getJavascriptManager().addCustomizedOnLoadScript(script) ;
  }
}