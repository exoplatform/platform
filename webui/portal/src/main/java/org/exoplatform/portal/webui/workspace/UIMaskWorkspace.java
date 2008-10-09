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

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIComponentDecorator;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Mar 13, 2007  
 */
@ComponentConfig(  
    id = "UIMaskWorkspace",
    template =  "system:/groovy/portal/webui/workspace/UIMaskWorkspace.gtmpl",
    events = @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)
)
public class UIMaskWorkspace extends UIComponentDecorator {  
  
  private int width_  = -1 ;
  private int height_ =  -1 ;
  private boolean isShow = false ;
  private boolean isUpdated = false ;
  
  public int getWindowWidth() { return width_ ; }
  public int getWindowHeight() { return height_ ; }
  public void setWindowSize(int w, int h) {
    width_ = w ;
    height_ = h ;
  }
  
  public boolean isShow() { return isShow; }
  public void setShow(boolean bln) {
    this.isShow = bln;
    if(bln == false) isUpdated = false ;
  }

  public boolean isUpdated() { return isUpdated ; }
  public void setUpdated(boolean bln) { this.isUpdated = bln ; }
  
  public <T extends UIComponent> T createUIComponent(Class<T> clazz, String configId, String id)throws Exception {
    T uicomponent = super.createUIComponent(clazz, configId, id);
    setUIComponent(uicomponent);
    return uicomponent;
  }
  
  public <T extends UIComponent> T createUIComponent(Class<T> clazz) throws Exception {
    return createUIComponent(clazz, null, null);
  }
  
  public void setUIComponent(UIComponent uicomponent) { 
    super.setUIComponent(uicomponent);
    setShow(uicomponent != null);
  }
  
  static  public class CloseActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      UIMaskWorkspace uiMaskWorkspace = null;
      UIComponent uiSource = event.getSource();
      if(uiSource instanceof UIMaskWorkspace) {
        uiMaskWorkspace = (UIMaskWorkspace) uiSource;
      } else {
        uiMaskWorkspace = uiSource.getAncestorOfType(UIMaskWorkspace.class);
      }
      if(uiMaskWorkspace == null || !uiMaskWorkspace.isShow()) return;
      uiMaskWorkspace.setUIComponent(null);
      uiMaskWorkspace.setWindowSize(-1, -1);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWorkspace) ;
    }
  }
}
