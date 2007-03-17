/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.exoplatform.portal.portlet.PortletPreferences;
import org.exoplatform.portal.portlet.Preference;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.services.portletcontainer.pci.model.ExoPortletPreferences;
import org.exoplatform.services.portletcontainer.persistence.PortletPreferencesPersister;
import org.exoplatform.test.BasicTestCase;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 22, 2006
 */
public class TestPortalDataService extends BasicTestCase {

  private PortalDAO service_;

  private PortletPreferencesPersister porletPreferencesService_;

  public TestPortalDataService(String name){
    super(name);
  }

  public void setUp() throws Exception {
    if(service_ != null) return;
    PortalContainer manager  = PortalContainer.getInstance();      
    service_ = (PortalDAO) manager.getComponentInstanceOfType(PortalDAO.class) ;
    porletPreferencesService_ = 
      (PortletPreferencesPersister) manager.getComponentInstance(PortletPreferencesPersister.class); 
  }

  public void teaDown() throws Exception {    
  }

  public void testPortalConfig() throws Exception {
    Object obj = loadObject("config");
    PortalConfig pconfig = (PortalConfig)obj;
    service_.savePortalConfig((PortalConfig)obj);

    pconfig.setOwner("admin");
    pconfig.setTitle("Portal1");    
    service_.savePortalConfig(pconfig);

    PortalConfig portalConfig = service_.getPortalConfig("admin");    
    assertTrue(portalConfig != null);
    assertEquals("expect portal config title is Portal1", 
        portalConfig.getTitle(), pconfig.getTitle());

    pconfig.setOwner("portal");
    pconfig.setTitle("Portal2");
    pconfig.setEditPermission("*:/guest");
    service_.savePortalConfig(pconfig);
    portalConfig = service_.getPortalConfig("portal");    
    assertEquals("expect edit permission is *:/guest ", 
        portalConfig.getEditPermission(),"*:/guest");

    service_.removePortalConfig("admin");
    portalConfig = service_.getPortalConfig("admin");    
    assertTrue(portalConfig == null);    

    pconfig = service_.getPortalConfig("exo");  
    pconfig.setDecorator("default");
    service_.savePortalConfig(pconfig);
    portalConfig = service_.getPortalConfig(pconfig.getOwner());
    assertEquals("expect new decorator is default ", portalConfig.getDecorator(),"default");

    PageList listData = service_.getPortalConfigs(); 
    assertEquals(listData.getAvailable(), 2);

//    Query query = new Query("exo", "*:/guest", null, PortalConfig.class.getName());   
//    PageList listDesData = service_.findDataDescriptions(query);
//    List list = listDesData.getPage(1);
//    assertTrue(list.size() == 1);
//    assertEquals(((Data)list.get(0)).getOwner(), "exo");
  }

  public void testPage() throws Exception {
    Page page  = loadPage();
    service_.savePage(page);

    page.setOwner("admin");
    page.setName("organization");
    page.setEditPermission("*:/admin");
    page.setWidth("100%");
    page.setTitle("organization");
    service_.savePage(page);

    Page savedPage  = service_.getPage(page.getPageId());
    assertEquals("expect page width is 100%", savedPage.getWidth(), "100%");
    assertEquals("Page owner is admin", page.getOwner(), "admin");
    assertEquals(page.getViewPermission(),"*:/guest");

    page = loadPage();
    page.setDecorator("exo");
    page.setTitle("Homepage");
    service_.savePage(page);

    savedPage  = service_.getPage(page.getPageId());
    assertEquals("Expect title is homepage ", savedPage.getTitle(), "Homepage");
    assertTrue("Expect decorator is exo ", savedPage.getDecorator().equals("exo"));

    service_.removePage(page.getPageId());
    savedPage  = service_.getPage("exo:/home");
    assertTrue("exo page removed ", savedPage ==  null);

    PageSet pageSet = service_.getPageOfOwner("admin");
    assertTrue(pageSet.getPages().size() == 1);
    page = pageSet.getPages().get(0);

    service_.removePageOfOwner("admin");
    savedPage  = service_.getPage(page.getPageId());
    assertTrue("Admin pages was removed ", savedPage ==  null);  
  }

  public void testPageNavigation() throws Exception {
    PageNavigation navigation = (PageNavigation)loadObject("navigation");
    service_.savePageNavigation(navigation);

    navigation.setOwner("portal");
    List<PageNode> pageNodes = navigation.getNodes();
    PageNode pageNode  = pageNodes.get(0);
    pageNode.setName("portal");
    pageNode.setDescription("new desciption of page node ");
    pageNode.setPageReference("admin:/home");
    service_.savePageNavigation(navigation);

    PageNavigation savedNavigation = service_.getPageNavigation("portal");
    assertEquals(savedNavigation.getNodes().size(), 1);
    assertEquals("Expect name of page node is portal ",
        savedNavigation.getNode(0).getName(), "portal");
    assertEquals("Expect page reference is admin:/home ",
        savedNavigation.getNode(0).getPageReference(), "admin:/home");

    pageNode.setPageReference("portal:/sitemap");
    navigation.addNode(pageNode);
    service_.savePageNavigation(navigation);

    savedNavigation = service_.getPageNavigation("portal");
    assertEquals("Expect page reference is portal:/sitemap ",
        savedNavigation.getNodes().get(0).getPageReference(), "portal:/sitemap");

    service_.removePageNavigation("portal");
    savedNavigation = service_.getPageNavigation("portal");
    assertTrue("Portal navigation was removed :",savedNavigation == null);
  }  

  public void testPortletPreferences() throws Exception {
    Preference pre  = new Preference();
    pre.setName("Introduction");
    pre.setReadOnly(true);
    ArrayList<String> values = new ArrayList<String>();
    values.addAll(Arrays.asList("id=war:/web-content/home/exoplatform"));
    pre.setValues(values);
    ArrayList<Preference> pres = new ArrayList<Preference>();
    pres.add(pre);

    PortletPreferences portletPreperences = new PortletPreferences();
    portletPreperences.setPreferences(pres);
    portletPreperences.setCreator("exo");
    portletPreperences.setModifier("exoadmin");    
    portletPreperences.setWindowId("exo:/content/DisplayStaticContent/news2");
    service_.savePortletPreferencesConfig(portletPreperences);

    ExoWindowID windowID = new ExoWindowID("exo:/content/DisplayStaticContent/news2");
    ExoPortletPreferences exoPortletPre = porletPreferencesService_.getPortletPreferences(windowID);
    assertTrue("find ExoPortletPreferences :", exoPortletPre != null);
  }

  private Page loadPage() throws Exception {
    Object obj = loadObject("pages");
    PageSet pageSet = (PageSet)obj;
    ArrayList list = pageSet.getPages();
    return (Page)list.get(0);
  }

  private Object loadObject(String name) throws Exception{
    IBindingFactory bfact = BindingDirectory.getFactory(PageSet.class);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    return uctx.unmarshalDocument(new FileInputStream("src/main/resources/"+name+".xml"), null);
  }

}
