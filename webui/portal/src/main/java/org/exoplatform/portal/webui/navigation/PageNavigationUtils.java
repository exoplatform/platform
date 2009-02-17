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
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.application.WebuiRequestContext;

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
  
  public static Object searchParentNode(PageNavigation nav, String uri){
    if(nav.getNodes() == null) return null;
    int last = uri.lastIndexOf("/");
    String parentUri = "";
    if (last > -1) parentUri = uri.substring(0, uri.lastIndexOf("/"));
    for(PageNode ele : nav.getNodes()) {
      if( ele.getUri().equals(uri)) return nav;
    }
    if(parentUri.equals("")) return null;
    return searchPageNodeByUri(nav, parentUri);
  }

  public static PageNavigation filter(PageNavigation nav, String userName) throws Exception {
    PageNavigation filter = nav.clone();
    filter.setNodes(new ArrayList<PageNode>());
//    if(nav.getNodes() == null || nav.getNodes().size() < 1) return null;
    for(PageNode node: nav.getNodes()){
      PageNode newNode = filter(node, userName);
      if(newNode != null ) filter.addNode(newNode);
    }
    return filter;
  }

  public static PageNode filter(PageNode node, String userName) throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    ExoContainer container = context.getApplication().getApplicationServiceContainer() ;
    UserPortalConfigService userService = (UserPortalConfigService)container.getComponentInstanceOfType(UserPortalConfigService.class);
    if(!node.isDisplay() || 
        (node.getPageReference() != null && userService.getPage(node.getPageReference(), userName) == null)) return null;
    PageNode copyNode = node.clone();
    copyNode.setChildren(new ArrayList<PageNode>());
    List<PageNode> children = node.getChildren();
    if(children != null) {
      for(PageNode child: children){
        PageNode newNode = filter(child, userName);
        if(newNode != null ) copyNode.getChildren().add(newNode);
      }
    }
    if((copyNode.getChildren() == null || copyNode.getChildren().size() == 0) 
        && (copyNode.getPageReference() == null)) return null;
    return copyNode;
  }
}
