/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
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
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
import org.exoplatform.webui.core.UITree;
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
      UIRightClickPopupMenu uiMenu = event.getSource();
      UIPageNodeSelector uiPageNodeSelector = uiMenu.getAncestorOfType(UIPageNodeSelector.class);
      if(uiPageNodeSelector.getSelectedNavigation() == null) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.NoPageNavigation", null)) ;
        
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector.getParent());
        return;
      }
      UIPortalApplication uiApp = uiPageNodeSelector.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector.getParent());
      UIPageNodeForm uiNodeForm = uiMaskWS.createUIComponent(UIPageNodeForm.class, null, null);
      uiNodeForm.setValues(null);
      uiMaskWS.setUIComponent(uiNodeForm);
      uiMaskWS.setShow(true);
      
      Object parent = null;
      List<PageNode> pageNodes = uiPageNodeSelector.getSelectedNavigation().getNodes();
      if(uri != null && uri.trim().length() > 0) { 
        for(PageNode pageNode : pageNodes) {
          parent = findPageNodeByUri(pageNode, uri);
          if(parent != null) break;
        }
      }
      if(parent == null) parent = uiPageNodeSelector.getSelectedNavigation();      
      uiNodeForm.setSelectedParent(parent);
    }
    
    private PageNode findPageNodeByUri(PageNode pageNode, String uri){
      if(pageNode.getUri().equals(uri)) return pageNode;
      List<PageNode> children = pageNode.getChildren();
      if(children == null) return null;
      for(PageNode ele : children){
        PageNode returnPageNode = findPageNodeByUri(ele, uri);
        if(returnPageNode != null) return returnPageNode;
      }
      return null; 
    }
  } 

  
  static public class EditPageNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      //TODO: Tung.Pham replace
      //--------------------------------------------------
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu uiPopupMenu = event.getSource();
      UIComponent uiParent = uiPopupMenu.getParent();
      UIPageNodeSelector uiPageNodeSelector = uiParent.getParent();
      uiPageNodeSelector.selectPageNodeByUri(uri);
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();

      PageNode node = uiPageNodeSelector.getSelectedPageNode();
      if(node == null) uiPageNodeSelector.loadSelectedNavigation();
      node = uiPageNodeSelector.getSelectedPageNode();
      if(node == null) return ;
      PortalRequestContext pcontext  = (PortalRequestContext)event.getRequestContext();
      UserPortalConfigService portalConfigService = uiPopupMenu.getApplicationComponent(UserPortalConfigService.class);
      Page page  = portalConfigService.getPage(node.getPageReference(), pcontext.getRemoteUser());
      if(page == null){
        Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }
      uiManagement.setPage(page) ;

      //--------------------------------------------------

