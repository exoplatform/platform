/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.view.listener.UIPageNodeActionListener.AddNodeActionListener;
import org.exoplatform.portal.component.view.listener.UIPageNodeActionListener.CopyNodeActionListener;
import org.exoplatform.portal.component.view.listener.UIPageNodeActionListener.DeleteNodeActionListener;
import org.exoplatform.portal.component.view.listener.UIPageNodeActionListener.EditPageNodeActionListener;
import org.exoplatform.portal.component.view.listener.UIPageNodeActionListener.EditSelectedNodeActionListener;
import org.exoplatform.portal.component.view.listener.UIPageNodeActionListener.PasteNodeActionListener;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.component.UIBreadcumbs;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIDropDownItemSelector;
import org.exoplatform.webui.component.UIRightClickPopupMenu;
import org.exoplatform.webui.component.UITree;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
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
      template = "app:/groovy/portal/webui/component/customization/UIPageNodeSelector.gtmpl" ,
      events = {
          @EventConfig(listeners = UIPageNodeSelector.ChangeNodeActionListener.class),
          @EventConfig(listeners = UIPageNodeSelector.SelectNavigationActionListener.class) 
      }
  ),
  @ComponentConfig(
      id = "PageNodePopupMenu",
      type = UIRightClickPopupMenu.class,
      template = "system:/groovy/webui/component/UIRightClickPopupMenu.gtmpl",
      events = {
        @EventConfig(listeners = AddNodeActionListener.class),
        @EventConfig(listeners = EditPageNodeActionListener.class),
        @EventConfig(listeners = EditSelectedNodeActionListener.class),
        @EventConfig(listeners = CopyNodeActionListener.class),
        @EventConfig(listeners = PasteNodeActionListener.class),
        @EventConfig(listeners = DeleteNodeActionListener.class)
      }
  ),
  @ComponentConfig(
      id = "UIPageNodeSelectorPopupMenu",
      type = UIRightClickPopupMenu.class,
      template = "system:/groovy/webui/component/UIRightClickPopupMenu.gtmpl",
      events = {
        @EventConfig(listeners = AddNodeActionListener.class),
        @EventConfig(listeners = PasteNodeActionListener.class)
      }
  )
})

public class UIPageNodeSelector extends UIContainer {
  
  private List<PageNavigation> navigations_;  
  private PageNavigation selectedNavigation;  
  private PageNode selectedPageNode;
  
  private PageNode copyNode_;
  
  public void setCopyPageNote(PageNode note) {  copyNode_ = note ; }
  public PageNode getCopyPasteNote() { return copyNode_ ; } 
  
  private String upLevelURI ;

	public UIPageNodeSelector() throws Exception {    
    addChild(UIRightClickPopupMenu.class, "UIPageNodeSelectorPopupMenu", null).setRendered(false);  
    addChild(UIBreadcumbs.class, null, null).setRendered(false);  
    UIDropDownItemSelector uiDopDownSelector = addChild(UIDropDownItemSelector.class, null, null);
    uiDopDownSelector.setTitle("Select Navigations");
    
    UITree uiTree = addChild(UITree.class, null, "TreePageSelector");    
    uiTree.setIcon("Icon NavigationPortalIcon");    
    uiTree.setSelectedIcon("Icon NavigationPortalIcon");
    uiTree.setBeanIdField("uri");
    uiTree.setBeanLabelField("label");   
    uiTree.setBeanIconField("icon");
    UIRightClickPopupMenu uiPopupMenu = createUIComponent(UIRightClickPopupMenu.class, "PageNodePopupMenu", null) ;
    uiTree.setUIRightClickPopupMenu(uiPopupMenu);
    
    loadNavigations();
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    for(PageNavigation navigation: navigations_) {
      String label = navigation.getOwnerId() + "'s Nav";
      options.add(new SelectItemOption<String>(label, navigation.getOwnerId()));
    }
    uiDopDownSelector.setOptions(options);
    uiDopDownSelector.setSelected(0);
	}
  
  public void loadNavigations() throws Exception {
    navigations_ = new ArrayList<PageNavigation>();
    
    List<PageNavigation> pnavigations = Util.getUIPortal().getNavigations();    
    PortalRequestContext pcontext = Util.getPortalRequestContext();
    String remoteUser  = pcontext.getRemoteUser();
//    UserACL userACL = getApplicationComponent(UserACL.class);
//    
//    for(PageNavigation nav  : pnavigations){
//      String permission = nav.getEditPermission();
//      if(userACL.hasPermission(nav.getOwnerId(), remoteUser, permission)){
//        navigations_.add(nav.clone());
//      }
//    }
    
    if(navigations_.size() < 1) return;
    selectedNavigation = navigations_.get(0);
    UITree tree = getChild(UITree.class);
    tree.setSibbling(selectedNavigation.getNodes());   
  }
  
  public void selectNavigation(String owner){    
    for(PageNavigation nav : navigations_){
      if(!nav.getOwnerId().equals(owner)) continue; 
      selectedNavigation = nav;
      UITree tree = getChild(UITree.class);
      tree.setSibbling(selectedNavigation.getNodes());      
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
  
  public String getUpLevelUri () { return upLevelURI ; }
  
  static public class ChangeNodeActionListener  extends EventListener<UITree> {
    public void execute(Event<UITree> event) throws Exception {      
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getParent();
      if(uri != null && uri.trim().length() > 0) uiPageNodeSelector.selectPageNodeByUri(uri);      
      
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
      Page page  = configService.getPage(node.getPageReference(), event.getRequestContext().getRemoteUser());
//      UserACL userACL = uiEditBar.getApplicationComponent(UserACL.class);
//      String accessUser = pcontext.getRemoteUser();     
//      if(page == null || !userACL.hasPermission(page.getOwnerId(), accessUser, page.getEditPermission())){
//        Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
//        uiParent.setRenderedChildrenOfTypes(childrenToRender);
//        return;
//      }
      
      uiEditBar.setRendered(true);
      UIPage uiPage = Util.toUIPage(node, Util.getUIPortalToolPanel());
      Util.getUIPortalToolPanel().setUIComponent(uiPage);
      
      if("Desktop".equals(uiPage.getFactoryId())) {
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
      String owner = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPageNodeSelector uiPageNodeSelector = event.getSource();
      if(owner == null){
        uiPageNodeSelector.setSelectedNavigation(null);
        return;
      }
      uiPageNodeSelector.selectNavigation(owner);      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector) ;
    }
  } 
  
}
