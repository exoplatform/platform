/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import org.exoplatform.portal.config.model.Properties;
import org.exoplatform.portal.webui.portal.UIPortalComponent;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * Jun 14, 2007  
 */
public class UIApplication  extends UIPortalComponent {
  
  private Properties properties;
  
  private boolean  showInfoBar = true ;
  private boolean  showWindowState = true ;
  
  private String   description;
  private String   icon;
  
  public Properties getProperties() {
    if(properties == null) properties  = new Properties();
    return properties; 
  }
  
  public void setProperties(Properties properties) { this.properties = properties; }
  
  public boolean getShowWindowState() { return showWindowState ; }
  public void    setShowWindowState(Boolean b) { showWindowState = b ; }
  
  public boolean getShowInfoBar() { return showInfoBar ; }
  public void    setShowInfoBar(Boolean b) {showInfoBar = b ;}
  
  public String getDescription() {  return  description ; }
  public void   setDescription(String s) { description = s ;}
  
  public String getIcon() { return icon ; }
  public void   setIcon(String s) { icon = s ; } 
}
