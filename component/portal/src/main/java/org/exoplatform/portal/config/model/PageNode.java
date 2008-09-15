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
package org.exoplatform.portal.config.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.exoplatform.commons.utils.ExpressionUtil;

public class PageNode  {
  
  private ArrayList<PageNode> children = new ArrayList<PageNode>(5) ;
  private String uri ;
  private String label ;
  private String icon ;
  private String name;
  private String resolvedLabel ;
  private Date startPublicationDate ;
  private Date endPublicationDate ;
  private boolean showPublicationDate = false ; 
  
  private boolean visible = true ;
  private String pageReference ;
  
  private transient boolean modifiable ;
  
  public PageNode() {  }
  
  public String getUri() { return uri ; }
  public void   setUri(String s) { uri = s ; }

  public String getLabel() { return label ; }
  public void   setLabel(String s) {
    label = s ;
    resolvedLabel = s ;
  }
  
  public String getIcon() { return icon ; }
  public void   setIcon(String s) { icon = s ; }

  public String getPageReference() { return pageReference ;}  
  public void   setPageReference(String s) { pageReference = s ;}
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  
  public String getResolvedLabel() { return resolvedLabel ;}
  public void setResolvedLabel(String res) { resolvedLabel = res ;}
  public void setResolvedLabel(ResourceBundle res) {
    resolvedLabel = ExpressionUtil.getExpressionValue(res, label) ;
    if(resolvedLabel == null) resolvedLabel = getName() ;
  }
  
  public List<PageNode> getChildren() { return children ;  }
  public void setChildren(ArrayList<PageNode> list) { children = list ; }
  
  public boolean isModifiable() { return modifiable ; }
  public void    setModifiable(boolean b) { modifiable = b ; }  
  
  public Date getStartPublicationDate() { return startPublicationDate ; }
  public void setStartPublicationDate(Date startDate) { startPublicationDate = startDate ; }

  public Date getEndPublicationDate() { return endPublicationDate ; }
  public void setEndPublicationDate(Date endDate) { endPublicationDate = endDate ; }

  public boolean isVisible() {
    if(visible && showPublicationDate) {
      if(endPublicationDate == null) return true ;
      else if(endPublicationDate.compareTo(Calendar.getInstance().getTime()) < 0 ) return false ;
    }
    return visible ; 
  }
  
  public void setVisible(Boolean b) { visible = b.booleanValue() ; }
  
  public void setShowPublicationDate(Boolean show) { showPublicationDate = show.booleanValue() ; }
  public boolean isShowPublicationDate() { return showPublicationDate ; }
  
  public PageNode clone() {
    PageNode newNode = new PageNode() ;
    newNode.setUri(uri);
    newNode.setLabel(label);
    newNode.setIcon(icon);
    newNode.setName(name);
    newNode.setResolvedLabel(resolvedLabel) ;
    newNode.setPageReference(pageReference);
    newNode.setModifiable(modifiable);
    newNode.setShowPublicationDate(showPublicationDate) ;
    newNode.setStartPublicationDate(startPublicationDate) ;
    newNode.setEndPublicationDate(endPublicationDate) ;
    newNode.setVisible(visible) ;
    if(children == null || children.size() < 1) return newNode;
    for(PageNode ele : children) {
      newNode.getChildren().add(ele.clone());
    }
    return newNode;
  }
  
}