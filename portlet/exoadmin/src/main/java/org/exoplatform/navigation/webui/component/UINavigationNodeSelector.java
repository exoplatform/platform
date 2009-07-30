/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.navigation.webui.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.navigation.PageNavigationUtils;
import org.exoplatform.portal.webui.page.UIDesktopPage;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageBody;
import org.exoplatform.portal.webui.page.UIPageEditWizard;
import org.exoplatform.portal.webui.page.UIPagePreview;
import org.exoplatform.portal.webui.page.UIPageTemplateOptions;
import org.exoplatform.portal.webui.page.UIWizardPageSetInfo;
import org.exoplatform.portal.webui.portal.UIPortalComposer;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMainActionListener;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Copied by The eXo Platform SARL Author May 28, 2009 3:07:15 PM
 */
@ComponentConfigs({
  @ComponentConfig(
                   template = "app:/groovy/navigation/webui/component/UINavigationNodeSelector.gtmpl",
                   events = { 
                       @EventConfig(listeners = UINavigationNodeSelector.ChangeNodeActionListener.class)
                   }
  ),
  @ComponentConfig(
                   id = "NavigationNodePopupMenu",
                   type = UIRightClickPopupMenu.class,
                   template = "system:/groovy/webui/core/UIRightClickPopupMenu.gtmpl",
                   events = {
                     @EventConfig(listeners = UINavigationNodeSelector.AddNodeActionListener.class),
                     @EventConfig(listeners = UINavigationNodeSelector.EditPageNodeActionListener.class),
                     @EventConfig(listeners = UINavigationNodeSelector.EditSelectedNodeActionListener.class),
                     @EventConfig(listeners = UINavigationNodeSelector.CopyNodeActionListener.class),
                     @EventConfig(listeners = UINavigationNodeSelector.CutNodeActionListener.class),
                     @EventConfig(listeners = UINavigationNodeSelector.CloneNodeActionListener.class),
                     @EventConfig(listeners = UINavigationNodeSelector.PasteNodeActionListener.class),
                     @EventConfig(listeners = UINavigationNodeSelector.MoveUpActionListener.class),
                     @EventConfig(listeners = UINavigationNodeSelector.MoveDownActionListener.class),
                     @EventConfig(listeners = UINavigationNodeSelector.DeleteNodeActionListener.class, confirm = "UIPageNodeSelector.deleteNavigation") 
                   }
  ),
  @ComponentConfig(
                   id = "UINavigationNodeSelectorPopupMenu",
                   type = UIRightClickPopupMenu.class,
                   template = "system:/groovy/webui/core/UIRightClickPopupMenu.gtmpl",
                   events = {}
  ) 
})
public class UINavigationNodeSelector extends UIContainer {

  private List<PageNavigation> navigations;

  private SelectedNode         selectedNode;

  private SelectedNode         copyNode;

  private List<PageNavigation> deleteNavigations = new ArrayList<PageNavigation>();

  public UINavigationNodeSelector() throws Exception {
    addChild(UIRightClickPopupMenu.class, "UINavigationNodeSelectorPopupMenu", null).setRendered(false);

    UITree uiTree = addChild(UITree.class, null, "TreeNodeSelector");
    uiTree.setIcon("DefaultPageIcon");
    uiTree.setSelectedIcon("DefaultPageIcon");
    uiTree.setBeanIdField("uri");
    uiTree.setBeanLabelField("resolvedLabel");
    uiTree.setBeanIconField("icon");

    UIRightClickPopupMenu uiPopupMenu = createUIComponent(UIRightClickPopupMenu.class,
                                                          "NavigationNodePopupMenu",
                                                          null);
    uiPopupMenu.setActions(new String[] { "AddNode", "EditPageNode", "EditSelectedNode",
        "CopyNode", "CloneNode", "CutNode", "DeleteNode", "MoveUp", "MoveDown" });
    uiTree.setUIRightClickPopupMenu(uiPopupMenu);
  }

  public List<PageNavigation> getNavigations() {
    return navigations;
  }

  public void initNavigations(List<PageNavigation> navis) throws Exception {
    navigations = navis;
    // set resolved label for navigations
    WebuiRequestContext requestContext = WebuiRequestContext.getCurrentInstance();
    refreshNavigation(requestContext.getLocale());

    updateUI();
    selectNavigation();
  }

