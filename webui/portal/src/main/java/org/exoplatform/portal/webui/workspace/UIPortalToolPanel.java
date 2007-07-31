/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.workspace;

import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.web.application.JavascriptManager;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIComponentDecorator;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 12, 2006
 */
@ComponentConfig(
    template = "system:/groovy/portal/webui/workspace/UIPortalToolPanel.gtmpl"
)
public class UIPortalToolPanel extends UIComponentDecorator {
  private boolean showMaskLayer = false;

  public UIPortalToolPanel() throws Exception {    
  }
  
  public <T extends UIComponent> void setWorkingComponent(Class<T> clazz, String id) throws Exception {
    UIComponent component = createUIComponent(clazz, null, id) ;
    setUIComponent(component) ;
  }
    
  public void setWorkingComponent(UIComponent component) {    
    setUIComponent(component) ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    JavascriptManager jsmanager = context.getJavascriptManager(); 
    String init = "eXo.core.UIMaskLayer.createMask('UIPortalToolPanel', null, 10) ;";
    UIComponent uiComponent = getUIComponent();
    if(uiComponent instanceof UIPage){
      UIPage uiPage = (UIPage) uiComponent;
      if(uiPage.isShowMaxWindow()){
        uiComponent.processRender(context);
        if(showMaskLayer ){
          init = "eXo.core.UIMaskLayer.createMask('UIPage', null, 10) ;";
          jsmanager.importJavascript("eXo.core.UIMaskLayer");
          jsmanager.addCustomizedOnLoadScript(init);
        }
        return;
      }
    }
    
    super.processRender(context);
    if(showMaskLayer){
      jsmanager.importJavascript("eXo.core.UIMaskLayer");
      jsmanager.addCustomizedOnLoadScript(init);
    }
  }
  
  public boolean isShowMaskLayer() { return showMaskLayer; }
  public void setShowMaskLayer(boolean showMaskLayer) { this.showMaskLayer = showMaskLayer; }
}