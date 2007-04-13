/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.customization.UIPageEditBar;
import org.exoplatform.portal.component.customization.UIPageForm;
import org.exoplatform.portal.component.customization.UIPageManagement;
import org.exoplatform.portal.component.customization.UIPageNavigationControlBar;
import org.exoplatform.portal.component.customization.UIPageNodeForm;
import org.exoplatform.portal.component.customization.UIPageNodeSelector;
import org.exoplatform.portal.component.customization.UIPageTemplateOptions;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIRightClickPopupMenu;
import org.exoplatform.webui.component.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jan 25, 2007  
 */
public class UIPageNodeActionListener {
  
  static public class AddNodeActionListener  extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {           
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu popupMenu = event.getSource();
      UIComponent parent = popupMenu.getParent();
      UIPageNodeSelector uiPageNodeSelector = parent.getParent();
      
      UIPortalApplication uiApp = uiPageNodeSelector.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector.<UIPageManagement>getParent());
      
      UIPageNodeForm uiPageNodeForm = uiMaskWS.createUIComponent(UIPageNodeForm.class, null, null);
      uiPageNodeForm.setValues(null);
      uiMaskWS.setUIComponent(uiPageNodeForm);
      uiMaskWS.setShow(true);
      
      if(uiPageNodeSelector.getSelectedPageNode() == null){
        uiPageNodeForm.setSelectedParent(uiPageNodeSelector.findPageNodeByUri(uri));
        return; 
      }
      uiPageNodeForm.setSelectedParent(uiPageNodeSelector.findPageNodeByUri(uri));
    }
  } 

  static public class EditPageNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {     
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu popupMenu = event.getSource();
      UIComponent parent = popupMenu.getParent();
      UIPageNodeSelector uiPageNodeSelector = parent.getParent();
      PageNode node  = uiPageNodeSelector.findPageNodeByUri(uri);

      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      
      if(node == null) node = Util.getUIPortal().getSelectedNode();
      if(node == null) return;

      UserPortalConfigService portalConfigService = popupMenu.getApplicationComponent(UserPortalConfigService.class);
      Page page  = portalConfigService.getPage(node.getPageReference(), event.getRequestContext().getRemoteUser());
      UIPage uiPage  = Util.toUIPage(page, uiToolPanel);
      
      UIPortalApplication uiApp = Util.getUIPortal().getAncestorOfType(UIPortalApplication.class);
      UIControlWorkspace uiControl = uiApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiControl);
      
      if ("Desktop".equals(page.getFactoryId())) {
        UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;      
        UIPageForm uiPageForm =  uiMaskWS.createUIComponent(UIPageForm.class);
        uiPageForm.removeChild(UIPageTemplateOptions.class);
        uiPageForm.setValues(uiPage);
        uiMaskWS.setUIComponent(uiPageForm);
        uiMaskWS.setWindowSize(640, 400);
        uiMaskWS.setShow(true);
        event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
        return ;
      }
      
      UserACL userACL = popupMenu.getApplicationComponent(UserACL.class);
      String accessUser = Util.getPortalRequestContext().getRemoteUser();
      if(page == null || !userACL.hasPermission(page.getOwner(), accessUser, page.getEditPermission())){
        Class [] childrenToRender = {UIPageNodeSelector.class };      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }
            
      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class) ;  
      uiToolPanel.setUIComponent(uiPage);
      if(uiPage.isShowMaxWindow()) {
        Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }

      Class [] childrenToRender = {UIPageEditBar.class, UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);

      UIPageEditBar uiPageEditBar = uiManagement.getChild(UIPageEditBar.class);
      uiPageEditBar.setUIPage(uiPage); 
      uiPageEditBar.showUIPage();
    }
  }

  static public class EditSelectedNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {     
      UIRightClickPopupMenu popupMenu = event.getSource();
      UIComponent parent = popupMenu.getParent();
      UIPageNodeSelector uiPageNodeSelector = parent.getParent();
      
      UIPortalApplication uiApp = uiPageNodeSelector.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector.<UIPageManagement>getParent());
      
      UIPageNodeForm uiNodeForm = uiMaskWS.createUIComponent(UIPageNodeForm.class, null, null);
      uiMaskWS.setUIComponent(uiNodeForm);
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      List<PageNode> pageNodes = uiPageNodeSelector.getSelectedNavigation().getNodes();
      List<Object> list = new ArrayList<Object>(2);
      list.add(uiPageNodeSelector.getSelectedNavigation());
      list.add(null);
      for(PageNode pageNode : pageNodes) {
        findPageNodeByUri(pageNode, list, uri);
      }
      if(list.get(1) == null && pageNodes.size() > 0) list.set(1, pageNodes.get(0));  
      uiNodeForm.setValues((PageNode)list.get(1));
      uiNodeForm.setSelectedParent(list.get(0));
      uiMaskWS.setShow(true);
    }
    
    private PageNode findPageNodeByUri(PageNode pageNode, List<Object> list, String uri){
      if(pageNode.getUri().equals(uri)) {
        list.set(1, pageNode);
        return pageNode;
      }
      List<PageNode> children = pageNode.getChildren();
      if(children == null) return null;
      for(PageNode ele : children){
        PageNode returnPageNode = findPageNodeByUri(ele, list, uri);
        if(returnPageNode == null) continue;
        list.set(0, pageNode);
      }
      return null; 
    }
  }

  static public class DeleteNodeActionListener  extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {  
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu popupMenu = event.getSource();
      UIComponent parent = popupMenu.getParent();
      UIPageNodeSelector uiPageNodeSelector = parent.getParent();
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);

      PageNode selectedPageNode = uiPageNodeSelector.findPageNodeByUri(uri);      
      if(selectedPageNode == null) return;
      List<PageNavigation> navigations = uiPageNodeSelector.getNavigations() ;
      List<PageNode> pageNodes = null;
      for(PageNavigation nav : navigations ){
        pageNodes = searchListPageNode(nav, selectedPageNode);
        if(pageNodes != null) break;
      }
      if(pageNodes == null) return;      
      pageNodes.remove(selectedPageNode);

      Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class}; 
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);

      if(pageNodes.size() > 0) return;    
      PageNode parentNode =  uiPageNodeSelector.getChild(UITree.class).getParentSelected();
      if (parentNode == null) return;
      uiPageNodeSelector.selectPageNodeByUri(parentNode.getUri());
    }

    private List<PageNode> searchListPageNode(PageNavigation nav, PageNode selectedPageNode){
      List<PageNode> pageNodes = nav.getNodes();
      if(pageNodes == null) return null;
      if(pageNodes.contains(selectedPageNode)) return pageNodes;
      for(PageNode pageNode : pageNodes){
        List<PageNode> list =  searchListPageNode(pageNode, selectedPageNode);
        if(list != null) return list;
      }
      return null;
    }

    private List<PageNode> searchListPageNode(PageNode node, PageNode selectedPageNode){
      List<PageNode> pageNodes = node.getChildren();
      if(pageNodes == null) return null;
      if(pageNodes.contains(selectedPageNode)) return pageNodes;
      for(PageNode pageNode : pageNodes){
        List<PageNode> list =  searchListPageNode(pageNode, selectedPageNode);
        if(list != null) return list;
      }
      return null;
    }
  }

  static public class CopyNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {      
      String value  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
//      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n\n" + value);
      UIRightClickPopupMenu popupMenu = event.getSource();
      UIComponent parent = popupMenu.getParent();
      UIPageNodeSelector uiPageNodeSelector = parent.getParent();
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();

      PageNode selectedPageNode = uiPageNodeSelector.findPageNodeByUri(value);     
      if(selectedPageNode == null)   return;      
      PageNode pageNode = new PageNode(selectedPageNode);
      uiPageNodeSelector.setCopyPageNote(pageNode) ;
      Class [] childrenToRender = new Class[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class };
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
    }
  }

  static public class PasteNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {   
      String value  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu popupMenu = event.getSource();
      UIComponent parent = popupMenu.getParent();
      UIPageNodeSelector uiPageNodeSelector = parent.getParent();
      UIPageManagement uiPageManagement = uiPageNodeSelector.getParent();

      PageNode srcNode = uiPageNodeSelector.getCopyPasteNote() ;
      if(srcNode == null)  return;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageManagement);

      String nodeUri = srcNode.getUri();
      int index = nodeUri.lastIndexOf("/") ;
      String preReplaceUri = "";
      if(index > -1) preReplaceUri = nodeUri.substring(0, index).trim();

      PageNode targetNode = uiPageNodeSelector.findPageNodeByUri(value) ;    
      String targetUri = null;       
      if(targetNode != null) targetUri = targetNode.getUri();

      replaceURI(preReplaceUri, targetUri, srcNode);      

      if(targetNode != null){
        if(!isChild(targetNode.getChildren(), srcNode)) targetNode.addChild(srcNode);
      }else{
        PageNavigation targetNav = uiPageNodeSelector.getSelectedNavigation();
        if(!isChild(targetNav.getNodes(), srcNode)) targetNav.addNode(srcNode);
      }
      Class [] childrenToRender = new Class[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
      uiPageManagement.setRenderedChildrenOfTypes(childrenToRender);      
      uiPageNodeSelector.setCopyPageNote(null);     

      if(targetNode == null)  return ; 
      UITree uiTree = uiPageManagement.findFirstComponentOfType(UITree.class);      
      uiTree.setChildren(targetNode.getChildren());
    }

    private boolean isChild(List<PageNode> children, PageNode node){
      if(children == null) return false;
      for(PageNode child : children){
        if(child.getUri().equals(node.getUri())) return true;
      }
      return false;
    }

    private void replaceURI(String preReplacePattern, String afterReplacePattern, PageNode node){      
      if(afterReplacePattern == null){       
        node.setUri(node.getName());
        return;
      }
      if(preReplacePattern.length() < 1){
        node.setUri(afterReplacePattern +"/"+node.getUri());
      }else{
        node.setUri(node.getUri().replaceFirst(preReplacePattern, afterReplacePattern));
      }
      List<PageNode> children = node.getChildren();
      if(children == null)  return; 
      for(PageNode child : children) replaceURI(preReplacePattern, afterReplacePattern, child); 
    }

  }
}
