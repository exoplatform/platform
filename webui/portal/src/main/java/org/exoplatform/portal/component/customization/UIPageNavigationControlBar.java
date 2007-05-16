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
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.webui.component.UIContainer;
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
        @EventConfig(listeners = UIPageNavigationControlBar.EditNavigationActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.SaveNavigationActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.SeparateLineActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.BackActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.RollbackActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.AbortActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.FinishActionListener.class)        
    }
)

public class UIPageNavigationControlBar extends UIToolbar {

  public UIPageNavigationControlBar() throws Exception {
    setToolbarStyle("ControlToolbar") ;
    setJavascript("Preview","onClick='eXo.portal.UIPortal.switchMode(this);'") ;
  }

  static public class RollbackActionListener extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiPageNav = event.getSource();
      UIPortalApplication uiPortalApp = uiPageNav.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);

      UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);
      PortalRequestContext prcontext = Util.getPortalRequestContext();

      UIPortal oldUIPortal = Util.getUIPortal();
      String remoteUser = prcontext.getRemoteUser();
      String ownerUser = oldUIPortal.getOwner();

      UserPortalConfig userPortalConfig = configService.getUserPortalConfig(ownerUser, remoteUser);
      UIPortal uiPortal = uiWorkingWS.createUIComponent(prcontext, UIPortal.class, null, null);
      PortalDataModelUtil.toUIPortal(uiPortal, userPortalConfig);
      oldUIPortal.setNavigation(uiPortal.getNavigations());

      UIPageNodeSelector uiPageNodeSelector = uiPageNav.<UIContainer>getParent().findFirstComponentOfType(UIPageNodeSelector.class);
      uiPageNodeSelector.loadNavigations();

      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiControl);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS);
    }
  }

  static public class BackActionListener extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiPageNav = event.getSource();
      UIPageManagement uiManagement = uiPageNav.getParent();
      UIPageEditBar uiPageEditBar = uiManagement.getChild(UIPageEditBar.class);
      Class [] childrenToRender = null;
      if(uiPageEditBar.isRendered()) {
        childrenToRender = new Class[]{UIPageEditBar.class, UIPageNodeSelector.class, UIPageNavigationControlBar.class};
      } else {
        childrenToRender = new Class[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class};
      }
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
    }
  }

  static public class SeparateLineActionListener extends EventListener<UIPageNavigationControlBar> {
    @SuppressWarnings("unused")
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {

    }
  }

  static public class EditNavigationActionListener extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {     
      UIPageNavigationControlBar bar = event.getSource();
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;     

      UIPageNavigationForm navigationForm = uiMaskWS.createUIComponent(UIPageNavigationForm.class, null, null);
      UIPageManagement management = bar.getParent();
      UIPageNodeSelector uiNavigationSelector = management.findFirstComponentOfType(UIPageNodeSelector.class);      
      navigationForm.setValues(uiNavigationSelector.getSelectedNavigation());
      uiMaskWS.setUIComponent(navigationForm);      
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }

  static public class SaveNavigationActionListener extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiControlBar = event.getSource();
      uiControlBar.saveNavigation(event);
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
      UIPageNavigationControlBar uiControlBar = event.getSource(); 
      uiControlBar.abort(event);
    }
  }

  public void saveNavigation(Event<UIPageNavigationControlBar> event) throws Exception {
    UIPageNavigationControlBar uiPageNav = event.getSource();
    UIPageManagement uiManagement = uiPageNav.getParent();
    UIPageNodeSelector uiNodeSelector = uiManagement.getChild(UIPageNodeSelector.class);

    List<PageNavigation> navs = uiNodeSelector.getNavigations();
    UserPortalConfigService dataService = uiManagement.getApplicationComponent(UserPortalConfigService.class);
    for(PageNavigation nav : navs) {
      dataService.update(nav);    
    }
    
    UIPortal uiPortal = Util.getUIPortal();
    for(PageNavigation editNav : navs) {
      setNavigation(uiPortal.getNavigations(), editNav);
    }
  }
  
  private void setNavigation(List<PageNavigation> navs, PageNavigation nav) {
    for(int i = 0; i < navs.size(); i++) {
      if(navs.get(i).getId().equals(nav.getId())) {
        navs.set(i, nav);
        return;
      }
    }
  }

  public void abort(Event<UIPageNavigationControlBar> event) throws Exception {
    UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
    PortalRequestContext prContext = Util.getPortalRequestContext();  
    
    UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
    UIControlWSWorkingArea uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
    uiWorking.setUIComponent(uiWorking.createUIComponent(UIWelcomeComponent.class, null, null));
    prContext.addUIComponentToUpdateByAjax(uiControl);    
    
    UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
    uiWorkingWS.setRenderedChild(UIPortal.class) ;
    prContext.addUIComponentToUpdateByAjax(uiWorkingWS) ;      
    prContext.setFullRender(true);
  }
}
