/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageEditBar;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
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
      PortalDataMapper.toUIPortal(uiPortal, userPortalConfig);
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
      PortalRequestContext pContext = (PortalRequestContext) event.getRequestContext();
      UIPageEditBar uiPageEditBar = uiManagement.getChild(UIPageEditBar.class);
      Class<?> [] childrenToRender = null;
      if(uiPageEditBar.isRendered()) {
        childrenToRender = new Class<?>[]{UIPageEditBar.class, UIPageNodeSelector.class, UIPageNavigationControlBar.class};
      } else {
        childrenToRender = new Class<?>[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class};
      }
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);
      pContext.addUIComponentToUpdateByAjax(uiManagement);
      
      
      UIPageNodeSelector nodeSelector =uiManagement.getChild(UIPageNodeSelector.class);
      PageNode node  = nodeSelector.getSelectedPageNode();
      if(node == null) return ;
      UIPage uiPage = Util.toUIPage(node, Util.getUIPortalToolPanel());
      UIPortalToolPanel toolPanel = Util.getUIPortalToolPanel() ; 
      toolPanel.setUIComponent(uiPage);
      toolPanel.getUIComponent().setRendered(true);
      
      UIPortalApplication uiPortalApp = uiPageNav.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pContext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pContext.setFullRender(true);
    }
  }
  
  static public class FinishActionListener  extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageManagement uiPageManagement = event.getSource().getParent(); 
      UIPageEditBar uiPageEditBar = uiPageManagement.getChild(UIPageEditBar.class);
      uiPageEditBar.savePage();
      //TODO: Tung.Pham added
      //------------------
      event.getSource().deleteNavigation() ;
      //------------------
      event.getSource().saveNavigation();
      event.getSource().abort(event);
      //TODO: Tung.Pham added
      //------------------
      UIPortal uiPortal = Util.getUIPortal() ;
      UIPageNodeSelector uiPageNodeSelector = uiPageManagement.getChild(UIPageNodeSelector.class) ;
      PageNode node = uiPageNodeSelector.getSelectedPageNode() ;
      if(node == null) {
        node = getExistPageNode(uiPageNodeSelector.getNavigations()) ;
      }
      if(node != null) {
        String uri = node.getUri() ;
        PageNodeEvent<UIPortal> pnevent ;
        pnevent = new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;      
        uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
      }
      //--------------------

    }
    
    //TODO: Tung.Pham added
    private PageNode getExistPageNode(List<PageNavigation> navis) {
      if(navis == null || navis.size() < 1) return null ;
      for(PageNavigation ele : navis) {
        if(getExistPageNode(ele) == null) continue ;
        return getExistPageNode(ele) ;
      }
      return null ;
    }

    //TODO: Tung.Pham added
    private PageNode getExistPageNode(PageNavigation navi) {
      if(navi == null || navi.getNodes().size() < 1) return null ;
      return navi.getNode(0) ;
    }

  }

  static public class AbortActionListener  extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiControlBar = event.getSource(); 
      uiControlBar.abort(event);
    }
  }

  public void saveNavigation() throws Exception {
    UIPageManagement uiManagement = getAncestorOfType(UIPageManagement.class);
    UIPageNodeSelector uiNodeSelector = uiManagement.getChild(UIPageNodeSelector.class);

    List<PageNavigation> navs = uiNodeSelector.getNavigations();
    UserPortalConfigService dataService = uiManagement.getApplicationComponent(UserPortalConfigService.class);
    String accessUser = Util.getPortalRequestContext().getRemoteUser() ;
    for(PageNavigation nav : navs) {
      //TODO: Tung.Pham modified
      //------------------------------------------------
      //dataService.update(nav);
      if(dataService.getPageNavigation(nav.getId(), accessUser) == null) dataService.create(nav) ;
      else dataService.update(nav) ;
    }
    
    //UIPortal uiPortal = Util.getUIPortal();
    //for(PageNavigation editNav : navs) {
    //  setNavigation(uiPortal.getNavigations(), editNav);
    //}
    List<PageNavigation> portalNavigations = Util.getUIPortal().getNavigations() ;
    for(int i = 0; i < navs.size(); i++) {
      if(!setNavigation(portalNavigations, navs.get(i))) portalNavigations.add(navs.get(i)) ;        
    }
    //------------------------------------------------
  }
  
  //TODO: Tung.Pham added
  public void deleteNavigation() throws Exception {
    UIPageManagement uiManagement = getAncestorOfType(UIPageManagement.class) ;
    UIPageNodeSelector uiPageNodeSelector = uiManagement.getChild(UIPageNodeSelector.class) ;
    
    List<PageNavigation> newNavis = uiPageNodeSelector.getNavigations() ;
    //Remove navis from Database
    UserPortalConfigService configService = uiManagement.getApplicationComponent(UserPortalConfigService.class) ;
    String accessUser = Util.getPortalRequestContext().getRemoteUser() ;
    String portalName = Util.getUIPortal().getName() ;
    UserPortalConfig userPortalConfig = configService.getUserPortalConfig(portalName, accessUser) ;
    if(userPortalConfig == null) return ;
    List<PageNavigation> originNavis = userPortalConfig.getNavigations() ;
    for(PageNavigation navi : originNavis) {
      if(!isExist(newNavis, navi) && navi.isModifiable()) configService.remove(navi);
    }
    
//    Iterator<PageNavigation> itr = originNavis.iterator() ;
//    while(itr.hasNext()) {
//      PageNavigation navi = itr.next() ;
//      if(!isExist(newNavis, navi) && navi.isModifiable()) {
//        itr.remove() ;
//        configService.remove(navi) ;
//      }
//    }
    
    //Remove navis from UIPortal
    Iterator<PageNavigation> itr = Util.getUIPortal().getNavigations().iterator() ;
    while(itr.hasNext()) {
      PageNavigation navi = itr.next() ;
      if(!isExist(newNavis, navi) && navi.isModifiable()) itr.remove() ;
    }
  }
  
  //TODO: Tung.Pham added
  private boolean isExist(List<PageNavigation> navis, PageNavigation navi) {
    for(PageNavigation ele : navis) {
      if(ele.getId().equals(navi.getId())) return true ;
    }
    
    return false ;
  }

//  private void setNavigation(List<PageNavigation> navs, PageNavigation nav) {
//    for(int i = 0; i < navs.size(); i++) {
//      if(navs.get(i).getId().equals(nav.getId())) {
//        navs.set(i, nav);
//        return;
//      }
//    }
//  }

  //TODO: Tung.Pham added
  private boolean setNavigation(List<PageNavigation> navs, PageNavigation nav) {
    for(int i = 0; i < navs.size(); i++) {
      if(navs.get(i).getId().equals(nav.getId())) {
        navs.set(i, nav);
        return true ;
      }
    }
    return false ;
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
