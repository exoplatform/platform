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

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.bean.DataFeed;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(template = "system:/groovy/webui/core/UIVirtualList.gtmpl",
                 events = {@EventConfig(listeners = UIVirtualList.LoadNextActionListener.class)})
public class UIVirtualList extends UIContainer {

  private int pageSize = 1;
  
  private int height;

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
  
  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }
  
  public String event(String name, String beanId) throws Exception {
    UIComponent parent = this.getParent();
    return parent.event(name, beanId);
  }
  
  public void setDataFeed(DataFeed dataFeed) {
    DataFeed exist = getDataFeed();
    if (exist != null) {
      this.removeChildById(exist.getId());
    }
    this.addChild(dataFeed);
  }
  
  public void attachDataSource(PageList datasource) throws Exception {
    DataFeed dataFeed = this.getDataFeed();
    datasource.setPageSize(this.getPageSize());
    dataFeed.setDataSource(datasource);
  }
  
  public DataFeed getDataFeed() {
      try {
        return this.getChild(DataFeed.class);
      } catch (Exception e) {
        throw new NullPointerException("dataFeed doesn't attached");
      }
  }
  
  static public class LoadNextActionListener extends EventListener<UIVirtualList> {
    public void execute(Event<UIVirtualList> event) throws Exception {
      UIVirtualList virtualList = event.getSource();
      DataFeed dataFeed = virtualList.getDataFeed();
      WebuiRequestContext rContext = event.getRequestContext();
      dataFeed.feedNext();
      if (!dataFeed.hasNext()) {
        rContext.getJavascriptManager().addJavascript("eXo.webui.UIVirtualList.loadFinished();");
      }
      rContext.addUIComponentToUpdateByAjax(dataFeed);      
    }
  }
}