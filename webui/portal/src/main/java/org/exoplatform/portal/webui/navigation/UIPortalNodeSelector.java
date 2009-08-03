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
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL Author : chungnv nguyenchung136@yahoo.com
 * Jun 23, 2006 10:07:15 AM
 */
@ComponentConfigs( {
    @ComponentConfig(template = "system:/groovy/portal/webui/navigation/UIPortalNodeSelector.gtmpl", events = {
        @EventConfig(listeners = UIPortalNodeSelector.ChangeNodeActionListener.class)}),
    @ComponentConfig(id = "PortalNodePopupMenu", type = UIRightClickPopupMenu.class, template = "system:/groovy/webui/core/UIRightClickPopupMenu.gtmpl", events = {
        @EventConfig(listeners = UIPortalNodeSelector.AddNodeActionListener.class),
        @EventConfig(listeners = UIPortalNodeSelector.EditPageNodeActionListener.class),
        @EventConfig(listeners = UIPortalNodeSelector.EditSelectedNodeActionListener.class),
        @EventConfig(listeners = UIPortalNodeSelector.CopyNodeActionListener.class),
        @EventConfig(listeners = UIPortalNodeSelector.CutNodeActionListener.class),
        @EventConfig(listeners = UIPortalNodeSelector.CloneNodeActionListener.class),
        @EventConfig(listeners = UIPortalNodeSelector.PasteNodeActionListener.class),
        @EventConfig(listeners = UIPortalNodeSelector.MoveUpActionListener.class),
        @EventConfig(listeners = UIPortalNodeSelector.MoveDownActionListener.class),
        @EventConfig(listeners = UIPortalNodeSelector.DeleteNodeActionListener.class, confirm = "UIPageNodeSelector.deleteNavigation") }),
    @ComponentConfig(id = "UIPortalNodeSelectorPopupMenu",type = UIRightClickPopupMenu.class, template = "system:/groovy/webui/core/UIRightClickPopupMenu.gtmpl",events = {}) })
public class UIPortalNodeSelector extends UIContainer {

  private List<PageNavigation> navigations;

  private SelectedNode         selectedNode;

  private SelectedNode         copyNode;

  private String               portalName;

  private List<PageNavigation> deleteNavigations = new ArrayList<PageNavigation>();

  public UIPortalNodeSelector() throws Exception {
    addChild(UIRightClickPopupMenu.class, "UIPortalNodeSelectorPopupMenu", null).setRendered(false);

    UITree uiTree = addChild(UITree.class, null, "TreeNodeSelector");
    uiTree.setIcon("DefaultPageIcon");
    uiTree.setSelectedIcon("DefaultPageIcon");
    uiTree.setBeanIdField("uri");
    uiTree.setBeanLabelField("resolvedLabel");
    uiTree.setBeanIconField("icon");

    UIRightClickPopupMenu uiPopupMenu = createUIComponent(UIRightClickPopupMenu.class,
                                                          "PortalNodePopupMenu",
                                                          null);
    uiPopupMenu.setActions(new String[] { "AddNode", "EditPageNode", "EditSelectedNode",
        "CopyNode", "CloneNode", "CutNode", "DeleteNode", "MoveUp", "MoveDown" });
    uiTree.setUIRightClickPopupMenu(uiPopupMenu);
  }

  public void loadNavigations(String portalName, String ownerType) throws Exception {
    this.portalName = portalName;
    navigations = new ArrayList<PageNavigation>();
    List<PageNavigation> pnavigations = getExistedNavigation(portalName, ownerType);
    for (PageNavigation nav : pnavigations) {
      if (nav.isModifiable())
        navigations.add(nav);
    }

    updateUI();

    selectNavigation();
  }

  public void loadNavigationByNavId(Integer navId, List<PageNavigation> navigations) throws Exception {
    this.navigations = navigations;

    selectNavigation(navId);
  }
  
