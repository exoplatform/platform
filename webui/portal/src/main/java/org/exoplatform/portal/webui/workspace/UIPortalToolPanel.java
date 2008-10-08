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
package org.exoplatform.portal.webui.workspace;

import org.exoplatform.portal.config.model.Page;
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
    UIComponent uiComponent = getUIComponent();
    if(uiComponent instanceof UIPage){
      UIPage uiPage = (UIPage) uiComponent;
      //if(uiPage.isShowMaxWindow()){
      if(Page.DESKTOP_PAGE.equals(uiPage.getFactoryId())){
        uiComponent.processRender(context);
        if(showMaskLayer ){
          jsmanager.importJavascript("eXo.core.UIMaskLayer");
          jsmanager.addCustomizedOnLoadScript("eXo.core.UIMaskLayer.createMask('UIPage', null, 10) ;");
        }
        return;
      }
    }
    
    super.processRender(context);
    if(showMaskLayer){
      jsmanager.importJavascript("eXo.core.UIMaskLayer");
      jsmanager.addCustomizedOnLoadScript("eXo.core.UIMaskLayer.createMask('UIPortalToolPanel', null, 10) ;");
    }
  }
  
  public boolean isShowMaskLayer() { return showMaskLayer; }
  public void setShowMaskLayer(boolean showMaskLayer) { this.showMaskLayer = showMaskLayer; }
}