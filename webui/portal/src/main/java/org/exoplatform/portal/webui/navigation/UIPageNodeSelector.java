/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.navigation.UIPageNavigationActionListener.CreateNavigationActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNavigationActionListener.DeleteNavigationActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNavigationActionListener.EditNavigationActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNavigationActionListener.SaveNavigationActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.AddNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.CopyNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.CutNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.DeleteNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.EditPageNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.EditSelectedNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.PasteNodeActionListener;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageEditBar;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIDropDownItemSelector;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfigs({
  @ComponentConfig(
      template = "app:/groovy/portal/webui/navigation/UIPageNodeSelector.gtmpl" ,
      events = {
        @EventConfig(listeners = UIPageNodeSelector.ChangeNodeActionListener.class),
        @EventConfig(listeners = CreateNavigationActionListener.class),
        @EventConfig(listeners = UIPageNodeSelector.SelectNavigationActionListener.class) 
      }
  ),
  @ComponentConfig(
      id = "PageNodePopupMenu",
      type = UIRightClickPopupMenu.class,
      template = "system:/groovy/webui/core/UIRightClickPopupMenu.gtmpl",
      events = {
        @EventConfig(listeners = AddNodeActionListener.class),
        @EventConfig(listeners = EditPageNodeActionListener.class),
        @EventConfig(listeners = EditSelectedNodeActionListener.class),
        @EventConfig(listeners = CopyNodeActionListener.class),
        @EventConfig(listeners = CutNodeActionListener.class),
        @EventConfig(listeners = PasteNodeActionListener.class),
        @EventConfig(listeners = DeleteNodeActionListener.class, confirm = "UIPageNodeSelector.deleteNavigation")
      }
  ),
  @ComponentConfig(
      id = "UIPageNodeSelectorPopupMenu",
      type = UIRightClickPopupMenu.class,
      template = "system:/groovy/webui/core/UIRightClickPopupMenu.gtmpl",
      events = {
        @EventConfig(listeners = AddNodeActionListener.class),
        @EventConfig(listeners = PasteNodeActionListener.class),
        @EventConfig(listeners = SaveNavigationActionListener.class),
        @EventConfig(listeners = EditNavigationActionListener.class),
        @EventConfig(listeners = DeleteNavigationActionListener.class, confirm = "UIPageNodeSelector.deleteNode")
      }
  )
})

public class UIPageNodeSelector extends UIContainer {
  
  private List<PageNavigation> navigations;
  
  private SelectedNode selectedNode;
  
  private SelectedNode copyNode;
  
  private List<PageNavigation> deleteNavigations = new ArrayList<PageNavigation>();
  
	public UIPageNodeSelector() throws Exception {    
    addChild(UIRightClickPopupMenu.class, "UIPageNodeSelectorPopupMenu", null).setRendered(false);  
//    addChild(UIBreadcumbs.class, null, null).setRendered(false);  
    
    UIDropDownItemSelector uiDopDownSelector = addChild(UIDropDownItemSelector.class, null, null);
    uiDopDownSelector.setTitle("Select Navigations");
    uiDopDownSelector.setOnServer(true);
    uiDopDownSelector.setOnChange("SelectNavigation");
    
    UITree uiTree = addChild(UITree.class, null, "TreePageSelector");    
    uiTree.setIcon("Icon NavigationPortalIcon");    
    uiTree.setSelectedIcon("Icon NavigationPortalIcon");
    uiTree.setBeanIdField("uri");
    uiTree.setBeanLabelField("label");   
    uiTree.setBeanIconField("icon");
    UIRightClickPopupMenu uiPopupMenu = createUIComponent(UIRightClickPopupMenu.class, "PageNodePopupMenu", null) ;
    uiTree.setUIRightClickPopupMenu(uiPopupMenu);
    
    loadNavigations();
	}
  
  public void loadNavigations() throws Exception {
    navigations = new ArrayList<PageNavigation>();
    List<PageNavigation> pnavigations = Util.getUIPortal().getNavigations();
    for(PageNavigation nav  : pnavigations){
      if(nav.isModifiable()) navigations.add(nav.clone()) ;
    }
    
    updateUI() ;
    
    PageNavigation portalSelectedNav = Util.getUIPortal().getSelectedNavigation() ;
    if(getPageNavigation(portalSelectedNav.getId()) != null) {
      selectNavigation(portalSelectedNav.getId()) ;
      PageNode portalSelectedNode = Util.getUIPortal().getSelectedNode() ;
      selectPageNodeByUri(portalSelectedNode.getUri()) ;  
      return;
    } 
    selectNavigation();
  }
  
  private void updateUI() {
    if(navigations == null || navigations.size() < 1) {
      getChild(UIDropDownItemSelector.class).setOptions(null) ;
      getChild(UIDropDownItemSelector.class).setSelectedItem(null) ;
      getChild(UITree.class).setSibbling(null) ;
      return ;
    }
    
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    for(PageNavigation navigation: navigations) {
      String label = navigation.getOwnerId() + "'s Nav";
      options.add(new SelectItemOption<String>(label, navigation.getId()));
    }
    UIDropDownItemSelector uiNavigationSelector = getChild(UIDropDownItemSelector.class);
    uiNavigationSelector.setOptions(options);
    if(options.size() > 0) uiNavigationSelector.setSelected(0);
  }
  
  private void selectNavigation() {
    if(navigations == null || navigations.size() < 1) return;
    if (selectedNode == null) {
      PageNavigation navigation = navigations.get(0);
      selectedNode = new SelectedNode(navigation, null, null);
      if(navigation.getNodes().size() > 0) selectedNode.setNode(navigation.getNodes().get(0));
    }
    selectNavigation(selectedNode.getPageNavigation().getId()) ;
    if(selectedNode.getNode() != null) selectPageNodeByUri(selectedNode.getNode().getUri()) ;
  }
  
