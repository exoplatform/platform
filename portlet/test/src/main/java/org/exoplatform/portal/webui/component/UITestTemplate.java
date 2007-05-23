/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : lxchiati  
 *          lebienthuy@gmail.com
 * Nov 23, 2006  
 */
@ComponentConfig(
    template =  "app:/groovy/webui/component/UITestTemplate.gtmpl"
)
public class UITestTemplate extends UIComponent { 
  public UITestTemplate() throws Exception {}
}
