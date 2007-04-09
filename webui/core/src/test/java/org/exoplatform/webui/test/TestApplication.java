/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
