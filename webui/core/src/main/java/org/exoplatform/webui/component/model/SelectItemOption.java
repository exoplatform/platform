/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.model;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 26, 2006
 */
public class SelectItemOption <T extends Object> {
  
  protected String label_ ;
  protected  T value_ ;
  protected  String icon_;
  protected boolean selected_ =  false ;
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