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
  
  private String[] accessPermissions ;
  
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
  
  public String[] getAccessPermissions() { return accessPermissions; }
  public void setAccessPermissions(String[] accessPermissions) {
    this.accessPermissions = accessPermissions;
  }

}