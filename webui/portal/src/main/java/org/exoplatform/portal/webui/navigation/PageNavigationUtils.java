/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.List;

import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jun 27, 2007  
 */
public class PageNavigationUtils {
  
  public static void removeNode(List<PageNode> list, String uri) {
    if(list == null) return;
    for(PageNode pageNode: list){
      if(pageNode.getUri().equalsIgnoreCase(uri)) {
        list.remove(pageNode);
        return;
      }
    }
  }
  
  public static PageNode[] searchPageNodesByUri(PageNode node, String uri){
    if(node.getUri().equals(uri) ) return new PageNode[] {null, node};
    if(node.getChildren() == null) return null;
    List<PageNode> children = node.getChildren(); 
    for(PageNode ele : children) {
      PageNode[] returnNodes = searchPageNodesByUri(ele, uri);
      if(returnNodes != null) {
        if(returnNodes[0] == null) returnNodes[0] = node;
        return returnNodes;
      }
    }
    return null;
  }
  
  public static PageNode[] searchPageNodesByUri(PageNavigation nav, String uri){
    if(nav.getNodes() == null) return null;
    List<PageNode> nodes = nav.getNodes(); 
    for(PageNode ele : nodes) {
      PageNode [] returnNodes = searchPageNodesByUri(ele, uri);
      if(returnNodes != null) return returnNodes;
    }
    return null;
  }
  
  public static PageNode searchPageNodeByUri(PageNode node, String uri){
    if(node.getUri().equals(uri) ) return node;
    if(node.getChildren() == null) return null;
    List<PageNode> children = node.getChildren(); 
    for(PageNode ele : children) {
      PageNode returnNode = searchPageNodeByUri(ele, uri);
      if(returnNode != null) return returnNode;
    }
    return null;
  }
  
  public static PageNode searchPageNodeByUri(PageNavigation nav, String uri){
    if(nav.getNodes() == null) return null;
    List<PageNode> nodes = nav.getNodes(); 
    for(PageNode ele : nodes) {
      PageNode returnNode = searchPageNodeByUri(ele, uri);
      if(returnNode != null) return returnNode;
    }
    return null;
  }
  
}