//      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
//      PortalRequestContext pcontext  = (PortalRequestContext)event.getRequestContext();
//      UIRightClickPopupMenu uiPopupMenu = event.getSource();
//      UIComponent uiParent = uiPopupMenu.getParent();
//      UIPageNodeSelector uiPageNodeSelector = uiParent.getParent();
//      uiPageNodeSelector.selectPageNodeByUri(uri);
//      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();
//      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
//      
//      UIPortalApplication uiApp = Util.getUIPortal().getAncestorOfType(UIPortalApplication.class);
//      UIControlWorkspace uiControl = uiApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
//      pcontext.addUIComponentToUpdateByAjax(uiControl);
//      
//      PageNode node = uiPageNodeSelector.getSelectedPageNode();
//      if(node == null) uiPageNodeSelector.loadSelectedNavigation();
//      node = uiPageNodeSelector.getSelectedPageNode();
//      if(node == null) return;
//      UserPortalConfigService portalConfigService = uiPopupMenu.getApplicationComponent(UserPortalConfigService.class);
//      Page page  = portalConfigService.getPage(node.getPageReference(), pcontext.getRemoteUser());
//      UIPage uiPage  = null;
//      if(page != null)  uiPage = Util.toUIPage(page, uiToolPanel);
//      if(page == null){
//        Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
//        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
//        return;
//      }
//      
//      if(!page.isModifiable()){
//        Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
//        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
//        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.Invalid-editPermission", null)) ;
//        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
//        return;
//      }
//            
//      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class) ;  
//      uiToolPanel.setUIComponent(uiPage);
//      
//      if (Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
//        UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;      
//        UIPageForm uiPageForm =  uiMaskWS.createUIComponent(UIPageForm.class);
//        uiPageForm.removeChild(UIPageTemplateOptions.class);
//        uiPageForm.setValues(uiPage);
//        uiMaskWS.setUIComponent(uiPageForm);
//        uiMaskWS.setWindowSize(640, 400);
//        uiMaskWS.setShow(true);
//        event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
//        return ;
//      }
//      // TODO Add Message to
//        UIApplication uiApplication = Util.getPortalRequestContext().getUIApplication() ;
//        uiApplication.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.notAvailable", null)) ;
//        
//        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApplication.getUIPopupMessages() );
//      
//      UIWorkspace uiWorkingWS = uiApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
//      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
//      pcontext.setFullRender(true);
//      
//      Class [] childrenToRender = {UIPageEditBar.class, UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
//      uiManagement.setRenderedChildrenOfTypes(childrenToRender);
//      UIPageEditBar uiPageEditBar = uiManagement.getChild(UIPageEditBar.class);
//      uiPageEditBar.setUIPage(uiPage); 
//      uiPageEditBar.showUIPage();
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
        if(findPageNodeByUri(pageNode, list, uri) != null) break;
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
      UIRightClickPopupMenu popupMenu = event.getSource();
      UIComponent parent = popupMenu.getParent();
      UIPageNodeSelector uiPageNodeSelector = parent.getParent();
      PageNavigation nav = uiPageNodeSelector.getSelectedNavigation();
      String uri = nav.getId() + "::" + value;
      uiPageNodeSelector.setCopyNodeUri(uri);
      uiPageNodeSelector.setCut(false);
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();

      Class [] childrenToRender = new Class[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class };
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
    }
  }
  
  static public class CutNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {      
      String value  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu popupMenu = event.getSource();
      UIComponent parent = popupMenu.getParent();
      UIPageNodeSelector uiPageNodeSelector = parent.getParent();
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      PageNavigation nav = uiPageNodeSelector.getSelectedNavigation();
      String uri = nav.getId() + "::" + value;
      uiPageNodeSelector.setCopyNodeUri(uri);
      uiPageNodeSelector.setCut(true);
      Class [] childrenToRender = new Class[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class };
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
    }
  }

  static public class PasteNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {   
      String value  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu popupMenu = event.getSource();
      UIPageNodeSelector uiPageNodeSelector =  popupMenu.getAncestorOfType(UIPageNodeSelector.class);
      UIPageManagement uiPageManagement = uiPageNodeSelector.getParent();

      PageNode srcNode = uiPageNodeSelector.getCopyNode();
      if(srcNode == null)  return;
      srcNode = srcNode.clone();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageManagement);
      String name = srcNode.getName();

      PageNode targetNode = uiPageNodeSelector.findPageNodeByUri(value) ;  
      if(targetNode.hasNode(srcNode)) return;
      if(targetNode.hasChildNode(name)){
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.paste.sameName", null)) ;
        
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return;
      }
      String targetUri= "";
      if(targetNode != null) targetUri = targetNode.getUri();
      srcNode.setUri(targetUri + "/" + name);
      targetNode.addChild(srcNode);
      if(uiPageNodeSelector.isCut()){
        uiPageNodeSelector.setCut(false);
        uiPageNodeSelector.deleteNode(uiPageNodeSelector.getCopyNodeUri());
      }
      Class [] childrenToRender = new Class[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
      uiPageManagement.setRenderedChildrenOfTypes(childrenToRender);      

      if(targetNode == null)  return ; 
      UITree uiTree = uiPageManagement.findFirstComponentOfType(UITree.class);  
      if(uiPageNodeSelector.getSelectedPageNode().getUri().equals(targetNode.getUri())){
        uiTree.setChildren(targetNode.getChildren());
      }
    }
  }
}
