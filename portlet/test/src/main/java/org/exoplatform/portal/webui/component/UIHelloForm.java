/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
    addUIFormInput(new UIFormStringInput("StringInput", "StringInput", null));
    addUIFormInput(new UIFormTextAreaInput("TextareaInput", "TextareaInput", null));
    addUIFormInput(new UIFormCheckBoxInput("CheckboxInput", "CheckboxInput", null));
    addUIFormInput(new UIFormRadioBoxInput("RadioInput", "RadioInput", options));
    addUIFormInput(new UIFormDateTimeInput("DateInput", "DateInput", new Date()));
    addUIFormInput(new UIFormSelectBox("SelectInput", "SelectInput", options));
  }
}
