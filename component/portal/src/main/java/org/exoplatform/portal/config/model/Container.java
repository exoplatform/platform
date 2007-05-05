/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.ArrayList;
/**
 * May 13, 2004
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: Container.java,v 1.8 2004/11/03 01:23:55 tuan08 Exp $
 **/
public class Container {
  
  protected String id ;
  
  protected String name ;
  protected String icon;
  protected String decorator ;
  protected String template;
  
  protected String   factoryId;
  
  protected String title;
  protected String description;
  
  protected String width;
  protected String height;
  
  protected ArrayList<Object> children ;
  
  public Container() {
    children = new ArrayList<Object>();
  }
  
  public String getId() { return id ;}
  public void   setId(String s) { id = s ; }
  
  public String getName() { return name ; }
  public void   setName(String s) { name = s ; }
  
  public String getIcon() { return icon; }
  public void setIcon(String icon) { this.icon = icon;  }
  
  public ArrayList<Object>   getChildren() {  return children ; }
  public void setChildren(ArrayList<Object> children) { this.children = children; }

  public String getHeight() { return height; }
  public void setHeight(String height) { this.height = height; }

  public String getWidth() { return width; }
  public void setWidth(String width) { this.width = width; }
  
  public String getDecorator() { return decorator ; }
  public void   setDecorator(String s) { decorator = s ; }
  
  public String getDescription() {  return  description ; }
  public void   setDescription(String des) { description = des ; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  public String getFactoryId() { return factoryId; }
  public void setFactoryId(String factoryId) { this.factoryId = factoryId; }

  public String getTemplate() { return template; }
  public void setTemplate(String template) { this.template = template; }
  
}