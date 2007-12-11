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

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.exoplatform.test.BasicTestCase;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 5, 2006
 */
public class TestApplication extends BasicTestCase {
  
  public  void  testApplication() throws Exception {
    Map<String, String> initParams = new HashMap<String, String>() ;
    initParams.put("webui.configuration", "webui.configuration") ;
    
    String basedir = System.getProperty("basedir") ;
    String webuiConfig =  basedir + "/src/main/resources/webui-configuration.xml";
    Map<String, URL> resources = new HashMap<String, URL>() ;
    resources.put("webui.configuration", new File(webuiConfig).toURL()) ;
    initParams.put("webui.configuration", new File(webuiConfig).toURL().toString()) ;
    
    MockApplication mock = new MockApplication(initParams, resources, null) ;
    mock.onInit() ;
    mock.onDestroy() ;
  }
  
}
