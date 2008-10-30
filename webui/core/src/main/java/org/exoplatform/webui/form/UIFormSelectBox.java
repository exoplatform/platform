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

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.model.SelectItemOption;
/**
 * Represents a select element
 * 
 */
public class UIFormSelectBox extends UIFormStringInput {
  
  /**
   * It make SelectBox's ability to select multiple values
   */
  private boolean isMultiple_ = false ;
  
  /**
   * The size of the list (number of select options)
   */
  private int size_ = 1 ;

  /**
   * The list of options
   */
  private List<SelectItemOption<String>> options_ ;
  
  /**
   * The javascript expression executed when an onChange event fires
   */
  private String onchange_;
  
  public UIFormSelectBox(String name, String bindingExpression, List<SelectItemOption<String>> options) {
    super(name, bindingExpression, null);
    setOptions(options);
  }
  
  public boolean isMultiple() {
	return isMultiple_;
  }
  
  final public UIFormSelectBox setMultiple(boolean bl) {
    isMultiple_ = bl ; return this ;
  }
  
  final public UIFormSelectBox setSize(int i) { 
    size_ = i ; return this ;
  }
  
  public UIFormSelectBox setValue(String value) {
    value_ = value ;
    for(SelectItemOption<String> option : options_) {
      if(option.getValue().equals(value_)) option.setSelected(true) ;
      else option.setSelected(false) ;
    }
    
    return this ;
  }

  public String[] getSelectedValues() {
    if(isMultiple_) {
      List<String> selectedValues = new ArrayList<String>() ;
      for(int i = 0; i < options_.size(); i ++) {
        SelectItemOption<String> item = options_.get(i) ; 
        if(item.isSelected()) selectedValues.add(item.getValue()); 
      }
      return selectedValues.toArray(new String[0]) ;
    }
    return new String[]{value_} ;
  }
  
  public UIFormSelectBox setSelectedValues(String[] values) {
    for(SelectItemOption<String> option : options_) {
      option.setSelected(false) ;
      for(String value : values) {
        if(value.equals(option.getValue())) {
          option.setSelected(true) ;
          break ;
        }
      }
    }
    
    return this ;
  }
    
  final public List<SelectItemOption<String>> getOptions() { return options_ ; }
  
  final public UIFormSelectBox setOptions(List<SelectItemOption<String>> options) { 
    options_ = options ; 
    if(options_ == null || options_.size() < 1) return this;
    value_ = options_.get(0).getValue();
    return this ;
  } 
  
  @Override
  public void reset() {
    // TODO Auto-generated method stub - dang.tung
    if(options_ == null || options_.size() < 1) return;
    value_ = options_.get(0).getValue();
    for(SelectItemOption<String> option : options_) {
      option.setSelected(false) ;
    }
    options_.get(0).setSelected(true) ;
  }
  
  public void setOnChange(String onchange){ onchange_ = onchange; }    
  
  @SuppressWarnings("deprecation")
  public UIFormSelectBox setDisabled(boolean disabled) {
    setEnable(!disabled);
    return this;
  }
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    String[] values = context.getRequestParameterValues(getId()) ;
    if(values == null) {
      value_ = null ;
      for(SelectItemOption<String> item : options_) {
        item.setSelected(false) ;
      }
      return ;
    }
    
    int i = 0 ;
    value_ = values[0] ;
    for(SelectItemOption<String> item: options_) {
      if (i > -1 && item.getValue().equals(values[i])) {
        item.setSelected(true) ;
        if(values.length == ++i) i = -1 ;
      } else item.setSelected(false) ;
    }
  }
    
//  protected String renderOnChangeAction(UIForm uiform) throws Exception {
//    StringBuilder builder = new StringBuilder();
//    builder.append(" onchange=\"javascript:eXo.webui.UIForm.submitForm('").
//            append("").append("','").append(onchange_).append("');\" ");
//    return builder.toString();
//  }
  
  protected String renderOnChangeEvent(UIForm uiForm) throws Exception {
    return uiForm.event(onchange_, (String)null);
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    ResourceBundle res = context.getApplicationResourceBundle() ;
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    String formId =  null ;
    if(uiForm.getId().equals("UISearchForm")) formId = uiForm.<UIComponent>getParent().getId() ;
    else formId = uiForm.getId() ;
   
    Writer w =  context.getWriter() ;
    w.write("<select class=\"selectbox\" name=\""); w.write(name); w.write("\"") ;
    if(onchange_ != null) {
      w.append(" onchange=\"").append(renderOnChangeEvent(uiForm)).append("\"");
    }
    
    if(isMultiple_)  w.write(" multiple=\"true\""); 
    if(size_ > 1)  w.write(" size=\"" + size_ + "\"");
    
    if (!enable_)  w.write(" disabled ");
    
    w.write(">\n") ;
    
    for(SelectItemOption<String> item : options_) {
      String label = item.getLabel() ;
      try {
        label = res.getString(formId + ".label.option." + item.getValue()) ;
      } catch(MissingResourceException ex) {}
      
      if(item.isSelected()) {
        w.write("<option selected=\"selected\" value=\""); w.write(item.getValue()); w.write("\">"); 
      } else {
        w.write("<option value=\""); w.write(item.getValue()); w.write("\">"); 
      }
      w.write(label); w.write("</option>\n");
    }
    
    w.write("</select>\n") ;
    if (this.isMandatory()) w.write(" *");
  }  
}