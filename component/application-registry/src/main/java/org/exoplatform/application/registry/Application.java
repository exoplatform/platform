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

package org.exoplatform.application.registry;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 15 juin 2004
 *
 */
public class Application {

  //private String id;
  
  private String categoryName ;
  private String displayName;
  private String description;
  private Date createdDate;
  private Date modifiedDate;
  private ArrayList<String> accessPermissions ;
  private String applicationGroup ;
  private String applicationName ;
  private String applicationType ;
  
  private int minWidthResolution ;
  
  public String getId() { 
    return categoryName + "/" + applicationName ;
  }   
  
  public String getDisplayName() { return displayName; }  
  public void setDisplayName(String displayName) { this.displayName = displayName; }
  
  public String getDescription() {
    return description == null? "" : description;
  }  
  public void setDescription(String s) { description = s; }

  public Date getCreatedDate() { return createdDate; }  
  public void setCreatedDate(Date d) { createdDate = d; }

  public Date getModifiedDate() { return modifiedDate; }  
  public void setModifiedDate(Date d) { modifiedDate = d; }
  
  public String getCategoryName() {   return categoryName; }  
  public void setCategoryName(String s) { this.categoryName = s; }
  
  public void setAccessPermissions(ArrayList<String> accessPerms) { 
    accessPermissions = accessPerms;
    
  }  
  public ArrayList<String> getAccessPermissions() { 
    if(accessPermissions == null) accessPermissions = new ArrayList<String>();
    return  accessPermissions; }
  
  public String getApplicationGroup() { return applicationGroup; }
  public void setApplicationGroup(String applicationGroup) {
    this.applicationGroup = applicationGroup;
  }
  
  public String getApplicationName() { return applicationName; }
  public void setApplicationName(String applicationName) { this.applicationName = applicationName; }
  
  public String getApplicationType() { return applicationType; }
  public void setApplicationType(String applicationType) { this.applicationType = applicationType; }
  
  public int getMinWidthResolution() { return minWidthResolution; }
  public void setMinWidthResolution(int minWidthResolution) { 
    this.minWidthResolution = minWidthResolution; 
  }
}