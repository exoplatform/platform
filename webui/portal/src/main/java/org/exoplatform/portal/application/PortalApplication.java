package org.exoplatform.portal.application;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.monitor.jvm.MemoryInfo;
import org.exoplatform.portal.application.handler.DownloadRequestHandler;
import org.exoplatform.portal.application.handler.PortalRequestHandler;
import org.exoplatform.portal.application.handler.RequestHandler;
import org.exoplatform.portal.application.handler.ServiceRequestHandler;
import org.exoplatform.portal.application.handler.UploadRequestHandler;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.templates.groovy.ApplicationResourceResolver;
import org.exoplatform.templates.groovy.ServletResourceResolver;
import org.exoplatform.webui.application.Application;

public class PortalApplication extends Application {
  
  private ServletConfig sconfig_ ;
  private String applicationId_  ;
  private String[] applicationResourceBundleNames_ ;
  private Map<String, RequestHandler> requestHandlers_ = new HashMap<String, RequestHandler>();
  
  public PortalApplication(ServletConfig config) throws Exception {
    sconfig_ = config ;
    String contextName = config.getServletContext().getServletContextName() ;
    applicationId_ = contextName + "/" + config.getServletName();
    ApplicationResourceResolver resolver = new ApplicationResourceResolver() ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext(), "war:")) ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext(), "app:")) ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext(), "system:")) ;
    resolver.addResourceResolver(new ServletResourceResolver(config.getServletContext().getContext("/eXoResources"), "resources:")) ;
    setResourceResolver(resolver) ;
    PortalRequestHandler prhandler = new PortalRequestHandler() ;
    requestHandlers_.put("/public",  prhandler) ;
    requestHandlers_.put("/private", prhandler) ;
    requestHandlers_.put("/upload",  new UploadRequestHandler()) ;
    requestHandlers_.put("/download",  new DownloadRequestHandler()) ;
    requestHandlers_.put("/service",  new ServiceRequestHandler()) ;
  }
  
  public void init() throws Exception {
    super.init() ;
    applicationResourceBundleNames_ =
      getConfigurationManager().getApplication().getInitParams().
      getParam("application.resource.bundle").getValue().split(",");
    for(int i = 0; i < applicationResourceBundleNames_.length; i++)  {
      applicationResourceBundleNames_[i] = applicationResourceBundleNames_[i].trim() ;
    }
  }
  
  public ServletConfig getServletConfig() { return sconfig_ ; }
  
  public String getApplicationId() { return applicationId_ ; }
  
  public String getApplicationName() {  return sconfig_.getServletName() ; }

  public URL getResource(String url) throws Exception {
    return sconfig_.getServletContext().getResource(url) ;
  }

  public InputStream getInputStream(String url) throws Exception {
    return sconfig_.getServletContext().getResourceAsStream(url);
  }

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
  
  public void service(HttpServletRequest req, HttpServletResponse res)   {
    System.out.println("\n\n\n == > no chay den day truoc nay\n\n");
    RootContainer rootContainer = RootContainer.getInstance() ;
    try {
      PortalContainer portalContainer = 
        rootContainer.getPortalContainer(sconfig_.getServletContext().getServletContextName()) ;
      PortalContainer.setInstance(portalContainer) ;
      List<ComponentRequestLifecycle> components = 
        portalContainer.getComponentInstancesOfType(ComponentRequestLifecycle.class) ;
      for(ComponentRequestLifecycle component : components) {
        component.startRequest(portalContainer);
      }

      String reqPath = req.getServletPath();
      RequestHandler rhandler = requestHandlers_.get(reqPath) ;
      if(rhandler != null) rhandler.execute(this, req, res) ;

      for(ComponentRequestLifecycle component : components) {
        component.endRequest(portalContainer);
      }
    } catch(Exception ex) {
      throw new  RuntimeException(ex) ;
    } finally {
      PortalContainer.setInstance(null) ;
    }
    
    System.gc() ;
    MemoryInfo minfo = 
      (MemoryInfo)  rootContainer.getComponentInstanceOfType(MemoryInfo.class) ;
    minfo.printMemoryInfo() ;
  } 
}
