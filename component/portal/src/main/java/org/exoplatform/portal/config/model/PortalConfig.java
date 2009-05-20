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
 * @version: $Id: PortalConfig.java,v 1.7 2004/08/06 03:02:29 tuan08 Exp $
 **/
public class PortalConfig {
  
  final public static String USER_TYPE = "user";
  final public static String GROUP_TYPE = "group";
  final public static String PORTAL_TYPE = "portal";
  final public static String SESSION_ALIVE = "sessionAlive" ;
  final public static String SESSION_ON_DEMAND = "onDemand" ;
  final public static String SESSION_ALWAYS = "always" ;
  final public static String SESSION_NEVER = "never" ;
  
	private String    name ;
//	private String    factoryId;
  private String    locale ;
  
  private String[]  accessPermissions ;
  private String editPermission;
  
  private Properties properties ;
  
  private String    skin;
  private String    title;
  
  private Container portalLayout;
  
  private String    creator ;
  private String    modifier ;
  
  private transient boolean modifiable ;
  
  public PortalConfig() {
    portalLayout = new Container();
  }
  
  public String getName() { return name ; }
  public void   setName(String s) { name = s  ; } 
 
  public String getLocale() { return locale ; }
  public void   setLocale(String s) { locale = s ; }
  
  public String [] getAccessPermissions() { return accessPermissions ; }
  public void   setAccessPermissions(String[] s) { accessPermissions = s ; }
  
  public String getEditPermission() { return editPermission; }
  public void setEditPermission(String editPermission) { this.editPermission = editPermission; }
  
  public String getSkin() { 
    if(skin == null || skin.length() < 1) return "Default";
    return skin; 
  }
  public void setSkin(String s ) { skin = s; }
  
  public Container   getPortalLayout() { return portalLayout; }
  public void setPortalLayout(Container container) { portalLayout = container; }
  
  public boolean isModifiable() { return modifiable ; }
  public void  setModifiable(boolean b) { modifiable = b ; }
  
//  public String getFactoryId() { 
//    return factoryId; 
//  }
//  public void setFactoryId(String factoryId) { this.factoryId = factoryId; }
  
  public String getCreator()  {  return creator ; }
  public void   setCreator(String s) { creator = s ; }
  
  public String getModifier() { return modifier ; }
  public void   setModifier(String s) { modifier = s ; }
  
  public String getTitle() { return title ; }
  public void   setTitle(String value) { title = value ; }
  
  public Properties getProperties() { return properties ; }
  public void setProperties(Properties props) { properties = props; }
  
  public String getProperty(String name) {
    if(name == null || properties == null || !properties.containsKey(name)) {
      throw new NullPointerException() ;
    }
    return properties.get(name) ;
  }
  
  public String getProperty(String name, String defaultValue) {
    String value = getProperty(name) ;
    if(value != null) return value ;
    return defaultValue ;
  }
  
  public void setProperty(String name, String value) {
    if(name == null || properties == null) throw new NullPointerException() ;
    if(value == null) properties.remove(name) ;
    else properties.setProperty(name, value) ;
  }
  
  public void removeProperty(String name) {
    if(name == null || properties == null) throw new NullPointerException() ;
    properties.remove(name) ;
  }
  
  static public class PortalConfigSet {
    private ArrayList<PortalConfig> portalConfigs ;
    
    public ArrayList<PortalConfig> getPortalConfigs() { return portalConfigs ; }
    public void setPortalConfigs(ArrayList<PortalConfig> list) { portalConfigs = list ; }
  }

}