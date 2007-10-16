/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.account;

import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Created by The eXo Platform SARL
 * @author tungnd
 *         tung.nguyen@exoplatform.com
 */

@ComponentConfig(
    template = "system:groovy/webui/core/UITabPane.gtmpl"
)

public class UIAccountSetting extends UIContainer {
  
  public UIAccountSetting() throws Exception {
    addChild(UIAccountProfiles.class, null, null) ;
    addChild(UIAccountChangePass.class, null, null).setRendered(false) ;
  }
}