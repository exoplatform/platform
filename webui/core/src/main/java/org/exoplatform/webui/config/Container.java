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
package org.exoplatform.webui.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 19, 2006
 */
public class Container extends Component {
  
  protected String title ;  
  protected List<Component> children = new ArrayList<Component>(5) ;
  
  public String getTitle() { return title ; }
  public void   setTitle(String s) { title = s ; } 
  
  public List   getChildren() {  return children ; }
  
  @SuppressWarnings("unchecked")
  public void   setChildren(List l) { children = l ; }
  public void   addChild(Component comp) { children.add(comp) ; }  
  
  public void addComponent(Component comp) {   children.add(comp) ;  }
  public Iterator<Component> getChildIterator() { return children.iterator() ; }
  
}
