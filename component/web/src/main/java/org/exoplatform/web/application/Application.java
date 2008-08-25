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
package org.exoplatform.web.application;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.resolver.ApplicationResourceResolver;
/**
 * Created by The eXo Platform SAS
 * May 7, 2006
 */
abstract public class Application extends BaseComponentPlugin {
  
  final static public String JSR168_APPLICATION_TYPE = "jsr168Application" ;
  final static public String EXO_PORTLET_TYPE = "portlet" ;
  final static public String EXO_WIDGET_TYPE = "eXoWidget" ;
  final static public String EXO_PORTAL_TYPE = "eXoPortal" ;
  final static public String EXO_GAGGET_TYPE = "eXoGadget" ;
 
  private List<ApplicationLifecycle>  lifecycleListeners_ ;   
  private ApplicationResourceResolver resourceResolver_ ;
  private Hashtable<String, Object> attributes_ =  new Hashtable<String, Object>() ;
  
  //TODO: Replcate by  method public ApplicationInfo getApplicationInfo() ;
  abstract public String getApplicationId() ;
  abstract public String getApplicationType() ;
  abstract public String getApplicationGroup() ;
  abstract public String getApplicationName() ;
  
  final public ApplicationResourceResolver getResourceResolver() { return resourceResolver_ ; }  
  final public void setResourceResolver(ApplicationResourceResolver resolver) { resourceResolver_ = resolver ; }
  
  final public Object  getAttribute(String name) { return attributes_.get(name) ; }  
  final public void    setAttribute(String name, Object value) { attributes_.put(name, value) ; }
  
  abstract public ResourceBundle  getResourceBundle(Locale locale) throws Exception ;
  abstract public ResourceBundle  getOwnerResourceBundle(String username, Locale locale) throws Exception ;
  
  public ExoContainer getApplicationServiceContainer() {  return ExoContainerContext.getCurrentContainer(); }
  
  final public List<ApplicationLifecycle> getApplicationLifecycle(){ return lifecycleListeners_; }
  final public void setApplicationLifecycle(List<ApplicationLifecycle> list) { lifecycleListeners_ = list ; }
  
  public void onInit() throws Exception {        
    for(ApplicationLifecycle lifecycle :  lifecycleListeners_) {
      lifecycle.onInit(this) ;
    }
  }

  public void onDestroy() throws Exception {
    for(ApplicationLifecycle lifecycle :  lifecycleListeners_) lifecycle.onDestroy(this) ;
  }
}