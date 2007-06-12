/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.core.model.SelectItemOption;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 26, 2006
 */
@ComponentConfig(template = "system:/groovy/webui/form/UIFormInputItemSelector.gtmpl" )

public class UIFormInputItemSelector extends UIFormInputBase<Object> {

  private Class type_;

  protected List<SelectItemCategory> categories_ = new ArrayList<SelectItemCategory>() ;

  public UIFormInputItemSelector(String name, String bindingField) throws Exception {
    super(name, bindingField, Object.class);    
    setComponentConfig(getClass(), null) ;
  }  

  public List<SelectItemCategory> getItemCategories() { return  categories_ ; } 

  public void setItemCategories(List<SelectItemCategory> categories) {     
    categories_ = categories ;   
    boolean selected = false;
    for(SelectItemCategory ele : categories){
      if(ele.isSelected()){
        if(selected)  ele.setSelected(false);
        else selected = true;
      }
    }
    if(!selected) categories_.get(0).setSelected(true);
  }  

  public SelectItemCategory getSelectedCategory(){
    for(SelectItemCategory category : categories_){
      if(category.isSelected()) return category;
    }
    if(categories_.size() > 0){
      SelectItemCategory category = categories_.get(0);
      category.setSelected(true);      
      category.getSelectItemOptions().get(0).setSelected(true);
      return category;
    }
    return null;
  }

  public SelectItemOption getSelectedItemOption() {
    SelectItemCategory selectedCategory = getSelectedCategory();
    if(selectedCategory == null) return null;
    return selectedCategory.getSelectedItemOption();
  }

  public Object getValue() {
    SelectItemCategory selectedCategory = getSelectedCategory();
    if(selectedCategory == null) return null;
    SelectItemOption selectedItem = selectedCategory.getSelectedItemOption();
    if(selectedItem == null ) return null;    
    return selectedItem.getValue();
  }

  @SuppressWarnings("unchecked")
  public UIFormInputItemSelector  setValue(Object input) {
    for(SelectItemCategory category : categories_){
      category.setSelected(isSelectItemCategory(category, input)) ;     
    }
    return this;
  }  

  @SuppressWarnings("unchecked")
  public Class getTypeValue(){
    if(type_ != null) return type_;
    if(getSelectedCategory() == null ||
        getSelectedCategory().getSelectedItemOption() == null ||
        getSelectedCategory().getSelectedItemOption().getValue() == null
    ) return typeValue_;
    return getSelectedCategory().getSelectedItemOption().getValue().getClass();
  }

  public void setTypeValue(Class type) { this.type_ = type; }

  private boolean isSelectItemCategory(SelectItemCategory category, Object input){
    List<SelectItemOption> options = category.getSelectItemOptions();
    for(SelectItemOption option : options){
      if(option.getValue().equals(input)){       
        option.setSelected(true);
        return true;
      }      
    }          
    return category.getName().equals(input);
  }

  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {   
    if(input == null || String.valueOf(input).length() < 1) return;    
    setValue(input) ;   
  }

}