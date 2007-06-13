/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.portal;

import java.util.List;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.UIContainer;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIGrid;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Thanh Tung
 *          tung.pham@exoplatform.com
 * May 25, 2007  
 */
@ComponentConfigs({
  @ComponentConfig(
    template = "app:/groovy/portal/webui/portal/UIChangePortal.gtmpl",
    events = @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class) 
//      @EventConfig(listeners = UIChangePortal.SelectPortalActionListener.class),
  ),
  @ComponentConfig(
    id = "PortalSelector",
    type = UIGrid.class,
    template = "app:/groovy/portal/webui/portal/UIPortalSelector.gtmpl"
  )
})

public class UIChangePortal extends UIContainer {
  
  public static String[] BEAN_FEILD = {"creator", "name", "skin", "factoryId"} ;
  public static String[] SELECT_ACTIONS = {"SelectPortal"} ;
  
  public UIChangePortal() throws Exception {
    setName("UIChangePortal") ;
    UIGrid uiGrid = addChild(UIGrid.class, "PortalSelector", null) ;
    uiGrid.configure("name", BEAN_FEILD, SELECT_ACTIONS) ;
  
    DataStorage dataService = getApplicationComponent(DataStorage.class) ;
    List<PortalConfig> configs = dataService.getAllPortalConfig() ;
    PageList pageList = new ObjectPageList(configs, 10) ;
    uiGrid.getUIPageIterator().setPageList(pageList) ;
  }
  
 /* static public class SelectPortalActionListener extends EventListener<UIChangePortal> {
    public void execute(Event<UIChangePortal> event) throws Exception {
      String portalName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      if (portalName == null) return ;
      
      UIChangePortal uiChangePortal = event.getSource() ;
      UIPortalApplication uiPortalApp = uiChangePortal.getAncestorOfType(UIPortalApplication.class) ;      
      UIWorkspace uiWorkspace = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID) ;
      
      UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class) ;
      PortalRequestContext prContext = Util.getPortalRequestContext() ;
      String remoteUser = prContext.getRemoteUser() ;
      UserPortalConfig userPortalConfig = configService.getUserPortalConfig(portalName, remoteUser) ;
      
      if (userPortalConfig == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIChangePortal.msg.Invalid-viewPermission", null)) ;
        prContext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages()) ;
        return ;
      }
      
      UIPortal uiPortal = uiWorkspace.createUIComponent(prContext, UIPortal.class, null, null) ;
      PortalDataMapper.toUIPortal(uiPortal, userPortalConfig) ;
      UIPortal oldPortal = uiWorkspace.getChild(UIPortal.class) ;
      uiWorkspace.replaceChild(oldPortal.getId(), uiPortal) ;
      uiWorkspace.setRenderedChild(UIPortal.class) ;
      if(uiPortal.getSkin() != null && uiPortal.getSkin().trim().length() > 0){
        uiPortalApp.setSkin(uiPortal.getSkin());
      }
      
      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID) ;
      UIControlWSWorkingArea uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID) ;
      uiWorking.setUIComponent(uiWorking.createUIComponent(UIWelcomeComponent.class, null, null)) ;
      
      prContext.addUIComponentToUpdateByAjax(uiControl) ;
      prContext.addUIComponentToUpdateByAjax(uiWorkspace) ;
      prContext.setFullRender(true) ;
      
      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ; 
      uiMaskWS.setUIComponent(null);
      prContext.addUIComponentToUpdateByAjax(uiMaskWS) ;
    }
  }*/
}

























