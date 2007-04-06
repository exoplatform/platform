package org.exoplatform.webui.application.mock;

import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.resolver.ApplicationResourceResolver;
import org.exoplatform.resolver.MockResourceResolver;
import org.exoplatform.webui.application.WebuiApplication;

public class MockApplication extends WebuiApplication {
  
  private Map<String, String> initParams_ ;
  private ResourceBundle appRes_ ;
  
  public MockApplication(Map<String,String> initParams,
                         Map<String, URL>  resources,
                         ResourceBundle appRes) {
    initParams_ =  initParams;
    appRes_ = appRes ;
    ApplicationResourceResolver resolver = new ApplicationResourceResolver() ;
    resolver.addResourceResolver(new MockResourceResolver(resources)) ;
    setResourceResolver(resolver) ;
  }
  
  public String getApplicationId() { return "MockApplication" ; }
  
  public String getApplicationName() {  return "MockApplication";  }

  @SuppressWarnings("unused")
  public ResourceBundle getResourceBundle(Locale locale) throws Exception {
    return appRes_ ;
  }
  
  @SuppressWarnings("unused")
  public ResourceBundle getOwnerResourceBundle(String username, Locale locale) throws Exception {
    return null;
  }

  public String getApplicationInitParam(String name) {  return initParams_.get(name); }

  @Override
  public ExoContainer getApplicationServiceContainer() {
    // TODO Auto-generated method stub
    return null;
  }
}
