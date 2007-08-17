/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
 * @see UIFormInputItemSelector
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