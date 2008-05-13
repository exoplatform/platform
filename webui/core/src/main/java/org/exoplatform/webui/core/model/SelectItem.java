package org.exoplatform.webui.core.model;


/**
 * Abstract class SelectItem is held by UIFormSelectBox
 * This class is extended by SelectItemOption and SelectItemOptionGroup
 * @author philippe
 *
 */
public abstract class SelectItem {
	/**
	 * The text that appears on the UI when the item is rendered
	 */
	private String label_ ;

	public SelectItem(String label) {
		this.label_ = label;
	}

	public String getLabel() {
		return label_;
	}

	public void setLabel(String label) {
		this.label_ = label;
	}
	
//	public abstract void setSelectedValue(String value) ;
	
}
