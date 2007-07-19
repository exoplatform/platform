/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.content.webui.component;

import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.content.ContentDAO;
import org.exoplatform.portal.content.model.ContentItem;
import org.exoplatform.portal.content.model.ContentNode;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UIBreadcumbs.LocalPath;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh  
 *          minhdv@exoplatform.com
 * Jul 6, 2006  
 */
@ComponentConfig(
  template =  "app:/groovy/content/webui/component/UIDetailContent.gtmpl",
  events = {
      @EventConfig(listeners = UIDetailContent.RefreshActionListener.class ),
      @EventConfig(listeners = UIDetailContent.SelectPathActionListener.class )
  }
)
public class UIDetailContent extends UIContainer {
  
  private UIPageIterator uiIterator_ ;
  private ContentNode node_;
    
  public UIDetailContent() throws Exception {
    //TODO: Tung.Pham added
    //-----------------------------
    UIBreadcumbs uiBreadcumbs = addChild(UIBreadcumbs.class, null, "ContentBreadcumbs") ;
    uiBreadcumbs.setBreadcumbsStyle("UIExplorerHistoryPath") ;
    //-----------------------------
    uiIterator_ = createUIComponent(UIPageIterator.class, null, null) ;
    addChild(uiIterator_);
  }
  
  public UIPageIterator  getUIPageIterator() {  return uiIterator_ ; }
  
  public void setContentNode(ContentNode node) {
    node_ = node;
    refresh(false);
  }
  
  void refresh(boolean removeCached) {    
    ContentDAO service = (ContentDAO) PortalContainer.getComponent(ContentDAO.class) ;
    try{
      if(removeCached) service.removeCache(node_.getId());
      PageList pageList = service.getContentData(node_);
      uiIterator_.setPageList(pageList);
    }catch (Exception e) {
      UIApplication uiApp = getAncestorOfType(UIApplication.class);
      uiApp.addMessage(new ApplicationMessage(e.getMessage(), null));      
    }
  }
  
  @SuppressWarnings("unchecked")
  public List<ContentItem> getListItems() throws Exception {
    return uiIterator_.getCurrentPageData() ; 
  }  
  
  public ContentNode getContentNode() { return node_ ; }
  
  static  public class RefreshActionListener extends EventListener<UIDetailContent> {
    public void execute(Event<UIDetailContent> event) throws Exception {
      UIDetailContent uiDetail = event.getSource();
      uiDetail.refresh(true);
    }
  }
  
  //TODO: Tung.Pham added
  static  public class SelectPathActionListener extends EventListener<UIBreadcumbs> {
    public void execute(Event<UIBreadcumbs> event) throws Exception {
      UIBreadcumbs uiBreadcumbs = event.getSource();
      UIContentPortlet uiContentPortlet = uiBreadcumbs.getAncestorOfType(UIContentPortlet.class) ;
      UIContentNavigation uiNavigation = uiContentPortlet.getChild(UIContentNavigation.class) ;
      LocalPath localPath = uiBreadcumbs.getSelectLocalPath() ;
      if(localPath != null) {
        String selectedNodeId = localPath.getId() ;
        uiNavigation.setSelectedNode(selectedNodeId) ;
      } else {
        uiNavigation.setSelectedNode(null) ;
      }
    }
  }

  
}
