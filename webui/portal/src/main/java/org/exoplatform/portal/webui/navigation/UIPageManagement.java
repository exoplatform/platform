/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.UIManagement;
import org.exoplatform.portal.webui.application.UIPortletOptions;
import org.exoplatform.portal.webui.container.UIContainerConfigOptions;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageBrowser;
import org.exoplatform.portal.webui.page.UIPageEditBar;
import org.exoplatform.portal.webui.page.UIPageForm;
import org.exoplatform.portal.webui.page.UIPageTemplateOptions;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIDescription;
import org.exoplatform.webui.event.Event;

@ComponentConfig(
  template = "app:/groovy/portal/webui/navigation/UIPageManagement.gtmpl"
)
public class UIPageManagement extends UIManagement {

  @SuppressWarnings("unused")
  public UIPageManagement() throws Exception {
    addChild(UIPageNodeSelector.class, null, null);
    addChild(UIPageEditBar.class, null, null).setRendered(false);
    addChild(UIDescription.class, null, "pageManagement").setRendered(false);
    addChild(UIContainerConfigOptions.class, null, null).setRendered(false);
    addChild(UIPortletOptions.class, null, null).setRendered(false);
    addChild(UIPageBrowser.UIPageBrowseControlBar.class, null, null).setRendered(false);
    addChild(UIPageNavigationControlBar.class, null, null);
    update();
  }

  private void update() throws Exception {
    UIPageNodeSelector uiPageNodeSelector = getChild(UIPageNodeSelector.class);
    uiPageNodeSelector.loadNavigations();
    //TODO: Tung.Pham
    //-----------------------------------
//    PageNode selectedNode = Util.getUIPortal().getSelectedNode();
//    if(selectedNode != null) uiPageNodeSelector.selectPageNodeByUri(selectedNode.getUri());
//    UIPageNodeSelector uiNodeSelector = findFirstComponentOfType(UIPageNodeSelector.class);
//
//    PageNode node = uiNodeSelector.getSelectedPageNode();
    //-----------------------------------
    PageNode node = uiPageNodeSelector.getSelectedPageNode();
    if (node == null) return;

    Class<?>[] childrenToRender = { UIPageNodeSelector.class, UIPageNavigationControlBar.class};
    setRenderedChildrenOfTypes(childrenToRender);

    UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();
    UserPortalConfigService portalConfigService = getApplicationComponent(UserPortalConfigService.class);
    Page page = portalConfigService.getPage(node.getPageReference(), Util.getPortalRequestContext().getRemoteUser());
    if(page == null) return;
    UIPage uiPage = Util.toUIPage(page, uiToolPanel);
    uiToolPanel.setUIComponent(uiPage);
    uiToolPanel.setRenderSibbling(UIPortalToolPanel.class);
  }

  public <T extends UIComponent> T setRendered(boolean b) {
    getChild(UIPageEditBar.class).setRendered(false);
    return super.<T> setRendered(b);
  }

  public void setMode(ManagementMode mode, Event<? extends UIComponent> event) throws Exception {
    mode_ = mode;
    if (mode == ManagementMode.EDIT) {
      //TODO: Tung.Pham modified
      //------------------------------------------------------------
      PortalRequestContext pcontext  = Util.getPortalRequestContext() ;
      UIPortalApplication uiApp = getAncestorOfType(UIPortalApplication.class);
      UIPageNodeSelector uiNodeSelector = getChild(UIPageNodeSelector.class);
      //UITree uiTree = uiNodeSelector.getChild(UITree.class);
      //UIRightClickPopupMenu uiPopupMenu = uiTree.findFirstComponentOfType(UIRightClickPopupMenu.class);
      //uiPopupMenu.createEvent("EditPageNode", event.getExecutionPhase(), event.getRequestContext()).broadcast();
      PageNode selectedNode = uiNodeSelector.getSelectedPageNode() ;
      if(selectedNode == null) {
        setPage(null) ;
        return ;
      }
      UserPortalConfigService portalConfigService = getApplicationComponent(UserPortalConfigService.class);
      Page page  = portalConfigService.getPage(selectedNode.getPageReference(), pcontext.getRemoteUser());

      if(!page.isModifiable()){
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.Invalid-editPermission", null)) ;
        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return;
      }
      setPage(page) ;
      //------------------------------------------------------------
      getChild(UIDescription.class).setRendered(false);
      return;
    }
    
    UIWorkspace uiWorkingWS = Util.updateUIApplication(event);
    getChild(UIPageNodeSelector.class).setRendered(false);
    getChild(UIPageNavigationControlBar.class).setRendered(false);
    getChild(UIDescription.class).setRendered(true);
    
    UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
    UIPageBrowser uiPageBrowser = uiToolPanel.createUIComponent(UIPageBrowser.class, null, null);
    uiToolPanel.setUIComponent(uiPageBrowser);
    uiPageBrowser.setShowAddNewPage(true);    
    uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
  }
  
  //TODO: Tung.Pham added
  public void setPage(Page page) throws Exception {
    PortalRequestContext pcontext  = Util.getPortalRequestContext() ;
    UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();
    UIPortalApplication uiApp = getAncestorOfType(UIPortalApplication.class);
    
    UIControlWorkspace uiControl = uiApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
    pcontext.addUIComponentToUpdateByAjax(uiControl);
    uiToolPanel.setRenderSibbling(UIPortalToolPanel.class) ;
    
    if(page == null){
      Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
      setRenderedChildrenOfTypes(childrenToRender);
      uiToolPanel.setUIComponent(null);
      UIWorkspace uiWorkingWS = uiApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      return;
    }
    
    UIPage uiPage  = Util.toUIPage(page, uiToolPanel);  
    uiToolPanel.setUIComponent(uiPage);

    if (Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;      
      UIPageForm uiPageForm =  uiMaskWS.createUIComponent(UIPageForm.class);
      uiPageForm.removeChild(UIPageTemplateOptions.class);
      uiPageForm.setValues(uiPage);
      uiMaskWS.setUIComponent(uiPageForm);
      uiMaskWS.setWindowSize(640, 400);
      uiMaskWS.setShow(true);
      pcontext.addUIComponentToUpdateByAjax(uiMaskWS);
      return ;
    }
    
    if(!page.isModifiable()) {
      Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
      setRenderedChildrenOfTypes(childrenToRender);
      return;
    }
    
    UIWorkspace uiWorkingWS = uiApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
    pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
    pcontext.setFullRender(true);
    
    Class [] childrenToRender = {UIPageEditBar.class, UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
    setRenderedChildrenOfTypes(childrenToRender);
    UIPageEditBar uiPageEditBar = getChild(UIPageEditBar.class);
    uiPageEditBar.setUIPage(uiPage); 
    uiPageEditBar.showUIPage();
  }

}