/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.io.Writer;

import org.exoplatform.webui.application.RequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : lxchiati  
 *          lebienthuyt@gmail.com 
 * Jun 6, 2006
 */
public class UIFormHiddenInput extends UIFormInputBase<String> {
  
    
  public UIFormHiddenInput(String name, String bindingExpression, String value) {
    super(name, bindingExpression, String.class);
    this.value_ = value ;
  }
  
  public UIFormHiddenInput(String name, String value) {
    this(name, null, value);
  }
  
  @SuppressWarnings("unused")
  public void decode(Object input, RequestContext context) throws Exception {
    value_ = (String) input;
    if(value_ != null && value_.length() == 0) value_ = null ;
  }
  
  public void processRender(RequestContext context) throws Exception {
  	Writer print =  context.getWriter() ;
  	print.write("<input name='") ; print.write(getName());
    print.write("'  type='hidden'");    
    print.write(" id='") ; print.write(getId()); print.write("'");
  	if(value_ != null && value_.length() > 0) {      
      print.write(" value='");  print.write(value_); print.write("'");
    }
  	print.write(" />") ;
  } 
}