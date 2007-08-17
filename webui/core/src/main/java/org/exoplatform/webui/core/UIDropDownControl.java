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
  private int selectedItemIndex_ ;
 
  public UIDropDownControl() throws Exception {
    options_ = new ArrayList<SelectItemOption<String>>() ;
  }
  
  public void setAction(String act) { action_ = act ; }
  public List<SelectItemOption<String>> getOptions() { return options_ ; }

  public void addItem(String value) {
    options_.add(new SelectItemOption<String>(value)) ;
  }
  
  public String event(String param) throws Exception {
    if(action_ == null) return super.event(config.getEvents().get(0).getName(), param);
    StringBuilder evt = new StringBuilder("javascript:eXo.webui.UIDropDownControl.selectItem(this,") ;
    evt.append(action_).append(",'").append(param).append("')") ;
    return evt.toString() ;
  }
}