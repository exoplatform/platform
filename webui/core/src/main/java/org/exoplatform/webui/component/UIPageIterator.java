package org.exoplatform.webui.component;

import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
    template = "system:/groovy/webui/component/UIPageIterator.gtmpl",
    events = @EventConfig(listeners = UIPageIterator.ShowPageActionListener.class )    
)

public class UIPageIterator extends UIComponent {
  
	private PageList pageList_ = PageList.EMPTY_LIST ;
	
	public UIPageIterator() throws Exception {
	}
	
	public void setPageList(PageList pageList) { 
    pageList_ = pageList ;
  } 
  
  public PageList getPageList() { return pageList_; }
  
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
  
  @SuppressWarnings("unused")
  static  public class ShowPageActionListener extends EventListener<UIPageIterator> {
    public void execute(Event<UIPageIterator> event) throws Exception {
      UIPageIterator uiPageIterator = event.getSource() ;
      int page = Integer.parseInt(event.getRequestContext().getRequestParameter(OBJECTID)) ;
      uiPageIterator.setCurrentPage(page) ;
      if(uiPageIterator.getParent() == null) return ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageIterator.getParent());
    }
  }
  
}
