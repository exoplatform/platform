/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.portal;

import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.form.UIForm;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          tung.pham@exoplatform.com
 * Nov 2, 2007  
 */

@ComponentConfig(
    template = "system:groovy/portal/webui/portal/UIPortalSettingForm.gtmpl",
    events = {
        @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)
    }
)
public class UIPortalSettingForm extends UIForm {

}
