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

import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 6, 2006
 * 
 * Represents a input string field
 * The value is stored in UIFormInputBase
 */
public class UIFormStringInput extends UIFormInputBase<String> {
  /**
   * type : text
   */
  final  static public short TEXT_TYPE = 0 ;
  /**
   * type : password
   */
  final  static public short PASSWORD_TYPE = 1 ;
  /**
   * type of the text field
   */
  private short type_ = TEXT_TYPE ;
  /**
   * max size of text field
   */
  private int maxLength = 0 ;
  
  public UIFormStringInput(String name, String bindingExpression, String value) {
    super(name, bindingExpression, String.class);
    this.value_ = value ; 
  }
  
  public UIFormStringInput(String name, String value) {
    this(name, null, value);
  }
  
  public UIFormStringInput setType(short type) {
    type_ = type;
    return this ;
  }
  
  public UIFormStringInput setMaxLength(int maxLength) {
    this.maxLength = maxLength ;
    return this ;
  }
  
  public int getMaxLength() {
    return maxLength ;
  }
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    String val = (String) input ;
    if((val == null || val.length() == 0) && type_ == PASSWORD_TYPE) return ; 
    value_ = val ;
    if(value_ != null && value_.length() == 0) value_ = null ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
  	Writer w =  context.getWriter() ;
  	w.write("<input name='") ; w.write(getName()); w.write('\''); 
  	if (type_ == PASSWORD_TYPE) w.write(" type='password'");
  	else w.write(" type='text'");    
    w.write(" id='") ; w.write(getId()); w.write('\'');
  	if(value_ != null && value_.length() > 0) {      
      w.write(" value='"); w.write(encodeValue(value_).toString()); w.write('\'');
    }
    if(maxLength > 0) w.write(" maxlength='" + maxLength + "'") ;
  	if (readonly_) w.write(" readonly ") ;
  	if (!enable_) w.write(" disabled ");
  	w.write("/>") ;
  	if (this.isMandatory()) w.write(" *");
  }
  
  private StringBuilder encodeValue(String value){
    char [] chars = {'\'', '"'};
    String [] refs = {"&#39;", "&#34;"};
    StringBuilder builder = new StringBuilder(value);
    int idx ;
    for(int i = 0; i < chars.length; i++){
     idx = indexOf(builder, chars[i], 0);
     while(idx > -1){
       builder = builder.replace(idx, idx+1, refs[i]);
       idx = indexOf(builder, chars[i], idx);
     }
    }    
    return builder;
  }
  
  private int indexOf(StringBuilder builder, char c, int from){
    int i = from;
    while(i < builder.length()){
      if(builder.charAt(i) == c) return i;
      i++;
    }
    return -1;
  }
}