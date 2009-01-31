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
package org.exoplatform.portal.config.security;

import org.exoplatform.portal.config.model.PageNavigation;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TestPortalNavACL extends AbstractTestUserACL {

  public void testNavEditByRoot() {
    PageNavigation nav = new PageNavigation();
    nav.setOwnerType("portal");
    nav.setOwnerId("foo");

    //
    assertTrue(root.hasEditPermission(nav));
    assertFalse(administrator.hasEditPermission(nav));
    assertFalse(manager.hasEditPermission(nav));
    assertFalse(user.hasEditPermission(nav));
    assertFalse(guest.hasEditPermission(nav));
  }
}