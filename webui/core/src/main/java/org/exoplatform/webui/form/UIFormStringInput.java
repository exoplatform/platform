/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form;

import java.io.Writer;

import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 6, 2006
 */
public class UIFormStringInput extends UIFormInputBase<String> {
  
  final  static public short TEXT_TYPE = 0 ;
  final  static public short PASSWORD_TYPE = 1 ;
  
  private short type_ = TEXT_TYPE ;
    
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
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    value_ = (String) input;
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
      //TODO TrongTT: The temporary solution for ajax updating problem in IE6 & IE7
      if(type_ == PASSWORD_TYPE) context.getJavascriptManager().addCustomizedOnLoadScript("document.getElementById('" + getId() + "').value = '" + encodeValue(value_).toString() + "';") ;
    }
  	if (readonly_) w.write(" readonly ");  	
  	w.write("/>") ;
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