  private void updateUI() {
    if (navigations == null || navigations.size() < 1) {      
      getChild(UITree.class).setSibbling(null);
      return;
    }

    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    for (PageNavigation navigation : navigations) { // navigation.getOwnerId()
      options.add(new SelectItemOption<String>(navigation.getOwnerType() + ":"
          + navigation.getOwnerId(), String.valueOf(navigation.getId())));
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

  private List<PageNavigation> getExistedNavigation(String portalName, String ownerType) throws Exception {
    PortalRequestContext prContext = Util.getPortalRequestContext();
    UserPortalConfigService configService = getApplicationComponent(UserPortalConfigService.class);
    UserPortalConfig config = configService.getUserPortalConfig(portalName,
                                                                prContext.getRemoteUser());
    List<PageNavigation> navis = config.getNavigations();
    if (ownerType != null) {
      Iterator<PageNavigation> itr = navis.iterator();
      while (itr.hasNext()) {
        PageNavigation nav = itr.next();
        if (!nav.getOwnerType().equals(ownerType))
          itr.remove();
      }
    }
    
    return navis;
  }

  static public class ChangeNodeActionListener extends EventListener<UITree> {
    public void execute(Event<UITree> event) throws Exception {
      String uri = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPortalNodeSelector uiPortalNodeSelector = event.getSource().getParent();
      uiPortalNodeSelector.selectPageNodeByUri(uri);
    }
  }  

  static public class AddNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
//      String uri = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
//      UIRightClickPopupMenu uiPopupMenu = event.getSource();
//      UIPortalNodeSelector uiPortalNodeSelector = uiPopupMenu.getAncestorOfType(UIPortalNodeSelector.class);
//      if (uiPortalNodeSelector.getSelectedNavigation() == null) {
//        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication();
//        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.NoPageNavigation", null));
//
//        event.getRequestContext().addUIComponentToUpdateByAjax(uiPortalNodeSelector.getParent());
//        return;
//      }
//
//      UIPortalApplication uiApp = uiPortalNodeSelector.getAncestorOfType(UIPortalApplication.class);
//      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortalNodeSelector.getParent());
//      UIPageNodeForm2 uiNodeForm = uiMaskWS.createUIComponent(UIPageNodeForm2.class, null, null);
//      uiNodeForm.setValues(null);
//      uiMaskWS.setUIComponent(uiNodeForm);
//      uiMaskWS.setShow(true);
//
//      Object parent = null;
//      List<PageNode> pageNodes = uiPortalNodeSelector.getSelectedNavigation().getNodes();
//      if (uri != null && uri.trim().length() > 0) {
//        for (PageNode pageNode : pageNodes) {
//          parent = PageNavigationUtils.searchPageNodeByUri(pageNode, uri);
//          if (parent != null)
//            break;
//        }
//      }
//      if (parent == null)
//        parent = uiPortalNodeSelector.getSelectedNavigation();
//      uiNodeForm.setSelectedParent(parent);
    }
  }

