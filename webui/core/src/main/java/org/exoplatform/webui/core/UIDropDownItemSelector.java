/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.form.UIForm;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          lebienthuy@gmail.com
 * Mar 28, 2007  
 */
@ComponentConfig(template = "system:/groovy/webui/core/UIDropDownItemSelector.gtmpl")
public class UIDropDownItemSelector extends UIComponent {

  private int size = 0 ;
  
  private String title;
  private boolean isEnable;
  private boolean onServer;
  private int maxShow = 5;
  
  private SelectItemOption<String> selected_;
  
  private List<SelectItemOption<String>> options_ ;
  private String onchange_;
  
  public UIDropDownItemSelector(){
    title = getName();
    options_ = new ArrayList<SelectItemOption<String>>();
    isEnable = true;
    selected_ = null;
    onServer = false;
  }
  
  public void setMaxShow(int max) {maxShow = max; }
  public int getMaxShow() { return maxShow; }
  
  public UIDropDownItemSelector(String title, List<SelectItemOption<String>> options) {
    this(title, options, true, null, true);
  }
  
  public UIDropDownItemSelector(String title, List<SelectItemOption<String>> options, boolean enable) {
    this(title, options, enable, null, true);
  }
  
  public UIDropDownItemSelector(String title, List<SelectItemOption<String>> options, 
                                boolean enable, SelectItemOption<String> selected) {
    this(title, options, enable, selected, true);
  }
  
  public UIDropDownItemSelector(String title, List<SelectItemOption<String>> options, 
                                boolean enable, SelectItemOption<String> selected, boolean onServer) {
    this.options_ = options;
    this.title = title;
    isEnable = enable;
    selected_ = selected;
    this.onServer = onServer;
    if(options == null)  return;
    size = options.size();
    if(selected_ == null && options_.size() > 0) selected_ = options_.get(0);    
  }
  
  public boolean isOnServer() {return onServer; }
  public void setOnServer(boolean onSever) { onServer = onSever; }

  public String getSelectedValue() {
    if(selected_ != null) return selected_.getValue();
    if(options_ == null || options_.size() < 1) return null;
    setSelectedItem(options_.get(0));
    return selected_.getValue();
  }
  
  public SelectItemOption<String> getOption(String value){
    for(SelectItemOption<String> option : options_) {
      if( option.getValue().equals(value)) return option;
    }
    return null;
  }
  public void setSelectedItem(SelectItemOption<String> select) { selected_ = select;}
  
  public SelectItemOption<String> getSelectedItem() { return selected_ ; }
  
  public void setSize(int i) { size = i ;}
  public int getSize() { 
    //return size;
    if(options_ != null) return options_.size() ;
    else return 0 ;
    }
  
  public String getTitle() {return title;}
  public void setTitle(String title){ this.title =title;}
    
  public void setOnChange(String onchange){onchange_ = onchange; }
  public String getOnChange() { return onchange_; }

  public List<SelectItemOption<String>> getOptions() { return options_ ; }
  public void setOptions(List<SelectItemOption<String>> options) { 
    options_ = options ; 
    if(options == null) return ; 
    size = options.size();
    if(options_.size() < 1)  return;
    selected_ = options_.get(0);
  }  
 
  public void setEnabled(boolean enable) { isEnable = enable; }
  public boolean isEnable() {return isEnable; }

  protected String renderOnChangeEvent(UIForm uiForm) throws Exception { 
    return uiForm.event(onchange_, null);
  }

  public void setSelected(int i) {
    if(options_ == null || i >= options_.size()) return;
    selected_ = options_.get(i); 
  }
  
  public void setSelected(String value) {
    if(options_ == null) return ;
    for(SelectItemOption<String> option: options_){
      if( option.getValue().equals(value)){
        setSelectedItem(option);
        break;
      }
    }
  }

  public void cleanItem() { options_.clear(); }
  
  public boolean addItem(String s) {
    if( s == null || s.length() < 1) return false ;
    SelectItemOption<String> option = new SelectItemOption<String>(s);
    options_.add(option);
    return true;
  }
  
  public boolean addItem(SelectItemOption<String> s) {
    if( s == null ) return false ;
    options_.add(s); return true;
  }
  
}