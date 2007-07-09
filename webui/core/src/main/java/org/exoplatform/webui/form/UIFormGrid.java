/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIGrid;
/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 9, 2007  
 */
@ComponentConfig(template = "system:/groovy/webui/core/UIGrid.gtmpl")
public class UIFormGrid  extends UIGrid {
  
  public UIFormGrid() throws Exception {
    uiIterator_ = createUIComponent(UIFormPageIterator.class, null, null);
  }
}
