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

import org.apache.commons.lang.StringEscapeUtils;
import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Represents a textarea element
 * The value is stored in UIFormInputBase
 */
public class UIFormTextAreaInput extends UIFormStringInput {
  /**
   * number of rows
   */
  private int rows = 10;
  /**
   * number of columns
   */
  private int columns = 30;
  
  public UIFormTextAreaInput(String name, String bindingExpression, String value) {
    super(name, bindingExpression, value);
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    String value =  getValue() ;
    if(value == null) value = getDefaultValue();
    w.append("<textarea class='textarea' name='").append(getName()).
      append("' id='").append(getId()).append("'");
    if(readonly_) w.write(" readonly ");
    if(!enable_)  w.write(" disabled ");
    w.append(" rows=\"").append(String.valueOf(rows)).append("\"");
    w.append(" cols=\"").append(String.valueOf(columns)).append("\"");
    w.write(">");
    if(value != null) w.write(StringEscapeUtils.escapeHtml(value)) ;  
    w.write("</textarea>");
    if (this.isMandatory()) w.write(" *");
  }

  public int getColumns() { return columns; }

  public void setColumns(int columns) { this.columns = columns; }
  
  public int getRows() { return rows; }

  public void setRows(int rows) { this.rows = rows; }
  
}
