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
import org.exoplatform.portal.webui.application.UIGadgetLifecycle;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.form.UIForm;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Dinh Tan
 *          tan.pham@exoplatform.com
 * Apr 24, 2008  
 */
@ComponentConfigs({
  @ComponentConfig(
      template = "app:/groovy/dashboard/webui/component/UIDashboardContainer.gtmpl",
      lifecycle = UIFormLifecycle.class
  ),
  @ComponentConfig(
      type = UIGadget.class ,
      id="UIGadget",
      lifecycle = UIGadgetLifecycle.class,
      template = "system:/groovy/portal/webui/application/UIGadget.gtmpl"
  )
})
public class UIDashboardContainer extends UIForm {
  
  private List<List<UIGadget>> columns = null;
  
  public UIDashboardContainer() throws Exception{
    columns = new ArrayList<List<UIGadget>>();
    columns.add(new ArrayList<UIGadget>());
  }
  
  private void addUIGadget(UIGadget gadget, int col) throws Exception{
    List<UIGadget> column = getColumn(col);
    if(column==null) return;
    column.add(gadget);
  }
  
  private void addUIGadget(UIGadget gadget, int col, int row) throws Exception{
    List<UIGadget> column = getColumn(col);
    if(column==null || row<0 || row>column.size()-1) return;
    column.add(row, gadget);
  }
  
  public void addUIGadget(String url, int col) throws Exception{
    UIGadget gadget = addChild(UIGadget.class, "UIGadget", null);
    addUIGadget(gadget, col);
  }
  
  public void addUIGadget(String url, int col, int row) throws Exception{
    UIGadget gadget = addChild(UIGadget.class, "UIGadget", null);
    addUIGadget(gadget, col, row);
  }
  
  public UIGadget getUIGadget(int col, int row) throws Exception{
    List<UIGadget> column = getColumn(col);
    if(column==null || row<0 || row>column.size()-1) return null;
    return column.get(row);
  }
  
  public UIGadget getUIGadget(String gadgetName) throws Exception{
    if(columns==null) {
      columns = new ArrayList<List<UIGadget>>();
      columns.add(new ArrayList<UIGadget>());
    }
    for(int iCol=0; iCol<columns.size(); iCol++)
      for(int iRow=0; iRow<columns.get(iCol).size(); iRow++)
         if(gadgetName.equals(columns.get(iCol).get(iRow).getName()))
           return columns.get(iCol).get(iRow);
    return null;
  }
  
  public UIGadget removeUIGadget(int col, int row) throws Exception{
    List<UIGadget> column = getColumn(col);
    if(column==null || row<0 || row>column.size()-1) return null;
    UIGadget temp = column.get(row);
    column.remove(row);
    return temp;
  }
  
  public UIGadget removeUIGadget(String gadgetName) throws Exception{
    if(columns==null) {
      columns = new ArrayList<List<UIGadget>>();
      columns.add(new ArrayList<UIGadget>());
    }
    for(int iCol=0; iCol<columns.size(); iCol++){
      List<UIGadget> column = columns.get(iCol);
      for(int iRow=0; iRow<column.size(); iRow++)
        if(gadgetName.equals(column.get(iRow).getName())){
          UIGadget temp = column.get(iRow);
          column.remove(iRow);
          return temp;
        }
    }
    return null;
  }
  
  public void moveUIGadget(UIGadget gadget, int col, int row) throws Exception{
    this.columns = getColumns();
    int srcCol = -1, srcRow= -1;
    for(int iCol=0; iCol<columns.size(); iCol++)
      for(int iRow=0; iCol<columns.get(iCol).size(); iRow++)
        if(gadget.getName().equals(columns.get(iCol).get(iRow).getName())){
          srcCol = iCol;
          srcRow = iRow;
          break;
        }
    if(srcCol<0 || srcRow<0) return;
    addUIGadget(gadget, col, row);
    columns.get(srcCol).remove(srcRow);
  }
  
  public void moveUIGadget(String gadgetName , int col, int row) throws Exception{
    this.columns = getColumns();
    int srcCol = -1, srcRow= -1;
    UIGadget gadget = null;
    for(int iCol=0; iCol<columns.size(); iCol++)
      for(int iRow=0; iCol<columns.get(iCol).size(); iRow++)
        if(gadgetName.equals(columns.get(iCol).get(iRow).getName())){
          srcCol = iCol;
          srcRow = iRow;
          gadget = columns.get(iCol).get(iRow);
          break;
        }
    if(srcCol<0 || srcRow<0) return;
    addUIGadget(gadget, col, row);
    columns.get(srcCol).remove(srcRow);
  }
  
  public List<List<UIGadget>> getColumns() throws Exception{
    if(columns==null) {
      columns = new ArrayList<List<UIGadget>>();
      columns.add(new ArrayList<UIGadget>());
    }
    return columns;
  }
  
  public List<UIGadget> getColumn(int col) throws Exception{
    if(columns==null) {
      columns = new ArrayList<List<UIGadget>>();
      columns.add(new ArrayList<UIGadget>());
    }
    if(col<0 || col>columns.size()) return null;
    return columns.get(col);
  }
    
  public boolean hasUIGadget() throws Exception{
    boolean flag = false;
    if(columns==null) {
      columns = new ArrayList<List<UIGadget>>();
      columns.add(new ArrayList<UIGadget>());
    }
    for(int iCol=0; iCol<columns.size(); iCol++)
      if(!columns.get(iCol).isEmpty()){
        flag = true;
        break;
      }
    return flag;    
  }
  
  public UIDashboardContainer setColumns(int num) throws Exception{
    if(num<1 || num>4) return null;
    columns = new ArrayList<List<UIGadget>>();
    for(int i=0; i<num; i++){
      columns.add(new ArrayList<UIGadget>());
    }
    return this;
  }
  
  public void renderUIGadget(int col, int row) throws Exception{
    UIGadget gadget = getUIGadget(col, row);
    if(gadget==null) return;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    gadget.processRender(context);
  }
}
