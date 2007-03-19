/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIExoStart;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.webui.component.UIComponentDecorator;
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
        @EventConfig(listeners = UIPageNavigationControlBar.RollbackActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.FinishActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.AbortActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.EditNavigationActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.SaveNavigationActionListener.class)
      }
)

public class UIPageNavigationControlBar extends UIToolbar {
  
  public UIPageNavigationControlBar() throws Exception {
    super();
    setToolbarStyle("PolyToolbar") ;
    setJavascript("Preview","onClick='eXo.portal.UIPortal.switchMode(this);'") ;
  }

  static public class RollbackActionListener extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiPageManagement = event.getSource();
      
      UIPortalApplication uiPortalApp = uiPageManagement.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);

      UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);
      PortalRequestContext prcontext = Util.getPortalRequestContext();
      String remoteUser = prcontext.getRemoteUser();
      String ownerUser = prcontext.getPortalOwner();
      UserPortalConfig userPortalConfig = configService.computeUserPortalConfig(ownerUser, remoteUser);
      UIPortal uiPortal = uiWorkingWS.createUIComponent(prcontext, UIPortal.class, null, null);
      PortalDataModelUtil.toUIPortal(uiPortal, userPortalConfig, true);

      UIPortal oldUIPortal = uiWorkingWS.getChild(UIPortal.class);
      oldUIPortal.setNavigation(uiPortal.getNavigations());

      UIPageNodeSelector uiPageNodeSelector = 
        uiPageManagement.findFirstComponentOfType(UIPageNodeSelector.class);
      uiPageNodeSelector.loadNavigations();

      uiWorkingWS.setRenderedChild(UIPortal.class);

      Util.updateUIApplication(event);
    }
  }
  
  static public class EditNavigationActionListener extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiManagement = event.getSource();
      UIPageManagement management = uiManagement.getParent();
      Util.updateUIApplication(event); 
      UIPageNodeSelector uiNavigationSelector = management.findFirstComponentOfType(UIPageNodeSelector.class);
      if(uiNavigationSelector.getSelectedNavigation() == null) return;
      management.setRenderedChild(UIPageNodeSelector.class);
      UIPageNavigationForm uiNavigationForm =
        Util.showComponentOnWorking(event.getSource(), UIPageNavigationForm.class);      
      uiNavigationForm.setValues(uiNavigationSelector.getSelectedNavigation());
    }
  }
  
  static public class SaveNavigationActionListener extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiManagement = event.getSource();
      uiManagement.saveNavigation(event);
    }
  }
  
  static public class FinishActionListener  extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageManagement uiPageManagement = event.getSource().getParent(); 
      UIPageEditBar uiPageEditBar = uiPageManagement.getChild(UIPageEditBar.class);
      uiPageEditBar.savePage();
      event.getSource().saveNavigation(event);
      event.getSource().abort(event);
    }
  }
  
  static public class AbortActionListener  extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiPageManagement = event.getSource(); 
      uiPageManagement.abort(event);
    }
}

  public void saveNavigation(Event<UIPageNavigationControlBar> event) throws Exception {
    UIPortal uiPortal = Util.getUIPortal();
    List<PageNavigation> navs = uiPortal.getNavigations();
    PortalDAO dataService = uiPortal.getApplicationComponent(PortalDAO.class);
    for(PageNavigation nav : navs){
      dataService.savePageNavigation(nav);        
    }
    
    PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
    UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
    
    UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
    UIComponentDecorator uiWorkingArea = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
    pcontext.addUIComponentToUpdateByAjax(uiWorkingArea);
  }
  
  public void abort(Event<UIPageNavigationControlBar> event) throws Exception {
    UIPortal portal = Util.getUIPortal();
    portal.setMode(UIPortal.COMPONENT_VIEW_MODE);
    portal.setRenderSibbling(UIPortal.class) ;    
    PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
    pcontext.setForceFullUpdate(true);
    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class);
    UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);  ;
    uiExoStart.setUIControlWSWorkingComponent(UIWelcomeComponent.class) ;
  }
}
