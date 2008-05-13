package org.exoplatform.webui.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Represents an optgroup in a select element
 * Holds a list of SelectItemOption that represent the options inside this optgroup
 * @author philippe
 *
 */
public class SelectOptionGroup extends SelectItem {
	
	/**
	 * The lis of SelectItemOption
	 */
	private List<SelectOption> options_ ;
	
	public SelectOptionGroup(String label) {
		this(label, new ArrayList<SelectOption>(3));
	}
	
	public SelectOptionGroup(String label, List<SelectOption> list) {
		super(label);
		if (list == null) list = new ArrayList<SelectOption>(3);
		options_ = list;
	}

	public List<SelectOption> getOptions() {
		return options_;
	}

	public void setOptions(List<SelectOption> options) {
		this.options_ = options;
	}
	/**
	 * Adds a SelectItemOption to the list
	 * @param option
	 */
	public void addOption(SelectOption option) {
		if (options_ == null) options_ = new ArrayList<SelectOption>(3);
		options_.add(option);
	}
	
	public void setSelectedValue(String[] values) {
		for (SelectOption option : options_) {
    	  option.setSelected(false) ;
	      for(String value : values) {
	        if(value.equals(option.getValue())) {
	        	option.setSelected(true) ;
	          break ;
	        }
		  }
		}
	}
	
	 public void setValue(String value) {
		    for(SelectOption option : options_) {
			      if(option.getValue().equals(value)) option.setSelected(true) ;
			      else option.setSelected(false) ;
		    }
		  }
	
	public Collection<String> getSelectedValues() {
	      List<String> selectedValues = new ArrayList<String>() ;
	      for(int i = 0; i < options_.size(); i ++) {
	    	  SelectOption item = options_.get(i) ; 
	          if(item.isSelected()) selectedValues.add(item.getValue());
	      }
	      return selectedValues ;
	}
	
	  public void reset() {
		    if(options_ == null || options_.size() < 1) return;
		    for(SelectOption option : options_) {
		    		option.setSelected(false) ;
		    }
		    options_.get(0).setSelected(true) ;
		  }
}
