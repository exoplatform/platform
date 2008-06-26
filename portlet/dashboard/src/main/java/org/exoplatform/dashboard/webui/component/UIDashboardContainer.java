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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.layout.PortalLayoutService;
import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

@ComponentConfigs({
  @ComponentConfig(
      template = "app:/groovy/dashboard/webui/component/UIDashboardContainer.gtmpl",
      lifecycle = UIFormLifecycle.class,
      initParams = @ParamConfig(
          name = "ContainerConfigs",
          value = "app:/WEB-INF/conf/uiconf/dashboard/webui/container/ContainerConfig.groovy"
      )
  )
})
public class UIDashboardContainer extends org.exoplatform.webui.core.UIContainer {
  
  public static final int MAX_COLUMN = 4;  
  final static public String COLUMN_CONTAINER = "column";
  final static public String ROW_CONTAINER = "row";
  public static final String ROOT_CONTAINER = "dashboard";
  private List<SelectItemOption<String>> containerOptions;
  private List<UIContainer> columns = new ArrayList<UIContainer>(3);
  
  public UIDashboardContainer(InitParams initParams) throws Exception {
    if(initParams == null) return;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    Param param = initParams.getParam("ContainerConfigs");          
    containerOptions = param.getMapGroovyObject(context);
    if(containerOptions == null) return;
    initData();
    PortalLayoutService service = getApplicationComponent(PortalLayoutService.class);
    Container container = service.getContainer(ROOT_CONTAINER);
    UIContainer uiRoot = createUIComponent(UIContainer.class, null, null); 
    PortalDataMapper.toUIContainer(uiRoot, container);
    addChild(uiRoot);
    columns = getColumns();
  }
  
  public List<UIContainer> getColumns() {
    List<UIContainer> list = new ArrayList<UIContainer>(3);
    findFirstComponentOfType(UIContainer.class).findComponentOfType(list, UIContainer.class);
    return list;
  }
  
  public Container createContainer(String type, String id) throws Exception {
    for(SelectItemOption<String> item : containerOptions) {
      if(item.getLabel().equals(type)) {
        Container container = toContainer(item.getValue());
        container.setId(id);
        return container;
      }
    }    
    return null;
  }
  
  private Container toContainer(String xml) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream( xml.getBytes()); 
    IBindingFactory bfact = BindingDirectory.getFactory(Container.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    return (Container) uctx.unmarshalDocument(is, null);
  }
  
  private void initData() throws Exception {
    PortalLayoutService service = getApplicationComponent(PortalLayoutService.class);
    if(service.getContainer(ROOT_CONTAINER) != null) return;
    Container root = createContainer(COLUMN_CONTAINER, ROOT_CONTAINER);
    ArrayList<Object> children = new ArrayList<Object>(3);
    //TODO: Use value form PortletPreference instead of "3"
    for(int i = 0; i < 3; i++) {
      children.add(createContainer(ROW_CONTAINER, "UIColumn-" + i));
    }
    root.setChildren(children);
    service.create(root);
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
    if (gadget != null) {
      UIContainer uiContainer = gadget.getParent();
      gadget.setParent(null);
      uiContainer.getChildren().remove(gadget);
    }
    return gadget;
  }
  
  public UIGadget removeUIGadget(final int col, final int row) throws Exception {
    UIGadget gadget = getUIGadget(col, row);
    if (gadget != null) {
      removeUIGadget(gadget.getId());
    }
    return gadget;
  }
  
  public void moveUIGadget(final String gadgetId, final int col, final int row) throws Exception {
    UIGadget gadget = removeUIGadget(gadgetId);
    if (gadget == null) {
      return;
    }
    addUIGadget(gadget, col, row);
  }
  
  public int getRenderedColumnsCount() throws Exception {
    if (columns == null) {
      columns = getColumns();
    }
    int count = 0;
    for (int i = 0; i < columns.size(); i++) {
      if (columns.get(i).isRendered()) { count++; }
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
    if (gadget != null) {
      flag = true;
    }
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
      if (columns.get(i).isRendered()) { colSize++; }
    }
    
    if (num < colSize) {
      for (int i = num; i < colSize; i++) {
        UIContainer tempCol = columns.get(i);
        List<UIComponent> components = new ArrayList<UIComponent>();
        for (UIComponent component : tempCol.getChildren()) {
          columns.get(num - 1).addChild(component);
          components.add(component);
        }
        tempCol.removeChild(UIGadget.class);
        for (UIComponent component : components) {
          component.setParent(columns.get(num - 1));
        }
        tempCol.setRendered(false);
      }
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