  public void loadNavigationByNavId(Integer navId, List<PageNavigation> navigations) throws Exception {
    this.navigations = navigations;
    // set resolved label for navigations
    WebuiRequestContext requestContext = WebuiRequestContext.getCurrentInstance();
    refreshNavigation(requestContext.getLocale());
    selectNavigation(navId);
  }

  private void updateUI() {
    if (navigations == null || navigations.size() < 1) {
      getChild(UITree.class).setSibbling(null);
      return;
    }
  }

  private void selectNavigation() {
    if (navigations == null || navigations.size() < 1)
      return;
    if (selectedNode == null) {
      PageNavigation navigation = navigations.get(0);
      selectedNode = new SelectedNode(navigation, null, null);
      if (navigation.getNodes().size() > 0)
        selectedNode.setNode(navigation.getNodes().get(0));
    }
    selectNavigation(selectedNode.getPageNavigation().getId());
    if (selectedNode.getNode() != null)
      selectPageNodeByUri(selectedNode.getNode().getUri());
  }

  public void selectNavigation(int id) {
    for (int i = 0; i < navigations.size(); i++) {
      if (navigations.get(i).getId() != id)
        continue;
      selectedNode = new SelectedNode(navigations.get(i), null, null);
      selectPageNodeByUri(null);
      UITree uiTree = getChild(UITree.class);
      uiTree.setSibbling(navigations.get(i).getNodes());
    }
  }

  public void refreshNavigation(Locale locale) {
    LocaleConfig localeConfig = getApplicationComponent(LocaleConfigService.class).getLocaleConfig(locale.getLanguage());
    for (PageNavigation nav : navigations) {
      if (nav.getOwnerType().equals(PortalConfig.USER_TYPE))
        continue;
      ResourceBundle res = localeConfig.getNavigationResourceBundle(nav.getOwnerType(),
                                                                    nav.getOwnerId());
      for (PageNode node : nav.getNodes()) {
        resolveLabel(res, node);
      }
    }
  }

  private void resolveLabel(ResourceBundle res, PageNode node) {
    node.setResolvedLabel(res);
    if (node.getChildren() == null)
      return;
    for (PageNode childNode : node.getChildren()) {
      resolveLabel(res, childNode);
    }
  }

  public void selectPageNodeByUri(String uri) {
    if (selectedNode == null)
      return;
    UITree tree = getChild(UITree.class);
    List<?> sibbling = tree.getSibbling();
    tree.setSibbling(null);
    tree.setParentSelected(null);
    selectedNode.setNode(searchPageNodeByUri(selectedNode.getPageNavigation(), uri));
    if (selectedNode.getNode() != null) {
      tree.setSelected(selectedNode.getNode());
      tree.setChildren(selectedNode.getNode().getChildren());
      return;
    }
    tree.setSelected(null);
    tree.setChildren(null);
    tree.setSibbling(sibbling);
  }

  public PageNode searchPageNodeByUri(PageNavigation pageNav, String uri) {
    if (pageNav == null || uri == null)
      return null;
    List<PageNode> pageNodes = pageNav.getNodes();
    UITree uiTree = getChild(UITree.class);
    for (PageNode ele : pageNodes) {
      PageNode returnPageNode = searchPageNodeByUri(ele, uri, uiTree);
      if (returnPageNode == null)
        continue;
      if (uiTree.getSibbling() == null)
        uiTree.setSibbling(pageNodes);
      return returnPageNode;
    }
    return null;
  }

  private PageNode searchPageNodeByUri(PageNode pageNode, String uri, UITree tree) {
    if (pageNode.getUri().equals(uri))
      return pageNode;
    List<PageNode> children = pageNode.getChildren();
    if (children == null)
      return null;
    for (PageNode ele : children) {
      PageNode returnPageNode = searchPageNodeByUri(ele, uri, tree);
      if (returnPageNode == null)
        continue;
      if (tree.getSibbling() == null)
        tree.setSibbling(children);
      if (tree.getParentSelected() == null)
        tree.setParentSelected(pageNode);
      selectedNode.setParentNode(pageNode);
      return returnPageNode;
    }
    return null;
  }

