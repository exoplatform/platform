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
 * Represents a info text element
 *
 */
public class UIFormInputInfo extends UIFormInputBase<String> {
  
  public UIFormInputInfo(String name, String bindingExpression, String value) {
    super(name, bindingExpression, String.class);
    this.value_ = value ;
  }
    
  @SuppressWarnings("unused")
  public void decode(Object input,  WebuiRequestContext context) throws Exception {    
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    w.append("<span id=\"").append(getId()).append("\" class=\"").append(getId()).append("\">") ;
    if (value_ != null) w.write(value_);    
    w.write("</span>");
  }
  
}