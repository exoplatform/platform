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
package org.exoplatform.portal.config;

import java.util.HashSet;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 23, 2006
 */
public class NewPortalConfig {
  
  private HashSet<String> predefinedOwner = new HashSet<String>(5);
  private String  ownerType;
  private String  templateOwner ;
  private String  templateLocation ;
  
  public HashSet<String> getPredefinedOwner() {   return predefinedOwner; }
  public void setPredefinedOwner(HashSet<String> s) {  this.predefinedOwner = s; }
  
  public String getTemplateLocation() {  return templateLocation; }
  public void setTemplateLocation(String s) { this.templateLocation = s; }
  
  public String getTemplateOwner() {  return templateOwner;}
  public void setTemplateOwner(String s) {  this.templateOwner = s; }
  
  public boolean isPredefinedOwner(String user) { return predefinedOwner.contains(user) ; }
  
  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }
  
}
