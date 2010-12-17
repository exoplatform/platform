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
package org.exoplatform.platform.migration.common.aio.object;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class PageNavigation {
  private String ownerType;
  private String ownerId;
  private String description;
  private transient boolean modifiable;
  private String creator;
  private String modifier;
  private ArrayList<PageNode> pageNodes = new ArrayList<PageNode>();
  private int priority = 1;
  private long serialMark;

  /** The original document might be null. */
  public Document document;

  public PageNavigation() {}

  public int getId() {
    return getOwner().hashCode();
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerType() {
    return ownerType;
  }

  public void setOwnerType(String ownerType) {
    this.ownerType = ownerType;
  }

  public boolean isModifiable() {
    return modifiable;
  }

  public void setModifiable(boolean b) {
    modifiable = b;
  }

  public void setDescription(String des) {
    description = des;
  }

  public String getDescription() {
    return description;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int i) {
    priority = i;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String s) {
    creator = s;
  }

  public String getModifier() {
    return modifier;
  }

  public void setModifier(String s) {
    modifier = s;
  }

  public String getOwner() {
    return ownerType + "::" + ownerId;
  }

  public void addNode(PageNode node) {
    if (pageNodes == null)
      pageNodes = new ArrayList<PageNode>();
    pageNodes.add(node);
  }

  public ArrayList<PageNode> getNodes() {
    return pageNodes;
  }

  public void setNodes(ArrayList<PageNode> nodes) {
    pageNodes = nodes;
  }

  public long getSerialMark() {
    return serialMark;
  }

  public void setSerialMark(long serialModifiedKey) {
    this.serialMark = serialModifiedKey;
  }

  public PageNode getNode(String name) {
    for (PageNode node : pageNodes) {
      if (node.getName().equals(name))
        return node;
    }
    return null;
  }
}