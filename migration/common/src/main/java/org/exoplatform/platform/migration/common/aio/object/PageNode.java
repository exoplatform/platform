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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PageNode {

  private ArrayList<PageNode> children = new ArrayList<PageNode>(5);
  private String uri;
  private String label;
  private String icon;
  private String name;
  private Date startPublicationDate;
  private Date endPublicationDate;
  private boolean showPublicationDate = false;

  private boolean visible = true;
  private String pageReference;

  private transient boolean modifiable;

  public PageNode() {}

  public String getUri() {
    return uri;
  }

  public void setUri(String s) {
    uri = s;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String s) {
    label = s;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String s) {
    icon = s;
  }

  public String getPageReference() {
    return pageReference;
  }

  public void setPageReference(String s) {
    pageReference = s;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<PageNode> getChildren() {
    return children;
  }

  public void setChildren(ArrayList<PageNode> list) {
    children = list;
  }

  public boolean isModifiable() {
    return modifiable;
  }

  public void setModifiable(boolean b) {
    modifiable = b;
  }

  public Date getStartPublicationDate() {
    return startPublicationDate;
  }

  public void setStartPublicationDate(Date startDate) {
    startPublicationDate = startDate;
  }

  public Date getEndPublicationDate() {
    return endPublicationDate;
  }

  public void setEndPublicationDate(Date endDate) {
    endPublicationDate = endDate;
  }

  public boolean isVisible() {
    return visible;
  }

  public boolean getVisible() {
    return visible;
  }

  public void setVisible(boolean b) {
    visible = b;
  }

  public void setShowPublicationDate(boolean show) {
    showPublicationDate = show;
  }

  public boolean isShowPublicationDate() {
    return showPublicationDate;
  }

  public PageNode getChild(String name) {
    if (children == null)
      return null;
    for (PageNode node : children) {
      if (node.getName().equals(name))
        return node;
    }
    return null;
  }
}