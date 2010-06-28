/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.commons.platform.modules;

import java.util.Collection;

import junit.framework.TestCase;

import org.exoplatform.commons.platform.modules.ModuleRegistry.ModulePlugin;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jun 24, 2010  
 */
public class TestModuleRegistry extends TestCase{

  
  public void testRegisterPlugin() throws Exception {
    ModuleRegistry service = new ModuleRegistry();
    
    ModulePlugin plugin = service.new ModulePlugin();
    plugin.setName("collaboration");
    plugin.setDescription("enables Chat, Mail, AddressBook and Calendar applications");
    service.register(plugin);
    
    Collection<Module> modules = service.getAvailableModules();
    assertTrue((modules.size() == 1));
    Module result = modules.iterator().next();
    assertEquals("collaboration",result.getName());
    assertFalse(result.isActive());  // must be explicitely activated
  }
  
  public void testActivatePlugin() throws Exception {
    ModuleRegistry service = new ModuleRegistry();
    
    ModulePlugin plugin = service.new ModulePlugin();
    plugin.setName("collaboration");
    plugin.setDescription("enables Chat, Mail, AddressBook and Calendar applications");
    service.register(plugin);
    service.activate(plugin); // activation
    
    Collection<Module> modules = service.getAvailableModules();
    assertTrue((modules.size() == 1));
    Module result = modules.iterator().next();
    assertEquals("collaboration",result.getName());
    assertTrue(result.isActive());  // should have been activated
  }
  
}
