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
import org.exoplatform.portal.webui.portal.UIPortal;
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
      uiPageNodeSelector.selectPageNodeByUri(uri);
      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      
      UIPortalApplication uiApp = Util.getUIPortal().getAncestorOfType(UIPortalApplication.class);
      UIControlWorkspace uiControl = uiApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiControl);
      UIWorkspace uiWorkingWS = uiApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
      pcontext.setFullRender(true);
      
      PageNode node = uiPageNodeSelector.getSelectedPageNode();
      if(node == null) {
        UIPortal uiPortal = Util.getUIPortal();
        uiPageNodeSelector.selectNavigation(uiPortal.getSelectedNavigation().getId());
        uiPageNodeSelector.selectPageNodeByUri(uiPortal.getSelectedNode().getUri());
        node = uiPageNodeSelector.getSelectedPageNode();
      }
      if(node == null) return;
      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class) ;
      UserPortalConfigService portalConfigService = uiPopupMenu.getApplicationComponent(UserPortalConfigService.class);
      Page page  = portalConfigService.getPage(node.getPageReference(), pcontext.getRemoteUser());
      if(page == null) {
        uiToolPanel.setUIComponent(null) ;
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.notAvailable", null)) ;
        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return;
      } 
      
      UIPage uiPage = Util.toUIPage(page, uiToolPanel);
      if(!page.isModifiable()){
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.Invalid-editPermission", null)) ;
        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return;
      }
            
      //uiToolPanel.setRenderSibbling(UIPortalToolPanel.class) ;  
      uiApp.findFirstComponentOfType(UIPageBody.class).setUIComponent(null);
      uiToolPanel.setUIComponent(uiPage);
      
      if (Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
        UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;      
        UIPageForm uiPageForm =  uiMaskWS.createUIComponent(UIPageForm.class);
        uiPageForm.removeChild(UIPageTemplateOptions.class);
        uiPageForm.setValues(uiPage);
        uiMaskWS.setUIComponent(uiPageForm);
        uiMaskWS.setWindowSize(640, 400);
        uiMaskWS.setShow(true);
        pcontext.addUIComponentToUpdateByAjax(uiMaskWS);
        return ;
      }
      
//      UIWorkspace uiWorkingWS = uiApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
//      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
//      pcontext.setFullRender(true);
      
      Class<?> [] childrenToRender = {UIPageEditBar.class, UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
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
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getAncestorOfType(UIPageNodeSelector.class);
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      Class<?> [] childrenToRender = new Class<?>[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class };
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);      
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
      
      PageNavigation nav = uiPageNodeSelector.getSelectedNavigation();
      if(nav == null) return;
      
      PageNode [] pageNodes = PageNavigationUtils.searchPageNodesByUri(nav, uri);
      if(pageNodes == null) return;
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
      event.getSource().setActions(new String[] {"AddNode", "EditPage", "EditSelectedNode", 
                                                 "CopyNode", "CutNode", "PasteNode", "DeleteNode"});
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
      popup.setActions(new String[] {"AddNode", "EditPage", "EditSelectedNode", "CopyNode", 
                                     "CutNode", "DeleteNode"});
       
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
  
  
  
}
