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
package org.exoplatform.webui.core;

import java.lang.reflect.Method;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.util.ReflectionUtil;
import org.exoplatform.webui.bean.UIDataFeed;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL * A grid element (represented by an HTML
 * table) that can be paginated with a UIPageIterator
 * 
 * @see UIPageIterator
 */
@ComponentConfig(template = "system:/groovy/webui/core/UIRepeater.gtmpl")
public class UIRepeater extends UIDataFeed {

  private PageList datasource = PageList.EMPTY_LIST;

  public UIRepeater() throws Exception {
    super();
  }
  
  /**
   * An array of String representing the fields in each bean
   */
  protected String[] beanField_;

  public UIRepeater configure(String[] beanField) {    
    this.beanField_ = beanField;
    return this;
  }

  public String[] getBeanFields() {
    return beanField_;
  }

  public List<?> getBeans() throws Exception {
    return datasource.currentPage() ;
  }


  public Object getFieldValue(Object bean, String field) throws Exception {
    Method method = ReflectionUtil.getGetBindingMethod(bean, field);
    return method.invoke(bean, ReflectionUtil.EMPTY_ARGS);
  }

  @Override
  public void feedNext() throws Exception {
    int page = datasource.getCurrentPage();
    page++;
    if (page <= datasource.getAvailablePage()) {
      datasource.getPage(page);
    }
  }

  @Override
  public boolean hasNext() {
    int page = datasource.getCurrentPage();
    if (page >= datasource.getAvailablePage()) {
      return false;
    }
    return true;
  }

  @Override
  public void setDataSource(PageList datasource) throws Exception {
    this.datasource = datasource;
    datasource.getPage(1);
  }
}
