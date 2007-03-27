/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIExoStart;
import org.exoplatform.portal.component.control.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.webui.component.UIToolbar;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : LeBienThuy  
 *          lebienthuy@gmail.com
 * Mar 16, 2007  
 */
@ComponentConfig(
    template = "system:/groovy/webui/component/UIToolbar.gtmpl",
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
  
  public void save(Event<UIPortalManagementControlBar> event) throws Exception {
    UIPortal uiPortal = Util.getUIPortal();     
    PortalConfig portalConfig  = PortalDataModelUtil.toPortalConfig(uiPortal, true);
    PortalDAO dataService = uiPortal.getApplicationComponent(PortalDAO.class);
    dataService.savePortalConfig(portalConfig);
    Util.updateUIApplication(event);
  }
  
  public void abort(Event<UIPortalManagementControlBar> event) throws Exception {
    UIPortal portal = Util.getUIPortal();
    portal.setMode(UIPortal.COMPONENT_VIEW_MODE);
    portal.setRenderSibbling(UIPortal.class) ;    
    PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
    pcontext.setForceFullUpdate(true);
    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class);
    UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);  ;
    uiExoStart.setUIControlWSWorkingComponent(UIWelcomeComponent.class) ;
  }
  
  static public class RollbackActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalManagementControlBar uiPortalManagement = event.getSource();      
      UIWorkspace uiWorkingWS = Util.updateUIApplication(event);
      
      UserPortalConfigService configService = uiPortalManagement.getApplicationComponent(UserPortalConfigService.class);     
      PortalRequestContext prContext = Util.getPortalRequestContext();     
      
      String remoteUser = prContext.getRemoteUser();
      String ownerUser = prContext.getPortalOwner();   
      UserPortalConfig userPortalConfig = configService.computeUserPortalConfig(ownerUser, remoteUser);      
      UIPortal uiPortal = uiWorkingWS.createUIComponent(prContext, UIPortal.class, null, null) ;
      PortalDataModelUtil.toUIPortal(uiPortal, userPortalConfig, true);
      
      UIPortal oldUIPortal =uiWorkingWS.getChild(UIPortal.class);
      uiWorkingWS.setBackupUIPortal(oldUIPortal);
      
      uiWorkingWS.replaceChild(oldUIPortal.getId(), uiPortal);
      uiWorkingWS.setRenderedChild(UIPortal.class) ;  
    }
  }
  
  static public class SaveActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalManagementControlBar uiPortalManagement = event.getSource(); 
      uiPortalManagement.save(event);
    }
  }  
  
  static public class FinishActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalManagementControlBar uiPortalManagement = event.getSource();   
      uiPortalManagement.save(event);
      uiPortalManagement.abort(event);
    }
  }
  
  static public class AbortActionListener  extends EventListener<UIPortalManagementControlBar> {
    public void execute(Event<UIPortalManagementControlBar> event) throws Exception {
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      PortalRequestContext prContext = Util.getPortalRequestContext();  
      uiWorkingWS.setRenderedChild(UIPortal.class) ;
      
      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      UIControlWSWorkingArea uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
      uiWorking.setUIComponent(uiWorking.createUIComponent(UIWelcomeComponent.class, null, null));
      prContext.addUIComponentToUpdateByAjax(uiControl);
      
      prContext.addUIComponentToUpdateByAjax(uiWorkingWS) ;      
      prContext.setForceFullUpdate(true);  
      
    }
  }
}
