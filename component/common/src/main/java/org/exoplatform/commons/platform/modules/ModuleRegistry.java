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
import java.util.HashMap;
import java.util.Map;

import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * 
 * Main registry to store and manage eXo Platform modules. Modules can be registered and also activated.<br/>
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jun 24, 2010  
 */
public class ModuleRegistry {

  private static final Log    LOG = ExoLogger.getExoLogger(ModuleRegistry.class);

  /**
   * modules indexed by name
   * 
   * @see Module#getName()
   */
  private Map<String, Module> modulesByName;

  public ModuleRegistry() {
    modulesByName = new HashMap<String, Module>();
  }

  /**
   * Get all available modules
   * 
   * @return the list of all modules registered
   * @see #registerModule(Module)
   */
  public Collection<Module> getAvailableModules() {
    return modulesByName.values();
  }

  /**
   * Register a module to the list of available modules
   * 
   * @param moduleName name of the module to register
   */
  public void registerModule(Module module) {
    modulesByName.put(module.getName(), module);
  }

  /**
   * Unregister a module from the list of available modules
   * 
   * @param moduleName name of the module to unregister
   */
  public void unregisterModule(Module moduleName) {
    modulesByName.remove(moduleName);
  }

  /**
   * Mark a module as active
   * 
   * @param moduleName name of the module to activate
   * @return true if the module was activated successfully, false otherwise
   */
  public boolean activateModule(String moduleName) {
    Module module = modulesByName.get(moduleName);
    if (module == null) {
      LOG.warn("could not find a registered module named '" + moduleName + "' to activate");
      return false;
    }
    module.setActive(true);
    return true;
  }

  /**
   * Plugin hook to register modules Uses the plugin name and descriptions to
   * define the module Is intended to be used as a component plugin to define
   * builtin modules. Can also be used as an external component plugin for
   * addons on top of the Platform.
   */
  public void register(ModulePlugin plugin) {
    Module module = new Module(plugin.getName(), plugin.getDescription());
    registerModule(module);
  }

  /**
   * Plugin hook for activating an existing module. Uses the plugin name as the
   * module name. This hook is intended to by used as an external component
   * plugin declared in extensions. Hence, an extension whose profile is not
   * active will not be activated.
   * 
   * @param plugin plugin to activate
   */
  public void activate(ModulePlugin plugin) {
    String name = plugin.getName();
    activateModule(name);
  }

  class ModulePlugin extends ComponentPlugin {
    // we just need name and description which are inherited from
    // ComponentPlugin
  }

}
