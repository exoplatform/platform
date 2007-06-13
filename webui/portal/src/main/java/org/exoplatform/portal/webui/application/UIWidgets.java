/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import java.util.List;

import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIDropDownItemSelector;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 16, 2007  
 */

@ComponentConfig(
  template = "system:/groovy/portal/webui/application/UIWidgets.gtmpl" ,
  events = @EventConfig(listeners = UIWidgets.ChangeOptionActionListener.class)
)
public class UIWidgets extends UIContainer {
  
  private String id;
  
  private String      ownerType;
  private String      ownerId;
  
  private String[]    accessPermissions ;
  
  private String editPermission;
  
  public UIWidgets() throws Exception {
    UIDropDownItemSelector uiDropDownItemSelector = addChild(UIDropDownItemSelector.class, null, null);
    uiDropDownItemSelector.setOnServer(true);
    uiDropDownItemSelector.setOnChange("ChangeOption");
  }
  
  public String getOwnerId() { return ownerId; }
  public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }

  public String[] getAccessPermissions(){  return accessPermissions; }
  public void     setAccessPermissions(String[] s) { accessPermissions = s ; }
  
  public String getEditPermission() { return editPermission; }
  public void setEditPermission(String editPermission) { this.editPermission = editPermission; }
  
  public String getId() {
    if(id == null) id = ownerType +"::"+ownerId;
    return id; 
  }
  
  public UIContainer getSelectedContainer() {
    UIContainer uiSelectedContainer = null;
    for(UIComponent uiChild : getChildren()) {
      if(uiChild  instanceof UIContainer && uiChild.isRendered()) {
        uiSelectedContainer = (UIContainer) uiChild;
        break;
      }
    }
    return uiSelectedContainer; 
  }
  
  public void setSelectedContainer(UIContainer uiWidgetContainer) {
    UIContainer uiSelectedContainer = getSelectedContainer();
    if(uiSelectedContainer != null) uiSelectedContainer.setRendered(false);
    if(uiWidgetContainer != null) uiWidgetContainer.setRendered(true);
  }
  
  public void updateDropdownList() {
    setSelectedContainer(getChild(UIContainer.class));
    UIDropDownItemSelector uiDropDownItemSelector = getChild(UIDropDownItemSelector.class);
    uiDropDownItemSelector.cleanItem();
    List<UIComponent> uiChildren = getChildren();
    for(int i = 1; i < uiChildren.size(); i++) {
      uiDropDownItemSelector.addItem(uiChildren.get(i).getId());
    }
  }
  
  static  public class ChangeOptionActionListener extends EventListener<UIWidgets> {
    public void execute(Event<UIWidgets> event) throws Exception {
      String selectedContainerId  = event.getRequestContext().getRequestParameter(OBJECTID);
      
      UIWidgets uiWidgets = event.getSource();
      UIDropDownItemSelector uiDropDownItemSelector = uiWidgets.getChild(UIDropDownItemSelector.class);
      SelectItemOption<String> option = uiDropDownItemSelector.getOption(selectedContainerId);
      if(option != null) uiDropDownItemSelector.setSelectedItem(option);
      if(uiWidgets.getSelectedContainer().getId().equals(selectedContainerId)) return;
      
      UIContainer newSelected = uiWidgets.getChildById(selectedContainerId) ;
      uiWidgets.getSelectedContainer().setRendered(false);
      uiWidgets.setSelectedContainer(newSelected);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWidgets.getParent());
    }
  }
}
