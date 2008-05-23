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
package org.exoplatform.gadget.web;

import java.io.Writer;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.exoplatform.web.application.mvc.MVCRequestContext;
/**
 * Created by The eXo Platform SAS
 * Author : dang.tung
 *          tungcnw@gmail.com
 * May 06, 2008   
 */
public class Sample extends GadgetApplication<UIGadget> {
  
  public String getApplicationId() { return "eXoGadgets/Sample"; }

  public String getApplicationName() { return "Sample"; }

  public String getApplicationGroup() { return "eXoGadgets"; }
  
  public void processRender(UIGadget uiGadget, Writer w) throws Exception {
    PortalRequestContext pContext = Util.getPortalRequestContext();
    MVCRequestContext appReqContext = new MVCRequestContext(this, pContext) ;
    String instanceId = uiGadget.getApplicationInstanceUniqueId() ;
    String url = uiGadget.getUrl() ;
    if(url==null || url.length()==0)url = "http://www.google.com/ig/modules/horoscope.xml";
    int posX = uiGadget.getProperties().getIntValue("locationX") ;
    int posY = uiGadget.getProperties().getIntValue("locationY") ;
    int zIndex = uiGadget.getProperties().getIntValue("zIndex") ;
    
    w.write("<div id='Sample' url = '" +url+"' applicationId = '"+instanceId+"' posX = '"+posX+"' posY = '"+posY+"' zIndex = '"+zIndex+"'><span></span></div>") ;
    
    String script = 
      "eXo.portal.UIPortal.createJSApplication('eXo.gadgets.web.sample.Sample','Sample" + instanceId + "','"+instanceId+"','/eXoGadgets/javascript/');";
    appReqContext.getJavascriptManager().addCustomizedOnLoadScript(script) ;
  }
}