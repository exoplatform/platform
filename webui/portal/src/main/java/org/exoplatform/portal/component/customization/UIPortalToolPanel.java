/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.portal.component.view.UIPage;
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
    template = "system:/groovy/portal/webui/component/customization/UIPortalToolPanel.gtmpl"
)
public class UIPortalToolPanel extends UIComponentDecorator {
  
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
    UIComponent uiComponent = getUIComponent();
    if(uiComponent instanceof UIPage){
      UIPage uiPage = (UIPage) uiComponent;
      if(uiPage.isShowMaxWindow()){
        uiComponent.processRender(context);
        return;
      }
    }
    super.processRender(context);
  }
  
}