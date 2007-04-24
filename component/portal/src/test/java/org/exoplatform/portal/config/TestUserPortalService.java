/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.io.FileInputStream;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.exoplatform.test.BasicTestCase;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 2, 2005
 */
public class TestUserPortalService extends BasicTestCase {
  
  final static String PORTAL_APP = "PortalApp" ; 
  public TestUserPortalService(String name) {
    super(name);    
  }
  
  public void testService() throws Exception {
    PortalContainer pcontainer = PortalContainer.getInstance() ;
    UserPortalConfigService service = 
      (UserPortalConfigService) pcontainer.getComponentInstanceOfType(UserPortalConfigService.class);
    assertTrue("Service Cannot be null",  service != null) ;
    
//-------------------- Save to UserPortalConfig -------------------------------------------------
    
    assertPortalConfigSave(service, "portalone") ;
    assertPageNavigationSave(service, "portalone") ;
    assertPageSetSave(service, "portalone", "pages.xml") ;
    
//---------------------Test UserPortalConfigService operations---------------------------------------------
    
    assertPortalConfigOperation(service, "portalone") ;
    assertPageNavigationOperation(service, "portalone") ;
    assertPageOperation(service, "portalone") ;
  }
  
  void assertPortalConfigSave(UserPortalConfigService service, String portalName) throws Exception{
    String portalFile = portalName + "/config.xml" ;
    PortalConfig config = loadObject(PortalConfig.class, portalFile) ;
    assertEquals("portalone", config.getOwner()) ;
    
    service.update(config) ;
  }
  
  void assertPortalConfigOperation(UserPortalConfigService service, String portalName) throws Exception {    
    UserPortalConfig userPortalConfig = service.getUserPortalConfig(portalName, "N/A") ;    
    assertNotNull(userPortalConfig) ;
    assertEquals("portalone", userPortalConfig.getPortalConfig().getOwner()) ; 
  }
  
  void assertPageOperation(UserPortalConfigService service, String portalName) throws Exception {
    String[] pageNames = {"page-one", "page-two"} ;
    for (String pageName : pageNames) {
      String pageId = portalName + ":/" + pageName ;
      Page page = service.getPage(pageId, "N/A") ;

      assertNotNull(page) ;
      assertEquals(pageName, page.getName()) ;
      assertEquals(pageId, page.getPageId()) ;
    }
  }
  
  void assertPageSetSave(UserPortalConfigService service, String portalName, String file) throws Exception {
    String pageFile = portalName + "/pages/" + file ;
    PageSet pageSet = loadObject(PageSet.class, pageFile) ;
    
    List<Page> pages = pageSet.getPages() ;
    for(Page p : pages) {
      service.update(p) ;
    }
    
    
  }
  void assertPageNavigationSave(UserPortalConfigService service, String portalName) throws Exception {
    String navigationFile = portalName + "/navigation.xml" ;
    PageNavigation navigation = loadObject(PageNavigation.class, navigationFile) ;
    assertEquals("portalone", navigation.getOwner()) ;
    
    service.update(navigation) ;
  }
  
  void assertPageNavigationOperation(UserPortalConfigService service, String portalName) throws Exception {
    UserPortalConfig userPortalConfig = service.getUserPortalConfig(portalName, "N/A") ;
    assertNotNull(userPortalConfig) ;
    
    int numberOfNavigations = userPortalConfig.getNavigations().size() ;
    assertEquals(1, numberOfNavigations) ;
    
    String pageNavigationOwner = userPortalConfig.getNavigations().get(0).getOwner() ;
    assertEquals("portalone", pageNavigationOwner) ;
  }  
  
  private <T> T loadObject(Class<T> clazz, String file) throws Exception{
    IBindingFactory bfact = BindingDirectory.getFactory(clazz) ;
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext() ;
    FileInputStream is = new FileInputStream("src/test/resources/PortalApp/" + file) ;
    return  (T) uctx.unmarshalDocument(is, null) ;
  }
  
}
