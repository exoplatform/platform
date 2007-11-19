/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.ArrayList;
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
  
  public PageNode clone() {
    PageNode newNode = new PageNode() ;
    newNode.setUri(uri);
    newNode.setLabel(label);
    newNode.setIcon(icon);
    newNode.setName(name);
    newNode.setResolvedLabel(resolvedLabel) ;
    newNode.setPageReference(pageReference);
    newNode.setModifiable(modifiable);
    if(children == null || children.size() < 1) return newNode;
    for(PageNode ele : children) {
      newNode.getChildren().add(ele.clone());
    }
    return newNode;
  }
  
}