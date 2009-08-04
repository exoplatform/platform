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

import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageBody;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(
    template = "system:/groovy/portal/webui/navigation/UIPageNodeSelector.gtmpl" ,
    events = {
      @EventConfig(listeners = UIPageNodeSelector.ChangeNodeActionListener.class)
    }
)

public class UIPageNodeSelector extends UIContainer {
  
//  private List<PageNavigation> navigations;
  private PageNavigation selectedNavigation;
  
  private SelectedNode selectedNode;
  
  private SelectedNode copyNode;
  
	public UIPageNodeSelector() throws Exception {    
    UITree uiTree = addChild(UITree.class, null, "TreePageSelector");    
    uiTree.setIcon("DefaultPageIcon");    
    uiTree.setSelectedIcon("DefaultPageIcon");
    uiTree.setBeanIdField("uri");
    uiTree.setBeanLabelField("resolvedLabel");   
    uiTree.setBeanIconField("icon");
    
    loadNavigations();
	}
	
  public void loadNavigations() throws Exception {
    PageNavigation portalSelectedNav = Util.getUIPortal().getSelectedNavigation() ;
    if(portalSelectedNav != null) {
      selectNavigation(portalSelectedNav);
      PageNode portalSelectedNode = Util.getUIPortal().getSelectedNode() ;
      if(portalSelectedNode != null) selectPageNodeByUri(portalSelectedNode.getUri()) ;  
      return;
    } 
    selectNavigation();
  }
  
  private void selectNavigation() {
    if(selectedNavigation == null) return;
    if (selectedNode == null || selectedNavigation.getId() != selectedNode.getPageNavigation().getId()) {
      selectedNode = new SelectedNode(selectedNavigation, null, null);
      if(selectedNavigation.getNodes().size() > 0) selectedNode.setNode(selectedNavigation.getNodes().get(0));
    }
    selectNavigation(selectedNode.getPageNavigation()) ;
    if(selectedNode.getNode() != null) selectPageNodeByUri(selectedNode.getNode().getUri()) ;
  }
  
  public void selectNavigation(PageNavigation pageNav){  
    selectedNavigation = pageNav;
    selectedNode = new SelectedNode(pageNav, null, null);
    selectPageNodeByUri(null) ;
    UITree uiTree = getChild(UITree.class);
    uiTree.setSibbling(pageNav.getNodes());      
  }
  
  public void selectPageNodeByUri(String uri){
    if(selectedNode == null || (selectedNavigation.getId() != selectedNode.getPageNavigation().getId())) return ;
    UITree tree = getChild(UITree.class);
    List<?> sibbling = tree.getSibbling();
    tree.setSibbling(null);
    tree.setParentSelected(null);
    selectedNode.setNode(searchPageNodeByUri(selectedNode.getPageNavigation(), uri));
    if(selectedNode.getNode() != null) {
      tree.setSelected(selectedNode.getNode());   
      tree.setChildren(selectedNode.getNode().getChildren());
      return ;
    }
    tree.setSelected(null);
    tree.setChildren(null);
    tree.setSibbling(sibbling);
  }
  
  public PageNode searchPageNodeByUri(PageNavigation pageNav, String uri) {
    if(pageNav == null || uri == null) return null;
    List<PageNode> pageNodes = pageNav.getNodes();
    UITree uiTree = getChild(UITree.class);
    for(PageNode ele : pageNodes){
      PageNode returnPageNode = searchPageNodeByUri(ele, uri, uiTree);
      if(returnPageNode == null) continue;
      if(uiTree.getSibbling() == null) uiTree.setSibbling(pageNodes);      
      return returnPageNode;
    }
    return null; 
  }  
    
  private PageNode searchPageNodeByUri(PageNode pageNode, String uri, UITree tree){
    if(pageNode.getUri().equals(uri)) return pageNode;
    List<PageNode> children = pageNode.getChildren();
    if(children == null) return null;
    for(PageNode ele : children){
      PageNode returnPageNode = searchPageNodeByUri(ele, uri, tree);
      if(returnPageNode == null) continue;
      if(tree.getSibbling() == null) tree.setSibbling(children);
      if(tree.getParentSelected() == null) tree.setParentSelected(pageNode);
      selectedNode.setParentNode(pageNode);
      return returnPageNode;
    }
    return null;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    UIRightClickPopupMenu uiPopupMenu = getChild(UIRightClickPopupMenu.class);
    if(uiPopupMenu != null) {
      uiPopupMenu.setRendered(true) ;
    }
    super.processRender(context) ;
  }
  
