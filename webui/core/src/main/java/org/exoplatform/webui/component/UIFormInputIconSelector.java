/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 26, 2006
 */
@ComponentConfig(
    template = "system:/groovy/webui/component/UIFormInputIconSelector.gtmpl",
    events = {
        @EventConfig(listeners = UIFormInputIconSelector.ChangeOptionSetActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIFormInputIconSelector.ChangeIconCategoryActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIFormInputIconSelector.SelectIconActionListener.class, phase = Phase.DECODE)
    },
    initParams = {
        @ParamConfig(
            name = "IconSet16x16", 
            value = "app:/WEB-INF/conf/uiconf/webui/component/IconSet16x16.groovy"
        ),
        @ParamConfig(
            name = "IconSet24x24", 
            value = "app:/WEB-INF/conf/uiconf/webui/component/IconSet24x24.groovy"
        ),
        @ParamConfig(
            name = "IconSet32x32", 
            value = "app:/WEB-INF/conf/uiconf/webui/component/IconSet32x32.groovy"
        )
    }  
)
public class UIFormInputIconSelector extends UIFormInputBase<String> {

  private List<String> optionSets = new ArrayList<String>() ;
  private List<IconSet>  iconSets = new ArrayList<IconSet>() ;
  private String paramDefault = "IconSet16x16" ;
  private CategoryIcon selectedIconCategory ;
  private IconSet selectedIconSet ;
  private String selectedIcon ;
  private String selectType = "page";
  public static final String[] SELECT_TYPE = {"portal", "page" };

  public UIFormInputIconSelector(String name, String bindingField) throws Exception {
    super(name, bindingField, String.class);
    setComponentConfig(UIFormInputIconSelector.class, null) ;
    UIDropDownItemSelector dropIconSet = addChild(UIDropDownItemSelector.class, null, null);
    this.setValues(paramDefault) ;
    selectType = "page" ;
    dropIconSet.setTitle("Select Icon Set");
  }
  
  private List<SelectItemOption<String>> getDropOptions() {
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>();
    for(String s: optionSets){
      options.add(new SelectItemOption<String>(s));
    }
    return options;
  }
  
  public void setType(String type) { selectType = type; }  
  public String getType() { return selectType; }
  
  public void setValues(String paramName) throws Exception {
    selectedIconCategory = null ;
    selectedIconSet = null ;
    selectedIcon = null ;
    iconSets.clear() ;
    optionSets.clear() ;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    for(Param param : getComponentConfig().getInitParams().getParams()) {
      if(param.getName().equals(paramName)) {
        CategoryIcon categoryIconSet =  param.getMapGroovyObject(context) ;
        if(selectedIconCategory == null) selectedIconCategory = categoryIconSet ;
        for(IconSet iconset : categoryIconSet.getCategory()) {
          if(selectedIconSet == null) setSelectedIconSet(iconset) ;
          IconCategory iconCategory = iconset.getIconCategory() ;
          if(selectedIcon == null) setSelectedIcon(iconCategory.getValue().get(0)) ;
          iconSets.add(iconset) ;
        }
      }
      optionSets.add(param.getName()) ;
      getChild(UIDropDownItemSelector.class).setOptions(getDropOptions());
    }
  }

  public List<String> getOptionSets() { return optionSets ; }

  public List<IconSet> getListIconSet() { return iconSets ; }

  public CategoryIcon getSelectedCategory() { return selectedIconCategory ; }
  public void setSelectedCategory(CategoryIcon category) { selectedIconCategory = category ; }

  public IconSet getSelectedIconSet() {  return selectedIconSet ; }
  public void setSelectedIconSet(IconSet iconset) { selectedIconSet = iconset ; }

  public List<String> getListIcon(IconSet set) {
    return set.getIconCategory().getValue() ;
  }
  
  public String getValue(){ return getSelectedIcon(); }    
  public UIFormInput setValue(String value){
    selectedIcon = value ;
    return this;
  }
  public void setSelectedIcon(String name) { selectedIcon = name ; }
  public String getSelectedIcon() { 
    if(selectedIcon != null)  return selectedIcon;
    IconSet set = getSelectedIconSet() ;
    IconCategory iconCategory = set.getIconCategory() ;
    selectedIcon = iconCategory.getValue().get(0) ;
    return selectedIcon ; 
  }

  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    if(input == null || String.valueOf(input).length() < 1) return;    
    selectedIcon = (String) input ;
  }
  
  static  public class ChangeOptionSetActionListener extends EventListener<UIFormInputIconSelector> {
    public void execute(Event<UIFormInputIconSelector> event) throws Exception {
      UIFormInputIconSelector uiForm = event.getSource() ; 
      String paramName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiForm.setValues(paramName) ;
      uiForm.setRenderSibbling(UIFormInputIconSelector.class);     
    }
  }

  static  public class ChangeIconCategoryActionListener extends EventListener<UIFormInputIconSelector> {
    public void execute(Event<UIFormInputIconSelector> event) throws Exception {
      UIFormInputIconSelector uiIconSelector = event.getSource() ; 
      String setName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiIconSelector.setSelectedIcon(null) ;
      for(IconSet set : uiIconSelector.getListIconSet()) {
        if(set.getName().equals(setName)) {
          uiIconSelector.setSelectedIconSet(set) ;   
        }
      }      
      uiIconSelector.setRenderSibbling(UIFormInputIconSelector.class) ;
      
      UIForm uiForm = uiIconSelector.getAncestorOfType(UIForm.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent());
    }
  }

  static public class SelectIconActionListener extends EventListener<UIFormInputIconSelector> {
    public void execute(Event<UIFormInputIconSelector> event) throws Exception {
      UIFormInputIconSelector uiIconSelector = event.getSource() ;
      String iconName = event.getRequestContext().getRequestParameter(OBJECTID) ;  
      uiIconSelector.setSelectedIcon(iconName) ;
      uiIconSelector.setRenderSibbling(UIFormInputIconSelector.class) ;
      uiIconSelector.setRenderSibbling(UIFormInputIconSelector.class) ;
      
      UIForm uiForm = uiIconSelector.getAncestorOfType(UIForm.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent());
    }
  }

  //TODO  Rename this to CategoryIconSet
  static public class CategoryIcon {

    private String name ;
    private String sizeOption ;
    private List<IconSet> category = new ArrayList<IconSet>() ;

    public CategoryIcon(String n,String s) { 
      name = n ;
      sizeOption = s ;
    }

    public String getSizeOption() { return sizeOption ; }
    public String getName() { return name ; }
    public CategoryIcon addCategory(IconSet set) {	
      category.add(set) ;
      return this ;
    }
    public List<IconSet> getCategory() { return category ; }

  }

  static public class IconSet {

    private String name ;
    private IconCategory iconcate_ = null;
    private IconSet set_ = null ;
    public IconSet(String n){
      name = n;
    }

    public String getName() { return name ; }
    public IconCategory  getIconCategory()  { return iconcate_ ; }
    public IconSet addCategories(IconCategory iconCate){ 
      iconcate_ = iconCate ;
      return this; 
    }

    public IconSet getIconSet() { return set_ ; } 
    public void addSets(IconSet set) { set_ = set  ; }

  }

  static public class IconCategory extends SelectItemOption<List<String>>  {

    public IconCategory(String name) {
      super(name, new ArrayList<String>());
    }

    public IconCategory addIcon(String icon) { 
      value_.add(icon);
      return this ;
    }
    
  }

}