  static public class EditPageNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
//      UIRightClickPopupMenu uiPopupMenu = event.getSource();
//      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
//      PortalRequestContext pcontext  = (PortalRequestContext)event.getRequestContext();
//      UIPortalNodeSelector uiPortalNodeSelector = uiPopupMenu.getAncestorOfType(UIPortalNodeSelector.class) ;
//      PageNavigation currentNav = uiPortalNodeSelector.getSelectedNavigation();
//      PageNode selectNode = PageNavigationUtils.searchPageNodeByUri(currentNav, uri);
//      
//      uiPortalNodeSelector.selectPageNodeByUri(uri);
//      UIPortalApplication uiPortalApp = Util.getUIPortalApplication() ;
//      UIPageManagement2 uiManagement = uiPortalNodeSelector.getParent();
//            
//      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
//      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
//      pcontext.setFullRender(true);
//      
//      UserPortalConfigService portalConfigService = uiPopupMenu.getApplicationComponent(UserPortalConfigService.class);
//      Page page = null;
//      if(selectNode.getPageReference() != null) page = portalConfigService.getPage(selectNode.getPageReference(), pcontext.getRemoteUser());
//      if(page == null) {
//        Class<?> [] childrenToRender = {UIPortalNodeSelector.class, UIPortalNavigationControlBar.class };      
//        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
//        if(selectNode.getPageReference() != null && portalConfigService.getPage(selectNode.getPageReference()) != null) {
//          uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.edit.NotEditPage", new String[]{})) ;
//        } else {
//          uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.PageNotExist", new String[]{})) ;
//        }
//        return;
//      }
//      
//      if(!page.isModifiable()){
//        Class<?> [] childrenToRender = {UIPortalNodeSelector.class, UIPortalNavigationControlBar.class};      
//        uiManagement.setRenderedChildrenOfTypes(childrenToRender);        
//        uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.Invalid-editPermission", null)) ;        
//        return;
//      }
//      
//      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;      
//      UIPageForm2 uiPageForm =  uiMaskWS.createUIComponent(UIPageForm2.class);
//      uiPageForm.removeChild(UIPageTemplateOptions.class);
//      
//      UIPage uiPage = Util.toUIPage(page, uiMaskWS);
//      uiPageForm.setValues(uiPage);
//      uiMaskWS.setUIComponent(uiPageForm);
//      uiMaskWS.setWindowSize(640, 400);
//      uiMaskWS.setShow(true);
//      pcontext.addUIComponentToUpdateByAjax(uiMaskWS);
//      Class<?> [] childrenToRender = {UIPortalNodeSelector.class, UIPortalNavigationControlBar.class};      
//      uiManagement.setRenderedChildrenOfTypes(childrenToRender);
    }
  }

  static public class EditSelectedNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
