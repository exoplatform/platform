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
package org.exoplatform.portal.webui.page;

import org.exoplatform.portal.webui.application.UIApplication;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.core.UIComponentDecorator;

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
      template = "system:/groovy/portal/webui/page/UIPagePreview.gtmpl"
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
