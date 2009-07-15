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

import java.util.Calendar;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.bean.UIDataFeed;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(template = "system:/groovy/webui/core/UIVirtualList.gtmpl", events = { @EventConfig(listeners = UIVirtualList.LoadNextActionListener.class) })
public class UIVirtualList extends UIComponentDecorator {

  private final static String COMPONENT_GENERATE_ID = "component_generate_id";

  public UIVirtualList() {
    this.generateId = new Double(Math.random()).toString() + "_"
        + Calendar.getInstance().getTimeInMillis();
  }

  private String generateId;

  private int    pageSize = 1;

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public String getGenerateId() {
    return this.generateId;
  }

  public String event(String name, String beanId) throws Exception {
    UIComponent parent = this.getParent();
    return parent.event(name, beanId);
  }

  public void dataBind(PageList datasource) throws Exception {
    UIDataFeed dataFeed = this.getDataFeed();
    datasource.setPageSize(this.getPageSize());
    dataFeed.setDataSource(datasource);
  }

  public UIDataFeed getDataFeed() {
    try {
      return (UIDataFeed) this.uicomponent_;
    } catch (Exception e) {
      throw new NullPointerException("dataFeed doesn't attached");
    }
  }

  static public class LoadNextActionListener extends EventListener<UIVirtualList> {
    public void execute(Event<UIVirtualList> event) throws Exception {
      UIVirtualList virtualList = event.getSource();
      UIDataFeed dataFeed = virtualList.getDataFeed();
      WebuiRequestContext rContext = event.getRequestContext();
      dataFeed.feedNext();
      if (!dataFeed.hasNext()) {
        String generateId = rContext.getRequestParameter(COMPONENT_GENERATE_ID);
        rContext.getJavascriptManager().addJavascript("eXo.webui.UIVirtualList.loadFinished('"
            + generateId + "');");
      }
      rContext.addUIComponentToUpdateByAjax(dataFeed);
    }
  }
}
