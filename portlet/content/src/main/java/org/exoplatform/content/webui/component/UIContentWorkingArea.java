/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.content.webui.component;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIDescription;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv@exoplatform.com
 * Jul 25, 2006  
 */

@ComponentConfig()
public class UIContentWorkingArea extends UIContainer {
  
  public UIContentWorkingArea() throws Exception {
    addChild(UIDetailContent.class, null, null);
    addChild(UIContentForm.class, null, null).setRendered(false) ;
    addChild(UIDescription.class, null, "contentPortlet").setRendered(false) ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    renderChildren(context) ;
  }
}
