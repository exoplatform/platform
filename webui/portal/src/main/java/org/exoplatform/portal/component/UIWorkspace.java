/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component;

import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIContainer;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 12, 2006
 */

@ComponentConfig(
  id = "UIWorkingWorkspace",
  template = "system:/groovy/portal/webui/component/UIWorkingWorkspace.gtmpl"
)
public class UIWorkspace extends UIContainer {
  
  private UIPortal backupUIPortal = null;
  
  public UIPortal getBackupUIPortal() { return backupUIPortal; }
  
  public void setBackupUIPortal(UIPortal uiPortal) { backupUIPortal = uiPortal;   }
}
