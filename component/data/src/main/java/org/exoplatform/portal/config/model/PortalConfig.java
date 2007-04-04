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
public class PortalConfig extends Component {
  
	private String owner ;
  private String locale ;
  private String viewPermission ;
  private String editPermission  ;
  private String skin;
  protected String title ;
  
  private Container widgetLayout;
  private Container portalLayout;
  
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
  
  public String getTitle() { return title ; }
  public void   setTitle(String s) { title = s ; }
  
  public Container   getWidgetLayout() { return widgetLayout; }
  public void setWidgetLayout(Container container) { widgetLayout = container; }
  
  public Container   getPortalLayout() { return portalLayout; }
  public void setPortalLayout(Container container) { portalLayout = container; }
  
  static public class PortalConfigSet {
    private ArrayList<PortalConfig> portalConfigs ;
    
    public ArrayList<PortalConfig> getPortalConfigs() { return portalConfigs ; }
    public void setPortalConfigs(ArrayList<PortalConfig> list) { portalConfigs = list ; }
  }
  
}