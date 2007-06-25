/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.DeleteNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.EditPageNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.EditSelectedNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.PasteNodeActionListener;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageEditBar;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIBreadcumbs;
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
  
  private List<PageNavigation> navigations_;  
  private PageNavigation selectedNavigation;  
  private PageNode selectedPageNode;
  
  private PageNode copyNode_;
  private String upLevelURI ;
  
  public void setCopyPageNote(PageNode note) {  copyNode_ = note ; }
  
  public PageNode getCopyPasteNote() { return copyNode_ ; }
  
	public UIPageNodeSelector() throws Exception {    
    addChild(UIRightClickPopupMenu.class, "UIPageNodeSelectorPopupMenu", null).setRendered(false);  
    addChild(UIBreadcumbs.class, null, null).setRendered(false);  
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
    navigations_ = new ArrayList<PageNavigation>();
    List<PageNavigation> pnavigations = Util.getUIPortal().getNavigations();
    for(PageNavigation nav  : pnavigations){
      if(nav.isModifiable()) navigations_.add(nav.clone()) ;
    }
    setDropdownItemSelector() ;
    PageNavigation portalSelectedNavi = Util.getUIPortal().getSelectedNavigation() ;
    if(getNavigation(portalSelectedNavi.getId()) != null) {
      selectNavigation(portalSelectedNavi.getId()) ;
      PageNode portalSelectedNode = Util.getUIPortal().getSelectedNode() ;
      selectPageNodeByUri(portalSelectedNode.getUri()) ;  
    } else loadSelectedNavigation();
  }
  
  //TODO: Tung.Pham added
  private void setDropdownItemSelector() {
    if(navigations_ == null && navigations_.size() < 1) {
      getChild(UIDropDownItemSelector.class).setOptions(null) ;
      getChild(UIDropDownItemSelector.class).setSelectedItem(null) ;
      getChild(UITree.class).setSibbling(null) ;
      return ;
    }
    
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    for(PageNavigation navigation: navigations_) {
      String label = navigation.getOwnerId() + "'s Nav";
      options.add(new SelectItemOption<String>(label, navigation.getId()));
    }
    UIDropDownItemSelector uiDopDownSelector = getChild(UIDropDownItemSelector.class);
    uiDopDownSelector.setOptions(options);
    if(options.size() > 0) uiDopDownSelector.setSelected(0);
  }
  
  public void loadSelectedNavigation() {
    if (selectedNavigation == null || getNavigation(selectedNavigation.getId()) == null) {
      if(navigations_ != null && navigations_.size() > 0) {
        selectNavigation(navigations_.get(0).getId()) ;
      }
    }
  }
  
