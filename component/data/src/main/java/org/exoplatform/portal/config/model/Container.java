/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.ArrayList;
/**
 * May 13, 2004
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: Container.java,v 1.8 2004/11/03 01:23:55 tuan08 Exp $
 **/
public class Container extends Component {
  
  protected String title ;
  protected String icon;
  protected ArrayList<Component> children ;
  
  public String getTitle() { return title ; }
  public void   setTitle(String s) { title = s ; }
  
  public String getIcon() { return icon; }
  public void setIcon(String icon) { this.icon = icon;  }
  
  public ArrayList<Component>   getChildren() {  return children ; }
  public void setChildren(ArrayList<Component> children) { this.children = children; }
  
}