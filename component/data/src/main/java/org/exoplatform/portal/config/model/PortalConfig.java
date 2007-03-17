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
public class PortalConfig extends Container {
  
	private String owner ;
  private String locale ;
  private String viewPermission ;
  private String editPermission  ;
  private String skin;
  
  public String getOwner() { return owner ; }
  public void   setOwner(String s) { owner = s  ; } 
 
  public String getLocale() { return locale ; }
  public void   setLocale(String s) { locale = s ; }
  
  public String getViewPermission() { return viewPermission ; }
  public void   setViewPermission(String s) { viewPermission = s ; }
  
  public String getEditPermission() { return editPermission ; }
  public void   setEditPermission(String s) { editPermission = s ; }
  
  public String getSkin() { return skin; }
  public void setSkin(String s ) { skin = s; }
  
  static public class PortalConfigSet {
    private ArrayList<PortalConfig> portalConfigs ;
    
    public ArrayList<PortalConfig> getPortalConfigs() { return portalConfigs ; }
    public void setPortalConfigs(ArrayList<PortalConfig> list) { portalConfigs = list ; }
  }
  
}