/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.dashboard.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.form.UIForm;

@ComponentConfigs({
  @ComponentConfig(
      template = "app:/groovy/dashboard/webui/component/UIDashboardContainer.gtmpl",
      lifecycle = UIFormLifecycle.class
  )
})
public class UIDashboardContainer extends UIForm {
  public final static int MAX_COLUMN = 4;
  
//  private List<List<UIGadget>> columns;
  
  private List<UIContainer> columns ;
  
  public UIDashboardContainer() throws Exception {
    columns = new ArrayList<UIContainer>();
    columns.add(addChild(UIContainer.class, null, "UIColumn-1"));
    columns.add(addChild(UIContainer.class, null, "UIColumn-2"));
    columns.add(addChild(UIContainer.class, null, "UIColumn-3"));
    columns.add(addChild(UIContainer.class, null, "UIColumn-4"));
  }
  
  public void addUIGadget(final UIGadget gadget, final int col, final int row) throws Exception {
    UIContainer uiContainer = getColumn(col);
    List<UIComponent> children = uiContainer.getChildren();
    if (uiContainer == null || row < 0 || row > children.size()) {
      return;
    }
    gadget.setParent(uiContainer);
    children.add(row, gadget);
  }
  
  public UIGadget getUIGadget(final int col, final int row) throws Exception {
    UIContainer uiContainer = getColumn(col);
    if (uiContainer == null || row < 0 || row >= uiContainer.getChildren().size()) {
      return null;
    }
    return uiContainer.getChild(row);
  }
  
  public UIGadget getUIGadget(final String gadgetId) throws Exception {
    for (int iCol = 0; iCol < getRenderedColumnsCount(); iCol++) {
      for (int iRow = 0; iRow < columns.get(iCol).getChildren().size(); iRow++) {
        UIGadget gadget = (UIGadget) columns.get(iCol).getChild(iRow);
        if (gadgetId.equals(gadget.getApplicationInstanceUniqueId())) {
          return gadget;
        }
      }
    }
    return null;
  }
  
  public UIGadget removeUIGadget(final String gadgetId) throws Exception {
    UIGadget gadget = getUIGadget(gadgetId);
    if(gadget != null) {
      UIContainer uiContainer = gadget.getParent();
      gadget.setParent(null);
      uiContainer.getChildren().remove(gadget);
    }
    return gadget;
  }
  
  public UIGadget removeUIGadget(final int col, final int row) throws Exception {
    UIGadget gadget = getUIGadget(col, row);
    if(gadget != null) {
      removeUIGadget(gadget.getId());
    }
    return gadget;
  }
  
  public void moveUIGadget(final String gadgetId, final int col, int row) throws Exception {
    UIGadget gadget = removeUIGadget(gadgetId);
    if (gadget == null) {
      return;
    }
    addUIGadget(gadget, col, row);
  }
  
  public List<UIContainer> getColumns() throws Exception {
    if (columns == null) {
      columns = new ArrayList<UIContainer>();
      for (int i = 0; i < MAX_COLUMN; i++) {
        UIContainer uiContainer = this.addChild(UIContainer.class, null, "Column"+(i+1));
        if(i==0) {
          uiContainer.setRendered(true);
        } else {
          uiContainer.setRendered(false);
        }
        columns.add(uiContainer);
      }
    }
    return columns;
  }
  
  public int getRenderedColumnsCount() throws Exception {
    if(columns == null) {
      columns = getColumns();
    }
    int count = 0;
    for (int i = 0; i < columns.size(); i++) {
      if(columns.get(i).isRendered()) { count++; }
    }
    return count;
  }
  
  public UIContainer getColumn(final int col) throws Exception {
    if (col < 0 || col > getRenderedColumnsCount()) { return null; }
    return columns.get(col);
  }
    
  public boolean hasUIGadget() throws Exception {
    boolean flag = false;
    UIGadget gadget = findFirstComponentOfType(UIGadget.class);
    if(gadget != null) flag = true;
    return flag;    
  }
  
  public UIDashboardContainer setColumns(final int num) throws Exception {
    if (num < 1 || num > MAX_COLUMN) {
      return null;
    }
    if (columns == null || columns.size() == 0) {
     columns = this.getColumns();
    }
    
    int colSize = 0;
    for (int i = 0; i < columns.size(); i++) {
      if(columns.get(i).isRendered()) { colSize++; }
    }
    
    if (num < colSize) {
      do {
        columns.get(--colSize).removeChild(UIGadget.class);
        columns.get(colSize).setRendered(false);
      } while (num < colSize);
    } else {
      if (num > colSize) {
        do {
          columns.get(colSize).setRendered(true);
          colSize++;
        } while (num > colSize);
      }
    }
    return this;
  }
  
  public void renderUIGadget(final int col, final int row) throws Exception {
    UIGadget gadget = getUIGadget(col, row);
    if (gadget == null) {
      return;
    }
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    gadget.processRender(context);
  }
}
