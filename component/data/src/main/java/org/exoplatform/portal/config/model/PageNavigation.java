/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.ArrayList;

/**
 * Jul 18, 2004 
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: NodeNavigation.java,v 1.1 2004/07/20 12:41:07 tuan08 Exp $
 */
public class PageNavigation {
  
	private String			owner;
  private String      accessPermission ;
  private String      editPermission ;
  private String      description ;
  private boolean     modifiable ;
	private ArrayList<PageNode>	pageNodes = new ArrayList<PageNode>();
  private int         priority = 0 ;
  
  public PageNode getNode(int idx) {	return pageNodes.get(idx); }
  
  public PageNode removeNode(int idx) {  
    PageNode node = pageNodes.get(idx);
    pageNodes.remove(idx);
    return node;
  }  
  
  public void removeNode(String uri) {
    for(PageNode pageNode: pageNodes){
      if(pageNode.getUri().equalsIgnoreCase(uri)) {
        pageNodes.remove(pageNode);
        break;
      }
    }
  }
  
  
  public void removeNode(PageNode page) { pageNodes.remove(page); }
  
	public void addNode(PageNode node) {
    if(pageNodes == null) pageNodes = new ArrayList<PageNode>();
    pageNodes.add(node); 
  }
  
  public ArrayList<PageNode> getNodes(){ return pageNodes; }
  public void setNodes(ArrayList<PageNode> nodes) { pageNodes = nodes; }

	public String getOwner() { return owner;}
	public void setOwner(String owner) { this.owner = owner; }
  
  public void setAccessPermission(String accessPermission){
    this.accessPermission = accessPermission;
  }  
  public String getAccessPermission(){  return accessPermission; }
  
  public void setEditPermission(String editPermission){  this.editPermission = editPermission; }  
  public String getEditPermission(){   return editPermission; }
  
  public boolean getModifiable(){  return modifiable; }
  public void    setModifiable(boolean b) { modifiable = b ; }
  
  public void setDescription(String des){  description = des; }  
  public String getDescription(){  return description; }
  
  public int  getPriority() { return priority ; }
  public void setPriority(int i) { priority  = i ; }
  
}