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
package org.exoplatform.webui.core;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.core.model.SelectItemOption;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Thanh Tung
 * Apr 16, 2007  
 * 
 * An item selector represented by a normal list
 */
@ComponentConfig(template = "system:/groovy/webui/core/UIItemSelector.gtmpl")
public class UIItemSelector extends UIComponent {
  /**
   * The name of this selector
   */
  private String name_;
  /**
   * The item categories, each category contains items
   */
  private List<SelectItemCategory> categories_ ;

  public UIItemSelector(String name) {
    name_ = name;
    setComponentConfig(getClass(), null) ;
    categories_  = new ArrayList<SelectItemCategory>();
  }

  public String getName() { return name_; }

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
