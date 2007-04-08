/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 8, 2006
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/portal/webui/component/customization/UIAddApplication.gtmpl"
)

public class UIAddApplication extends UIForm { 

  public UIAddApplication() throws Exception {//InitParams initParams
    System.out.println("\n\n\n_____________________________\n_________________________\n\n");
    addChild(UIAddPortlet.class, null, null).setRendered(true);
//    addChild(UIPageForm.class, null, null).setRendered(false);
//    addChild(UIAddJSApplication.class, null, null).setRendered(false);
//    <% uicomponent.renderChildren(); %>
  } 
  
  public int size(){
    System.out.println("\n\n\n____________________\n______________ " + getChildren().size());
    return getChildren().size();
  }
}
