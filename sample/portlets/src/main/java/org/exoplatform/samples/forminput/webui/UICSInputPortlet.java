/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.samples.forminput.webui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.ext.UIFormColorPicker;
import org.exoplatform.webui.form.ext.UIFormComboBox;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Apr 14, 2009  
 */
@ComponentConfig(
                 lifecycle = UIApplicationLifecycle.class
)
public class UICSInputPortlet extends UIPortletApplication {

  public UICSInputPortlet() throws Exception {
    addChild(UITestForm.class, null, null);
  }
  
  @ComponentConfig(
                   lifecycle = UIFormLifecycle.class,
                   template = "app:/groovy/webui/component/UICSInputForm.gtmpl",
                   events = {
                     @EventConfig(listeners = UITestForm.SubmitActionListener.class)
                   }
  )
  static public class UITestForm extends UIForm {
    
    Map<String, String> data = new HashMap<String, String>();
    
    public UITestForm() throws Exception {
      ArrayList<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
      options.add(new SelectItemOption<String>("option 1", "option 1"));
      options.add(new SelectItemOption<String>("option 2", "option 2"));
      options.add(new SelectItemOption<String>("option 3", "option 3"));
      options.add(new SelectItemOption<String>("option 4", "option 4"));
      addUIFormInput(new UIFormComboBox("UICombobox", "UICombobox", options));
      addUIFormInput(new UIFormColorPicker("UIColorPicker", "UIColorPicker", (String) null));
    }
    static public class SubmitActionListener extends EventListener<UITestForm> {

      public void execute(Event<UITestForm> event) throws Exception {
        UITestForm uiForm = event.getSource();
        UIFormComboBox uiComboBox = uiForm.getChild(UIFormComboBox.class);
        uiForm.data.put(uiComboBox.getId(), uiComboBox.getValue());
        UIFormColorPicker uiColorPicker = uiForm.getChild(UIFormColorPicker.class);
        uiForm.data.put(uiColorPicker.getId(), uiColorPicker.getValue());
      }
      
    }
  }
}
