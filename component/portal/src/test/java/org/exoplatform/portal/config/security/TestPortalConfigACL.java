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

import org.exoplatform.portal.config.model.PortalConfig;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TestPortalConfigACL extends AbstractTestUserACL {


  public void testFoo() {
    PortalConfig portal = new PortalConfig();
    portal.setAccessPermissions(new String[0]);

    //
    assertTrue(root.hasEditPermission(portal));
    assertFalse(administrator.hasEditPermission(portal));
    assertFalse(manager.hasEditPermission(portal));
    assertFalse(user.hasEditPermission(portal));
    assertFalse(guest.hasEditPermission(portal));

    //
    assertTrue(root.hasPermission(portal));
    assertFalse(administrator.hasPermission(portal));
    assertFalse(manager.hasPermission(portal));
    assertFalse(user.hasPermission(portal));
    assertFalse(guest.hasPermission(portal));
  }

  public void testPortalAccessible() {
    PortalConfig portal = new PortalConfig();
    portal.setAccessPermissions(new String[]{"manager:/manageable"});

    //
    assertTrue(root.hasEditPermission(portal));
    assertFalse(administrator.hasEditPermission(portal));
    assertFalse(manager.hasEditPermission(portal));
    assertFalse(user.hasEditPermission(portal));
    assertFalse(guest.hasEditPermission(portal));

    //
    assertTrue(root.hasPermission(portal));
    assertFalse(administrator.hasPermission(portal));
    assertTrue(manager.hasPermission(portal));
    assertFalse(user.hasPermission(portal));
    assertFalse(guest.hasPermission(portal));
  }

  public void testPortalEditable() {
    PortalConfig portal = new PortalConfig();
    portal.setAccessPermissions(new String[0]);
    portal.setEditPermission("manager:/manageable");

    //
    assertTrue(root.hasEditPermission(portal));
    assertFalse(administrator.hasEditPermission(portal));
    assertTrue(manager.hasEditPermission(portal));
    assertFalse(user.hasEditPermission(portal));
    assertFalse(guest.hasEditPermission(portal));

    //
    assertTrue(root.hasPermission(portal));
    assertFalse(administrator.hasPermission(portal));
    assertTrue(manager.hasPermission(portal));
    assertFalse(user.hasPermission(portal));
    assertFalse(guest.hasPermission(portal));
  }

  public void testPortalAccessibleAndEditable() {
    PortalConfig portal = new PortalConfig();
    portal.setAccessPermissions(new String[]{"manager:/manageable"});
    portal.setEditPermission("manager:/manageable");

    //
    assertTrue(root.hasEditPermission(portal));
    assertFalse(administrator.hasEditPermission(portal));
    assertTrue(manager.hasEditPermission(portal));
    assertFalse(user.hasEditPermission(portal));
    assertFalse(guest.hasEditPermission(portal));

    //
    assertTrue(root.hasPermission(portal));
    assertFalse(administrator.hasPermission(portal));
    assertTrue(manager.hasPermission(portal));
    assertFalse(user.hasPermission(portal));
    assertFalse(guest.hasPermission(portal));
  }

}
