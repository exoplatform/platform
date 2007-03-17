/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
