/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form;

import java.io.Writer;

import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * November 07, 2007
 */
public class UIFormWYSIWYGInput extends UIFormInputBase<String> {
  
  private int height_ = 300 ;
  
  public UIFormWYSIWYGInput(String name, String bindingExpression, String value) {
    super(name, bindingExpression, String.class);
    this.value_ = value ;
  }
  
  public int getHeight() { return height_ ; }
  public void setHeight(int height) { this.height_ = height; }

  public void decode(Object input, WebuiRequestContext context) throws Exception {
    value_ = (String) input;
    if(value_ != null && value_.length() == 0) value_ = null ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    StringBuilder jsExec = new StringBuilder("new FCKeditor('").append(getName()).
                           append("', null, ").append(height_).
                           append(").ReplaceTextarea();") ;
    Writer w =  context.getWriter() ;
    
    if (value_ == null) value_ = "" ;
    value_ = value_.replaceAll("'", "\\\\'");
    value_ = value_.replaceAll("[\r\n]", "");
    w.write("<textarea id='" + getName() + "' name='" + getName() + "'>" + value_ + "</textarea>") ;
    context.getJavascriptManager().addJavascript(jsExec.toString()) ;
  }
}