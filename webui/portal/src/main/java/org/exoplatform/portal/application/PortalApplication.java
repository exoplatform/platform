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
package org.exoplatform.portal.application;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.resolver.ApplicationResourceResolver;
import org.exoplatform.resolver.ServletResourceResolver;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.webui.application.WebuiApplication;

/**
 * The PortalApplication class is an implementation of the WebuiApplication abstract class
 * which defines the type of application that can be deployed in our framework (that includes 
 * portal, portlets, widgets...)
 * 
 * This class is a wrapper of all portal information such as ResourceBundle for i18n, the current 
 * ExoContainer in use as well as the init parameters defined along with the servlet web.xml
 */
public class PortalApplication extends WebuiApplication {
  
  protected static Log log = ExoLogger.getLogger("portal:PortalApplication");  
  
  final static public String PORTAL_APPLICATION_ID = "PortalApplication" ;
  
  private ServletConfig sconfig_ ;
  private String[] applicationResourceBundleNames_ ;
  
  /**
   * The constructor references resource resolvers that allows the ApplicationResourceResolver to
   * extract files from different locations such as the current war or external one such as the resource 
   * one where several static files are shared among all portal instances.
   * 
   * 
   * @param config, the servlet config that contains init params such as the path location of
   * the XML configuration file for the WebUI framework
   */
  public PortalApplication(ServletConfig config) throws Exception {
    sconfig_ = config ;
    ApplicationResourceResolver resolver = new ApplicationResourceResolver() ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext(), "war:")) ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext(), "app:")) ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext(), "system:")) ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext().getContext("/eXoResources"), "resources:")) ;
    setResourceResolver(resolver) ;
  }
  
  
  /**
   * This method first calls the super.onInit() of the WebuiApplication. That super method parse the XML
   * file and stores its content in the ConfigurationManager object. It also set up he StateManager and 
   * init the application lifecycle phases.
   * 
   * Then we get all the properties file that will be used to create ResourceBundles
   */
  public void onInit() throws Exception {
    super.onInit() ;
    applicationResourceBundleNames_ =
      getConfigurationManager().getApplication().getInitParams().
      getParam("application.resource.bundle").getValue().split(",");
    for(int i = 0; i < applicationResourceBundleNames_.length; i++)  {
      applicationResourceBundleNames_[i] = applicationResourceBundleNames_[i].trim() ;
    }
  }
  
  public ServletConfig getServletConfig() { return sconfig_ ; }
  
  public String getApplicationId() { return PORTAL_APPLICATION_ID ; }  
  public String getApplicationName() {  return sconfig_.getServletName() ; }
  public String getApplicationGroup() {
    return sconfig_.getServletContext().getServletContextName() ;
  }

  public String getApplicationType() { return EXO_PORTAL_TYPE ; }

  /**
   * extract ResourceBundle from the ResourceBundleService using the bundle defined in the configuration XML
   * file for the UI application 
   */
  public ResourceBundle getResourceBundle(Locale locale) throws Exception {
    ExoContainer  appContainer = getApplicationServiceContainer() ;
    ResourceBundleService service = 
      (ResourceBundleService) appContainer.getComponentInstanceOfType(ResourceBundleService.class) ;
    ResourceBundle res = service.getResourceBundle(applicationResourceBundleNames_, locale) ;
    return res;
  }

  /**
   * extract the ResourceBundle associated with the current user from the ResourceBundleService
   */
  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {
    ExoContainer  appContainer = getApplicationServiceContainer() ;
    ResourceBundleService service = 
      (ResourceBundleService)appContainer.getComponentInstanceOfType(ResourceBundleService.class) ;
    ResourceBundle res = service.getResourceBundle("locale.users." + username, locale) ;
    return res;
  }

  @SuppressWarnings("hiding")
  public String getApplicationInitParam(String name) { 
    String value = sconfig_.getInitParameter(name);
    return value ;
  }
}
