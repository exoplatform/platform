/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.HashMap;

/**
 * May 13, 2004
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: Portlet.java,v 1.7 2004/09/30 01:00:05 tuan08 Exp $
 **/
public class Application {
  
  public final static String PORTLET_TYPE = "portlet";
  public final static String WIDGET_TYPE = "eXoWidget";
  public final static String EXO_APPLICATION_TYPE = "eXoApplication";
  
  private String id;
  private String instanceId ;
  private String applicationType = PORTLET_TYPE;
  private String title;
  
  private String icon ; 
  private String description;

  private boolean  showInfoBar = true ;
  private boolean  showApplicationState = true ;
  private boolean  showApplicationMode = true ;
  
  private int locationX;
  private int locationY;
  
  private HashMap<String, String> properties;
  
  public Application(){
  }
  
  public String getId() { return id ;}
  public void   setId(String value) { id = value ; }
  
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

  public int getLocationX() { return locationX; }
  public void setLocationX(int locationX) { this.locationX = locationX; }

  public int getLocationY() { return locationY; }
  public void setLocationY(int locationY) { this.locationY = locationY; }
  
  public HashMap<String, String> getProperites() { return properties; }
  public void setProperites(HashMap<String, String> properties) { this.properties = properties; }
  
}