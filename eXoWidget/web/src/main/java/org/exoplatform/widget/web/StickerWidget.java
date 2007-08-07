/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.widget.web;

import java.io.Writer;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.application.UIWidget;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.widget.jcr.UserWidgetDataService;
import org.exoplatform.web.application.mvc.MVCRequestContext;
import org.exoplatform.web.application.widget.WidgetApplication;
//import org.exoplatform.webui.config.annotation.ComponentConfig;
//import org.exoplatform.webui.config.annotation.EventConfig;
//import org.exoplatform.webui.event.Event;
//import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Vu Duy Tu
 *          duytucntt@gmail.com
 * June 13, 2007  
 */

//@ComponentConfig(
//  events = {
//    @EventConfig(listeners = StickerWidget.SaveContentActionListener.class )
//  }
//)
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
//    try{ int k = 3/0;
//    }catch (Exception e) { e.printStackTrace();}
//    
    PortalContainer container  = PortalContainer.getInstance();
    UserWidgetDataService service = 
      (UserWidgetDataService)container.getComponentInstanceOfType(UserWidgetDataService.class) ;    

    PortalRequestContext pContext = Util.getPortalRequestContext();
    MVCRequestContext appReqContext = new MVCRequestContext(this, pContext) ;
    String instanceId = uiWidget.getApplicationInstanceId() ;
//    System.out.println("\n\n\n-------------Idne = " + uiWidget.getApplicationInstanceUniqueId());
    int posX = uiWidget.getProperties().getIntValue("locationX") ;
    int posY = uiWidget.getProperties().getIntValue("locationY") ;
    int zIndex = uiWidget.getProperties().getIntValue("zIndex") ;
    String content = service.getWidgetData(pContext.getRemoteUser(), getApplicationName(), uiWidget.getApplicationInstanceUniqueId());
    if(content == null ) content = "";
    
    w.append("<div id = 'UIStickerWidget' applicationId = '").
      append(instanceId).
      append("' posX = '").append(String.valueOf(posX)). 
      append("' posY = '").append(String.valueOf(posY)).
      append("' zIndex = '").append(String.valueOf(zIndex)).
      append("' content = '").append(content).
      append("'><span></span></div>") ;
    String script = 
      "eXo.portal.UIPortal.createJSApplication('eXo.widget.web.sticker.UIStickerWidget','UIStickerWidget','"+instanceId+"','/eXoWidgetWeb/javascript/');";
    appReqContext.getJavascriptManager().addCustomizedOnLoadScript(script) ;
  }
  
//  static public class SaveContentActionListener  extends EventListener<StickerWidget> {
//    public void execute(Event<StickerWidget> event) throws Exception {
//      System.out.println("\n\n\n\n\n\n\n\n\n  SAVE CONTENT !!!  \n\n\n\n\n\n\n\n\n\n");      
//    }
//  }
}