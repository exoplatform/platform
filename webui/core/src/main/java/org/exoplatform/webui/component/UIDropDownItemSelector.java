/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          lebienthuy@gmail.com
 * Mar 28, 2007  
 */

@ComponentConfig(template = "system:/groovy/webui/component/UIDropDownItemSelector.gtmpl")

public class UIDropDownItemSelector extends UIComponent {

  private int size_ = 0 ;
  
  private String title_;
  private boolean isEnable_;
  private boolean onServer_;
  private int maxShow_ = 5;
  
  private SelectItemOption selected_;
  
  private List<SelectItemOption<String>> options_ ;
  private String onchange_;
  
  public UIDropDownItemSelector(){
    title_ = getName();
    options_ = new ArrayList<SelectItemOption<String>>();
    isEnable_ = true;
    selected_ = null;
    onServer_ = true;
  }
  
  public void setMaxShow(int max) {maxShow_ = max; }
  public int getMaxShow() { return maxShow_; }
  
  public UIDropDownItemSelector(String title, List<SelectItemOption<String>> options) {
    this(title, options, true, null, true);
  }
  
  public UIDropDownItemSelector(String title, List<SelectItemOption<String>> options, boolean enable) {
    this(title, options, enable, null, true);
  }
  
  public UIDropDownItemSelector(String title, List<SelectItemOption<String>> options, 
                                boolean enable, SelectItemOption selected) {
    this(title, options, enable, selected, true);
  }
  
  public UIDropDownItemSelector(String title, List<SelectItemOption<String>> options, boolean enable, 
                                SelectItemOption selected, boolean onServer) {
    this.options_ = options;
    title_ = title;
    isEnable_ = enable;
    selected_ = selected;
    onServer_ = onServer;
    if(options != null) size_ = options.size();  
  }
  
  public boolean isOnServer() {return onServer_; }
  public void setOnServer(boolean onSever) { onServer_ = onSever; }

  public String getSelected() {
    if(selected_ != null)
      return (String) selected_.getValue();
    return null;
  }
  public void setSelected(SelectItemOption select){ selected_ = select;}
  
  public void setSize(int i) { size_ = i ;}
  public int getSize() { return size_; }  
  
  public String getTitle() {return title_;}
  public void setTitle(String title){ title_ =title;}
    
  public void setOnChange(String onchange){onchange_ = onchange; }    
  public String getOnChange() { return onchange_; }

  public List<SelectItemOption<String>> getOptions() { return options_ ; }
  public void setOptions(List<SelectItemOption<String>> options) { 
    options_ = options ; 
    if(options != null) size_ = options.size();  
  }  
 
  public void setEnabled(boolean enable) { isEnable_ = enable; }
  public boolean isEnable() {return isEnable_; }

  protected String renderOnChangeEvent(UIForm uiForm) throws Exception {
    return uiForm.event(onchange_, null);
  }

  public void setSelected(int i) {
    selected_ = options_.get(i);
  }
  
}