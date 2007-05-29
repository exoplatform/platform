/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormCheckBoxInput;
import org.exoplatform.webui.component.UIFormDateTimeInput;
import org.exoplatform.webui.component.UIFormRadioBoxInput;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTextAreaInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Philippe Aristote
 *          philippe.aristote@gmail.com
 * May 23, 2007  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/component/UIForm.gtmpl"
)
public class UIHelloForm extends UIForm {
  
  @SuppressWarnings("unchecked")
  public UIHelloForm() throws Exception {
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    options.add(new SelectItemOption<String>("Option 1", "Value 1"));
    options.add(new SelectItemOption<String>("Option 2", "Value 2"));
    addUIFormInput(new UIFormStringInput("StringInput", "StringInput", null));
    addUIFormInput(new UIFormTextAreaInput("TextareaInput", "TextareaInput", null));
    addUIFormInput(new UIFormCheckBoxInput("CheckboxInput", "CheckboxInput", null));
    addUIFormInput(new UIFormRadioBoxInput("RadioInput", "RadioInput", options));
    addUIFormInput(new UIFormDateTimeInput("DateInput", "DateInput", new Date()));
    addUIFormInput(new UIFormSelectBox("SelectInput", "SelectInput", options));
  }
}
