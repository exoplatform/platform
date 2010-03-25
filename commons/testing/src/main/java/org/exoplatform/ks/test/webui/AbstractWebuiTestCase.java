/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.ks.test.webui;


import java.util.HashMap;

import junit.framework.TestCase;

import org.exoplatform.ks.test.mock.MockParentRequestContext;
import org.exoplatform.ks.test.mock.MockResourceBundle;
import org.exoplatform.ks.test.mock.MockWebUIRequestContext;
import org.exoplatform.ks.test.mock.MockWebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Abstract TestCase to test a Webui UIComponent
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice Lamarque</a>
 * @version $Revision$
 */
public abstract class AbstractWebuiTestCase extends TestCase {


  protected MockWebuiApplication webuiApplication;

  public AbstractWebuiTestCase() throws Exception {
    webuiApplication = new MockWebuiApplication();
    webuiApplication.setResourceBundle(new MockResourceBundle(new HashMap<String, Object>()));
  }

  public final void setUp() throws Exception {

    initRequest();
    
    doSetUp();
  }



  private void initRequest() {
    MockWebUIRequestContext context = new MockWebUIRequestContext(webuiApplication);
    context.setParentAppRequestContext(new MockParentRequestContext(null)); // a webuirequestcontext requires a parent...
    WebuiRequestContext.setCurrentInstance(context);
  }
  
  protected void doSetUp() {
    // to be overriden
  }
  


  /**
   * Convenience method to set an entry in the application resource bundle
   * @param key
   * @param value
   */
  protected void setResourceBundleEntry(String key, String value) {
    getAppRes().put(key, value);
  }

  
  /**
   * Convenience method to access the app resource bundle mock
   * @return
   */
  private MockResourceBundle getAppRes() {
    try {
      return (MockResourceBundle) webuiApplication.getResourceBundle(null);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    return null;
  }
  
  

  
}
