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
import org.exoplatform.portal.webui.navigation.UIPageNodeSelector.SelectedNode;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageBody;
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
      UIRightClickPopupMenu uiPopupMenu = event.getSource();
      UIPageNodeSelector uiPageNodeSelector = uiPopupMenu.getAncestorOfType(UIPageNodeSelector.class);
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
          parent = PageNavigationUtils.searchPageNodeByUri(pageNode, uri);
          if(parent != null) break;
        }
      }
      if(parent == null) parent = uiPageNodeSelector.getSelectedNavigation();      
      uiNodeForm.setSelectedParent(parent);
    }
    
  } 

  static public class EditPageNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      PortalRequestContext pcontext  = (PortalRequestContext)event.getRequestContext();
      UIRightClickPopupMenu uiPopupMenu = event.getSource();
      UIPageNodeSelector uiPageNodeSelector = uiPopupMenu.<UIComponent>getParent().getParent();
      PageNavigation currentNav = uiPageNodeSelector.getSelectedNavigation();
      PageNode selectNode = PageNavigationUtils.searchPageNodeByUri(currentNav, uri);
      UserPortalConfigService portalConfigService = uiPopupMenu.getApplicationComponent(UserPortalConfigService.class);
      Page page  = portalConfigService.getPage(selectNode.getPageReference(), pcontext.getRemoteUser());
      
      uiPageNodeSelector.selectPageNodeByUri(uri);
      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      
      UIPortalApplication uiApp = Util.getUIPortal().getAncestorOfType(UIPortalApplication.class);
      UIControlWorkspace uiControl = uiApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiControl);
      UIWorkspace uiWorkingWS = uiApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;   
      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class) ;
      uiToolPanel.setShowMaskLayer(true) ;
      pcontext.setFullRender(true);
      if(page == null ) {
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        uiToolPanel.setUIComponent(null) ;
        uiApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.edit.null", new String[]{})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()); 
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return;
      }
      UIPageEditBar uiPageEditBar = uiManagement.getChild(UIPageEditBar.class);
      UIPage uiPage = uiPageEditBar.getUIPage() ;
      if(uiPage == null || !uiPage.getPageId().equals(page.getPageId())) uiPage = Util.toUIPage(page, uiToolPanel); 
      uiApp.findFirstComponentOfType(UIPageBody.class).setUIComponent(null);
      
      if(!page.isModifiable()){
        uiToolPanel.setUIComponent(uiPage) ;
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.Invalid-editPermission", null)) ;
        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return;
      }
      
      uiPageEditBar.setUIPage(uiPage); 
      uiPageEditBar.showUIPage();

      if(!Page.DESKTOP_PAGE.equals(page.getFactoryId()))  {
        Class<?> [] childrenToRender = {UIPageEditBar.class, UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }

      //      PageNode node = uiPageNodeSelector.getSelectedPageNode();
//      if(node == null) {
//        UIPortal uiPortal = Util.getUIPortal();
//        uiPageNodeSelector.selectNavigation(uiPortal.getSelectedNavigation().getId());
//        uiPageNodeSelector.selectPageNodeByUri(uiPortal.getSelectedNode().getUri());
//        node = uiPageNodeSelector.getSelectedPageNode();
//      }
//      
//      if(node == null) return;
//      if(page == null) {
//        uiToolPanel.setUIComponent(null) ;
//        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
//        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
//        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.notAvailable", null)) ;
//        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
//        return;
//      } 
      
      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;      
      UIPageForm uiPageForm =  uiMaskWS.createUIComponent(UIPageForm.class);
      uiPageForm.removeChild(UIPageTemplateOptions.class);
      uiPageForm.setValues(uiPage);
      uiMaskWS.setUIComponent(uiPageForm);
      uiMaskWS.setWindowSize(640, 400);
      uiMaskWS.setShow(true);
      pcontext.addUIComponentToUpdateByAjax(uiMaskWS);
      Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);
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
      PageNavigation selectedNav = uiPageNodeSelector.getSelectedNavigation();
      Object obj = PageNavigationUtils.searchParentNode(selectedNav, uri);
      PageNode selectedNode = PageNavigationUtils.searchPageNodeByUri(selectedNav, uri);
      uiNodeForm.setValues(selectedNode);
      uiNodeForm.setSelectedParent(obj);
      uiMaskWS.setShow(true);
    }
  }

  static public class DeleteNodeActionListener  extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {  
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext() ;
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getAncestorOfType(UIPageNodeSelector.class);
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      Class<?> [] childrenToRender = new Class<?>[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class };
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);      
      pcontext.addUIComponentToUpdateByAjax(uiManagement);
      
      PageNavigation nav = uiPageNodeSelector.getSelectedNavigation();
      if(nav == null) return;
      
      PageNode [] pageNodes = PageNavigationUtils.searchPageNodesByUri(nav, uri);
      if(pageNodes == null) return;
      //--------------------------------
      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel() ;
      uiToolPanel.setUIComponent(null) ;
      UIWorkspace uiWorkspace = uiToolPanel.getAncestorOfType(UIWorkspace.class) ;
      pcontext.setFullRender(true) ;
      pcontext.addUIComponentToUpdateByAjax(uiWorkspace);
      //--------------------------------
      if(pageNodes[0] == null) {
        nav.getNodes().remove(pageNodes[1]);
        return;
      }
      pageNodes[0].getChildren().remove(pageNodes[1]);
      uiPageNodeSelector.selectPageNodeByUri(pageNodes[0].getUri());
    }
    
  }

  static public class CopyNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {      
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getAncestorOfType(UIPageNodeSelector.class);
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      Class<?> [] childrenToRender = new Class<?>[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class };
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
      
      PageNavigation nav = uiPageNodeSelector.getSelectedNavigation();
      if(nav == null) return;
      PageNode [] pageNodes = PageNavigationUtils.searchPageNodesByUri(nav, uri);
      if(pageNodes == null) return;
      SelectedNode selectedNode = new SelectedNode(nav, pageNodes[0], pageNodes[1]);
      selectedNode.setDeleteNode(false);
      uiPageNodeSelector.setCopyNode(selectedNode);
      event.getSource().setActions(new String[] {"AddNode", "EditPageNode", "EditSelectedNode", 
                                                 "CopyNode", "CutNode", "PasteNode", "DeleteNode", "MoveUp", "MoveDown"});
    }
  }
  
  static public class CutNodeActionListener extends UIPageNodeActionListener.CopyNodeActionListener {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {      
      super.execute(event);
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getAncestorOfType(UIPageNodeSelector.class);
      if(uiPageNodeSelector.getCopyNode() == null) return; 
      uiPageNodeSelector.getCopyNode().setDeleteNode(true);
    }
  }

  static public class PasteNodeActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {   
      String targetUri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIRightClickPopupMenu uiPopupMenu = event.getSource();
      UIPageNodeSelector uiPageNodeSelector =  uiPopupMenu.getAncestorOfType(UIPageNodeSelector.class);
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      Class<?> [] childrenToRender = new Class<?>[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class };
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
      
      SelectedNode selectedNode = uiPageNodeSelector.getCopyNode(); 
      if(selectedNode == null) return;
      
      PageNode newNode = selectedNode.getNode().clone();
      if(selectedNode.getParentNode() != null) {
        String parentUri = selectedNode.getParentNode().getUri();
        String newUri = selectedNode.getNode().getUri();
        int idx = newUri.indexOf(parentUri+"/");
        if(idx > -1) newNode.setUri(newUri.substring(idx + parentUri.length()+1));
        if(newNode.getUri().charAt(0) == '/') newNode.setUri(newNode.getUri().substring(1));
      }
      
      PageNavigation targetNav = uiPageNodeSelector.getSelectedNavigation();
      PageNode targetNode = PageNavigationUtils.searchPageNodeByUri(targetNav, targetUri);
      if(targetNode != null) newNode.setUri(targetNode.getUri()+"/"+newNode.getUri());

      if( (targetNode != null && hasNode(targetNode, newNode.getUri())) || 
          hasNode(targetNav, newNode.getUri()) ){
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.paste.sameName", null)) ;
        
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return;
      }
      
      if(selectedNode.isDeleteNode()) {
        if(selectedNode.getParentNode() != null) {
          selectedNode.getParentNode().getChildren().remove(selectedNode.getNode());
        } else {
          selectedNode.getPageNavigation().getNodes().remove(selectedNode.getNode());
        }
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector);
      uiPageNodeSelector.setCopyNode(null);
      UITree uitree = uiPageNodeSelector.getChild(UITree.class);
      UIRightClickPopupMenu popup = uitree.getUIRightClickPopupMenu();
      popup.setActions(new String[] {"AddNode", "EditPageNode", "EditSelectedNode", "CopyNode", 
                                     "CutNode", "DeleteNode", "MoveUp", "MoveDown"});
       
      if(targetNode == null) { 
        targetNav.addNode(newNode);
        return;
      }
      targetNode.getChildren().add(newNode);
      uiPageNodeSelector.selectPageNodeByUri(targetNode.getUri());
    }
    
    private boolean hasNode(PageNode node, String uri) {
      if(node == null) return false;
      List<PageNode> children = node.getChildren();
      if(children == null) {
        node.setChildren(new ArrayList<PageNode>());
        return false;
      }
      for(PageNode ele : children) {
        if(ele.getUri().equals(uri)) return true;
      }
      return false;
    }
    
    private boolean hasNode(PageNavigation nav, String uri) {
      List<PageNode> nodes = nav.getNodes();
      for(PageNode ele : nodes) {
        if(ele.getUri().equals(uri)) return true;
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
      UIPageNodeSelector uiPageNodeSelector =  event.getSource().getAncestorOfType(UIPageNodeSelector.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector.getParent());
      PageNavigation nav = uiPageNodeSelector.getSelectedNavigation();
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
  
  static public class MoveDownActionListener extends UIPageNodeActionListener.MoveUpActionListener{
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {      
      super.moveNode(event, 1);
    }
  }
}