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

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.portal.webui.application.GadgetUtil;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.URLValidator;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jul 4, 2008  
 */

@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    events = {
      @EventConfig(listeners = UIAddGadget.AddActionListener.class),
      @EventConfig(listeners = UIAddGadget.CancelActionListener.class, phase = Phase.DECODE)
    }
)

public class UIAddGadget extends UIForm {
  
  static final String FIELD_URL = "url" ;
  
  public UIAddGadget() throws Exception {
    addUIFormInput(new UIFormStringInput(FIELD_URL, null, null).
        addValidator(MandatoryValidator.class).addValidator(URLValidator.class)) ;
  }
  
  public static class AddActionListener extends EventListener<UIAddGadget> {

    public void execute(Event<UIAddGadget> event) throws Exception {
      UIAddGadget uiForm = event.getSource() ;
      WebuiRequestContext context = event.getRequestContext() ;
      GadgetRegistryService service = uiForm.getApplicationComponent(GadgetRegistryService.class) ;
      String url = uiForm.getUIStringInput(FIELD_URL) .getValue();
      String name = "gadget" + url.hashCode();
      service.saveGadget(GadgetUtil.toGadget(name, url, false)) ;
      UIGadgetManagement uiManagement = uiForm.getParent() ;
      uiManagement.initData() ;
      uiManagement.setSelectedGadget(name);
      context.addUIComponentToUpdateByAjax(uiManagement) ;
    }    
  }
  
  public static class CancelActionListener extends EventListener<UIAddGadget> {

    public void execute(Event<UIAddGadget> event) throws Exception {
      UIAddGadget uiForm = event.getSource() ;
      UIGadgetManagement uiManagement = uiForm.getParent() ;
      Gadget selectedGadget = uiManagement.getSelectedGadget();
      if(selectedGadget != null) {
        uiManagement.setSelectedGadget(selectedGadget.getName());
      } else uiManagement.reload();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;      
    }
    
  }
  
}