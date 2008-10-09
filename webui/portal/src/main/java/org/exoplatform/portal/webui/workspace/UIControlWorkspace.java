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

import java.io.Writer;

import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponentDecorator;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
  id = "UIControlWorkspace",
  template = "system:/groovy/portal/webui/workspace/UIControlWorkspace.gtmpl",
  events = @EventConfig(listeners = UIControlWorkspace.SetVisibleActionListener.class)
)
public class UIControlWorkspace extends UIContainer {
  
  public  static String WORKING_AREA_ID = "UIControlWSWorkingArea" ;
  
  private String visible = "false";

  public UIControlWorkspace() throws Exception {
    addChild(UIExoStart.class, null, null) ;
    UIControlWSWorkingArea uiWorking = addChild(UIControlWSWorkingArea.class, null, WORKING_AREA_ID);
    uiWorking.setUIComponent(uiWorking.createUIComponent(UIWelcomeComponent.class, null, null));
  } 
  
  public String getVisible() { return visible; }
  public void setVisible(String visible) { this.visible = visible; }
  
  static public class SetVisibleActionListener extends EventListener<UIControlWorkspace> {
    public void execute(Event<UIControlWorkspace> event) throws Exception {
      UIControlWorkspace uiControlWorkspace = event.getSource();
      uiControlWorkspace.setVisible(event.getRequestContext().getRequestParameter(OBJECTID));
      event.getRequestContext().setResponseComplete(true) ;
    }
  }

  @ComponentConfig()
  static public class UIControlWSWorkingArea extends UIComponentDecorator {
    public void processRender(WebuiRequestContext context) throws Exception {      
      Writer w =  context.getWriter() ;
      w.write("<div id=\"") ; w.write(getId()); w.write("\">");
      super.renderChildren();
      w.write("</div>");
    }
  }

}

