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

  final static private String FIELD_THEME = "UIItemThemeSelector" ;
  
  public UIFormInputThemeSelector(String name, String bindingField) throws Exception {
    super(name, bindingField, String.class) ;
    setComponentConfig(UIFormInputThemeSelector.class, null) ;
    UIItemThemeSelector uiThemeSelector = new UIItemThemeSelector(FIELD_THEME, null) ;
    addChild(uiThemeSelector) ;
  }
  public void decode(Object input, WebuiRequestContext context) throws Exception {
  }
}