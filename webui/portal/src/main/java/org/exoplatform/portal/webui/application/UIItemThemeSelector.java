/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.exoplatform.portal.webui.application.UIItemThemeSelector;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIDropDownControl;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputBase;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          tung.pham@exoplatform.com
 * Modify : dang.tung
 *          tungcnw@gmail.com
 * Nov 5, 2007  
 */

@ComponentConfigs ( {
  @ComponentConfig  (
      template = "system:/groovy/webui/form/UIItemThemeSelector.gtmpl",
      events = {
          @EventConfig(listeners = UIItemThemeSelector.SelectThemeActionListener.class, phase = Phase.DECODE),
          @EventConfig(listeners = UIItemThemeSelector.SetDefaultActionListener.class, phase = Phase.DECODE)
      }
  ),
  @ComponentConfig(
      type = UIDropDownControl.class,
      id = "ThemeDropDown",
      template = "system:/groovy/webui/core/UIDropDownControl.gtmpl",
      events = {
          @EventConfig(listeners = UIItemThemeSelector.ChangeOptionActionListener.class)
      }

  )
})
public class UIItemThemeSelector extends UIFormInputBase<String> {

  private String selectedTheme ;
  private List<ThemeCategory> categories = new ArrayList<ThemeCategory>();
  private ThemeCategory selectedCategory ;
  public static final String DEFAULT_THEME = "DefaultTheme" ;
  
  public UIItemThemeSelector(String name, String bindingField) throws Exception {
    super(name, bindingField, String.class) ;
    setComponentConfig(UIItemThemeSelector.class, null) ;
    addChild(UIDropDownControl.class, "ThemeDropDown", null) ;
  }
  
  @SuppressWarnings("unchecked")
  public UIFormInput setValue(String value) {
    setSelectedTheme(value) ;
    return this ;
  }
  
  public String getValue() {
    return getSelectedTheme() ; 
  }
  
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    String value = String.valueOf(input) ;
    if(value.equals("null") || value.trim().length() < 1) selectedTheme = null ;
    else selectedTheme = value ; 
  }
  
  
  public String event(String name, String beanId) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    return uiForm.event(name, beanId);
  }
  

  public String event(String name) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    return uiForm.event(name);
  }

  public void reset() {
    super.reset();
    selectedCategory = null ;
    selectedTheme = DEFAULT_THEME ;
    getChild(UIDropDownControl.class).setValue(0) ;
  }

  public  void setValues(Map<String, Set<String>> themeSet) {
    categories.clear();
    if(themeSet == null) {
      selectedCategory = null ;
      selectedTheme = null ;
      return ;
    }
    Iterator<Entry<String, Set<String>>> itr = themeSet.entrySet().iterator();
    while(itr.hasNext()) {
      Entry<String, Set<String>> cateEntry = itr.next() ;
      ThemeCategory category = new ThemeCategory(cateEntry.getKey()) ;
      List<String> themes = new ArrayList<String>(cateEntry.getValue()) ;
      for(String theme : themes) {
        category.addTheme(theme);
      }
      categories.add(category) ;
    }
    setSelectedCategory(categories.get(0)) ;
    getChild(UIDropDownControl.class).setOptions(getDropDownOptions()) ;
  }
    
  public ThemeCategory getSelectedCategory() {
    if(selectedCategory == null && categories.size() > 0) return categories.get(0) ;
    return selectedCategory;
  }

  public void setSelectedCategory(ThemeCategory selectedCate) {
    if(selectedCate == null) {
      setSelectedCategory((String)null) ;
      return ;
    }
    String cateName = selectedCate.getName() ;
    setSelectedCategory(cateName) ;
  }
  
  public void setSelectedCategory(String cateName) {
    selectedCategory = null ;
    if(cateName == null) return ;
    UIDropDownControl uiDropDown = getChild(UIDropDownControl.class) ;
    for(ThemeCategory cate : categories) {
      if(cate.getName().equals(cateName)) {
        selectedCategory = cate;
        uiDropDown.setValue(cateName) ;
        return ;
      }
    }    
  }

  public String getSelectedTheme() {
    if(selectedTheme == null || selectedTheme.trim().length() < 1) selectedTheme = DEFAULT_THEME ;
    return selectedTheme ;
  }

  public void setSelectedTheme(String value) {
    selectedTheme = null ;
    for(ThemeCategory cate : categories) {
      List<String> themes = cate.getThemes() ;
      if(themes == null) continue ;
      for(String theme : themes) {
        if(theme.equals(value)) {
          selectedTheme = value ;
          setSelectedCategory(cate) ;
          return ;
        }
      }
    }
  }
  
  public List<ThemeCategory> getCategories() {
    if(categories == null) return new ArrayList<ThemeCategory>() ;
    return categories ;
  }
  
  public void setCategories(List<ThemeCategory> list) {
    categories = list ;
    getChild(UIDropDownControl.class).setOptions(getDropDownOptions()) ;
  }
  
  private List<SelectItemOption<String>> getDropDownOptions() {
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    if(categories != null) {
      for(ThemeCategory ele : categories) {
        String cateName = ele.getName() ;
        options.add(new SelectItemOption<String>(cateName, cateName)) ;
      }      
    }
    return options ;
  }
  
  public static class SelectThemeActionListener extends EventListener<UIItemThemeSelector> {

    public void execute(Event<UIItemThemeSelector> event) throws Exception {
      UIItemThemeSelector uiFormInput = event.getSource() ;
      String theme = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiFormInput.setSelectedTheme(theme) ;
      UIForm uiForm = uiFormInput.getAncestorOfType(UIForm.class) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()) ;      
    }
    
  }
  
  public static class ChangeOptionActionListener extends EventListener<UIDropDownControl> {

    public void execute(Event<UIDropDownControl> event) throws Exception {
      UIDropDownControl uiDropDown = event.getSource() ;
      String category = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIItemThemeSelector uiFormInput = uiDropDown.getParent() ;
      uiFormInput.setSelectedCategory(category) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiFormInput) ;
    }
    
  }
  
  public static class SetDefaultActionListener extends EventListener<UIItemThemeSelector> {

    public void execute(Event<UIItemThemeSelector> event) throws Exception {
      UIItemThemeSelector uiFormInput = event.getSource() ;
      uiFormInput.reset() ;
      UIForm uiForm = uiFormInput.getAncestorOfType(UIForm.class) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()) ;      
    }
    
  }
  
  static public class ThemeCategory {
    
    private String name_ ;
    private String description_ ;
    private List<String> themes_ ;
    
    public ThemeCategory(String name) {
      name_ = name ;
      description_ = name ;
    }
    
    public ThemeCategory(String name, String description) {
      name_ = name ;
      description_ = description ;
    }

    public String getName() { return name_ ; }
    public void setName(String name) {name_ = name ; }
    
    public String getDescription() { return description_ ; }
    public void setDescription(String description) {description_ = description ; }

    public List<String> getThemes() { return themes_ ; }
    public void setThemes(List<String> themes) { themes_ = themes ; }
    
    public void addTheme(String theme) {
      if(themes_ == null) themes_ = new ArrayList<String>() ;
      themes_.add(theme) ;
    }
  }

}