  public void selectNavigation(String id){    
    for(int i = 0; i < navigations.size(); i++){
      if(!navigations.get(i).getId().equals(id)) continue ;
      selectedNode = new SelectedNode(navigations.get(i), null, null);
      selectPageNodeByUri(null) ;
      UITree tree = getChild(UITree.class);
      tree.setSibbling(navigations.get(i).getNodes());      
      UIDropDownItemSelector uiDopDownSelector = getChild(UIDropDownItemSelector.class);
      uiDopDownSelector.setSelected(i);
    }
  }
  
  public void selectPageNodeByUri(String uri){    
    if(selectedNode == null) return ;
    UITree uiTree = getChild(UITree.class);
    List<?> sibbling = uiTree.getSibbling();
    uiTree.setSibbling(null);
    uiTree.setParentSelected(null);
    selectedNode.setNode(searchPageNodeByUri(selectedNode.getPageNavigation(), uri));
    if(selectedNode.getNode() != null) {
      uiTree.setSelected(selectedNode.getNode());   
      uiTree.setChildren(selectedNode.getNode().getChildren());
      return ;
    }
    uiTree.setSelected(null);
    uiTree.setChildren(null);
    uiTree.setSibbling(sibbling);
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
  
  public List<PageNavigation> getPageNavigations() { 
    if(navigations == null) navigations = new ArrayList<PageNavigation>();    
    return navigations;  
  }
 
  public void addPageNavigation(PageNavigation navigation) {
    if(navigations == null) navigations = new ArrayList<PageNavigation>() ;
    navigations.add(navigation) ;
    updateUI() ;
  }
  
  public void deletePageNavigation(PageNavigation navigation) {
    if(navigations == null || navigations.size() < 1) return ;
    navigations.remove(navigation);
    deleteNavigations.add(navigation);
    selectedNode = null;
    selectNavigation();    
    updateUI() ;
  }

  private PageNavigation getPageNavigation(String id) {
    for(PageNavigation ele : getPageNavigations()) {
      if(ele.getId().equals(id)) return ele ;
    }
    return null ;
  }
  
  public SelectedNode getCopyNode() { return copyNode; }
  public void setCopyNode(SelectedNode copyNode) { this.copyNode = copyNode; }
  
  static public class ChangeNodeActionListener  extends EventListener<UITree> {
    public void execute(Event<UITree> event) throws Exception {      
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getParent();
      uiPageNodeSelector.selectPageNodeByUri(uri);
      
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID) ;
      pcontext.addUIComponentToUpdateByAjax(uiControl) ;
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setFullRender(true);
      
      UIContainer uiParent = uiPageNodeSelector.getParent();
      //pcontext.addUIComponentToUpdateByAjax(uiParent) ;
      UIPageEditBar uiEditBar = uiParent.getChild(UIPageEditBar.class);   
      if(uiPageNodeSelector.getSelectedNode() == null) return;
      PageNode node  = uiPageNodeSelector.getSelectedNode().getNode();
      //TODO: Tung.Pham added
      //-------------------------------------------------------------
      if(node == null) {
        UIPortal uiPortal = Util.getUIPortal();
        uiPageNodeSelector.selectNavigation(uiPortal.getSelectedNavigation().getId());
        uiPageNodeSelector.selectPageNodeByUri(uiPortal.getSelectedNode().getUri());
        node = uiPageNodeSelector.getSelectedPageNode();
      }
      //-------------------------------------------------------------
      if(node == null) return;  
      
      UserPortalConfigService configService = uiParent.getApplicationComponent(UserPortalConfigService.class);
      Page page = configService.getPage(node.getPageReference(), event.getRequestContext().getRemoteUser());
      
      if(page == null){
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiParent.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }
      
      UIPage uiPage = Util.toUIPage(node, Util.getUIPortalToolPanel());
      UIPortalToolPanel toolPanel = Util.getUIPortalToolPanel() ; 
      toolPanel.setUIComponent(uiPage);
      toolPanel.setRenderSibbling(UIPortalToolPanel.class) ;

      if(!page.isModifiable()) {
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiParent.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }
      
      uiEditBar.setRendered(true);
      if(Page.DESKTOP_PAGE.equals(uiPage.getFactoryId())) {
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiParent.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }
      
      uiEditBar.setUIPage(uiPage);
      Class<?> [] childrenToRender = {UIPageEditBar.class, 
                                      UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
      uiParent.setRenderedChildrenOfTypes(childrenToRender);
    }
  }
  
  static public class SelectNavigationActionListener  extends EventListener<UIPageNodeSelector> {
    public void execute(Event<UIPageNodeSelector> event) throws Exception {
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPageNodeSelector uiPageNodeSelector = event.getSource();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector.getParent()) ;
      if(id == null) return ;
      uiPageNodeSelector.selectNavigation(id);
    }
  }

  public static class SelectedNode {
    
    private PageNavigation nav;
    
    private PageNode parentNode;
    
    private PageNode node;
    
    private boolean deleteNode = false;
    
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
    
  }

  public SelectedNode getSelectedNode() { return selectedNode; }
  
  public PageNavigation getSelectedNavigation(){ 
    return selectedNode == null ? null : selectedNode.getPageNavigation(); 
  }  
  
  public PageNode getSelectedPageNode() { 
    return selectedNode == null ? null : selectedNode.getNode() ; 
  }
  
  public String getUpLevelUri () { return selectedNode.getParentNode().getUri() ; }

  public List<PageNavigation> getDeleteNavigations() { return deleteNavigations; }
}
