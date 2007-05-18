/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIDropDownItemSelector;
import org.exoplatform.webui.component.UIFormInputItemSelector;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Viet Chung
 *          nguyenchung136@yahoo.com
 * Aug 10, 2006  
 */
@ComponentConfig(
    template = "app:/groovy/portal/webui/component/customization/UIPageTemplateOptions.gtmpl",
    initParams = @ParamConfig(
        name = "PageLayout",
        value = "app:/WEB-INF/conf/uiconf/portal/webui/component/customization/PageConfigOptions.groovy"
    )
)
public class UIPageTemplateOptions extends UIFormInputItemSelector {
  
  private SelectItemOption selectedItemOption_  = null;

  @SuppressWarnings("unchecked")
  public UIPageTemplateOptions(InitParams initParams) throws Exception {
    super("UIPageTemplateOptions", null) ;
    if(initParams == null) return ;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    Param param = initParams.getParam("PageLayout");          
    categories_ = param.getMapGroovyObject(context) ;   
    
    SelectItemCategory category = getSelectedCategory();
    if(category == null) return ;
    SelectItemOption itemOption = category.getSelectedItemOption();
    if(itemOption == null) return ;
    selectedItemOption_ = itemOption;
    
    List<SelectItemOption<String>> itemOptions = new ArrayList<SelectItemOption<String>>();
    
    for(SelectItemCategory itemCategory: categories_){
      itemOptions.add(new SelectItemOption(itemCategory.getName()));
    }
    UIDropDownItemSelector dropDownItemSelector = addChild(UIDropDownItemSelector.class, null, null);
    dropDownItemSelector.setOptions(itemOptions);
    dropDownItemSelector.setTitle("Select Page Layout");
    dropDownItemSelector.setSelected(0);
  }
  
  public void setSelectOptionItem(String value) {
    for(SelectItemCategory itemCategory : categories_){      
      for(SelectItemOption itemOption : itemCategory.getSelectItemOptions()){
        if(itemOption.getLabel().equals(value)){
          selectedItemOption_ = itemOption;
          return;
        }
      }
    }
    selectedItemOption_ = null;
  }
  
  public SelectItemOption getSelectedItemOption() { return selectedItemOption_; }
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {  
    setSelectOptionItem((String)input) ;
  }
  
  public void setSelectedOption(SelectItemOption selectedItemOption){
    selectedItemOption_ = selectedItemOption;
  }
  
  public Page getSelectedOption() throws Exception {
    if(selectedItemOption_ == null) return null; 
    return toPage(selectedItemOption_.getValue().toString()); 
  }
  
  private Page toPage(String xml) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream( xml.getBytes()) ; 
    IBindingFactory bfact = BindingDirectory.getFactory(Container.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    System.out.println("\n\n "+ xml +"\n\n\n");
    return (Page) uctx.unmarshalDocument(is, null);
  }

 
}