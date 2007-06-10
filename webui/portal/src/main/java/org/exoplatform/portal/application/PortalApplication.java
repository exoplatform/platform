package org.exoplatform.portal.application;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.resolver.ApplicationResourceResolver;
import org.exoplatform.resolver.ServletResourceResolver;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.webui.application.WebuiApplication;

public class PortalApplication extends WebuiApplication {
  
  final static public String PORTAL_APPLICATION_ID = "PortalApplication" ;
  
  private ServletConfig sconfig_ ;
  private String[] applicationResourceBundleNames_ ;
  
  public PortalApplication(ServletConfig config) throws Exception {
    sconfig_ = config ;
    ApplicationResourceResolver resolver = new ApplicationResourceResolver() ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext(), "war:")) ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext(), "app:")) ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext(), "system:")) ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext().getContext("/eXoResources"), "resources:")) ;
    setResourceResolver(resolver) ;
  }
  
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


  public ResourceBundle getResourceBundle(Locale locale) throws Exception {
    ExoContainer  appContainer = getApplicationServiceContainer() ;
    ResourceBundleService service = 
      (ResourceBundleService) appContainer.getComponentInstanceOfType(ResourceBundleService.class) ;
    ResourceBundle res = service.getResourceBundle(applicationResourceBundleNames_, locale) ;
    return res;
  }

  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {
    ExoContainer  appContainer = getApplicationServiceContainer() ;
    ResourceBundleService service = 
      (ResourceBundleService)appContainer.getComponentInstanceOfType(ResourceBundleService.class) ;
    ResourceBundle res = service.getResourceBundle("locale.users." + username, locale) ;
    return res;
  }

  public String getApplicationInitParam(String name) { 
    String value = sconfig_.getInitParameter(name);
    return value ;
  }
  
  public ExoContainer getApplicationServiceContainer() { return PortalContainer.getInstance() ; }

}
