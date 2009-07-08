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
package org.exoplatform.portal.config;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;

public class UserPortalConfig {
  
  private PortalConfig  portal ;
  
  private List<PageNavigation> navigations ;
  
  public UserPortalConfig(){
    
  }
  
  public UserPortalConfig(PortalConfig portal, List<PageNavigation> navigations){
    this.portal = portal;
    this.navigations = navigations;
  }
  
  public PortalConfig getPortalConfig() { return portal ; }
  public void   setPortal(PortalConfig portal) {  this.portal =  portal ; }
  
  public void  setNavigations(List<PageNavigation> navs) { navigations = navs ; }
  public List<PageNavigation>  getNavigations()  { return navigations ; }
  
  public void addNavigation(PageNavigation nav) {
    if(navigations == null) navigations = new ArrayList<PageNavigation>() ;
    if(nav == null) return;
    navigations.add(nav) ;
  }
}