//      UIRightClickPopupMenu popupMenu = event.getSource();
//      UIComponent parent = popupMenu.getParent();
//      UIPortalNodeSelector uiPortalNodeSelector = parent.getParent();     
//      UIPortalApplication uiApp = uiPortalNodeSelector.getAncestorOfType(UIPortalApplication.class);      
//      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortalNodeSelector.<UIPageManagement2>getParent());
//      
//      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
//      PageNavigation selectedNav = uiPortalNodeSelector.getSelectedNavigation();
//      Object obj = PageNavigationUtils.searchParentNode(selectedNav, uri);
//      PageNode selectedNode = PageNavigationUtils.searchPageNodeByUri(selectedNav, uri);
//      String pageId = selectedNode.getPageReference();
//      UserPortalConfigService service = parent.getApplicationComponent(UserPortalConfigService.class);
//      PortalRequestContext pcontext = Util.getPortalRequestContext();
//      UIPortalApplication uiPortalApp = parent.getAncestorOfType(UIPortalApplication.class);
//      Page node = (pageId != null) ? service.getPage(pageId) : null ;
//      if(node != null) {
//        UserACL userACL = parent.getApplicationComponent(UserACL.class) ;
//        if(!userACL.hasPermission(node, pcontext.getRemoteUser())) {
//          uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.UserNotPermission", new String[]{pageId}, 1)) ;;
//          return;
//        }
//      } 
//      
//      UIPageNodeForm2 uiNodeForm = uiMaskWS.createUIComponent(UIPageNodeForm2.class, null, null);
//      uiMaskWS.setUIComponent(uiNodeForm);     
//      uiNodeForm.setValues(selectedNode);
//      uiNodeForm.setSelectedParent(obj);   
    }
  }

  static public class CopyNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIPortalNodeSelector uiPortalNodeSelector = event.getSource().getAncestorOfType(UIPortalNodeSelector.class);
      //UIPageManagement2 uiManagement = uiPortalNodeSelector.getParent();
      Class<?> [] childrenToRender = new Class<?>[]{UIPortalNodeSelector.class};
      //uiManagement.setRenderedChildrenOfTypes(childrenToRender);      
      //event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
      
      PageNavigation nav = uiPortalNodeSelector.getSelectedNavigation();
      if(nav == null) return;
      PageNode [] pageNodes = PageNavigationUtils.searchPageNodesByUri(nav, uri);
      if(pageNodes == null) return;
      SelectedNode selectedNode = new SelectedNode(nav, pageNodes[0], pageNodes[1]);
      selectedNode.setDeleteNode(false);
      uiPortalNodeSelector.setCopyNode(selectedNode);
      event.getSource().setActions(new String[] {"AddNode", "EditPageNode", "EditSelectedNode", 
                                                 "CopyNode", "CloneNode", "CutNode", "PasteNode", "DeleteNode", "MoveUp", "MoveDown"});
    }
  }

  static public class CutNodeActionListener extends UIPortalNodeSelector.CopyNodeActionListener {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      super.execute(event);
      UIPortalNodeSelector uiPortalNodeSelector = event.getSource().getAncestorOfType(UIPortalNodeSelector.class);
      if(uiPortalNodeSelector.getCopyNode() == null) return; 
      uiPortalNodeSelector.getCopyNode().setDeleteNode(true);
    }
  }

  static public class CloneNodeActionListener extends UIPortalNodeSelector.CopyNodeActionListener {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      super.execute(event);
      UIPortalNodeSelector uiPortalNodeSelector = event.getSource().getAncestorOfType(UIPortalNodeSelector.class);
      uiPortalNodeSelector.getCopyNode().setCloneNode(true);
    }
  }
  
  static public class PasteNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      String targetUri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu uiPopupMenu = event.getSource();
      UIPortalNodeSelector uiPortalNodeSelector =  uiPopupMenu.getAncestorOfType(UIPortalNodeSelector.class);
      //UIPageManagement2 uiManagement = uiPortalNodeSelector.getParent();
      Class<?> [] childrenToRender = new Class<?>[]{UIPortalNodeSelector.class};
      //uiManagement.setRenderedChildrenOfTypes(childrenToRender);      
      //event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
      SelectedNode selectedNode = uiPortalNodeSelector.getCopyNode();
      if(selectedNode == null) return;
      
      PageNode newNode = selectedNode.getNode().clone();
      PageNavigation targetNav = uiPortalNodeSelector.getSelectedNavigation();
      PageNode targetNode = PageNavigationUtils.searchPageNodeByUri(targetNav, targetUri);
              
      if(targetNode != null && newNode.getUri().equals(targetNode.getUri())) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.paste.sameSrcAndDes", null)) ;
        return;
      }
      
      if(isExistChild(targetNode, newNode) || (targetNode == null && isExitChild(targetNav, newNode))) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.paste.sameName", null)) ;
        return;
      }
      if(selectedNode.isDeleteNode()) {
        if(selectedNode.getParentNode() != null) {
          selectedNode.getParentNode().getChildren().remove(selectedNode.getNode());
        } else {
          selectedNode.getPageNavigation().getNodes().remove(selectedNode.getNode());
        }
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortalNodeSelector);
      uiPortalNodeSelector.setCopyNode(null);
      UITree uitree = uiPortalNodeSelector.getChild(UITree.class);
      UIRightClickPopupMenu popup = uitree.getUIRightClickPopupMenu();
      popup.setActions(new String[] {"AddNode", "EditPageNode", "EditSelectedNode", "CopyNode", 
                                     "CutNode", "CloneNode", "DeleteNode", "MoveUp", "MoveDown"});
       
      UserPortalConfigService service = uiPopupMenu.getApplicationComponent(UserPortalConfigService.class);
      if(targetNode == null) { 
        newNode.setUri(newNode.getName());
        targetNav.addNode(newNode);
        if(selectedNode.isCloneNode()) {
          clonePageFromNode(newNode, targetNav.getOwnerType(), targetNav.getOwnerId(), service);
        }
        return;
      }
      setNewUri(targetNode, newNode);
      targetNode.getChildren().add(newNode);
      if(selectedNode.isCloneNode()) {
        clonePageFromNode(newNode, targetNav.getOwnerType(), targetNav.getOwnerId(), service);
      }
      uiPortalNodeSelector.selectPageNodeByUri(targetNode.getUri());
    }
    
    private void clonePageFromNode(PageNode node, String ownerType,
                                   String ownerId, UserPortalConfigService service) throws Exception {
      String pageId = node.getPageReference();
      if(pageId != null) {
        Page page = service.getPage(pageId);
        if(page != null) {
          String newName = "page" + node.hashCode();
          page = service.renewPage(pageId, newName, ownerType, ownerId, null);
          node.setPageReference(page.getPageId());
        }
      }
      List<PageNode> children = node.getChildren();
      if(children == null || children.size() < 1) return ;
      for(PageNode ele : children) {
        clonePageFromNode(ele, ownerType, ownerId, service);
      }
    }
    
    private void setNewUri(PageNode parent, PageNode child) {
      String newUri = (parent != null) ? parent.getUri() + "/" + child.getName() : child.getName() ;
      child.setUri(newUri) ;
      List<PageNode> children = child.getChildren() ;
      if(children != null) for(PageNode node : children) setNewUri(child, node) ; 
    }
    
    private boolean isExistChild(PageNode parent, PageNode child) {
      if(parent == null) return false;
      List<PageNode> nodes = parent.getChildren();
      if(nodes == null) {
        parent.setChildren(new ArrayList<PageNode>());
        return false;
      }
      for (PageNode node: nodes) {
        if(node.getName().equals(child.getName())) return true;
      }
      return false;
    }
    
    private boolean isExitChild(PageNavigation nav, PageNode child) {
      List<PageNode> nodes = nav.getNodes();
      if(nodes.size() == 0) return false;
      for(PageNode node : nodes){
        if(node.getName().equals(child.getName())) return true;
      }
      return false;
    }
  }
  
  static public class MoveUpActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {      
      moveNode(event, -1);
    }

    protected void moveNode(Event<UIRightClickPopupMenu> event, int i) {
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIPortalNodeSelector uiPortalNodeSelector =  event.getSource().getAncestorOfType(UIPortalNodeSelector.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortalNodeSelector.getParent());
      PageNavigation nav = uiPortalNodeSelector.getSelectedNavigation();
      PageNode targetNode = PageNavigationUtils.searchPageNodeByUri(nav, uri);
      Object parentNode = PageNavigationUtils.searchParentNode(nav, uri);
      List<PageNode> children = new ArrayList<PageNode>();
      if(parentNode instanceof PageNavigation){
        children = ((PageNavigation)parentNode).getNodes();
      } else if(parentNode instanceof PageNode){
        children = ((PageNode)parentNode).getChildren();
      }
      int k = children.indexOf(targetNode);
      if(k < 0) return ;
      if(k == 0 && i == -1) return;
      if(k == children.size() - 1 && i == 1) return;
      children.remove(k);
      children.add(k + i, targetNode);
    }
  }
  
  static public class MoveDownActionListener extends UIPortalNodeSelector.MoveUpActionListener {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      super.moveNode(event, 1);
    }
  }
  
  static public class DeleteNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