  public List<PageNavigation> getPageNavigations() {
    if (navigations == null)
      navigations = new ArrayList<PageNavigation>();
    return navigations;
  }

  public void addPageNavigation(PageNavigation navigation) {
    if (navigations == null)
      navigations = new ArrayList<PageNavigation>();
    navigations.add(navigation);
    updateUI();
  }

  public void deletePageNavigation(PageNavigation navigation) {
    if (navigations == null || navigations.size() < 1)
      return;
    navigations.remove(navigation);
    deleteNavigations.add(navigation);
    selectedNode = null;
    selectNavigation();
    updateUI();
  }

  public PageNavigation getPageNavigation(int id) {
    for (PageNavigation ele : getPageNavigations()) {
      if (ele.getId() == id)
        return ele;
    }
    return null;
  }

  public void processRender(WebuiRequestContext context) throws Exception {
    UIRightClickPopupMenu uiPopupMenu = getChild(UIRightClickPopupMenu.class);
    if (uiPopupMenu != null) {
      if (navigations == null || navigations.size() < 1)
        uiPopupMenu.setRendered(false);
      else
        uiPopupMenu.setRendered(true);
    }
    super.processRender(context);
  }

  public SelectedNode getCopyNode() {
    return copyNode;
  }

  public void setCopyNode(SelectedNode copyNode) {
    this.copyNode = copyNode;
  }

  static public class ChangeNodeActionListener extends EventListener<UITree> {
    public void execute(Event<UITree> event) throws Exception {
      String uri = event.getRequestContext().getRequestParameter(OBJECTID);
      UINavigationNodeSelector uiNodeSelector = event.getSource().getParent();
      uiNodeSelector.selectPageNodeByUri(uri);

      UINavigationManagement nodeManager = uiNodeSelector.getParent();
      event.getRequestContext().addUIComponentToUpdateByAjax(nodeManager);

      UIContainer uiParent = uiNodeSelector.getParent();
      Class<?>[] childrenToRender = { UINavigationNodeSelector.class };
      uiParent.setRenderedChildrenOfTypes(childrenToRender);
    }
  }

  static public class AddNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      String uri = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu uiPopupMenu = event.getSource();

      UINavigationNodeSelector uiNodeSelector = uiPopupMenu.getAncestorOfType(UINavigationNodeSelector.class);
      UIPopupWindow uiManagementPopup = uiNodeSelector.getAncestorOfType(UIPopupWindow.class);
      UIPageNodeForm2 uiNodeForm = uiManagementPopup.createUIComponent(UIPageNodeForm2.class,
                                                                       null,
                                                                       null);
      uiNodeForm.setValues(null);
      uiManagementPopup.setUIComponent(uiNodeForm);

