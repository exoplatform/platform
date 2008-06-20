/**
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
 **/
package org.exoplatform.portal.test.selenium.tools;

import com.thoughtworks.selenium.SeleneseTestCase;

/**
 * add some tools for helping creating selenium test case.
 */
public class AbstractPortalTestCase extends SeleneseTestCase {
  /**
   * the default timeout. it is used when waiting for a page to load
   */
  public static final String DEFAULTTIMEOUT = "30000";

  /**
   * the default timeout in sec. it is used when waiting for a page to load
   */
  public static final int DEFAULTTIMEOUTSEC = 30;

  /**
   * the default timeout password in eXo.
   */
  public static final String DEFAULTPASSWORD = "exo";

  /**
   * login to the portal using the given credentials.
   * @param login the username to login in the portal
   * @param password the password of the user
   */
  public final void login(final String login, final String password) {
    selenium.open("/portal/private/classic/");
    selenium.type("j_username", login);
    selenium.type("j_password", password);
    selenium.click("UIPortalLoginFormAction");
    selenium.waitForPageToLoad(DEFAULTTIMEOUT);
    assertEquals("http://localhost:8080/portal/private/classic/", selenium.getLocation());
  }

  /**
   * add the capture of screenshot in case of error.
   * @throws Exception on an initialization error
   */
  public void setUp() throws Exception {
    setCaptureScreetShotOnFailure(true);
    super.setUp();
  }

  /**
   * login to the portal with the root account.
   */
  public final void loginAsRoot() {
    login("root", DEFAULTPASSWORD);
  }

  /**
   * login to the portal with the john account.
   */
  public final void loginAsJohn() {
    login("john", DEFAULTPASSWORD);
  }

  /**
   * login to the portal with the marry account.
   */
  public final void loginAsMarry() {
    login("marry", DEFAULTPASSWORD);
  }

  /**
   * logout of the portal.
   */
  public final void logout() {
    selenium.click("link=Sign out");
    selenium.waitForPageToLoad(DEFAULTTIMEOUT);
    assertEquals("http://localhost:8080/portal/public/classic/", selenium.getLocation());
  }

  /**
   *
   * return the number of element in a collection.
   * @param locator xpath expression to locate the element
   * @return the number of element
   */
  protected final int getLastIndex(final String locator) {
    int i = 1;
    while (selenium.isElementPresent(locator + "[" + i + "]")) {
      i++;
    }
    return i - 1;
  }


}
