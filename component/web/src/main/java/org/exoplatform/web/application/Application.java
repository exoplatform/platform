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
import org.exoplatform.templates.groovy.ApplicationResourceResolver;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
abstract public class Application {
 
  private Application parent_ ;
  private List<ApplicationLifecycle>  lifecycleListeners_ ;   
  private ApplicationResourceResolver resourceResolver_ ;
  private Hashtable<String, Object> attributes_ =  new Hashtable<String, Object>() ;
  
  public void init() throws Exception {        
    for(ApplicationLifecycle lifecycle :  lifecycleListeners_) {
      lifecycle.init(this) ;
    }
  }

  public void destroy() throws Exception {
    for(ApplicationLifecycle lifecycle :  lifecycleListeners_) lifecycle.destroy(this) ;
  }
  
  abstract public String getApplicationId() ;
  
  final public Application getParentApplication() { return parent_ ; }
  
  final public ApplicationResourceResolver getResourceResolver() { return resourceResolver_ ; }  
  final public void setResourceResolver(ApplicationResourceResolver resolver) { resourceResolver_ = resolver ; }
  
  final public Object  getAttribute(String name) { return attributes_.get(name) ; }  
  final public void    setAttribute(String name, Object value) { attributes_.put(name, value) ; }
  
  abstract public String getApplicationName() ;
  
  abstract public ResourceBundle  getResourceBundle(Locale locale) throws Exception ;
  
  abstract public ResourceBundle  getOwnerResourceBundle(String username, Locale locale) throws Exception ;
  
  abstract public ExoContainer getApplicationServiceContainer() ;
  
  final public List<ApplicationLifecycle> getApplicationLifecycle(){ return lifecycleListeners_; }
  final public void setApplicationLifecycle(List<ApplicationLifecycle> list) { lifecycleListeners_ = list ; }
  
}