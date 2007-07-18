/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.portal;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIExoStart;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIToolbar;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : LeBienThuy  
 *          lebienthuy@gmail.com
 * Mar 16, 2007  
 */
@ComponentConfig(
    template = "system:/groovy/webui/core/UIToolbar.gtmpl",
    events = {   
        @EventConfig(listeners = UIPortalManagementControlBar.RollbackActionListener.class),
        @EventConfig(listeners = UIPortalManagementControlBar.AbortActionListener.class),
        @EventConfig(listeners = UIPortalManagementControlBar.SaveActionListener.class),
        @EventConfig(listeners = UIPortalManagementControlBar.FinishActionListener.class)
    }
)

public class UIPortalManagementControlBar extends UIToolbar {
  
  public UIPortalManagementControlBar() throws Exception {
    super();
    setToolbarStyle("ControlToolbar") ;
    setJavascript("Preview","onClick='eXo.portal.UIPortal.switchMode(this);'") ;
  }
  
  public void save() throws Exception {
    UIPortal uiPortal = Util.getUIPortal();     
    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class);
    
    PortalConfig portalConfig  = PortalDataMapper.toPortal(uiPortal);
    UserPortalConfig userPortalConfig = uiPortalApp.getUserPortalConfig();
    userPortalConfig.setPortal(portalConfig);
    
    UserPortalConfigService configService = getApplicationComponent(UserPortalConfigService.class);     
    configService.update(portalConfig);
    
    uiPortalApp.setSkin(uiPortal.getSkin());
  }
  
  public void abort(Event<UIPortalManagementControlBar> event) throws Exception {
    UIPortal portal = Util.getUIPortal();
    portal.setMode(UIPortal.COMPONENT_VIEW_MODE);
    portal.setRenderSibbling(UIPortal.class) ;    
    PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
    pcontext.setFullRender(true);
    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class);
    UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);  ;
    uiExoStart.setUIControlWSWorkingComponent(UIWelcomeComponent.class) ;
    
    UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
    UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
    pcontext.addUIComponentToUpdateByAjax(uiControl);
    pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;  
  }
  
  static public class RollbackActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalManagementControlBar uiPortalManagement = event.getSource();      
      UIWorkspace uiWorkingWS = Util.updateUIApplication(event);
      
      UserPortalConfigService configService = uiPortalManagement.getApplicationComponent(UserPortalConfigService.class);     
      PortalRequestContext prContext = Util.getPortalRequestContext();     
      
      String remoteUser = prContext.getRemoteUser();
      String ownerUser = prContext.getPortalOwner();   
      UserPortalConfig userPortalConfig = configService.getUserPortalConfig(ownerUser, remoteUser);      
      UIPortal uiPortal = uiWorkingWS.createUIComponent(prContext, UIPortal.class, null, null) ;
      PortalDataMapper.toUIPortal(uiPortal, userPortalConfig);
      
      UIPortal oldUIPortal =uiWorkingWS.getChild(UIPortal.class);
      uiWorkingWS.setBackupUIPortal(oldUIPortal);
      
      uiWorkingWS.replaceChild(oldUIPortal.getId(), uiPortal);
      uiWorkingWS.setRenderedChild(UIPortal.class) ;  
    }
  }
  
  static public class SaveActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalManagementControlBar uiPortalManagement = event.getSource(); 
      uiPortalManagement.save();
      Util.updateUIApplication(event);
    }
  }  
  
  static public class FinishActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalManagementControlBar uiPortalManagement = event.getSource();   
      uiPortalManagement.save();
      uiPortalManagement.abort(event);
    }
  }
  
  static public class AbortActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      
      PortalRequestContext prContext = Util.getPortalRequestContext();  
      UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);     
      
      String remoteUser = prContext.getRemoteUser();
      String ownerUser = prContext.getPortalOwner();   
      UserPortalConfig userPortalConfig = configService.getUserPortalConfig(ownerUser, remoteUser);      
      UIPortal uiPortal = uiWorkingWS.createUIComponent(prContext, UIPortal.class, null, null) ;
      PortalDataMapper.toUIPortal(uiPortal, userPortalConfig);
      
      UIPortal oldUIPortal =uiWorkingWS.getChild(UIPortal.class);
      uiWorkingWS.setBackupUIPortal(oldUIPortal);
      uiWorkingWS.replaceChild(oldUIPortal.getId(), uiPortal);
      uiWorkingWS.setRenderedChild(UIPortal.class) ;  
      
      event.getSource().abort(event);
    }
  }
}
