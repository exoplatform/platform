/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.view.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Thi Hoa
 *          hoa.nguyen@exoplatform.com
 * Sep 5, 2006  
 */
@ComponentConfig(
    template = "app:/groovy/portal/webui/component/customization/UISkinSelector.gtmpl",
    events = @EventConfig(listeners = UISkinSelector.ChangeSkinActionListener.class)
)
public class UISkinSelector extends UIContainer{
  public UISkinSelector() throws Exception{   
    
  }
  
  @SuppressWarnings("unused")
  public List<String> getSkins() throws Exception {
    List<String> listSkin = new ArrayList<String>() ;
    /* 
    SkinConfigService skinConfigService = 
      (SkinConfigService)appContainer.getComponentInstanceOfType(SkinConfigService.class) ;
    Collection decorator =  skinConfigService.getPortalDecorators() ;
        */
    return listSkin ;
  }
  
  static  public class ChangeSkinActionListener extends EventListener<UISkinSelector> {
    public void execute(Event<UISkinSelector> event) throws Exception {      
      String skin  = event.getRequestContext().getRequestParameter(OBJECTID);
      UISkinSelector uicomp = event.getSource() ;
      UIPortalApplication uiApp = uicomp.getAncestorOfType(UIPortalApplication.class);      
      uiApp.setSkin(skin);
    }
  } 

}