  public SelectedNode getCopyNode() { return copyNode; }
  public void setCopyNode(SelectedNode copyNode) { this.copyNode = copyNode; }

  public SelectedNode getSelectedNode() { return selectedNode; }
  
  public PageNavigation getSelectedNavigation(){ 
    return selectedNavigation; 
  }  
  
  public PageNode getSelectedPageNode() { 
    return selectedNode == null ? null : selectedNode.getNode() ; 
  }
  
  public String getUpLevelUri () { return selectedNode.getParentNode().getUri() ; }
  
//  private List<PageNavigation> getExistedNavigation(List<PageNavigation> navis) throws Exception {
//    Iterator<PageNavigation> itr = navis.iterator() ;
//    UserPortalConfigService configService = getApplicationComponent(UserPortalConfigService.class);
//    while(itr.hasNext()) {
//      PageNavigation nav = itr.next() ;
//      if(configService.getPageNavigation(nav.getOwnerType(), nav.getOwnerId()) == null) itr.remove() ;
//    }
//    return navis ;
//  }
  
  static public class ChangeNodeActionListener  extends EventListener<UITree> {
    public void execute(Event<UITree> event) throws Exception {      
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getParent();
      uiPageNodeSelector.selectPageNodeByUri(uri);
      
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      UIPortalApplication uiPortalApp = uiPageNodeSelector.getAncestorOfType(UIPortalApplication.class);
      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel() ;
      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class) ;
      uiToolPanel.setShowMaskLayer(true);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);    
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setFullRender(true);
      
      UIContainer uiParent = uiPageNodeSelector.getParent();
      PageNode node = null;
      if(uiPageNodeSelector.getSelectedNode() == null) {
        node = Util.getUIPortal().getSelectedNode();
      } else {
        node  = uiPageNodeSelector.getSelectedNode().getNode();
      }
      if(node == null) {
        uiPageNodeSelector.selectNavigation(uiPageNodeSelector.getSelectedNavigation());
        uiToolPanel.setUIComponent(null) ;
        return ;
      }
      
      UserPortalConfigService configService = uiParent.getApplicationComponent(UserPortalConfigService.class);
      Page page = null;
      if(node.getPageReference() != null) {
        page = configService.getPage(node.getPageReference(), event.getRequestContext().getRemoteUser());
      } 
      
      if(page == null){
        uiToolPanel.setUIComponent(null) ;
        return;
      }
      
      UIPage uiPage = Util.toUIPage(node, uiToolPanel); 
      UIPageBody uiPageBody = uiPortalApp.findFirstComponentOfType(UIPageBody.class) ; 
      if(uiPageBody.getUIComponent() != null) uiPageBody.setUIComponent(null);
      uiToolPanel.setUIComponent(uiPage);
    }
  }
  

  public static class SelectedNode {
    
    private PageNavigation nav;
    
    private PageNode parentNode;
    
    private PageNode node;
    
    private boolean deleteNode = false;
    
    private boolean cloneNode = false;
    
    public SelectedNode(PageNavigation nav, PageNode parentNode, PageNode node) {
      this.nav = nav;
      this.parentNode = parentNode;
      this.node = node;
    }

    public PageNavigation getPageNavigation() { return nav; }
    public void setPageNavigation(PageNavigation nav) { this.nav = nav; }

    public PageNode getParentNode() { return parentNode; }
    public void setParentNode(PageNode parentNode) { this.parentNode = parentNode; }

    public PageNode getNode() { return node; }
    public void setNode(PageNode node) { this.node = node; }

    public boolean isDeleteNode() { return deleteNode; }
    public void setDeleteNode(boolean deleteNode) { this.deleteNode = deleteNode; }
    
    public boolean isCloneNode() { return cloneNode; }
    public void setCloneNode(boolean b) { cloneNode = b; }
  }

}
