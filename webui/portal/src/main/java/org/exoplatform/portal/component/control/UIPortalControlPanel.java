/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.control;

import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIQuickHelp;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
@ComponentConfig(
    template = "system:/groovy/portal/webui/component/control/UIPortalControlPanel.gtmpl" ,
    initParams = @ParamConfig(
        name = "help.UIPortalControlPanelQuickHelp",
        value = "system:/WEB-INF/conf/uiconf/portal/webui/component/control/UIPortalControlPanelQuickHelp.xhtml"
    )
)
public class UIPortalControlPanel extends UIContainer {
  
  private UIComponent uiComp_ ;

  public  UIPortalControlPanel(InitParams params) throws Exception{
    addChild(UIQuickBar.class, null, null) ;    
    UIQuickHelp uiQuickHelp = addChild(UIQuickHelp.class, null, null) ;
    uiQuickHelp.setHelpUri(params.getParam("help.UIPortalControlPanelQuickHelp").getValue()) ;
    uiComp_ = uiQuickHelp ;
  }

  public UIComponent getHelpComponent() { return uiComp_ ; }

  public String getQuickHelpUri() { 
    return getComponentConfig().getInitParams().getParam("help.UIPortalControlPanelQuickHelp").getValue() ; 
  }

  public <T extends UIComponent> UIComponent replaceWorkingUIComponent(Class<T> clazz , 
                                                                       String configId, 
                                                                       String id) throws Exception {
    UIComponent uiCurrent = getChild(1) ;
    return replaceChild(uiCurrent.getId(), clazz, configId, id) ;
  }

}
