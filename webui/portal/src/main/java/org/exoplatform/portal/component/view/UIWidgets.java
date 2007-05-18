/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import java.util.List;

import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIDropDownItemSelector;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 16, 2007  
 */

@ComponentConfig(
  template = "system:/groovy/portal/webui/component/view/UIWidgets.gtmpl" ,
  events = @EventConfig(listeners = UIWidgets.ChangeOptionActionListener.class)
)

public class UIWidgets extends UIContainer {
  
  private UIWidgetContainer uiSelectedContainer_;
  
  public UIWidgets() throws Exception {
    UIDropDownItemSelector uiDropDownItemSelector = addChild(UIDropDownItemSelector.class, null, null);
    uiDropDownItemSelector.setOnServer(true);
    uiDropDownItemSelector.setOnChange("ChangeOption");
    
    
    addChild(UIWidgetContainer.class, null, "Information").setRendered(false);
    addChild(UIWidgetContainer.class, null, "Calendar").setRendered(false);
    addChild(UIWidgetContainer.class, null, "Calculator").setRendered(false);
    updateDropdownList();
  }
  
  public UIWidgetContainer getSelectedContainer() { return uiSelectedContainer_; }
  
  public void setSelectedContainer(UIWidgetContainer uiSelectedContainer) {
    uiSelectedContainer_ = uiSelectedContainer;
    uiSelectedContainer.setRendered(true);
  }
  
  public void updateDropdownList() {
    setSelectedContainer(getChild(UIWidgetContainer.class));
    UIDropDownItemSelector dropDownItemSelector = getChild(UIDropDownItemSelector.class);
    dropDownItemSelector.cleanItem();
    List<UIComponent> children = getChildren();
    for(UIComponent child : children) {
      if(child instanceof UIWidgetContainer) {
        dropDownItemSelector.addItem(child.getId());
      }
    }
  }
  
  static  public class ChangeOptionActionListener extends EventListener<UIWidgets> {
    public void execute(Event<UIWidgets> event) throws Exception {
      String selectedContainerId  = event.getRequestContext().getRequestParameter(OBJECTID);
      
      UIWidgets uiWidgets = event.getSource();
      UIDropDownItemSelector uiDropDownItemSelector = uiWidgets.getChild(UIDropDownItemSelector.class);
      SelectItemOption<String> option = uiDropDownItemSelector.getOption(selectedContainerId);
      if(option != null) uiDropDownItemSelector.setSelected(option);
      if(uiWidgets.getSelectedContainer().getId().equals(selectedContainerId)) return;
      
      UIWidgetContainer newSelected = uiWidgets.getChildById(selectedContainerId) ;
      uiWidgets.getSelectedContainer().setRendered(false);
      uiWidgets.setSelectedContainer(newSelected);
    }
  }
}
