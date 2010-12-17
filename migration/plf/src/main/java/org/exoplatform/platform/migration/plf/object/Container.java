/**
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.platform.migration.plf.object;

import java.util.ArrayList;

import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.pom.data.ContainerData;

/**
 * @author Tuan Nguyen
 **/
public class Container extends ModelObject {

  protected String id;

  protected String name;

  protected String icon;

  protected String template;

  protected String factoryId;

  protected String title;

  protected String description;

  protected String width;

  protected String height;

  // Here to please jibx binding but not used anymore
  protected String decorator;

  protected String[] accessPermissions;

  protected ArrayList<ModelObject> children;

  public Container() {
    children = new ArrayList<ModelObject>();
  }

  public Container(String storageId) {
    super(storageId);

    //
    this.children = new ArrayList<ModelObject>();
  }

  public String getId() {
    return id;
  }

  public void setId(String s) {
    id = s;
  }

  public String getName() {
    return name;
  }

  public void setName(String s) {
    name = s;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public ArrayList<ModelObject> getChildren() {
    return children;
  }

  public void setChildren(ArrayList<ModelObject> children) {
    this.children = children;
  }

  public String getHeight() {
    return height;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  public String getWidth() {
    return width;
  }

  public void setWidth(String width) {
    this.width = width;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String des) {
    description = des;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getFactoryId() {
    return factoryId;
  }

  public void setFactoryId(String factoryId) {
    this.factoryId = factoryId;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String[] getAccessPermissions() {
    return accessPermissions;
  }

  public void setAccessPermissions(String[] accessPermissions) {
    this.accessPermissions = accessPermissions;
  }

  public String getDecorator() {
    // Here to please jibx binding but not used anymore
    return null;
  }

  // Here to please jibx binding but not used anymore
  public void setDecorator(String decorator) {
  // Here to please jibx binding but not used anymore
  }

  @Override
  public ContainerData build() {
    return null;
  }
}