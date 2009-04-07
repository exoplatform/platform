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
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
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
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
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
      UIRightClickPopupMenu uiPopupMenu = event.getSource();
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      PortalRequestContext pcontext  = (PortalRequestContext)event.getRequestContext();
      UIPageNodeSelector uiPageNodeSelector = uiPopupMenu.getAncestorOfType(UIPageNodeSelector.class) ;
      PageNavigation currentNav = uiPageNodeSelector.getSelectedNavigation();
      PageNode selectNode = PageNavigationUtils.searchPageNodeByUri(currentNav, uri);
      
      uiPageNodeSelector.selectPageNodeByUri(uri);
      UIPortalApplication uiPortalApp = Util.getUIPortalApplication() ;
      UIPortalToolPanel uiToolPanel = uiPortalApp.findFirstComponentOfType(UIPortalToolPanel.class) ;
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      
      UIControlWorkspace uiControl = uiPortalApp.getChildById(UIPortalApplication.UI_CONTROL_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiControl);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;   
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
      uiToolPanel.setShowMaskLayer(true) ;
      pcontext.setFullRender(true);
      UserPortalConfigService portalConfigService = uiPopupMenu.getApplicationComponent(UserPortalConfigService.class);
      Page page = null;
      if(selectNode.getPageReference() != null) page = portalConfigService.getPage(selectNode.getPageReference(), pcontext.getRemoteUser());
      if(page == null) {
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        uiToolPanel.setUIComponent(null) ;
        if(selectNode.getPageReference() != null && portalConfigService.getPage(selectNode.getPageReference()) != null) {
          uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.edit.NotEditPage", new String[]{})) ;
        } else {
          uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.PageNotExist", new String[]{})) ;
        }
        return;
      }
      UIPageEditBar uiPageEditBar = uiManagement.getChild(UIPageEditBar.class);
      UIPage uiPage = uiPageEditBar.getUIPage() ;
      if(uiPage == null || !uiPage.getPageId().equals(page.getPageId())) uiPage = Util.toUIPage(page, uiToolPanel); 
      uiPortalApp.findFirstComponentOfType(UIPageBody.class).setUIComponent(null);
      
      if(!page.isModifiable()){
        uiToolPanel.setUIComponent(uiPage) ;
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);        
        uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.Invalid-editPermission", null)) ;        
        return;
      }
      
      uiPageEditBar.setUIPage(uiPage); 
      uiPageEditBar.showUIPage();

      if(!Page.DESKTOP_PAGE.equals(uiPage.getFactoryId()))  {
        Class<?> [] childrenToRender = {UIPageEditBar.class, UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }
      
      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;      
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
      
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      PageNavigation selectedNav = uiPageNodeSelector.getSelectedNavigation();
      Object obj = PageNavigationUtils.searchParentNode(selectedNav, uri);
      PageNode selectedNode = PageNavigationUtils.searchPageNodeByUri(selectedNav, uri);
      String pageId = selectedNode.getPageReference();
      UserPortalConfigService service = parent.getApplicationComponent(UserPortalConfigService.class);
      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UIPortalApplication uiPortalApp = parent.getAncestorOfType(UIPortalApplication.class);
      Page node = (pageId != null) ? service.getPage(pageId) : null ;
      if(node != null) {
        UserACL userACL = parent.getApplicationComponent(UserACL.class) ;
        if(!userACL.hasPermission(node, pcontext.getRemoteUser())) {
          uiPortalApp.addMessage(new ApplicationMessage("UIPageBrowser.msg.UserNotPermission", new String[]{pageId}, 1)) ;;
          return;
        }
      } 
      
      UIPageNodeForm uiNodeForm = uiMaskWS.createUIComponent(UIPageNodeForm.class, null, null);
      uiMaskWS.setUIComponent(uiNodeForm);     
      uiNodeForm.setValues(selectedNode);
      uiNodeForm.setSelectedParent(obj);    
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
      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel() ;
      uiToolPanel.setUIComponent(null) ;
      UIWorkingWorkspace uiWorkspace = uiToolPanel.getAncestorOfType(UIWorkingWorkspace.class) ;
      pcontext.setFullRender(true) ;
      pcontext.addUIComponentToUpdateByAjax(uiWorkspace);
      if(pageNodes[0] == null) {
        nav.getNodes().remove(pageNodes[1]);
        return;
      }
      pageNodes[0].getChildren().remove(pageNodes[1]);
      uiPageNodeSelector.selectPageNodeByUri(pageNodes[0].getUri());
      if(pageNodes[0].getPageReference() != null) {
        UIPage uiPage = Util.toUIPage(pageNodes[0], uiToolPanel) ;
        uiToolPanel.setUIComponent(uiPage) ;
      }
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
                                                 "CopyNode", "CloneNode", "CutNode", "PasteNode", "DeleteNode", "MoveUp", "MoveDown"});
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
  
  static public class CloneNodeActionListener extends UIPageNodeActionListener.CopyNodeActionListener {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      super.execute(event);
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getAncestorOfType(UIPageNodeSelector.class);
      uiPageNodeSelector.getCopyNode().setCloneNode(true);
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
      PageNavigation targetNav = uiPageNodeSelector.getSelectedNavigation();
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
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector);
      uiPageNodeSelector.setCopyNode(null);
      UITree uitree = uiPageNodeSelector.getChild(UITree.class);
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
      uiPageNodeSelector.selectPageNodeByUri(targetNode.getUri());

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