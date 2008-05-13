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
import org.exoplatform.webui.core.model.SelectItem;
import org.exoplatform.webui.core.model.SelectOption;
import org.exoplatform.webui.core.model.SelectOptionGroup;
/**
 * Represents a select element
 * 
 */
public class UIFormSelectBoxWithGroups extends UIFormStringInput {
  
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
  private List<SelectItem> options_ ;
  
  /**
   * The javascript expression executed when an onChange event fires
   */
  private String onchange_;
  
  public UIFormSelectBoxWithGroups(String name, String bindingExpression, List<SelectItem> options) {
    super(name, bindingExpression, null);
    setOptions(options);
  }
  
  final public UIFormSelectBoxWithGroups setMultiple(boolean bl) {
    isMultiple_ = bl ; return this ;
  }
  
  final public UIFormSelectBoxWithGroups setSize(int i) { 
    size_ = i ; return this ;
  }
  
  public UIFormSelectBoxWithGroups setValue(String value) {
    value_ = value ;
    for(SelectItem option : options_) {
    	if (option instanceof SelectOption) {
	      if(((SelectOption)option).getValue().equals(value_)) ((SelectOption)option).setSelected(true) ;
	      else ((SelectOption)option).setSelected(false) ;
    	} else if (option instanceof SelectOptionGroup) {
    		((SelectOptionGroup)option).setValue(value);
    	}
    }
    
    return this ;
  }

  public String[] getSelectedValues() {
    if(isMultiple_) {
      List<String> selectedValues = new ArrayList<String>() ;
      for(SelectItem option : options_) {
    	if (option instanceof SelectOption) {
    		if(((SelectOption)option).isSelected()) selectedValues.add(((SelectOption)option).getValue()); 
    	} else if (option instanceof SelectOptionGroup) {
    		selectedValues.addAll(((SelectOptionGroup)option).getSelectedValues());
    	}
    	
      }
      return selectedValues.toArray(new String[0]) ;
    }
    return new String[]{value_} ;
  }
  
  public UIFormSelectBoxWithGroups setSelectedValues(String[] values) {
    for(SelectItem option : options_) {
    	if (option instanceof SelectOption) {
    		((SelectOption)option).setSelected(false) ;
	      for(String value : values) {
	        if(value.equals(((SelectOption)option).getValue())) {
	        	((SelectOption)option).setSelected(true) ;
	          break ;
	        }
	      }
    	} else if (option instanceof SelectOptionGroup) {
    		((SelectOptionGroup)option).setSelectedValue(values);
    	}
    }
    
    return this ;
  }
    
  final public List<SelectItem> getOptions() { return options_ ; }
  
  final public UIFormSelectBoxWithGroups setOptions(List<SelectItem> options) {
    options_ = options ; 
    if(options_ == null || options_.size() < 1) return this;
    for (SelectItem option : options_) {
    	if (option instanceof SelectOption) {
    		value_ = ((SelectOption)option).getValue();
    		break;
    	}
    }
    return this ;
  }
  
  public UIFormSelectBoxWithGroups addOptionGroup(String label, List<SelectOption> options) {
	  SelectOptionGroup group = new SelectOptionGroup(label);
	  group.setOptions(options);
	  options_.add(group);
	  return this;
  }
  
  @Override
  public void reset() {
    // TODO Auto-generated method stub - dang.tung
    if(options_ == null || options_.size() < 1) return;
    for(SelectItem option : options_) {
    	if (option instanceof SelectOption) ((SelectOption)option).setSelected(false) ;
    	else if (option instanceof SelectOptionGroup) {
    		((SelectOptionGroup)option).reset();
     	}
    }
    for (SelectItem option : options_) {
    	if (option instanceof SelectOption) {
    		value_ = ((SelectOption)option).getValue();
    		((SelectOption)option).setSelected(true);
    		break;
    	}
    }
  }
  
  public void setOnChange(String onchange){ onchange_ = onchange; }    
  
  @SuppressWarnings("deprecation")
  public UIFormSelectBoxWithGroups setDisabled(boolean disabled) {
    setEnable(!disabled);
    return this;
  }
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    String[] values = context.getRequestParameterValues(getId()) ;
    if(values == null) {
      value_ = null ;
      for(SelectItem option : options_) {
      	if (option instanceof SelectOption) ((SelectOption)option).setSelected(false) ;
      	 else if (option instanceof SelectOptionGroup) {
      		for (SelectOption opt : ((SelectOptionGroup)option).getOptions()) {
      			opt.setSelected(false);
      		}
     	}
      }
      return ;
    }
    
    int i = 0 ;
    value_ = values[0] ;
    for(SelectItem item: options_) {
    	if (item instanceof SelectOption) {
	      if (i > -1 && ((SelectOption)item).getValue().equals(values[i])) {
	    	  ((SelectOption)item).setSelected(true) ;
	        if(values.length == ++i) i = -1 ;
	      } else ((SelectOption)item).setSelected(false) ;
    	} else if (item instanceof SelectOptionGroup) {
    		for (SelectOption opt : ((SelectOptionGroup)item).getOptions()) {
    			if (i > -1 && ((SelectOption)opt).getValue().equals(values[i])) {
    		    	((SelectOption)opt).setSelected(true) ;
    			}
    		    if(values.length == ++i) i = -1 ;
    		}
    	}
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
  
  protected UIForm getFrom() {
	    return getAncestorOfType(UIForm.class) ;
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
    
    for(SelectItem item : options_) {
    	 String label = item.getLabel() ;
    	if (item instanceof SelectOption) {
    		try {
    	       label = res.getString(formId + ".label.option." + ((SelectOption)item).getValue()) ;
    	    } catch(MissingResourceException ex) {}
    	         
    		w.write(renderOption(((SelectOption)item), label));
    		
    	} else if (item instanceof SelectOptionGroup) {
    		label = item.getLabel();
    		try {
	    	    label = res.getString(getFrom().getId() + ".optionGroup.label." + label);
	    	    } catch(MissingResourceException ex) {
	    	    	System.out.println("Could not find: " + getFrom().getId() + ".optionGroup.label." + label);
	    	    }
    		w.write("<optgroup label=\""); w.write(label); w.write("\">\n");
    		for (SelectOption opt : ((SelectOptionGroup)item).getOptions()) {
    			label = opt.getLabel();
    			try {
    	    	    label = res.getString(formId + ".label.option." + opt.getValue()) ;
    	    	    } catch(MissingResourceException ex) {}
    	    	w.write(renderOption(opt, label));
    	    		
    		}
    		w.write("</optgroup>\n");
    	}
    }
    w.write("</select>\n") ;
    if (this.isMandatory()) w.write(" *");
  }
  
  private String renderOption(SelectOption option, String label) {
	  StringBuffer buf = new StringBuffer();
	  buf.append("<option value=\"");  buf.append(option.getValue());
	  if (option.isSelected())  buf.append("selected=\"selected\"");
	  buf.append("\">");
	  buf.append(label); buf.append("</option>\n");
	  return buf.toString();
  }

}