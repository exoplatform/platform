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

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.application.UserWidgetStorage;
import org.exoplatform.portal.webui.application.UIWidget;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.mvc.MVCRequestContext;
import org.exoplatform.web.application.widget.WidgetApplication;

/**
 * Created by The eXo Platform SAS
 * Author : Vu Duy Tu
 *          duytucntt@gmail.com
 * June 13, 2007  
 */

public class StickerWidget extends WidgetApplication<UIWidget> {
  private String content_;
  
  public String getContent() {
    return content_;
  }

  public void setContent(String content) {
    this.content_ = content;
  }

  public String getApplicationId() { 
    return "eXoWidgetWeb/StickerWidget"; }

  public String getApplicationName() { return "StickerWidget"; }

  public String getApplicationGroup() { return "eXoWidgetWeb"; }
  
  public void processRender(UIWidget uiWidget, Writer w) throws Exception {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    UserWidgetStorage service = 
      (UserWidgetStorage)container.getComponentInstanceOfType(UserWidgetStorage.class) ;    

    PortalRequestContext pContext = Util.getPortalRequestContext();
    MVCRequestContext appReqContext = new MVCRequestContext(this, pContext) ;
    String instanceId = uiWidget.getApplicationInstanceUniqueId();
    int posX = uiWidget.getProperties().getIntValue("locationX") ;
    int posY = uiWidget.getProperties().getIntValue("locationY") ;
    int zIndex = uiWidget.getProperties().getIntValue("zIndex") ;
    String userName = pContext.getRemoteUser() ;
    byte[] bytes = null ;
    if(userName != null && userName.trim().length() > 0) {
      bytes = (byte[]) service.get(pContext.getRemoteUser(), getApplicationName(), instanceId);
    }
    String content = null ;
    if(bytes != null ) content = new String(bytes);
    if(content == null ) content = "";
    content = content.trim();
    w.append("<div id = 'UIStickerWidget' applicationId = '" + instanceId).
      append("' posX = '").append(String.valueOf(posX)). 
      append("' posY = '").append(String.valueOf(posY)).
      append("' zIndex = '").append(String.valueOf(zIndex)).
      append("' content = '").append(content).
      append("'><span></span></div>") ;
    String script = 
      "eXo.portal.UIPortal.createJSApplication('eXo.widget.web.sticker.UIStickerWidget','UIStickerWidget','"+instanceId+"','/eXoWidgetWeb/javascript/');";
    appReqContext.getJavascriptManager().addCustomizedOnLoadScript(script) ;
  }

}