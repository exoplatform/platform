/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application.portlet;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.resolver.ApplicationResourceResolver;
import org.exoplatform.resolver.PortletResourceResolver;
import org.exoplatform.webui.application.WebuiApplication;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 26, 2006
 */
public class PortletApplication extends WebuiApplication {
  
  private PortletConfig portletConfig_ ;
  private String applicationId_ ;
  private PortletContext portletContext_;

  public PortletApplication(PortletConfig config) throws Exception {
    portletConfig_ = config ;
    portletContext_ = config.getPortletContext();
    String contextName = portletContext_.getPortletContextName();
    applicationId_ = contextName + "/" + config.getPortletName() + "/" + this.hashCode() ;
    
    ApplicationResourceResolver resolver = new ApplicationResourceResolver() ;
    resolver.addResourceResolver(new PortletResourceResolver(portletContext_, "app:")) ;
    resolver.addResourceResolver(new PortletResourceResolver(portletContext_, "par:")) ;
    setResourceResolver(resolver) ;
  }
  
  public PortletConfig getPortletConfig() { return portletConfig_ ; }
  
  public String getApplicationId() { return applicationId_ ; }
  
  public String getApplicationName() {  return portletConfig_.getPortletName() ; }

  public URL getResource(String url) throws Exception { return portletContext_.getResource(url) ; }

  public InputStream getInputStream(String url) throws Exception { return portletContext_.getResourceAsStream(url); }
  
  public ResourceBundle getResourceBundle(Locale locale) throws Exception {    
    return portletConfig_.getResourceBundle(locale) ;
  }
    
  @SuppressWarnings("unused")
  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {
    return null;
  }

  public String getApplicationInitParam(String name) { 
    String value = portletConfig_.getInitParameter(name);
    return value ;
  }
  
  public ExoContainer getApplicationServiceContainer() { return PortalContainer.getInstance() ; }
  
}