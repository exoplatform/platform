/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIDescription;
import org.exoplatform.webui.component.UIRightClickPopupMenu;
import org.exoplatform.webui.component.UITree;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.event.Event;

@ComponentConfig(
  template = "app:/groovy/portal/webui/component/customization/UIPageManagement.gtmpl"
)
public class UIPageManagement extends UIManagement {

  @SuppressWarnings("unused")
  public UIPageManagement() throws Exception {
    addChild(UIPageNodeSelector.class, null, null);
    addChild(UIPageEditBar.class, null, null).setRendered(false);
    addChild(UIDescription.class, null, "pageManagement").setRendered(false);
    addChild(UIContainerConfigOptions.class, null, null).setRendered(false);
    addChild(UIPortletOptions.class, null, null).setRendered(false);
    addChild(UIPageBrowseControlBar.class, null, null).setRendered(false);
    addChild(UIPageNavigationControlBar.class, null, null);
    update();
  }

  private void update() throws Exception {
    UIPageNodeSelector uiPageNodeSelector = getChild(UIPageNodeSelector.class);
    uiPageNodeSelector.loadNavigations();
    PageNode selectedNode = Util.getUIPortal().getSelectedNode();
    if(selectedNode != null) uiPageNodeSelector.selectPageNodeByUri(selectedNode.getUri());
    UIPageNodeSelector uiNodeSelector = findFirstComponentOfType(UIPageNodeSelector.class);

    PageNode node = uiNodeSelector.getSelectedPageNode();
    if (node == null) return;

    Class[] childrenToRender = { UIPageNodeSelector.class, UIPageNavigationControlBar.class};
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
      UIPageNodeSelector uiNodeSelector = getChild(UIPageNodeSelector.class);
      UITree uiTree = uiNodeSelector.getChild(UITree.class);
      UIRightClickPopupMenu uiPopupMenu = uiTree.findFirstComponentOfType(UIRightClickPopupMenu.class);
      uiPopupMenu.createEvent("EditPageNode", event.getExecutionPhase(), event.getRequestContext()).broadcast();
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

}