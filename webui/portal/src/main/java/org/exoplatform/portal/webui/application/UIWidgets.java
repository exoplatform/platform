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
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIDropDownControl;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 16, 2007  
 */

@ComponentConfigs( {
  @ComponentConfig (
    template = "system:/groovy/portal/webui/application/UIWidgets.gtmpl" ,
    events = {
        @EventConfig(listeners = UIWidgets.ManageContainerActionListener.class)
    }
  ),
  
  @ComponentConfig (
    type = UIDropDownControl.class ,
    id = "UIDropDownWidgets",
    template = "system:/groovy/webui/core/UIDropDownControl.gtmpl",
    events = {
        @EventConfig(listeners = UIWidgets.ChangeOptionActionListener.class)
    }
  )
})
public class UIWidgets extends UIContainer {
  
  private String id;
  
  private String      ownerType;
  private String      ownerId;
  
  private String[]    accessPermissions ;
  
  private String editPermission;
  
  private UIDropDownControl uiContainerSelector_ ;
  
  public UIWidgets() throws Exception {
    setName(getClass().getSimpleName()) ;
    uiContainerSelector_ = createUIComponent(UIDropDownControl.class, "UIDropDownWidgets", "UIDropDownWidgets") ;
    uiContainerSelector_.setParent(this);
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
  
  public UIDropDownControl getUIDropDownControl() {
    return uiContainerSelector_ ;
  }
  
  @SuppressWarnings("unchecked")
  public UIComponent findComponentById(String lookupId) {
    if(uiContainerSelector_.getId().equals(lookupId)) return uiContainerSelector_ ;
    return super.findComponentById(lookupId) ;
  }
  
  public UIContainer getSelectedContainer() {
    for(UIComponent uiChild : getChildren()) {
      if(uiChild.isRendered()) {
        return (UIContainer) uiChild;
      }
    }
    return null; 
  }
  
  public void setSelectedContainer(int idx) {
    for(UIComponent uiChild : getChildren()) {
      uiChild.setRendered(false) ;
    }
    getChildren().get(idx).setRendered(true) ;
    uiContainerSelector_.setValue(idx) ;
  }
  
  public void setSelectedContainer(String containerId) {
    for(UIComponent uiChild : getChildren()) {
      if(uiChild.getId().equals(containerId)) uiChild.setRendered(true) ;
      else uiChild.setRendered(false) ;
    }
    uiContainerSelector_.setValue(containerId) ;
  }
  
  public void updateDropdownList() {
    List<UIComponent> uiChilddren = getChildren() ;
    if(uiChilddren == null || uiChilddren.size() < 1) {
      uiContainerSelector_.cleanItem() ;
      return ;
    }
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    for(UIComponent container : uiChilddren) {
      options.add(new SelectItemOption<String>(container.getName(), container.getId())) ;
    }
    uiContainerSelector_.setOptions(options) ;
    setSelectedContainer(0) ;
  }
  
  static  public class ChangeOptionActionListener extends EventListener<UIDropDownControl> {
    public void execute(Event<UIDropDownControl> event) throws Exception {
      String selectedContainerId  = event.getRequestContext().getRequestParameter(OBJECTID);      
      UIDropDownControl dropDown = event.getSource();
      UIWidgets uiWidgets = dropDown.getParent();
      uiWidgets.setSelectedContainer(selectedContainerId) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWidgets.getParent());
    }
  }
  
  static public class ManageContainerActionListener extends EventListener<UIWidgets> {
    public void execute(Event<UIWidgets> event) throws Exception {
      UIWidgets uiWidgets = event.getSource() ;
      UIPortalApplication uiPortalApp = uiWidgets.getAncestorOfType(UIPortalApplication.class) ;
      UIMaskWorkspace uiMaskWorkspace = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWorkspace.createUIComponent(UIWidgetContainerManagement.class, null, null) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWorkspace) ;
    }    
  }
  
}
