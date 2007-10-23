/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.account;

import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Created by The eXo Platform SARL
 * @author tungnd
 *         tung.nguyen@exoplatform.com
 */
import org.exoplatform.webui.config.annotation.EventConfig;

@ComponentConfig(
    template = "system:groovy/portal/webui/portal/UIAccountSetting.gtmpl",
    events = {
        @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)   
    }
)
public class UIAccountSetting extends UIContainer {
  
  final static public String[] ACTIONS = {"Close"} ;
  
  public String[] getActions() { return ACTIONS ; }
  
  public UIAccountSetting() throws Exception {
    addChild(UIAccountProfiles.class, null, null) ;
    addChild(UIAccountChangePass.class, null, null).setRendered(false) ;
  }
}