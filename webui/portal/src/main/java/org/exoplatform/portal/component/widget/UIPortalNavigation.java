/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.widget;

import java.util.List;

import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.view.event.PageNodeEvent;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
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

  private PageNode selectedNode_ ;
  private Object selectedParent_ ; 

  public UIComponent getViewModeUIComponent() { return null; }

  public List<PageNavigation> getNavigations() {
    return Util.getUIPortal().getNavigations(); 
  }

  public Object getSelectedParent() { return selectedParent_ ; }
  public PageNode getSelectedPageNode() {
    if(selectedNode_ != null)  return selectedNode_;
    selectedNode_ = Util.getUIPortal().getSelectedNode();    
    return selectedNode_ ; 
  }  

  public void setSelectedPageNode(String uri) {
    selectedNode_ = null ;
    selectedParent_ = null ;
    if (uri == null)  return;
    List <PageNavigation> pageNavs = getNavigations() ;
    for(PageNavigation pageNav : pageNavs) {
      findPageNode(pageNav, pageNav.getNodes(), uri);
    }   
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
      setSelectedPageNode(uiPortal.getSelectedNode().getUri()) ;      
    }
    super.processRender(context);
  }

  private void findPageNode(Object parent, List<PageNode> children, String uri) {
    for(PageNode node : children) {
      if(node.getUri().equals(uri)) {
        selectedNode_ = node ;
        selectedParent_ = parent ;
        return;
      }
      if(node.getChildren() == null) continue;
      findPageNode(node, node.getChildren(), uri);
    }
  }

  static  public class SelectNodeActionListener extends EventListener<UIPortalNavigation> {
    public void execute(Event<UIPortalNavigation> event) throws Exception {      
      UIPortalNavigation uiNavigation = event.getSource();
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      uiNavigation.setSelectedPageNode(uri) ;
      
      UIPortal uiPortal = Util.getUIPortal();      
      PageNodeEvent<UIPortal> pnevent ;
      pnevent = new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;      
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
    }
  }

  static  public class UpLevelActionListener extends EventListener<UIPortalNavigation> {
    public void execute(Event<UIPortalNavigation> event) throws Exception {
      UIPortalNavigation uiNavigation = event.getSource();      
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      uiNavigation.setSelectedPageNode(uri) ;
      
      UIPortal uiPortal = Util.getUIPortal();
      PageNodeEvent<UIPortal> pnevent = 
        new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;      
    }
  }

}
