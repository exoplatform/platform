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
package org.exoplatform.webui.test;

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

  @Override
  public String getApplicationGroup() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getApplicationType() {
    // TODO Auto-generated method stub
    return null;
  }
}
