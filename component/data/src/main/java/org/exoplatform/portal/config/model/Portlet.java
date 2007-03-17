/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

/**
 * May 13, 2004
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: Portlet.java,v 1.7 2004/09/30 01:00:05 tuan08 Exp $
 **/
public class Portlet extends Component {
  
  private String title;
  private String windowId ;
  private String portletStyle ;
  private String icon ; 
  private String description;

  private boolean  showInfoBar = true ;
  private boolean  showWindowState = true ;
  private boolean  showPortletMode = true ;
  
  public String getTitle() { return title ; }
  public void   setTitle(String s) { title = s ;}
  
  public String getWindowId() { return windowId ; }
  public void   setWindowId(String s) { windowId = s ;}
  
  public String getDescription() {  return  description ; }
  public void   setDescription(String s) { description = s ;}
  
  public String getPortletStyle() {  return  portletStyle ; }
  public void   setPortletStyle(String s) { portletStyle = s ;}
  
  public boolean getShowInfoBar() { return showInfoBar ; }
  public void    setShowInfoBar(Boolean b) { showInfoBar = b ; }
  
  public boolean getShowWindowState() { return showWindowState ; }
  public void    setShowWindowState(Boolean b) { showWindowState = b ; }
  
  public boolean getShowPortletMode() { return showPortletMode ; }
  public void    setShowPortletMode(Boolean b) { showPortletMode = b ; }
  
  public String getIcon() { return icon ; }
  public void setIcon(String s) { icon = s ; }
}