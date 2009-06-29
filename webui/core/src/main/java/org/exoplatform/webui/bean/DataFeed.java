/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.webui.bean;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPageIterator;

/**
 * Created by The eXo Platform SAS Author : liem.nguyen ncliam@gmail.com Jun 26,
 * 2009
 */
public abstract class DataFeed extends UIComponent {

  protected UIPageIterator uiIterator_;

  public DataFeed() throws Exception {
    uiIterator_ = createUIComponent(UIPageIterator.class, null, null);
    uiIterator_.setParent(this);
    uiIterator_.setRendered(false);
  }

  public void feedNext() throws Exception {
    int page = uiIterator_.getCurrentPage();    
    page++;
    if (page <= uiIterator_.getAvailablePage()) {
      uiIterator_.setCurrentPage(page);      
    }
  }
  
  public boolean hasNext() {
    int page = uiIterator_.getCurrentPage();
    if (page>=uiIterator_.getAvailablePage()) {
      return false;
    }
    return true;
  }

  public void setDataSource(PageList datasource) throws Exception {
    uiIterator_.setPageList(datasource);
    uiIterator_.setCurrentPage(1);
  }
}
