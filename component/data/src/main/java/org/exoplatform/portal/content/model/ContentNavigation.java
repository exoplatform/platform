/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content.model;

import java.util.ArrayList;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 30, 2006
 */
public class ContentNavigation {
  
	private String			owner;
  private String      description ;
	private ArrayList<ContentNode>	contentNodes;
  
  public ContentNode getNode(int idx) {	return contentNodes.get(idx); }
  
  public ContentNode removeNode(int idx) {  
    ContentNode node = contentNodes.get(idx);
    contentNodes.remove(idx);
    return node;
  }  
  
	public void addNode(ContentNode node) {
    if(contentNodes == null) contentNodes = new ArrayList<ContentNode>();
    contentNodes.add(node); 
  }
  
  public ArrayList<ContentNode> getNodes(){ return contentNodes; }

	public String getOwner() { return owner;}
	public void setOwner(String owner) { this.owner = owner; }
  
  public void setDescription(String des){  description = des; }  
  public String getDescription(){  return description; }
  
}