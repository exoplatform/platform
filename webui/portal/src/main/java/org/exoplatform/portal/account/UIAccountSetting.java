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
package org.exoplatform.portal.account;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 */

import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
/**
 * Created by The eXo Platform SARL
 * @author tungnd
 *         tung.nguyen@exoplatform.com
 */

@ComponentConfig(
    template = "system:groovy/portal/webui/portal/UIAccountSettingForm.gtmpl",
    //template = "system:/groovy/webui/form/UIVTabInputSet.gtmpl",
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