//  private boolean findSelectedNode(PageNavigation nav, List<PageNode> nodes, PageNode node) {
//    if(nodes == null) return false;
//    for(PageNode ele : nodes) {
//      if(ele != node)  continue;        
//      if(nav.isModifiable()) {
//        selectNavigation(nav.getId());
//        selectPageNodeByUri(node.getUri());
//      }  
//      return true;
//    }
//    for(PageNode ele : nodes) {
//      if(findSelectedNode(nav, ele.getChildren(), node)) return true;
//    }
//    return false;
//  }
  
  public void selectNavigation(String id){    
    for(int i = 0; i < navigations_.size(); i++){
      if(!navigations_.get(i).getId().equals(id)) continue; 
      selectedNavigation = navigations_.get(i);
      UITree tree = getChild(UITree.class);
      tree.setSibbling(selectedNavigation.getNodes());      
      UIDropDownItemSelector uiDopDownSelector = getChild(UIDropDownItemSelector.class);
      uiDopDownSelector.setSelected(i);
    }
  }
  
  public void selectPageNodeByUri(String uri){    
    upLevelURI = null; 
    UITree tree = getChild(UITree.class);
    List<?> sibbling = tree.getSibbling();
    tree.setSibbling(null);
    tree.setParentSelected(null);
    selectedPageNode = findPageNodeByUri(selectedNavigation, uri, tree);
    if(selectedPageNode == null){
      tree.setSelected(null);
      tree.setChildren(null);
      tree.setSibbling(sibbling);
      return ;
    }
    tree.setSelected(selectedPageNode);   
    tree.setChildren(selectedPageNode.getChildren());
  }
  
  public PageNode findPageNodeByUri(PageNavigation pageNav, String uri, UITree tree){
    if(pageNav == null) return null;
    List<PageNode> pageNodes = pageNav.getNodes();    
    for(PageNode ele : pageNodes){
      PageNode returnPageNode = findPageNodeByUri(ele, uri, tree);
      if(returnPageNode == null) continue;
      if(tree.getSibbling() == null) tree.setSibbling(pageNodes);      
      return returnPageNode;
    }
    return null; 
  }  
  
  public PageNode findPageNodeByUri(String uri) {
    return findPageNodeByUri(selectedNavigation, uri, getChild(UITree.class));
  }
  
  public PageNode findPageNodeByName(String name){
    if(selectedNavigation == null) return null;
    List<PageNode> pageNodes = selectedNavigation.getNodes();    
    for(PageNode ele : pageNodes){
      PageNode returnPageNode = findPageNodeByName(ele, name);
      if(returnPageNode == null) continue;
      return returnPageNode;
    }
    return null; 
  }
  
  private PageNode findPageNodeByName(PageNode root, String name) {
    if(root.getName().equals(name)) return root;
    List<PageNode> children = root.getChildren();
    if(children == null) return null;
    for(PageNode ele : children){
      PageNode returnPageNode = findPageNodeByName(ele, name);
      if(returnPageNode == null) continue;
      return returnPageNode;
    }
    return null;
  }
  public PageNavigation getPageNavigation(){ return selectedNavigation; }
  
  private PageNode findPageNodeByUri(PageNode pageNode, String uri, UITree tree){
    if(pageNode.getUri().equals(uri)) return pageNode;
    List<PageNode> children = pageNode.getChildren();
    if(children == null) return null;
    for(PageNode ele : children){
      PageNode returnPageNode = findPageNodeByUri(ele, uri, tree);
      if(returnPageNode == null) continue;
      if(tree.getSibbling() == null) tree.setSibbling(children);
      if(tree.getParentSelected() == null) tree.setParentSelected(pageNode);
      if(upLevelURI == null) upLevelURI = pageNode.getUri();      
      return returnPageNode;
    }
    return null;
  }
  
  public List<PageNavigation> getNavigations() { 
    if(navigations_ == null) navigations_ = new ArrayList<PageNavigation>();    
    return navigations_;  
  }
  
  public PageNavigation getSelectedNavigation(){ return selectedNavigation; }  
  public void setSelectedNavigation(PageNavigation nav){ selectedNavigation = nav; }  
  
  public PageNode getSelectedPageNode() { return selectedPageNode; }
  public void setSelectedPageNode(PageNode node) { selectedPageNode = node ;}
  
  public String getUpLevelUri () { return upLevelURI ; }
  
  //TODO: Tung.Pham added
  public void addNavigation(PageNavigation navi) {
    if(navigations_ == null) navigations_ = new ArrayList<PageNavigation>() ;
    navigations_.add(navi) ;
    setDropdownItemSelector() ;
  }
  
  //TODO: Tung.Pham added
  public void removeNavigation(PageNavigation navi) {
    if(navigations_ == null || navigations_.size() < 1) return ;
    Iterator<PageNavigation> itr = navigations_.iterator() ;
    while(itr.hasNext()){
      if(itr.next().getId().equals(navi.getId())) itr.remove() ;
    }
    setDropdownItemSelector() ;
  }

  //TODO: Tung.Pham
  private PageNavigation getNavigation(String id) {
    for(PageNavigation ele : getNavigations()) {
      if(ele.getId().equals(id)) return ele ;
    }
    
    return null ;
  }
  
  static public class ChangeNodeActionListener  extends EventListener<UITree> {
    public void execute(Event<UITree> event) throws Exception {      
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      //TODO: Tung.Pham modified
      //----------------------------------------------------------
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getParent();
      //if(uri != null && uri.trim().length() > 0) uiPageNodeSelector.selectPageNodeByUri(uri);      
      uiPageNodeSelector.selectPageNodeByUri(uri);
      //----------------------------------------------------------
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setFullRender(true);
      
      UIContainer uiParent = uiPageNodeSelector.getParent();
      pcontext.addUIComponentToUpdateByAjax(uiParent) ;
      UIPageEditBar uiEditBar = uiParent.getChild(UIPageEditBar.class);      
      PageNode node  = uiPageNodeSelector.getSelectedPageNode();
      if(node == null) return;  
      
      UserPortalConfigService configService = uiParent.getApplicationComponent(UserPortalConfigService.class);
      Page page = configService.getPage(node.getPageReference(), event.getRequestContext().getRemoteUser());
      
      if(page == null){
        Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiParent.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }
      
      UIPage uiPage = Util.toUIPage(node, Util.getUIPortalToolPanel());
      UIPortalToolPanel toolPanel = Util.getUIPortalToolPanel() ; 
      toolPanel.setUIComponent(uiPage);
      toolPanel.setRenderSibbling(UIPortalToolPanel.class) ;

      if(!page.isModifiable()) {
        Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiParent.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }
      
      uiEditBar.setRendered(true);
      if(Page.DESKTOP_PAGE.equals(uiPage.getFactoryId())) {
        Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiParent.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }
      
      uiEditBar.setUIPage(uiPage);
      Class [] childrenToRender = {UIPageEditBar.class, 
                                   UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
      uiParent.setRenderedChildrenOfTypes(childrenToRender);
    }
  }
  
  static public class SelectNavigationActionListener  extends EventListener<UIPageNodeSelector> {
    public void execute(Event<UIPageNodeSelector> event) throws Exception {
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPageNodeSelector uiPageNodeSelector = event.getSource();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector.getParent()) ;
      if(id == null) {
        uiPageNodeSelector.setSelectedNavigation(null);
        return;
      }
      //uiPageNodeSelector.setSelectedPageNode(null) ;
      uiPageNodeSelector.selectNavigation(id);
    }
  }
  
}
