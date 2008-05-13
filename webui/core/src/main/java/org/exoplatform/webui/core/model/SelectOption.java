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
package org.exoplatform.webui.core.model;


/**
 * Created by The eXo Platform SARL
 * Author : Philippe Aristote
 *          philippe.aristote@gmail.com
 * 
 * An item in a UIFormInputItemSelector
 * Each item is actually held in a SelectItemCategory, which is held by the UIFormInputItemSelector
 * @see SelectItemCategory
 * @see org.exoplatform.webui.form.UIFormInputItemSelector
 */
public class SelectOption extends SelectItem {
  /**
   * The label of the item
   */
 // protected String label_ ;
  /**
   * THe value of the item
   */
  protected  String value_ ;
  /**
   * The icon url of the item
   */
  protected  String icon_;
  /**
   * Whether this item is selected
   */
  protected boolean selected_ =  false ;
  /**
   * A description of the item
   */
  protected String description_ ;
  /**
   * Whether this item is disabled
   */
  protected boolean disabled_ = false;
  
  public SelectOption(String label, String value, String icon) {
    this(label, value, "", icon);
  }
  
  public SelectOption(String label, String value, String desc, String icon) {
	super(label);
    //label_ = label ;
    value_ = value ;
    description_ = desc ; 
    icon_ = icon ;
  }
  
  public SelectOption(String label, String value, String desc, String icon, boolean selected) {
    this(label, value, desc, icon);
    selected_ = selected;
  }
  
  public boolean isDisabled() {return disabled_;}
  public void setDisabled(boolean disabled) {this.disabled_ = disabled;}

  public SelectOption(String label, String value) {
    this(label, value, "", null);
  }
  
  public SelectOption(String value) {
    this(value.toString(), value, "", null);
  }
  
//  public  String getLabel() {  return label_ ;}
//  public void setLabel(String s) { label_ = s ; }
  
  public String getValue() { return value_ ; }
  public void   setValue(String s) { value_ =  s ; }
  
  public  String getDescription() { return description_ ; }
  public  void   setDescription(String s) { description_ = s ;}
  
  public boolean isSelected() { return selected_ ; }
  public void    setSelected(boolean b) { selected_ = b ; }

  public String getIcon() { return icon_; }
  public void setIcon(String icon) { this.icon_ = icon; }

}