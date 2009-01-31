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

import org.exoplatform.portal.config.model.Page;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TestUserPageACL extends AbstractTestUserACL {
  public void testUserPageIsAlwaysUsableOnlyByItsOwner() {
    Page[] pages = new Page[]{new Page(), new Page(), new Page(), new Page(), new Page()};

    //
    pages[0].setOwnerType("user");
    pages[0].setOwnerId("user");
    pages[0].setAccessPermissions(new String[0]);

    //
    pages[1].setOwnerType("user");
    pages[1].setOwnerId("user");
    pages[1].setAccessPermissions(new String[]{"manager:/manageable"});

    //
    pages[2].setOwnerType("user");
    pages[2].setOwnerId("user");
    pages[2].setAccessPermissions(new String[0]);
    pages[2].setEditPermission("manager:/manageable") ;

    //
    pages[3].setOwnerType("user");
    pages[3].setOwnerId("user");
    pages[3].setAccessPermissions(new String[]{"Everyone"});

    //
    pages[4].setOwnerType("user");
    pages[4].setOwnerId("user");
    pages[4].setAccessPermissions(new String[0]);
    pages[4].setEditPermission("Everyone") ;

    //
    for (Page page : pages) {
      assertFalse(root.hasPermission(page));
      assertFalse(administrator.hasPermission(page));
      assertFalse(manager.hasPermission(page));
      assertTrue(user.hasPermission(page));
      assertFalse(guest.hasPermission(page));

      //
      assertFalse(root.hasEditPermission(page));
      assertFalse(administrator.hasEditPermission(page));
      assertFalse(manager.hasEditPermission(page));
      assertTrue(user.hasEditPermission(page));
      assertFalse(guest.hasEditPermission(page));
    }
  }
}
