/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.model.SelectItemOption;

/**
 * Created by The eXo Platform SARL
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * July 12, 2007  
 * 
 * A drop down selector
 */
@ComponentConfig(template = "system:/groovy/webui/core/UIDropDownControl.gtmpl")
public class UIDropDownControl extends UIComponent {
  /**
   * The action to perform when an item is selected
   */
  private String action_ ;
  /**
   * The list of items
   */
  private List<SelectItemOption<String>> options_ ;
  /**
   * The index in the list of the selected item
   */
  private int selectedItemIndex_ = 0;
  
  public UIDropDownControl() throws Exception {
    options_ = new ArrayList<SelectItemOption<String>>() ;
  }
  
  public void setValue(String value) {
    int ln = options_.size() ;
    for(int i = 0; i < ln; i++) {
      if(options_.get(i).getValue().equals(value)) {
        selectedItemIndex_ = i ; return ;
      }
    }
  }
  
  // todo: tungnd
  public void setValue(int i) {
        selectedItemIndex_ = i ; return ;
  }
  // todo: tungnd
  public void cleanItem() { options_.clear(); }
  // todo: tungnd
  public void setOptions(List<SelectItemOption<String>> options) { 
    options_ = options ; 
    if(options == null) return ; 
    if(options_.size() < 1)  return;
  } 
  // todo: tungnd return -1 if have no option
  public int getSelectedIndex() {
    if (options_.size() < 1) return -1 ;
    return selectedItemIndex_ ;
  }
  // todo: tungnd get label of item
  public String getLabel() { return options_.get(selectedItemIndex_).getLabel() ; }

  public String getValue() { return options_.get(selectedItemIndex_).getValue() ; }
  
  public void setAction(String act) { action_ = act ; }
  public String getAction() {return action_ ; }
  
  public List<SelectItemOption<String>> getOptions() { return options_ ; }
  
  public void addItem(SelectItemOption<String> opt) { options_.add(opt) ; }
  public void addItem(String value) { options_.add(new SelectItemOption<String>(value)) ; }
  public void addItem(String label, String value) { options_.add(new SelectItemOption<String>(label, value)) ; }
  
  public String event(String param) throws Exception {
    if(action_ == null) {
      return super.event(config.getEvents().get(0).getName(), param);
    }
    int selectedIndex = 0; 
    for(int i = 0; i < options_.size(); i++) {
      if(options_.get(i).getValue().equals(param)) {
        selectedIndex = i ;
      }
    }
    StringBuilder evt = new StringBuilder("javascript:eXo.webui.UIDropDownControl.selectItem(") ;
    evt.append(action_).append(",'").append(this.getId()).append("','").append(selectedIndex).append("')") ;
    return evt.toString() ;
  }
}