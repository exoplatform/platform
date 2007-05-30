/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.resolver.ApplicationResourceResolver;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
abstract public class Application extends BaseComponentPlugin {
  
  final  static public String JSR168_APPLICATION_TYPE = "jsr168Application" ;
  final  static public String EXO_APPLICATION_TYPE = "eXoApplication" ;
  final  static public String EXO_WIDGET_TYPE = "eXoWidget" ;
  final  static public String EXO_PORTAL_TYPE = "eXoPortal" ;
 
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
  
  public ExoContainer getApplicationServiceContainer() {  return PortalContainer.getInstance() ; }
  
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