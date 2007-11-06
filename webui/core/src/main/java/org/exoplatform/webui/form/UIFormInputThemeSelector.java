/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIDropDownControl;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.core.model.SelectItemOption;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          tung.pham@exoplatform.com
 * Nov 5, 2007  
 */

@ComponentConfig(
    template = "system:/groovy/webui/form/UIFormInputThemeSelector.gtmpl" 
    
)
public class UIFormInputThemeSelector extends UIFormInputBase<String> {

  private String selectedCategory ;
  private String selectedTheme ;
  private List<SelectItemCategory> categories = new ArrayList<SelectItemCategory>();
  
  public UIFormInputThemeSelector(String name, String bindingField) throws Exception {
    super(name, bindingField, String.class) ;
    setComponentConfig(UIFormInputThemeSelector.class, null) ;
    UIDropDownControl uiCategoryDropDown = addChild(UIDropDownControl.class, null, null) ;
    uiCategoryDropDown.setAction("eXo.webui.UIDropDownControl.init") ;
  }
  
  public  void setValues(Map<String, Set<String>> themeSet) {
    Iterator<Entry<String, Set<String>>> itr = themeSet.entrySet().iterator();
    categories.clear();
    while(itr.hasNext()) {
      Entry<String, Set<String>> cateEntry = itr.next() ;
      SelectItemCategory category = new SelectItemCategory(cateEntry.getKey()) ;
      List<String> themes = new ArrayList<String>(cateEntry.getValue()) ;
      for(String ele : themes) {
        category.addSelectItemOption(new SelectItemOption<String>(ele, ele)) ;
      }
      categories.add(category) ;
    }
    getChild(UIDropDownControl.class).setOptions(getDropDownOptions()) ;
  }
  
  private List<SelectItemOption<String>> getDropDownOptions() {
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    for(SelectItemCategory ele : categories) {
      String cateName = ele.getName() ;
      options.add(new SelectItemOption<String>(cateName, cateName)) ;
    }
    return options ;
  }
  
  @SuppressWarnings("unchecked")
  public UIFormInput setValue(String value) {
    selectedTheme = value ;
    return this ;
  }
  
  public String getValue() {
    return getSelectedTheme() ;
  }
  
  public String getSelectedCategory() {
    return selectedCategory;
  }

  public void setSelectedCategory(String selectedCategory) {
    this.selectedCategory = selectedCategory;
  }

  public String getSelectedTheme() {
    return selectedTheme;
  }

  public void setSelectedTheme(String selectedTheme) {
    this.selectedTheme = selectedTheme;
  }
  
  public List<SelectItemCategory> getCategories() {
    if(categories == null) return new ArrayList<SelectItemCategory>() ;
    return categories ;
  }
  
  public void setCategories(List<SelectItemCategory> list) {
    categories = list ;
  }

  public void decode(Object input, WebuiRequestContext context) throws Exception {
  }

}