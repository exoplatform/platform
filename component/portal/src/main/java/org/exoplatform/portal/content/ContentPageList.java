/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.content.model.ContentItem;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Aug 7, 2006  
 */
public class ContentPageList<T extends ContentItem> extends PageList {
  
  private List<T> items_ ;
  
  public ContentPageList(List<T> items){
    super(10);
    items_ = items;
    setAvailablePage(items.size());
  }
  
  public List<T> getItems() { return items_ ; }
  
  public void populateCurrentPage(int page) throws Exception   {
    int idx = (page-1)*getPageSize();
    List<ContentItem> list = new ArrayList<ContentItem>(page);
    while(idx < Math.min(page*getPageSize(), getAvailable())){
      list.add(items_.get(idx));
      idx++;
    }
    currentListPage_ = list;
  }
  
  public List<?> getAll(){ return null; }
  
}
