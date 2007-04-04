/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.ArrayList;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Apr 3, 2007  
 */
public class PortalLayout extends Component {

  protected ArrayList<Component> children ;
  
  public ArrayList<Component>   getChildren() {  return children ; }
  public void setChildren(ArrayList<Component> children) { this.children = children; }
}
