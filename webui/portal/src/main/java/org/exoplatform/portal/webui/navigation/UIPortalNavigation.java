/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.WindowState;

import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.page.UIPageBody;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh 
 *          minhdv81@yahoo.com
 * Jul 12, 2006  
 */

public class UIPortalNavigation extends UIComponent {

  protected PageNode selectedNode_ ;
  protected Object selectedParent_ ; 

  public UIComponent getViewModeUIComponent() { return null; }

  public List<PageNavigation> getNavigations() throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    List<PageNavigation> result = new ArrayList<PageNavigation>();
    for(PageNavigation nav: Util.getUIPortal().getNavigations()){
      result.add(PageNavigationUtils.filter(nav, context.getRemoteUser() ));
    }
    return result;
  }
  
  public PageNavigation getSelectedNavigation() {
    PageNavigation nav = Util.getUIPortal().getSelectedNavigation();
    if(nav != null) return nav;
    if(Util.getUIPortal().getNavigations().size() < 1) return null;
    return Util.getUIPortal().getNavigations().get(0);
  }

  public Object getSelectedParent() { return selectedParent_ ; }
  public PageNode getSelectedPageNode() {
    if(selectedNode_ != null)  return selectedNode_;
    selectedNode_ = Util.getUIPortal().getSelectedNode();    
    return selectedNode_ ; 
  }  
  
  public boolean isSelectedNode(PageNode node){
    if(selectedNode_ != null && node.getUri().equals(selectedNode_.getUri())) return true;
    if(selectedParent_ == null || selectedParent_ instanceof PageNavigation) return false; 
    PageNode pageNode = (PageNode)selectedParent_;
    return node.getUri().equals(pageNode.getUri());
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    UIPortal uiPortal = Util.getUIPortal(); 
    if(uiPortal.getSelectedNode() != selectedNode_){
      setSelectedPageNode(uiPortal.getSelectedNode()) ;      
    }
    super.processRender(context);
  }
  
  private void setSelectedPageNode(PageNode selectedNode) throws Exception {
    selectedNode_ = selectedNode;
    selectedParent_ = null;
    String seletctUri = selectedNode.getUri();
    int index = seletctUri.lastIndexOf("/");
    String parentUri = null;
    if(index > 0) parentUri = seletctUri.substring(0, seletctUri.lastIndexOf("/"));
    List <PageNavigation> pageNavs = getNavigations() ;
    for(PageNavigation pageNav : pageNavs) {
      if( PageNavigationUtils.searchPageNodeByUri(pageNav, selectedNode.getUri()) != null){
        if(parentUri == null || parentUri.length() < 1 ) selectedParent_ = pageNav;
        else selectedParent_ = PageNavigationUtils.searchPageNodeByUri(pageNav, parentUri);
        break;
      }
    } 
  }

  static  public class SelectNodeActionListener extends EventListener<UIPortalNavigation> {
    public void execute(Event<UIPortalNavigation> event) throws Exception {      
      UIPortalNavigation uiNavigation = event.getSource();
      UIPortal uiPortal = Util.getUIPortal();
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      int index = uri.lastIndexOf("::");
      String id = uri.substring(index +2);
      PageNavigation selectNav = null;
      if(index <= 0) {selectNav = uiPortal.getSelectedNavigation();}
      else {
        String navId = uri.substring(0, index);
        selectNav = uiPortal.getPageNavigation(navId);
      }
      PageNode selectNode = PageNavigationUtils.searchPageNodeByUri(selectNav, id);
      uiNavigation.selectedNode_ = selectNode;
      String parentUri = null;
      index = uri.lastIndexOf("/");
      if(index > 0) parentUri = uri.substring(0, index);
      if(parentUri == null || parentUri.length() < 1) uiNavigation.selectedParent_ = selectNav;
      else uiNavigation.selectedParent_ = PageNavigationUtils.searchPageNodeByUri(selectNav, parentUri);
      UIPageBody uiPageBody = uiPortal.findFirstComponentOfType(UIPageBody.class);
      if(uiPageBody != null) {
        if(uiPageBody.getMaximizedUIComponent() != null) {
          UIPortlet currentPortlet =  (UIPortlet) uiPageBody.getMaximizedUIComponent();
          currentPortlet.setCurrentWindowState(WindowState.NORMAL);
          uiPageBody.setMaximizedUIComponent(null);
        }
      }
      PageNodeEvent<UIPortal> pnevent ;
      pnevent = new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;      
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
    }
  }

  static  public class UpLevelActionListener extends EventListener<UIPortalNavigation> {
    public void execute(Event<UIPortalNavigation> event) throws Exception {
      UIPortalNavigation uiNavigation = event.getSource();      
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID); 
      UIPortal uiPortal = Util.getUIPortal();
      
      int index = uri.lastIndexOf("::");
      String id = uri.substring(index +2);
      PageNavigation selectNav = null;
      if(index <= 0) {selectNav = uiPortal.getSelectedNavigation();}
      else {
        String navId = uri.substring(0, index);
        selectNav = uiPortal.getPageNavigation(navId);
      }
      PageNode selectNode = PageNavigationUtils.searchPageNodeByUri(selectNav, id);
      uiNavigation.selectedNode_ = selectNode;
      String parentUri = null;
      index = uri.lastIndexOf("/");
      if(index > 0) parentUri = uri.substring(0, index);
      if(parentUri == null || parentUri.length() < 1) uiNavigation.selectedParent_ = selectNav;
      else uiNavigation.selectedParent_ = PageNavigationUtils.searchPageNodeByUri(selectNav, parentUri);
      UIPageBody uiPageBody = uiPortal.findFirstComponentOfType(UIPageBody.class);
      if(uiPageBody != null) {
        if(uiPageBody.getMaximizedUIComponent() != null) {
          UIPortlet currentPortlet =  (UIPortlet) uiPageBody.getMaximizedUIComponent();
          currentPortlet.setCurrentWindowState(WindowState.NORMAL);
          uiPageBody.setMaximizedUIComponent(null);
        }
      }
     
      PageNodeEvent<UIPortal> pnevent = 
        new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;      
    }
  }

}
