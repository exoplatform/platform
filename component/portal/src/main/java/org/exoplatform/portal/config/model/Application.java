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


/**
 * May 13, 2004
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: Portlet.java,v 1.7 2004/09/30 01:00:05 tuan08 Exp $
 **/
public class Application {
  
  private String id;
  private String instanceId ;
  private String applicationType = org.exoplatform.web.application.Application.EXO_PORTLET_TYPE;
  private String title;
  
  private String icon ; 
  private String description;

  private boolean  showInfoBar = true ;
  private boolean  showApplicationState = true ;
  private boolean  showApplicationMode = true ;
  private String theme;
  private String width ;
  private String height ;
  private Properties properties;
  
  private String[] accessPermissions ;
  private String editPermission ;
  private transient boolean isModifiable ;
  
  public Application(){
  }
  
  public String getWidth() { return width ; }
  public void   setWidth(String s) { width = s ;}
  
  public String getHeight() { return height ; }
  public void   setHeight(String s) { height = s ;}
  
  public String getId() { return id ;}
  public void   setId(String value) { id = value ; }
  
  public String[] getAccessPermissions() { return accessPermissions; }
  public void setAccessPermissions(String[] accessPermissions) {
    this.accessPermissions = accessPermissions;
  }

  public String getEditPermission() { return editPermission; }
  public void setEditPermission(String editPermission) {
    this.editPermission = editPermission;
  }
  
  public boolean isModifiable() { return isModifiable ; }
  public void setModifiable(boolean modifiable) { isModifiable = modifiable ; }
  
  public String getInstanceId() { return instanceId ; }
  public void   setInstanceId(String value) { instanceId = value ;}
  
  public boolean getShowInfoBar() { return showInfoBar ; }
  public void    setShowInfoBar(Boolean b) { showInfoBar = b ; }
  
  public boolean getShowApplicationState() { return showApplicationState ; }
  public void    setShowApplicationState(Boolean b) { showApplicationState = b ; }
  
  public boolean getShowApplicationMode() { return showApplicationMode ; }
  public void    setShowApplicationMode(Boolean b) { showApplicationMode = b ; }
  
  public String getIcon() { return icon ; }
  public void setIcon(String value) { icon = value ; }

  public String getApplicationType() { return applicationType; }
  public void setApplicationType(String applicationId) { this.applicationType = applicationId; }
  
  public String getDescription() {  return  description ; }
  public void   setDescription(String des) { description = des ; }
  
  public String getTitle() { return title ; }
  public void   setTitle(String value) { title = value ; }

  public Properties getProperties() {
    if(properties == null) properties  = new Properties();
    return properties; 
  }
  public void setProperties(Properties properties) { this.properties = properties; }

  public String getTheme() { return theme; }
  public void setTheme(String theme) { this.theme = theme; }
  
}