/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UILoginForm;
import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy  
 *          lebienthuy@gmail.com
 * Mar 19, 2007  
 */
@ComponentConfig (
    template = "system:/groovy/webui/component/UIBannerPortlet.gtmpl",
    events = @EventConfig(listeners = UIBannerPortlet.LoginActionListener.class)
)
public class UIBannerPortlet extends UIPortletApplication {
  public UIBannerPortlet() throws Exception {
    super();
    System.out.println("\n\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
  }

  static  public class LoginActionListener extends EventListener<UIBannerPortlet> {
    public void execute(Event<UIBannerPortlet> event) throws Exception {      
      @SuppressWarnings("unused")
      UIBannerPortlet uicom = event.getSource();
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      UILoginForm uiForm = uiMaskWS.createUIComponent(UILoginForm.class, null, null);
      uiMaskWS.setUIComponent(uiForm) ;
      uiMaskWS.setShow(true) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }

}
