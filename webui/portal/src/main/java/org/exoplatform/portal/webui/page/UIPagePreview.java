/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.page;

import org.exoplatform.portal.webui.application.UIApplication;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIComponentDecorator;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv@exoplatform.com
 * Aug 23, 2006  
 */

@ComponentConfigs({
  @ComponentConfig( lifecycle = UIComponentDecorator.UIComponentDecoratorLifecycle.class ),
  @ComponentConfig(
      id = "UIPagePreviewWithMessage", 
      template = "app:/groovy/portal/webui/page/UIPagePreview.gtmpl"
        //events = @EventConfig(listeners = UIPagePreview.BackActionListener.class)
  )
})
public class UIPagePreview extends UIComponentDecorator {  
//  private UIComponent uiBackComponent ;
//  
//  public UIComponent getBackComponent() { return uiBackComponent ; }
//  public void setBackComponent(UIComponent uiComp) { uiBackComponent = uiComp ; }
//  
//  public boolean hasBackEvent(){ return uiBackComponent != null; }
  
//  static public class BackActionListener extends EventListener<UIPagePreview> {
//    public void execute(Event<UIPagePreview> event) throws Exception {
//      UIPagePreview uiPreview = event.getSource() ;
//      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();      
//      UIComponent uiComp = uiPreview.getBackComponent() ;
//      uiToolPanel.setUIComponent(uiComp) ;
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiToolPanel) ;
//    }
//  }
  public boolean isPageHasApplication(){
    if(uicomponent_ == null) return false ;
    UIApplication existingApp = uicomponent_.findFirstComponentOfType(UIApplication.class) ;
    if(existingApp != null) return true ;
    
    return false ;
  }
}
