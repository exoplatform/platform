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

import javax.portlet.PortletPreferences;

import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.layout.PortalLayoutService;
import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
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
  
  /**
   * Specify max number of columns in dashboard container
   * Default value is 4
   */
  public static final int MAX_COLUMN = 4;  
  
  /**
   * Column Type of Container
   */
  public static final String COLUMN_CONTAINER = "column";
  
  /**
   * Row Type of Container
   */
  public static final String ROW_CONTAINER = "row";
  
  /**
   * Root Type of Container 
   */
  public static final String ROOT_CONTAINER = "dashboard";
  
  /**
   * 
   */
  private List<SelectItemOption<String>> containerOptions;
  
  /**
   * Specify windowId of <code>UIPortlet</code> that contains <code>UIDashboardPortlet</code> which this belong to 
   * @see UIPortlet
   * @see UIDashboardPortlet
   */
  private String windowId;


    /**
   * Constructs new UIDashboardContainer which belongs to a UIDashboardPortlet
   * @param initParams initial parameters
   * @throws Exception if can't create UIDashboardContainer
   * @see UIDashboardPortlet
   * @see InitParams
   */
  public UIDashboardContainer(final InitParams initParams) throws Exception {
    if (initParams == null) { return; }
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    windowId = ((PortletRequestContext) context).getRequest().getWindowID();
    
    Param param = initParams.getParam("ContainerConfigs");
    containerOptions = param.getMapGroovyObject(context);
    if (containerOptions == null) { return; }

   // UIContainer uiRoot = createUIComponent(UIContainer.class, null, null);
    addChild(UIContainer.class, null, null);
  }