//      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
//      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext() ;
//      UIPortalNodeSelector uiPortalNodeSelector = event.getSource().getAncestorOfType(UIPortalNodeSelector.class);
//      UIPageManagement2 uiManagement = uiPortalNodeSelector.getParent();
//      Class<?> [] childrenToRender = new Class<?>[]{UIPortalNodeSelector.class, UIPortalNavigationControlBar.class };
//      uiManagement.setRenderedChildrenOfTypes(childrenToRender);      
//      pcontext.addUIComponentToUpdateByAjax(uiManagement);
//      
//      PageNavigation nav = uiPortalNodeSelector.getSelectedNavigation();
//      if(nav == null) return;
//      
//      PageNode [] pageNodes = PageNavigationUtils.searchPageNodesByUri(nav, uri);
//      if(pageNodes == null) return;
//      
//      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
//      UIWorkingWorkspace uiWorkspace = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
//      pcontext.setFullRender(true) ;
//      pcontext.addUIComponentToUpdateByAjax(uiWorkspace);
//      if(pageNodes[0] == null) {
//        nav.getNodes().remove(pageNodes[1]);
//        return;
//      }
//      pageNodes[0].getChildren().remove(pageNodes[1]);
//      uiPortalNodeSelector.selectPageNodeByUri(pageNodes[0].getUri());
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

  public String getPortalName() {
    return portalName;
  }

  public void setPortalName(String portalName) {
    this.portalName = portalName;
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
