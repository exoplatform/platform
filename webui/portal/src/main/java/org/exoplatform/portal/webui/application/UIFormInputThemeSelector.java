/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.form.UIFormInputBase;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          tung.pham@exoplatform.com
 * Nov 5, 2007  
 */

@ComponentConfig  (
    template = "system:/groovy/webui/form/UIFormInputThemeSelector.gtmpl"
)
public class UIFormInputThemeSelector extends UIFormInputBase<String> {

  final static private String FIELD_THEME = "themetemp" ;
  
  public UIFormInputThemeSelector(String name, String bindingField) throws Exception {
    super(name, bindingField, String.class) ;
    setComponentConfig(UIFormInputThemeSelector.class, null) ;
    UIItemThemeSelector uiThemeSelector = new UIItemThemeSelector(FIELD_THEME, null) ;
    addChild(uiThemeSelector) ;
  }
  public void decode(Object input, WebuiRequestContext context) throws Exception {
  }
}