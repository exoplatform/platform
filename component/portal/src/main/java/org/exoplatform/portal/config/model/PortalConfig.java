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
  private String    accessGroups;
  private transient String[]  accessGroup ;
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
  
  public String [] getAccessGroup() { return accessGroup ; }
  public void   setAccessGroup(String[] s) { accessGroup = s ; }
  
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
  
  public String getAccessGroups(){
    if(accessGroup == null)  return "";
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < accessGroup.length; i++) {
      builder.append(accessGroup[i]) ;
      if (i < accessGroup.length -1) builder.append(',');
    }
    return builder.toString();
  }
  public void setAccessGroups(String s){ 
    this.accessGroups = s;
    if(accessGroups == null) return ;
    accessGroup = accessGroups.split(",");
    for(int i = 0; i < accessGroup.length; i++) {
      accessGroup[i] = accessGroup[i].trim(); 
    }
  }
  
  static public class PortalConfigSet {
    private ArrayList<PortalConfig> portalConfigs ;
    
    public ArrayList<PortalConfig> getPortalConfigs() { return portalConfigs ; }
    public void setPortalConfigs(ArrayList<PortalConfig> list) { portalConfigs = list ; }
  }
  
}