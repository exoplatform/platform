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
package org.exoplatform.portal.webui.workspace;

import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 12, 2006
 */

@ComponentConfig(
  id = "UIWorkingWorkspace",
  template = "system:/groovy/portal/webui/workspace/UIWorkingWorkspace.gtmpl",
  events = {
      @EventConfig(listeners = UIMainActionListener.CreatePortalActionListener.class),
      @EventConfig(listeners = UIMainActionListener.EditCurrentPageActionListener.class),
      @EventConfig(listeners = UIMainActionListener.PageCreationWizardActionListener.class),
      @EventConfig(listeners = UIMainActionListener.EditInlineActionListener.class)
  }
)
public class UIWorkingWorkspace extends UIContainer {
  
  private UIPortal backupUIPortal = null;
  
  public UIPortal getBackupUIPortal() { return backupUIPortal; }
  
  public void setBackupUIPortal(UIPortal uiPortal) { backupUIPortal = uiPortal;   }
}