      Object parent = null;
      List<PageNode> pageNodes = uiNodeSelector.getSelectedNavigation().getNodes();
      if (uri != null && uri.trim().length() > 0) {
        for (PageNode pageNode : pageNodes) {
          parent = PageNavigationUtils.searchPageNodeByUri(pageNode, uri);
          if (parent != null)
            break;
        }
      }
      if (parent == null)
        parent = uiNodeSelector.getSelectedNavigation();
      uiNodeForm.setSelectedParent(parent);
      uiManagementPopup.setWindowSize(800, 500);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagementPopup);
    }
  }

  
  static public class EditPageNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      // get URI 
      String uri = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      
      // get UINavigationNodeSelector  
      UIRightClickPopupMenu uiPopupMenu = event.getSource();

      UINavigationNodeSelector uiNodeSelector = uiPopupMenu.getAncestorOfType(UINavigationNodeSelector.class);
      
      // get Selected PageNode 
      PageNode selectedPageNode = null;
      List<PageNode> pageNodes = uiNodeSelector.getSelectedNavigation().getNodes();
      if (uri != null && uri.trim().length() > 0) {
        for (PageNode pageNode : pageNodes) {
          selectedPageNode = PageNavigationUtils.searchPageNodeByUri(pageNode, uri);
          if (selectedPageNode != null)
            break;
        }
      }
      
      UIPortalApplication uiApp = Util.getUIPortalApplication();
      uiApp.setModeState(UIPortalApplication.APP_BLOCK_EDIT_MODE);
      UIWorkingWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
      uiWorkingWS.addChild(UIPortalComposer.class, "UIPageEditor", null);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class);
      uiToolPanel.setShowMaskLayer(false);
      uiToolPanel.setWorkingComponent(UIPage.class, null);
      UIPage uiPage = (UIPage) uiToolPanel.getUIComponent();

      UserPortalConfigService userService = uiToolPanel.getApplicationComponent(UserPortalConfigService.class);
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
      
      // get selected page
      Page selectPage = null;
      if (selectedPageNode.getPageReference() != null) {
        selectPage = userService.getPage(selectedPageNode.getPageReference(),
                                         context.getRemoteUser());
      }

      selectPage.setModifier(context.getRemoteUser());
      selectPage.setTitle(selectedPageNode.getLabel());
      
      // convert Page to UIPage
      PortalDataMapper.toUIPage(uiPage, selectPage);

      Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS);
      Util.getPortalRequestContext().setFullRender(true);
    }
  }

  static public class EditSelectedNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      WebuiRequestContext ctx = event.getRequestContext();
      UIRightClickPopupMenu popupMenu = event.getSource();
      UIApplication uiApp = ctx.getUIApplication();
      String uri = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UINavigationNodeSelector uiNodeSelector = popupMenu.getAncestorOfType(UINavigationNodeSelector.class);
      PageNavigation selectedNav = uiNodeSelector.getSelectedNavigation();
      Object obj = PageNavigationUtils.searchParentNode(selectedNav, uri);
      PageNode selectedNode = PageNavigationUtils.searchPageNodeByUri(selectedNav, uri);
      String pageId = selectedNode.getPageReference();

      UserPortalConfigService service = uiApp.getApplicationComponent(UserPortalConfigService.class);
      Page node = (pageId != null) ? service.getPage(pageId) : null;
      if (node != null) {
        UserACL userACL = uiApp.getApplicationComponent(UserACL.class);
        if (!userACL.hasPermission(node)) {
          uiApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.UserNotPermission",
                                                  new String[] { pageId },
                                                  1));
          ;
          return;
        }
      }
      UIPopupWindow uiManagementPopup = uiNodeSelector.getAncestorOfType(UIPopupWindow.class);
      UIPageNodeForm2 uiNodeForm = uiApp.createUIComponent(UIPageNodeForm2.class, null, null);
      uiManagementPopup.setUIComponent(uiNodeForm);
      uiNodeForm.setValues(selectedNode);
      uiNodeForm.setSelectedParent(obj);
      uiManagementPopup.setWindowSize(800, 500);
      ctx.addUIComponentToUpdateByAjax(uiManagementPopup);
    }
  }

  static public class CopyNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      String uri = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UINavigationNodeSelector uiNodeSelector = event.getSource()
      .getAncestorOfType(UINavigationNodeSelector.class);
      UINavigationManagement uiManagement = uiNodeSelector.getParent();
      Class<?>[] childrenToRender = new Class<?>[] { UINavigationNodeSelector.class };
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);

      PageNavigation nav = uiNodeSelector.getSelectedNavigation();
      if (nav == null)
        return;
      PageNode[] pageNodes = PageNavigationUtils.searchPageNodesByUri(nav, uri);
      if (pageNodes == null)
        return;
      SelectedNode selectedNode = new SelectedNode(nav, pageNodes[0], pageNodes[1]);
      selectedNode.setDeleteNode(false);
      uiNodeSelector.setCopyNode(selectedNode);
      event.getSource().setActions(new String[] { "AddNode", "EditPageNode", "EditSelectedNode",
          "CopyNode", "CloneNode", "CutNode", "PasteNode", "DeleteNode", "MoveUp", "MoveDown" });
    }
  }

  static public class CutNodeActionListener extends UINavigationNodeSelector.CopyNodeActionListener {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      super.execute(event);
      UINavigationNodeSelector uiNodeSelector = event.getSource()
      .getAncestorOfType(UINavigationNodeSelector.class);
      if (uiNodeSelector.getCopyNode() == null)
        return;
      uiNodeSelector.getCopyNode().setDeleteNode(true);
    }
  }

  static public class CloneNodeActionListener extends
  UINavigationNodeSelector.CopyNodeActionListener {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      super.execute(event);
      UINavigationNodeSelector uiNodeSelector = event.getSource()
      .getAncestorOfType(UINavigationNodeSelector.class);
      uiNodeSelector.getCopyNode().setCloneNode(true);
    }
  }

  static public class PasteNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      String targetUri = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu uiPopupMenu = event.getSource();
      UINavigationNodeSelector uiNodeSelector = uiPopupMenu.getAncestorOfType(UINavigationNodeSelector.class);
      UINavigationManagement uiManagement = uiNodeSelector.getParent();
      Class<?>[] childrenToRender = new Class<?>[] { UINavigationNodeSelector.class };
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
      SelectedNode selectedNode = uiNodeSelector.getCopyNode();
      if (selectedNode == null)
        return;

      PageNode newNode = selectedNode.getNode().clone();
      PageNavigation targetNav = uiNodeSelector.getSelectedNavigation();
      PageNode targetNode = PageNavigationUtils.searchPageNodeByUri(targetNav, targetUri);

      if (targetNode != null && newNode.getUri().equals(targetNode.getUri())) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication();
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.paste.sameSrcAndDes", null));
        return;
      }

      if (isExistChild(targetNode, newNode)
          || (targetNode == null && isExitChild(targetNav, newNode))) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication();
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.paste.sameName", null));
        return;
      }
      if (selectedNode.isDeleteNode()) {
        if (selectedNode.getParentNode() != null) {
          selectedNode.getParentNode().getChildren().remove(selectedNode.getNode());
        } else {
          selectedNode.getPageNavigation().getNodes().remove(selectedNode.getNode());
        }
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uiNodeSelector);
      uiNodeSelector.setCopyNode(null);
      UITree uitree = uiNodeSelector.getChild(UITree.class);
      UIRightClickPopupMenu popup = uitree.getUIRightClickPopupMenu();
      popup.setActions(new String[] { "AddNode", "EditPageNode", "EditSelectedNode", "CopyNode",
          "CutNode", "CloneNode", "DeleteNode", "MoveUp", "MoveDown" });

      UserPortalConfigService service = uiPopupMenu.getApplicationComponent(UserPortalConfigService.class);
      if (targetNode == null) {
        newNode.setUri(newNode.getName());
        targetNav.addNode(newNode);
        if (selectedNode.isCloneNode()) {
          clonePageFromNode(newNode, targetNav.getOwnerType(), targetNav.getOwnerId(), service);
        }
        return;
      }
      setNewUri(targetNode, newNode);
      targetNode.getChildren().add(newNode);
      if (selectedNode.isCloneNode()) {
        clonePageFromNode(newNode, targetNav.getOwnerType(), targetNav.getOwnerId(), service);
      }
      uiNodeSelector.selectPageNodeByUri(targetNode.getUri());
    }

    private void clonePageFromNode(PageNode node,
                                   String ownerType,
                                   String ownerId,
                                   UserPortalConfigService service) throws Exception {
      String pageId = node.getPageReference();
      if (pageId != null) {
        Page page = service.getPage(pageId);
        if (page != null) {
          String newName = "page" + node.hashCode();
          page = service.renewPage(pageId, newName, ownerType, ownerId, null);
          node.setPageReference(page.getPageId());
        }
      }
      List<PageNode> children = node.getChildren();
      if (children == null || children.size() < 1)
        return;
      for (PageNode ele : children) {
        clonePageFromNode(ele, ownerType, ownerId, service);
      }
    }

    private void setNewUri(PageNode parent, PageNode child) {
      String newUri = (parent != null) ? parent.getUri() + "/" + child.getName() : child.getName();
      child.setUri(newUri);
      List<PageNode> children = child.getChildren();
      if (children != null)
        for (PageNode node : children)
          setNewUri(child, node);
    }

    private boolean isExistChild(PageNode parent, PageNode child) {
      if (parent == null)
        return false;
      List<PageNode> nodes = parent.getChildren();
      if (nodes == null) {
        parent.setChildren(new ArrayList<PageNode>());
        return false;
      }
      for (PageNode node : nodes) {
        if (node.getName().equals(child.getName()))
          return true;
      }
      return false;
    }

    private boolean isExitChild(PageNavigation nav, PageNode child) {
      List<PageNode> nodes = nav.getNodes();
      if (nodes.size() == 0)
        return false;
      for (PageNode node : nodes) {
        if (node.getName().equals(child.getName()))
          return true;
      }
      return false;
    }
  }

  static public class MoveUpActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      moveNode(event, -1);
    }

    protected void moveNode(Event<UIRightClickPopupMenu> event, int i) {
      String uri = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UINavigationNodeSelector uiNodeSelector = event.getSource()
      .getAncestorOfType(UINavigationNodeSelector.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiNodeSelector.getParent());
      PageNavigation nav = uiNodeSelector.getSelectedNavigation();
      PageNode targetNode = PageNavigationUtils.searchPageNodeByUri(nav, uri);
      Object parentNode = PageNavigationUtils.searchParentNode(nav, uri);
      List<PageNode> children = new ArrayList<PageNode>();
      if (parentNode instanceof PageNavigation) {
        children = ((PageNavigation) parentNode).getNodes();
      } else if (parentNode instanceof PageNode) {
        children = ((PageNode) parentNode).getChildren();
      }
      int k = children.indexOf(targetNode);
      if (k < 0)
        return;
      if (k == 0 && i == -1)
        return;
      if (k == children.size() - 1 && i == 1)
        return;
      children.remove(k);
      children.add(k + i, targetNode);
    }
  }

  static public class MoveDownActionListener extends UINavigationNodeSelector.MoveUpActionListener {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      super.moveNode(event, 1);
    }
  }

  static public class DeleteNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      String uri = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      WebuiRequestContext pcontext = event.getRequestContext();
      UINavigationNodeSelector uiNodeSelector = event.getSource()
      .getAncestorOfType(UINavigationNodeSelector.class);
      pcontext.addUIComponentToUpdateByAjax(uiNodeSelector);

      PageNavigation nav = uiNodeSelector.getSelectedNavigation();
      if (nav == null)
        return;

      PageNode[] pageNodes = PageNavigationUtils.searchPageNodesByUri(nav, uri);
      if (pageNodes == null)
        return;

      if (pageNodes[0] == null) {
        nav.getNodes().remove(pageNodes[1]);
        return;
      }
      pageNodes[0].getChildren().remove(pageNodes[1]);
      uiNodeSelector.selectPageNodeByUri(pageNodes[0].getUri());
    }
  }

  public SelectedNode getSelectedNode() {
    return selectedNode;
  }

  public PageNavigation getSelectedNavigation() {
    return selectedNode == null ? null : selectedNode.getPageNavigation();
  }

  public PageNode getSelectedPageNode() {
    return selectedNode == null ? null : selectedNode.getNode();
  }

  public String getUpLevelUri() {
    return selectedNode.getParentNode().getUri();
  }

  public List<PageNavigation> getDeleteNavigations() {
    return deleteNavigations;
  }

  public static class SelectedNode {

    private PageNavigation nav;

    private PageNode       parentNode;

    private PageNode       node;

    private boolean        deleteNode = false;

    private boolean        cloneNode  = false;

    public SelectedNode(PageNavigation nav, PageNode parentNode, PageNode node) {
      this.nav = nav;
      this.parentNode = parentNode;
      this.node = node;
    }

    public PageNavigation getPageNavigation() {
      return nav;
    }

    public void setPageNavigation(PageNavigation nav) {
      this.nav = nav;
    }

    public PageNode getParentNode() {
      return parentNode;
    }

    public void setParentNode(PageNode parentNode) {
      this.parentNode = parentNode;
    }

    public PageNode getNode() {
      return node;
    }

    public void setNode(PageNode node) {
      this.node = node;
    }

    public boolean isDeleteNode() {
      return deleteNode;
    }

    public void setDeleteNode(boolean deleteNode) {
      this.deleteNode = deleteNode;
    }

    public boolean isCloneNode() {
      return cloneNode;
    }

    public void setCloneNode(boolean b) {
      cloneNode = b;
    }
  }

}
