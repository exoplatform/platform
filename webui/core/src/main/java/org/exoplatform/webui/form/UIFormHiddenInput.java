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
 * Author : lxchiati  
 *          lebienthuyt@gmail.com 
 * Jun 6, 2006
 * 
 * Repesents a hidden input field
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
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    value_ = (String) input;
    if(value_ != null && value_.length() == 0) value_ = null ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
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