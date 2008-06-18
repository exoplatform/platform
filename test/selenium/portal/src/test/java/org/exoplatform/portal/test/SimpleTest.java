package org.exoplatform.portal.test;

import com.thoughtworks.selenium.SeleneseTestCase;

public class  SimpleTest extends SeleneseTestCase {
	public void setUp() throws Exception {
		setUp("http://localhost:8080/", "*chrome");
	}
	
	public void testLogin() throws Exception {
		selenium.open("/portal/private/classic/");
		selenium.type("j_username", "root");
		selenium.type("j_password", "exo");
		selenium.click("UIPortalLoginFormAction");
		selenium.waitForPageToLoad("30000");
		assertEquals("http://localhost:8080/portal/private/classic/", selenium.getLocation());
		selenium.click("link=Sign out");
		selenium.waitForPageToLoad("30000");
		assertEquals("http://localhost:8080/portal/public/classic/", selenium.getLocation());
	}

  public void testLoginTwice() {
    selenium.open("/portal/private/classic/");
		selenium.type("j_username", "root");
		selenium.type("j_password", "exo");
		selenium.click("UIPortalLoginFormAction");
		selenium.waitForPageToLoad("30000");

    if (!selenium.getHtmlSource().contains("/portal/private/classic/administration")) {
      fail("the administration link is not present");
    }
    
    selenium.click("//a[@onclick='eXo.portal.logout();']");
		selenium.waitForPageToLoad("30000");
    assertEquals("http://localhost:8080/portal/public/classic/", selenium.getLocation());

    selenium.open("/portal/private/classic/");
		selenium.type("j_username", "root");
		selenium.type("j_password", "exo");
		selenium.click("UIPortalLoginFormAction");
		selenium.waitForPageToLoad("30000");

    if (!selenium.getHtmlSource().contains("/portal/private/classic/administration")) {
      fail("the administration link is not present");
    }
    
    selenium.click("//a[@onclick='eXo.portal.logout();']");
		selenium.waitForPageToLoad("30000");
    assertEquals("http://localhost:8080/portal/public/classic/", selenium.getLocation());
    
  }
  
  public void testLoadHomePage() throws Exception {
		selenium.open("/portal/public/classic/");
		selenium.click("link=Home");
		assertEquals("eXo Portal", selenium.getTitle());
	}

  public void testLoadPortlets() throws Exception {
		selenium.open("/portal/private/classic/");
		selenium.type("j_username", "root");
		selenium.type("j_password", "exo");
		selenium.click("UIPortalLoginFormAction");
		selenium.waitForPageToLoad("30000");
		selenium.open("/portal/private/classic/administration/applicationregistry");
		selenium.waitForPageToLoad("30000");
		selenium.click("//a[@class=\"CategoryControlIcon ImportApplicationsIcon\"]");
		selenium.click("//a[@class=\"CategoryControlIcon ImportPortletsIcon\"]");
		selenium.click("//a[@onclick='eXo.portal.logout();']");
		selenium.waitForPageToLoad("30000");
	}



}