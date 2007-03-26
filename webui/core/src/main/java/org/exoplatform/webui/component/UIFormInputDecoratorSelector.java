/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 26, 2006
 */
@ComponentConfig(
  type = UIFormInputDecoratorSelector.class,  
  template = "system:/groovy/webui/component/UIFormInputDecoratorSelector.gtmpl"
)
public class UIFormInputDecoratorSelector extends UIFormInputBase<String> {
  
  private  List<SelectItemOption> options_ ;
  private  SelectItemOption selectOption_ ; 
  
  public UIFormInputDecoratorSelector(String name, String bindingField) throws Exception {
    super(name, bindingField, String.class);
    setComponentConfig(UIFormInputDecoratorSelector.class, null) ;
  }
 
  public String getSelectDecorator() {  return  getValue() ;}
  
  
  public String getValue() { 
    if(selectOption_ != null) return  selectOption_.getValue().toString() ; 
    return null ;
  }
 
  public void  setValue(Object input) {
    if(input instanceof SelectItemOption){
      selectOption_ = (SelectItemOption) input ;
      return;
    } 
    setSelectOptionItem((String)input);
  }
  
  public List<SelectItemOption>  getOptions() {  return options_ ; }
  
  public void setOptions(List<SelectItemOption> options) { 
    options_ = options ;
    for(SelectItemOption op :  options) {
      if(op.isSelected()) selectOption_  = op  ;
    }
    if(selectOption_ == null) selectOption_ = options.get(0) ;
    options.get(0).setSelected(true) ;
  }
  
  public SelectItemOption getSelectOption() { return selectOption_ ; } 
  
  public void setSelectOptionItem(String value) {
    selectOption_ = null ;
    for(SelectItemOption op :  options_) {
      if(!op.getValue().equals(value))  continue;
      selectOption_  = op  ;
      break ;
    }
  }
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {  
    setSelectOptionItem((String)input) ;
  }
  
}
