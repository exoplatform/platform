/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.customization.UIPageEditBar;
import org.exoplatform.portal.component.customization.UIPageForm;
import org.exoplatform.portal.component.customization.UIPageManagement;
import org.exoplatform.portal.component.customization.UIPageNavigationControlBar;
import org.exoplatform.portal.component.customization.UIPageNavigationForm;
import org.exoplatform.portal.component.customization.UIPageNodeForm;
import org.exoplatform.portal.component.customization.UIPageNodeSelector;
import org.exoplatform.portal.component.customization.UIPageTemplateOptions;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIContainer;
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
      UIRightClickPopupMenu uiMenu = event.getSource();
      UIPageNodeSelector uiPageNodeSelector = uiMenu.getAncestorOfType(UIPageNodeSelector.class);
      
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
      String uri  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      PortalRequestContext pcontext  = (PortalRequestContext)event.getRequestContext();
      UIRightClickPopupMenu uiPopupMenu = event.getSource();
      UIComponent uiParent = uiPopupMenu.getParent();
      UIPageNodeSelector uiPageNodeSelector = uiParent.getParent();
      uiPageNodeSelector.selectPageNodeByUri(uri);

      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();
      
      UIPortalApplication uiApp = Util.getUIPortal().getAncestorOfType(UIPortalApplication.class);
      UIControlWorkspace uiControl = uiApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiControl);
      
      PageNode node = uiPageNodeSelector.getSelectedPageNode();
      if(node == null) uiPageNodeSelector.loadSelectedNavigation();
      node = uiPageNodeSelector.getSelectedPageNode();
      if(node == null) return;
      
      UserPortalConfigService portalConfigService = uiPopupMenu.getApplicationComponent(UserPortalConfigService.class);
      Page page  = portalConfigService.getPage(node.getPageReference(), pcontext.getRemoteUser());
      UIPage uiPage  = null;
      if(page != null)  uiPage = Util.toUIPage(page, uiToolPanel);
      if(page == null || !uiPage.isModifiable()){
        Class [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class};      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        return;
      }
            
      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class) ;  
      uiToolPanel.setUIComponent(uiPage);
      
      if (Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
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
      // TODO Add Message to
//        UIApplication uiApplication = Util.getPortalRequestContext().getUIApplication() ;
//        uiApplication.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.notAvailable", null)) ;
//        
//        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApplication.getUIPopupMessages() );
      
      UIWorkspace uiWorkingWS = uiApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
      pcontext.setFullRender(true);
      
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
      UIPageManagement uiManagement = uiPageNodeSelector.getParent();

      PageNode selectedPageNode = uiPageNodeSelector.findPageNodeByUri(value);     
      if(selectedPageNode == null)   return;      
      PageNode pageNode = selectedPageNode.clone();
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
      UIPageNodeSelector uiPageNodeSelector =  popupMenu.getAncestorOfType(UIPageNodeSelector.class);
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
      if(afterReplacePattern == null) return;
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
  
  static public class EditNavigationActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      UIRightClickPopupMenu uiControlBar = event.getSource();
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;     

      UIPageNavigationForm uiNavigationForm = uiMaskWS.createUIComponent(UIPageNavigationForm.class, null, null);
      UIPageManagement uiPManagement = uiControlBar.getAncestorOfType(UIPageManagement.class);
      UIPageNodeSelector uiNavigationSelector = uiPManagement.findFirstComponentOfType(UIPageNodeSelector.class);
      PageNavigation nav = uiNavigationSelector.getSelectedNavigation();
      if(nav == null) {
        uiApp.addMessage(new ApplicationMessage("UIPageNavigationControlBar.msg.noEditablePageNavigation", new String[]{})) ;;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());  
        return ;
      }
      uiNavigationForm.setValues(nav);
      uiMaskWS.setUIComponent(uiNavigationForm);      
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
  
  static public class CreateNavigationActionListener extends EventListener<UIPageNodeSelector> {
    public void execute(Event<UIPageNodeSelector> event) throws Exception { 
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;     

      UIPageNavigationForm uiNavigationForm = uiMaskWS.createUIComponent(UIPageNavigationForm.class, null, null);
      uiMaskWS.setUIComponent(uiNavigationForm);      
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      
      
     /* PortalRequestContext prContext = Util.getPortalRequestContext();
      PageNavigation navigation = new PageNavigation();
      String userName = prContext.getRemoteUser();
      navigation.setOwnerType(PortalConfig.USER_TYPE);
      navigation.setOwnerId(userName);
      navigation.setCreator(userName);
      navigation.setModifier(userName);
      navigation.setModifiable(true);
      
      UserPortalConfigService dataService = event.getSource().getApplicationComponent(UserPortalConfigService.class);
      dataService.create(navigation);
      
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getParent();
      Util.getUIPortal().getNavigations().add(navigation);
      uiPageNodeSelector.loadNavigations();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector);*/
    }
  }
  
  static public class DeleteNavigationActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception { 
      UIRightClickPopupMenu uiPopup = event.getSource();
      UIPageNodeSelector pageNodeSelector = uiPopup.getAncestorOfType(UIPageNodeSelector.class);
      PageNavigation k = pageNodeSelector.getSelectedNavigation();
//      String abc = k.getId();
//      DataStorage storage = pageNodeSelector.getApplicationComponent(DataStorage.class);
      UserPortalConfigService configService = pageNodeSelector.getApplicationComponent(UserPortalConfigService.class);
//      pageNodeSelector.getNavigations().remove(k);
      
      boolean ppp = Util.getUIPortal().getNavigations().remove(k);
      
      List<PageNavigation> list = Util.getUIPortal().getNavigations();
      int i = 0;
      for( i = 0; i < list.size(); i ++) {
        if( list.get(i).getId().equals(k.getId())){
          break; 
        }
      }
      list.remove(i);
      System.out.println(Util.getUIPortal().getNavigations().contains(k) + " -- Size: " + Util.getUIPortal().getNavigations().size() + ppp);
      configService.remove(k);
      
      pageNodeSelector.loadNavigations();
      pageNodeSelector.selectNavigation(pageNodeSelector.getNavigations().get(0).getId());
      event.getRequestContext().addUIComponentToUpdateByAjax(pageNodeSelector.getAncestorOfType(UIPageManagement.class));      
    }
  }
  
  static public class SaveNavigationActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      UIComponent uiPopup = event.getSource();
      
      UIPageManagement uiManagement = uiPopup.getAncestorOfType(UIPageManagement.class);
      UIPageNavigationControlBar uiControlBar = uiManagement.getChild(UIPageNavigationControlBar.class);
      UIPageNodeSelector uiNodeSelector = uiManagement.getChild(UIPageNodeSelector.class);
      List<PageNavigation> navs = uiNodeSelector.getNavigations();
      if(navs == null || navs.size() < 1) {
        UIPortalApplication uiApp = uiManagement.getAncestorOfType(UIPortalApplication.class);
        uiApp.addMessage(new ApplicationMessage("UIPageNavigationControlBar.msg.noEditablePageNavigation", new String[]{})) ;;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());  
        return ;
      }
      
      uiControlBar.saveNavigation();

    }
  }
}
