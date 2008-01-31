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
package org.exoplatform.portal.webui.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormDateTimeInput;
import org.exoplatform.webui.form.UIFormRadioBoxInput;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;

/**
 * Created by The eXo Platform SARL
 * Author : Philippe Aristote
 *          philippe.aristote@gmail.com
 * May 23, 2007  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl"
)
public class UIHelloForm extends UIForm {
  
  @SuppressWarnings("unchecked")
  public UIHelloForm() throws Exception {
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    options.add(new SelectItemOption<String>("Option 1", "Value 1"));
    options.add(new SelectItemOption<String>("Option 2", "Value 2"));
    options.add(new SelectItemOption<String>("Option 3", "Value 3"));
    addUIFormInput(new UIFormStringInput("StringInput", "StringInput", null).setMaxLength(5));
    addUIFormInput(new UIFormTextAreaInput("TextareaInput", "TextareaInput", null));
    addUIFormInput(new UIFormCheckBoxInput("CheckboxInput", "CheckboxInput", null));
    addUIFormInput(new UIFormRadioBoxInput("RadioInput", "RadioInput", options));
    addUIFormInput(new UIFormDateTimeInput("DateInput", "DateInput", new Date()));
    addUIFormInput(new UIFormSelectBox("SelectInput", "SelectInput", options));
  }
}
