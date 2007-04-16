/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv@exoplatform.com
 * Aug 9, 2006  
 */

@ComponentConfig(
    template =  "system:/groovy/webui/component/UIJSPageIterator.gtmpl",
    events = {
      @EventConfig(name = "ShowPage", listeners = UIJSPageIterator.ShowPageActionListener.class )
    }
)
//@TODO UIPageIterator
public class UIJSPageIterator extends UIComponent {
 
  private PageList pageList_ = PageList.EMPTY_LIST ;
  
  public UIJSPageIterator() throws Exception {
  }
  
  public void setPageList(PageList pageList) throws Exception { 
    pageList_ = pageList ; 
  }
  
  public int getAvailablePage() { return pageList_.getAvailablePage() ; } 
  
  public int getCurrentPage() { return  pageList_.getCurrentPage() ; }  
  
  public List getCurrentPageData() throws Exception { return  pageList_.currentPage() ; }  
  
  public int getAvailable() { return pageList_.getAvailable() ; }
  
  public int getFrom() { return pageList_.getFrom() ; }
  
  public int getTo() { return pageList_ .getTo() ; }
  
  
  public Object getObjectInPage(int index) throws Exception {
    return pageList_.currentPage().get(index) ;
  }
  
  public void setCurrentPage(int page) throws Exception {
    pageList_.getPage(page) ;
  }
  
  static  public class ShowPageActionListener extends EventListener<UIJSPageIterator> {
    public void execute(Event<UIJSPageIterator> event) throws Exception {
      UIJSPageIterator uiIterator = event.getSource() ;
      String page = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiIterator.setCurrentPage(Integer.parseInt(page)) ; 
      event.getRequestContext().addUIComponentToUpdateByAjax(event.getSource().getParent());
    }
  }
  
}
