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

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
@ComponentConfig(template = "system:/groovy/webui/form/UIFormTableInputSet.gtmpl" )
public class UIFormTableInputSet extends UIFormInputSet {

  private String name ;
  private String [] columns ;  

  public UIFormTableInputSet() throws Exception {}
  
  public String getName() { return name ; }
  public void setName(String name) { this.name = name; }

  public String [] getColumns() { return columns; }

  public void setColumns(String [] columns) { this.columns = columns; }
  
  public void processDecode(WebuiRequestContext context) throws Exception {
    for(UIComponent child : getChildren())  {
      child.processDecode(context) ;
    }
  }

}