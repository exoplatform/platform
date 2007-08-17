/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Thi Hoa
 *          hoa.nguyen@exoplatform.com
 * Aug 10, 2006  
 * 
 * Represents a group of SelectItemOption, held in a UIFormInputItemSelector
 * @see UIFormInputItemSelector
 * @see SelectItemOption
 */
public class SelectItemCategory {
  /**
   * The name of the category
   */
  private String name_ ;
  /**
   * The label of the category
   */
  private String label_;
  /**
   * The list of SelectItemOption that this category contains
   */
  private List<SelectItemOption> options_ ;
  /**
   * Whether this category is selected
   */
  protected boolean selected_ =  false ;
  
  public SelectItemCategory(String name){
    this.name_ = name ;    
  }
  
  public SelectItemCategory(String name, boolean selected ){
    this.name_ = name ; 
    label_ = name;
    this.selected_ = selected;
  }
  
  public void setLabel(String label) { label_ = label; }
  public String getLabel(){ return label_;}
  
  public String getName() {  return name_; }
  public void setName(String name) { name_ = name; }
  
  public boolean isSelected() { return selected_ ; }
  public void    setSelected(boolean b) { selected_ = b ; }
  
  @SuppressWarnings("unchecked")
  public <T extends SelectItemOption> List<T> getSelectItemOptions() { 
    return (List<T>)options_; 
  }
  
  @SuppressWarnings("unchecked")
  public <T extends SelectItemOption> void setSelectItemOptions(List<T> options) {
    options_ = (List<SelectItemOption>)options; 
  }  
  
  public SelectItemCategory addSelectItemOption(SelectItemOption option) {
    if(options_ == null) options_ = new ArrayList<SelectItemOption>() ;
    options_.add(option) ;
    return this ;
  }
  
  public SelectItemOption getSelectedItemOption(){
    if(options_ == null) return null;
    for(SelectItemOption item : options_){
      if(item.isSelected()) return item;
    }
    return options_.get(0);
  }
}
