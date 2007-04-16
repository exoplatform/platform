/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Thanh Tung
 * Apr 16, 2007  
 */

@ComponentConfig( template = "system:/groovy/webui/component/UIItemSelector.gtmpl" )

public class UIItemSelector extends UIContainer {
  String name_;
  private List<SelectItemCategory> categories_ = new ArrayList<SelectItemCategory>() ;
  
  public UIItemSelector(String name) {
    name_ = name;
    setComponentConfig(getClass(), null) ;
  }
  
  public String getName() {
    return name_;
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
  
  public SelectItemCategory getSelectedItemCategory() {
    for(SelectItemCategory category : categories_) {
      if (category.isSelected()) return category;
    }
    if (categories_.size() > 0) {
      SelectItemCategory category = categories_.get(0);
      category.setSelected(true);
      category.getSelectItemOptions().get(0).setSelected(true);
      return category;
    }
    return null;
  }
  
  public SelectItemOption getSelectedItemOption() {
    SelectItemCategory selectedCategory = getSelectedItemCategory();
    if (selectedCategory != null) return selectedCategory.getSelectedItemOption();
    return null;
  }

}












































