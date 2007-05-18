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
 * @version: $Id: PortalConfig.java,v 1.7 2004/08/06 03:02:29 tuan08 Exp $
 **/
public class PortalConfig {
  
  final public static String USER_TYPE = "user";
  final public static String GROUP_TYPE = "group";
  final public static String PORTAL_TYPE = "portal";
  
	private String    name ;
	private String    factoryId;
  private String    locale ;
  
  private String    accessPermission;
  private transient String[]  accessPermissions ;
  private String editPermission;
  
  private String    skin;
  private String    title;
  
  private Container widgetLayout;
  private Container portalLayout;
  
  private String    creator ;
  private String    modifier ;
  
  private transient boolean modifiable ;
  
  public PortalConfig() {
    portalLayout = new Container();
    widgetLayout = new Container();
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
  
  public Container getWidgetLayout() { return widgetLayout; }
  public void setWidgetLayout(Container container) { widgetLayout = container; }
  
  public Container   getPortalLayout() { return portalLayout; }
  public void setPortalLayout(Container container) { portalLayout = container; }
  
  public boolean isModifiable() { return modifiable ; }
  public void  setModifiable(boolean b) { modifiable = b ; }
  
  public String getFactoryId() { 
    return factoryId; 
  }
  public void setFactoryId(String factoryId) { this.factoryId = factoryId; }
  
  public String getCreator()  {  return creator ; }
  public void   setCreator(String s) { creator = s ; }
  
  public String getModifier() { return modifier ; }
  public void   setModifier(String s) { modifier = s ; }
  
  public String getTitle() { return title ; }
  public void   setTitle(String value) { title = value ; }
  
  public String getAccessPermission(){
    if(accessPermissions == null)  return "";
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < accessPermissions.length; i++) {
      builder.append(accessPermissions[i]) ;
      if (i < accessPermissions.length -1) builder.append(',');
    }
    return builder.toString();
  }
  public void setAccessPermission(String s){ 
    this.accessPermission = s;
    if(accessPermission == null) return ;
    accessPermissions = accessPermission.split(",");
    for(int i = 0; i < accessPermissions.length; i++) {
      accessPermissions[i] = accessPermissions[i].trim(); 
    }
  }
  
  static public class PortalConfigSet {
    private ArrayList<PortalConfig> portalConfigs ;
    
    public ArrayList<PortalConfig> getPortalConfigs() { return portalConfigs ; }
    public void setPortalConfigs(ArrayList<PortalConfig> list) { portalConfigs = list ; }
  }
  
  static public class WidgetSet {
    
    private ArrayList<Container> pages ;
    
    public WidgetSet() { pages = new ArrayList<Container>(); }
    
    public ArrayList<Container> getWidgets() { return pages ; }
    public void setWidgets(ArrayList<Container> list) { pages = list ; }
  }
  
}