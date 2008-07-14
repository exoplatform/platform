/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.applicationregistry.webui.component;

import java.util.List;

import org.exoplatform.web.application.gadget.GadgetApplication;
import org.exoplatform.web.application.gadget.GadgetRegistryService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 24, 2008  
 */

@ComponentConfig(
    template = "app:/groovy/applicationregistry/webui/component/UIGadgetManagement.gtmpl",
    events = {
        @EventConfig(listeners = UIGadgetManagement.AddRemoteGadgetActionListener.class),
        @EventConfig(listeners = UIGadgetManagement.RemoveGadgetActionListener.class)
    }
)

public class UIGadgetManagement extends UIContainer {
  
  private List<GadgetApplication> gadgets_ ;
  
  public UIGadgetManagement() throws Exception {
    reload() ;
  }
  
  public void reload() throws Exception {
    GadgetRegistryService service = getApplicationComponent(GadgetRegistryService.class) ;
    gadgets_ = service.getAllGadgets() ;
  }
  
  public List<GadgetApplication> getGadgets() throws Exception {
    return gadgets_ ; 
  }
  

  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
  }
  
  public static class AddRemoteGadgetActionListener extends EventListener<UIGadgetManagement> {

    public void execute(Event<UIGadgetManagement> event) throws Exception {
      UIGadgetManagement uiManagement = event.getSource() ;
      uiManagement.getChildren().clear() ;
      uiManagement.addChild(UIGadgetEditor.class, null, null) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
    
  }
  
  public static class RemoveGadgetActionListener extends EventListener<UIGadgetManagement> {

    public void execute(Event<UIGadgetManagement> event) throws Exception {
      UIGadgetManagement uiManagement = event.getSource() ;
      String id = event.getRequestContext().getRequestParameter(OBJECTID) ;
      GadgetRegistryService service = uiManagement.getApplicationComponent(GadgetRegistryService.class) ;
      service.removeGadget(id) ;
      uiManagement.reload() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
    
  }

}
