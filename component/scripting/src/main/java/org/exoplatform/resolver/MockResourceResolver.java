package org.exoplatform.resolver;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class MockResourceResolver extends ResourceResolver {
  
  private Map<String, URL> resources_ ;  
  
  public MockResourceResolver(Map<String, URL> mapResources) {
    resources_ = mapResources ;
  }
  
  public URL getResource(String url) throws Exception {
    return resources_.get(url);
  }

  public InputStream getInputStream(String url) throws Exception {
    URL result = resources_.get(url);
    if(result != null)  return result.openStream() ;
    return null;
  }

  @SuppressWarnings("unused")
  public List<URL> getResources(String url) throws Exception {
    return null;
  }

  @SuppressWarnings("unused")
  public List<InputStream> getInputStreams(String url) throws Exception {
    return null;
  }

  @SuppressWarnings("unused")
  public boolean isModified(String url, long lastAccess) { 
    return false ;
  }
  
  public String getResourceScheme() {  return null; }

}
