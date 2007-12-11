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
package org.exoplatform.webui.form;

import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.model.SelectItemOption;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 26, 2006
 * 
 * Represents a selector of decorators for drop down menus
 */
@ComponentConfig(
  type = UIFormInputDecoratorSelector.class,  
  template = "system:/groovy/webui/form/UIFormInputDecoratorSelector.gtmpl"
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
