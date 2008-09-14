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
package org.exoplatform.portal.webui.portal;

import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
/**
 * Jun 5, 2006
 */
public class  PageNodeEvent<T extends UIComponent> extends Event<T> {
  
  final static  public String CHANGE_PAGE_NODE = "ChangePageNode" ;
  
  private String  targetNodeUri ; 
  
  public PageNodeEvent(T source, String name, String  targetNodeUri) {
    super(source, name, null);
    this.targetNodeUri = targetNodeUri ;
  }
  
  public String getTargetNodeUri() { return targetNodeUri ; }
}