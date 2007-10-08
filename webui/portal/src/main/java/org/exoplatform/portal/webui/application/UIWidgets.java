/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
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
  events = {
      @EventConfig(listeners = UIWidgets.ChangeOptionActionListener.class),
      @EventConfig(listeners = UIWidgets.ManageContainerActionListener.class)
  }
)
public class UIWidgets extends UIContainer {
  
  private String id;
  
  private String      ownerType;
  private String      ownerId;
  
  private String[]    accessPermissions ;
  
  private String editPermission;
  
  private UIDropDownItemSelector uiContainerSelector_ ;
  
  public UIWidgets() throws Exception {
    setName(getClass().getSimpleName()) ;
    uiContainerSelector_ = createUIComponent(UIDropDownItemSelector.class, null, null) ;
    uiContainerSelector_.setTitle("SelectContainer") ;
    uiContainerSelector_.setOnServer(true) ;
    uiContainerSelector_.setOnChange("ChangeOption") ;
    uiContainerSelector_.setParent(this) ;
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
  
  public UIDropDownItemSelector getUIDropDownItemSelector() {
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
    uiContainerSelector_.setSelected(idx) ;
  }
  
  public void setSelectedContainer(String containerId) {
    for(UIComponent uiChild : getChildren()) {
      if(uiChild.getId().equals(containerId)) uiChild.setRendered(true) ;
      else uiChild.setRendered(false) ;
    }
    uiContainerSelector_.setSelected(containerId) ;
  }
  
  public void updateDropdownList() {
    List<UIComponent> uiChilddren = getChildren() ;
    if(uiChilddren == null || uiChilddren.size() < 1) {
      uiContainerSelector_.cleanItem() ;
      uiContainerSelector_.setSelectedItem(null) ;
      return ;
    }
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    for(UIComponent container : uiChilddren) {
      options.add(new SelectItemOption<String>(container.getName(), container.getId())) ;
    }
    uiContainerSelector_.setOptions(options) ;
    setSelectedContainer(0) ;
  }
  
  static  public class ChangeOptionActionListener extends EventListener<UIWidgets> {
    
    public void execute(Event<UIWidgets> event) throws Exception {
      String selectedContainerId  = event.getRequestContext().getRequestParameter(OBJECTID);      
      UIWidgets uiWidgets = event.getSource();
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
