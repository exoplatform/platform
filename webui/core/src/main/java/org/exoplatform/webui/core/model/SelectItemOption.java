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
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 26, 2006
 * 
 * An item in a UIFormInputItemSelector
 * Each item is actually held in a SelectItemCategory, which is held by the UIFormInputItemSelector
 * @see SelectItemCategory
 * @see org.exoplatform.webui.form.UIFormInputItemSelector
 */
public class SelectItemOption <T extends Object> {
  /**
   * The label of the item
   */
  protected String label_ ;
  /**
   * THe value of the item
   */
  protected  T value_ ;
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
  
  public SelectItemOption(String label, T value, String icon) {
    this(label, value, "", icon);
  }
  
  public SelectItemOption(String label, T value, String desc, String icon) {
    label_ = label ;
    value_ = value ;
    description_ = desc ; 
    icon_ = icon ;
  }
  
  public SelectItemOption(String label, T value, String desc, String icon, boolean selected) {
    this(label, value, desc, icon);
    selected_ = selected;
  }
  
  public SelectItemOption(String label, T value) {
    this(label, value, "", null);
  }
  
  public SelectItemOption(T value) {
    this(value.toString(), value, "", null);
  }
  
  public  String getLabel() {  return label_ ;}
  public void setLabel(String s) { label_ = s ; }
  
  public T getValue() { return value_ ; }
  public void   setValue(T s) { value_ =  s ; }
  
  public  String getDescription() { return description_ ; }
  public  void   setDescription(String s) { description_ = s ;}
  
  public boolean isSelected() { return selected_ ; }
  public void    setSelected(boolean b) { selected_ = b ; }

  public String getIcon() { return icon_; }
  public void setIcon(String icon) { this.icon_ = icon; }
  
}