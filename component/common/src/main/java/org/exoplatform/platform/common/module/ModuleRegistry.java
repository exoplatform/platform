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
package org.exoplatform.platform.common.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.info.MetaInfo;
import org.picocontainer.Startable;

/**
 * Main registry to store and manage eXo Platform modules. Modules can be
 * registered and also activated.<br/>
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com
 * Jun 24, 2010
 */
public class ModuleRegistry implements Startable {

  public static final String ALL_MODULES_PROFILE = "all";

  private static final Log LOG = ExoLogger.getExoLogger(ModuleRegistry.class);

  /**
   * modules indexed by name
   * 
   * @see Module#getName()
   */
  private Map<String, Module> modulesByName = new HashMap<String, Module>();

  /**
   * modules indexed by webapp name
   */
  private Map<String, Set<Module>> modulesByWebapp = new HashMap<String, Set<Module>>();

  /**
   * modules indexed by portlet name
   */
  private Map<String, Set<Module>> modulesByPortlet = new HashMap<String, Set<Module>>();

  /**
   * modules indexed by portlet name
   */
  private Map<String, LocalizedString> portletDisplayNames = new HashMap<String, LocalizedString>();

  public ModuleRegistry(InitParams initParams) {
    Iterator<Module> iterator = initParams.getObjectParamValues(Module.class).iterator();
    while (iterator.hasNext()) {
      Module module = iterator.next();
      if (LOG.isDebugEnabled()) {
        LOG.debug(module.toString());
      }
      modulesByName.put(module.getName(), module);
    }
  }

  /**
   * Add a module by plugin injection
   */
  public void addModule(ModulePlugin modulePlugin) {
    if (modulePlugin != null && modulePlugin.getModule() != null) {
      modulesByName.put(modulePlugin.getModule().getName(), modulePlugin.getModule());
    }
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

  @Override
  public void start() {
    // Compute Modules by webapp & by portletID (= webapp/portletName )
    Collection<Module> modules = getAvailableModules();
    for (Module module : modules) {
      for (String webappName : module.getWebapps()) {
        Set<Module> webappModules = modulesByWebapp.get(webappName);
        if (webappModules == null) {
          webappModules = new HashSet<Module>();
          modulesByWebapp.put(webappName, webappModules);
        }
        webappModules.add(module);
      }
    }
    for (Module module : modules) {
      if (module.getPortlets() != null && !module.getPortlets().isEmpty()) {
        for (String portletId : module.getPortlets()) {
          if (!portletId.contains("/")) {
            LOG.warn(portletId + " isn't a valid portlet ID, it have to be something like: {webappName}/{portletName}.");
            continue;
          }
          Set<Module> portletModules = modulesByPortlet.get(portletId);
          if (portletModules == null) {
            portletModules = new HashSet<Module>();
            modulesByWebapp.put(portletId, portletModules);

            // Add related webapp modules to this portletId modules too
            String[] portletIdSplitted = portletId.split("/");
            String webappName = portletIdSplitted[0];
            Set<Module> webappModules = modulesByWebapp.get(webappName);
            if (webappModules != null && !webappModules.isEmpty()) {
              portletModules.addAll(webappModules);
            }
          }
          portletModules.add(module);
        }
      }
    }

    PortletInvoker portletInvoker = (PortletInvoker) PortalContainer.getComponent(PortletInvoker.class);
    try {
      Set<org.gatein.pc.api.Portlet> portlets = portletInvoker.getPortlets();
      for (org.gatein.pc.api.Portlet portlet : portlets) {
        portletDisplayNames.put(portlet.getInfo().getName(), portlet.getInfo().getMeta().getMetaValue(MetaInfo.DISPLAY_NAME));
      }
    } catch (PortletInvokerException exception) {
      exception.printStackTrace();
    }
  }
  
  public String getDisplayName(String portletName, Locale locale) {
    String portletDisplayName = portletName;
    if(portletDisplayNames.get(portletName) != null) {
      portletDisplayName = portletDisplayNames.get(portletName).getValue(locale, true).getString();
    }
    return portletDisplayName;
  }

  /**
   * @param webappName
   * @return List of profiles/modules that activate a webapp
   */
  public Set<String> getModulesForWebapp(String webappName) {
    Set<String> profileNames = new HashSet<String>();
    Set<Module> webappModules = modulesByWebapp.get(webappName);
    if (webappModules != null && !webappModules.isEmpty()) {
      for (Module module : webappModules) {
        profileNames.add(module.getName());
      }
    }
    profileNames.add(ALL_MODULES_PROFILE);
    return profileNames;
  }

  /**
   * @param portletID
   * @return List of profiles/modules that activate a portlet
   */
  public Set<String> getModulesForPortlet(String portletID) {
    Set<String> profileNames = new HashSet<String>();
    Set<Module> portletModules = modulesByPortlet.get(portletID);
    if (portletModules == null || portletModules.isEmpty()) {
      // Add related webapp modules to this portletId modules too
      String[] portletIdSplitted = portletID.split("/");
      String webappName = portletIdSplitted[0];
      profileNames = getModulesForWebapp(webappName);
    } else {
      for (Module module : portletModules) {
        profileNames.add(module.getName());
      }
    }
    profileNames.add(ALL_MODULES_PROFILE);
    return profileNames;
  }

  @Override
  public void stop() {}
}
