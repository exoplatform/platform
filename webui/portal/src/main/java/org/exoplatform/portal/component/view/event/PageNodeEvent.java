/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.event;

import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.event.Event;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 5, 2006
 */
public class  PageNodeEvent<T extends UIComponent> extends Event<T> {
  
  final static  public String CHANGE_PAGE_NODE = "ChangePageNode" ;
  //final static  public String REMOVE_NODE = "removeNode" ;
  
  private String  sourceNodeUri ; 
  private String  targetNodeUri ; 
  
  public PageNodeEvent(T source, String name, String sourceNodeUri, String  targetNodeUri) {
    super(source, name, null);
    this.sourceNodeUri = sourceNodeUri ;
    this.targetNodeUri = targetNodeUri ;
  }
  
  public String getSourceNodeUri() { return sourceNodeUri ; }
  
  public String getTargetNodeUri() { return targetNodeUri ; }

}