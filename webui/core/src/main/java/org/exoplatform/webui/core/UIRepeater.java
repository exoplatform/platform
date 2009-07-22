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
public class UIRepeater extends UIComponent implements UIDataFeed {

  private PageList datasource = PageList.EMPTY_LIST;

  public UIRepeater() throws Exception {
    super();
  }
  
  /**
   * The bean field that holds the id of this bean
   */
  protected  String beanIdField_ ;
  
  /**
   * An array of String representing the fields in each bean
   */
  protected String[] beanField_;
  /**
   * An array of String representing the actions on each bean
   */
  protected  String[] action_ ;
  
  protected  String label_ ;

  public UIRepeater configure(String beanIdField, String[] beanField, String[] action) {
    this.beanIdField_ =  beanIdField ;
    this.beanField_ =  beanField ;
    this.action_ = action ;
    return this ;
  }
  
  public String getBeanIdField()  { return beanIdField_ ; }

  public String[] getBeanFields() {
    return beanField_;
  }
  
  public String[]  getBeanActions() { return action_ ; }

  public List<?> getBeans() throws Exception {
    return datasource.currentPage() ;
  }

  public String getLabel() { return label_ ; }
  public void setLabel(String label) { label_ = label ; }

  public Object getFieldValue(Object bean, String field) throws Exception {
    Method method = ReflectionUtil.getGetBindingMethod(bean, field);
    return method.invoke(bean, ReflectionUtil.EMPTY_ARGS);
  }
  
  public void feedNext() throws Exception {
    int page = datasource.getCurrentPage();
    page++;
    if (page <= datasource.getAvailablePage()) {
      datasource.getPage(page);
    }
  }
  
  public boolean hasNext() {
    int page = datasource.getCurrentPage();
    if (page >= datasource.getAvailablePage()) {
      return false;
    }
    return true;
  }
  
  public void setDataSource(PageList datasource) throws Exception {
    this.datasource = datasource;
    datasource.getPage(1);
  }
  
  public PageList getDataSource() {
    return this.datasource;  
  }
  
  public String getUniqueId() {
    UIVirtualList parent = this.getParent(); 
    return parent.getGenerateId();
  }
}
