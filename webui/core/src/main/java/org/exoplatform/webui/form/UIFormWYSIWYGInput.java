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
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * November 07, 2007
 */
@Deprecated
/**
 * Should use org.exoplatform.webui.form.wysiwyg.UIFormWYSIWYGInput
 * */
public class UIFormWYSIWYGInput extends UIFormInputBase<String> {
  
  private int height_ = 300;
  private boolean isBasic_ = false;
  /**
   * number of rows
   */
  private int rows = 70;
  /**
   * number of columns
   */
  private int columns = 110;
  
  public UIFormWYSIWYGInput(String name, String bindingExpression, String value, boolean isBasic) {
    super(name, bindingExpression, String.class);
    this.value_ = value ;
    this.isBasic_ = isBasic ;
  }
  
  public int getHeight() { return height_ ; }
  public void setHeight(int height) { this.height_ = height; }
  public int getColumns() { return columns; }

  public void setColumns(int columns) { this.columns = columns; }
  
  public int getRows() { return rows; }

  public void setRows(int rows) { this.rows = rows; }

  public void decode(Object input, WebuiRequestContext context) throws Exception {
    value_ = (String) input;
    if(value_ != null && value_.length() == 0) value_ = null ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    String toolbarSet = "Default" ;
    if(isBasic_) toolbarSet = "Basic" ;
    StringBuilder jsExec = new StringBuilder("new FCKeditor('").append(getName()).
                           append("', null, ").append(height_).append(", '" + toolbarSet + "'").
                           append(").ReplaceTextarea();") ;
    
    Writer w =  context.getWriter() ;
    
    if (value_ == null) value_ = "" ;
    value_ = value_.replaceAll("'", "\\\\'");
    value_ = value_.replaceAll("[\r\n]", "");
    w.write("<textarea id='" + getName() + "' rows='" + String.valueOf(rows) + "' cols='" + String.valueOf(columns) + "' name='" + getName() + "'>" + value_ + "</textarea>");
    if (this.isMandatory()) w.write(" *");
    context.getJavascriptManager().addJavascript(jsExec.toString());
  }
}