/*  public boolean isPrivate() {
    return isPrivate;
  }

  public String getOwner() {
    return owner;
  }     */


  public void processRender(WebuiRequestContext context) throws Exception {
    //----
    initData();
    PortalLayoutService service = getApplicationComponent(PortalLayoutService.class);
    UIDashboardPortlet parent = getParent();

    Container container = service.getContainer(ROOT_CONTAINER  + "-" + windowId, parent.getOwner());
    UIContainer uiRoot = getChild(UIContainer.class);

    //remove the existing children, mybe it should be done in PortalDataMapper.toUIContainer
    uiRoot.getChildren().clear();

    PortalDataMapper.toUIContainer(uiRoot, container);
    //----
    super.processRender(context);
  }


  /**
   * Adds an UIGadget into UIDashboardContainer in specified position
   * @param gadget  UIGadget to add in UIDashboardContainer
   * @param col index of column to add
   * @param row index of row to add 
   * @return <code>UIGadget</code> object which added in
   * @see UIGadget
   */
  public void addUIGadget(final UIGadget gadget, final int col, final int row) {
    UIContainer uiContainer = getColumn(col);
    List<UIComponent> children = uiContainer.getChildren();
    if (uiContainer == null || row < 0 || row > children.size()) {
      return;
    }
    gadget.setParent(uiContainer);
    children.add(row, gadget);
  }
  
  /**
   * Gets an UIGadget at specified position
   * @param col index of column
   * @param row index of row
   * @return <code>null</code> if specified position doesn't exist
   *        <code>UIGadget</code> otherwise
   * @see UIGadget
   */
  public UIGadget getUIGadget(final int col, final int row) {
    UIContainer uiContainer = getColumn(col);
    if (uiContainer == null || row < 0 || row >= uiContainer.getChildren().size()) {
      return null;
    }
    return uiContainer.getChild(row);
  }
  
  /**
   * Gets an UIGadget which has applicationInstanceUniqueId_ according to specified Id
   * @param gadgetId applicationInstanceUniqueId_ of UIGadget 
   * @return <code>null</code> if UIGadget doesn't exist<br>
   *        <code>UIGadget<code> otherwise
   * @see UIGadget
   */
  public UIGadget getUIGadget(final String gadgetId) {
    List<UIContainer> columns = getColumns();
    for (int iCol = 0; iCol < getColumns().size(); iCol++) {
      for (int iRow = 0; iRow < columns.get(iCol).getChildren().size(); iRow++) {
        UIGadget gadget = (UIGadget) columns.get(iCol).getChild(iRow);
        if (gadgetId.equals(gadget.getApplicationInstanceUniqueId())) {
          return gadget;
        }
      }
    }
    return null;
  }
  
  /**
   * Removes an UIGadget belongs to this which has specified applicationInstanceUniqueId_ 
   * @param gadgetId applicationInstanceUniqueId_ of UIGadget
   * @return <code>UIGadget</code> which deleted<br>
   *          <code>null</code> otherwise
   * @see UIGadget
   */
  public UIGadget removeUIGadget(final String gadgetId) {
    UIGadget gadget = getUIGadget(gadgetId);
    if (gadget != null) {
      UIContainer uiContainer = gadget.getParent();
      gadget.setParent(null);
      uiContainer.getChildren().remove(gadget);
    }
    return gadget;
  }
  
  /**
   * Removes an UIGadget at specified position
   * @param col index of the column
   * @param row index of the row
   * @return <code>UIGadget</code> which deleted<br>
   *          <code>null</code> otherwise
   * @see UIGadget
   */
  public UIGadget removeUIGadget(final int col, final int row) {
    UIGadget gadget = getUIGadget(col, row);
    if (gadget != null) {
      removeUIGadget(gadget.getId());
    }
    return gadget;
  }
  
  /**
   * Moves an UIGadget which has specified applicationInstanceUniqueId_ to specified position
   * @param gadgetId applicationInstanceUniqueId_ of UIGadget
   * @param col index of destination column
   * @param row index of destination row
   * @see UIGadget
   */
  public void moveUIGadget(final String gadgetId, final int col, final int row) {
    UIGadget gadget = removeUIGadget(gadgetId);
    if (gadget == null) {
      return;
    }
    addUIGadget(gadget, col, row);
  }
  
  /**
   * Gets an UIContainer representation of a column at specified index 
   * @param col index of column
   * @return <code>UIContainer</code> if the column exist<br>
   *          <code>null</code> otherwise
   * @see UIContainer
   */
  public UIContainer getColumn(final int col) {
    if (col < 0 || col > getColumns().size()) { return null; }
    return getColumns().get(col);
  }
    
  /**
   * Tests if this UIDashboardContainer has UIGadget
   * @return <code>false</code> if and only if this UIDashboardContainer has no UIGadget<br>
   *          <code>true</code> otherwise
   */
  public boolean hasUIGadget() {
    boolean flag = false;
    UIGadget gadget = findFirstComponentOfType(UIGadget.class);
    if (gadget != null) {
      flag = true;
    }
    return flag;    
  }
  
  /**
   * Sets total of columns of this UIDashboardContainer, total of columns is between 1 and <tt>MAX_COLUMN</tt>
   * @param num total of columns
   * @return <code>null</code> if totals of columns is less than 1 or greater than <tt>MAX_COLUMN</tt>
   *         this <code>UIDashboardContainer</code> otherwise
   * @throws Exception if this UIDashboardContainer can not create new UIComponent
   */
  public UIDashboardContainer setColumns(final int num) throws Exception {
    if (num < 1 || num > MAX_COLUMN) {
      return null;
    }
    
    UIContainer uiRoot = findFirstComponentOfType(UIContainer.class);
    List<UIContainer> columns = getColumns();
    int colSize = columns.size();
    
    if (num < colSize) {
      for (int i = num; i < colSize; i++) {
        UIContainer tempCol = columns.get(i);
        List<UIComponent> components = new ArrayList<UIComponent>();
        for (UIComponent component : tempCol.getChildren()) {
          columns.get(num - 1).addChild(component);
          components.add(component);
        }
        uiRoot.removeChildById(tempCol.getId());
        for (UIComponent component : components) {
          component.setParent(columns.get(num - 1));
        }
      }
    } else {
      if (num > colSize) {
        do {
          UIContainer uiContainer = createUIComponent(UIContainer.class, null, null);
          PortalDataMapper.toUIContainer(uiContainer, 
              createContainer(ROW_CONTAINER, "UIColumn-" + colSize));
          uiRoot.addChild(uiContainer);
          colSize++;
        } while (num > colSize);
      }
    }
    return this;
  }
  
  /**
   * Returns a <tt>List</tt> of the UIContainers of this UIDashboardContainer which representation of list of columns.
   * @return <code>List</code> of UIContainer
   * @see List
   * @see UIContainer
   */
  public List<UIContainer> getColumns() {
    List<UIContainer> list = new ArrayList<UIContainer>();
    UIContainer uiRoot = findFirstComponentOfType(UIContainer.class);
    uiRoot.findComponentOfType(list, UIContainer.class);
    if (list.size() > 0 && list.contains(uiRoot)) {
      list.remove(uiRoot);
    }
    return list;
  }
  
  /**
   * Creates a Container that representation of row or column in this UIDashboardContainer
   * @param type type of Container, that is <tt>ROW_CONTAINER</tt> or <tt>COLUMN_CONTAINER</tt>
   * @param id  id of Container
   * @return <code>Container</code> if {@link UIDashboardContainer} has specified type
   *          <br><code>null</code> otherwise
   * @throws Exception if <code>toContainer(String)</code> method throws an Exception
   * @see Container
   * @see UIDashboardContainer#toContainer(String)
   */
  public Container createContainer(final String type, final String id) throws Exception {
    for (SelectItemOption<String> item : containerOptions) {
      if (item.getLabel().equals(type)) {
        Container container = toContainer(item.getValue());
        container.setId(id);
        return container;
      }
    }    
    return null;
  }
  
  /**
   * Creates a Container from a xml template 
   * @param xml template of Container
   * @return <code>null</code> if template is not suitable for {@link Container}<br />
   *          <code>Container</code> otherwise
   * @throws Exception if have problems in unmarshal process
   * @see IUnmarshallingContext#unmarshalDocument(java.io.InputStream, String)
   */
  private Container toContainer(final String xml) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes()); 
    IBindingFactory bfact = BindingDirectory.getFactory(Container.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    return (Container) uctx.unmarshalDocument(is, null);
  }
  
  /**
   * Loads all data from database through {@link PortalLayoutService} to initialize UIDashboardContainer 
   * @throws Exception
   */
  private void initData() throws Exception {
    PortalLayoutService service = getApplicationComponent(PortalLayoutService.class);

    UIDashboardPortlet parent = getParent();

    if (service.getContainer(ROOT_CONTAINER + "-" + windowId, parent.getOwner()) != null) { return; }
    Container root = createContainer(COLUMN_CONTAINER, ROOT_CONTAINER + "-" + windowId);
    ArrayList<Object> children = new ArrayList<Object>();
    
    //TODO: Use value from PortletPreference
    PortletRequestContext pcontext = (PortletRequestContext) 
      WebuiRequestContext.getCurrentInstance();
    PortletPreferences pref = pcontext.getRequest().getPreferences();
    int totalCols = Integer.parseInt(pref.getValue(UIDashboardEditForm.TOTAL_COLUMNS, "3"));
    for (int i = 0; i < totalCols; i++) {
      children.add(createContainer(ROW_CONTAINER, "UIColumn-" + i));
    }
    root.setChildren(children);

    service.create(root, parent.getOwner());
  }
  
  /**
   * Saves all <tt>UIComponent</tt> of this <tt>UIDashboardContainer</tt> to database 
   * @throws Exception
   */
  public void save() throws Exception {
    UIContainer uiRoot = findFirstComponentOfType(UIContainer.class);
    PortalLayoutService service = getApplicationComponent(PortalLayoutService.class);

    UIDashboardPortlet parent = getParent();

    service.save(PortalDataMapper.toContainer(uiRoot), parent.getOwner());
  }

}


