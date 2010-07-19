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
package org.exoplatform.platform.migration.component.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.ExternalComponentPlugins;
import org.exoplatform.platform.migration.handlers.ComponentHandler;
import org.exoplatform.platform.migration.handlers.impl.UserPortalConfigHandler;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.jcr.DataMapper;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform
 * haikel.thamri@exoplatform.com 28 juin 2010
 */

@URITemplate("/marshall/")
public class MarshallConfiguration implements ResourceContainer {
  private Log                       log             = ExoLogger.getLogger(this.getClass());

  HashMap<String, ComponentHandler> handlersHashMap = new HashMap<String, ComponentHandler>();

  private RegistryService           regService_;

  private DataMapper                mapper_         = new DataMapper();

  final private static String       ROOT_CONF_DIR   = "C:\\conf";

  public MarshallConfiguration(RegistryService service) {
    regService_ = service;
    handlersHashMap.put(UserPortalConfigService.class.getName(), new UserPortalConfigHandler());
  }

  @HTTPMethod("GET")
  @URITemplate("/configuration/")
  public Response marshallComponenet() throws Exception {
    log.info("marshallComponenet Start...");
    Iterator it;
    // Config of Root Container
    {

      ExoContainer rootContainer = ExoContainerContext.getTopContainer();
      ArrayList<Component> rootComponents = new ArrayList<Component>();
      File rootConfFolder = new File(ROOT_CONF_DIR);
      if (rootConfFolder.exists())
        rootConfFolder.delete();
      rootConfFolder.mkdirs();
      ConfigurationManager rootManager = (ConfigurationManager) rootContainer.getComponentInstanceOfType(ConfigurationManager.class);
      for (it = rootManager.getConfiguration().getComponentIterator(); it.hasNext();) {
        Component component = (Component) it.next();
        ExternalComponentPlugins externalComponentPlugins = rootManager.getConfiguration()
                                                                       .getExternalComponentPlugins(component.getKey());
        if (externalComponentPlugins != null
            && externalComponentPlugins.getComponentPlugins() != null)

          if (component.getComponentPlugins() != null)
            if (component.getComponentPlugins() != null && externalComponentPlugins != null
                && externalComponentPlugins.getComponentPlugins() != null) {
              component.getComponentPlugins()
                       .addAll(externalComponentPlugins.getComponentPlugins());
            } else {
              if (component.getComponentPlugins() == null && externalComponentPlugins != null
                  && externalComponentPlugins.getComponentPlugins() != null) {

                component.setComponentPlugins((ArrayList) externalComponentPlugins.getComponentPlugins());
              }
            }
        ComponentHandler handler = handlersHashMap.get(component.getKey());
        if (handler != null) {
          handler.invoke(component, ROOT_CONF_DIR);
        } else {
          Configuration configuration = new Configuration();
          configuration.addComponent(component);
          toXML(configuration, ROOT_CONF_DIR + File.separator + component.getKey() + ".xml");
        }

      }

    }
    // Config of portal container
    {
      PortalContainer portalContainer = PortalContainer.getInstance();

      ConfigurationManager portalManager = (ConfigurationManager) portalContainer.getComponentInstanceOfType(ConfigurationManager.class);
      File portalConfFolder = new File(ROOT_CONF_DIR + File.separator + "portal");
      portalConfFolder.mkdirs();
      for (it = portalManager.getConfiguration().getComponentIterator(); it.hasNext();) {
        Component component = (Component) it.next();
        ExternalComponentPlugins externalComponentPlugins = portalManager.getConfiguration()
                                                                         .getExternalComponentPlugins(component.getKey());

        if (component.getComponentPlugins() != null && externalComponentPlugins != null
            && externalComponentPlugins.getComponentPlugins() != null) {
          component.getComponentPlugins().addAll(externalComponentPlugins.getComponentPlugins());
        } else {
          if (component.getComponentPlugins() == null && externalComponentPlugins != null
              && externalComponentPlugins.getComponentPlugins() != null) {
            component.setComponentPlugins((ArrayList) externalComponentPlugins.getComponentPlugins());
          }
        }
        ComponentHandler handler = handlersHashMap.get(component.getKey());
        if (handler != null) {
          handler.invoke(component, ROOT_CONF_DIR);
        } else {
          Configuration configuration = new Configuration();
          configuration.addComponent(component);
          toXML(configuration, portalConfFolder.getPath() + File.separator + component.getKey()
              + ".xml");
        }
      }

      log.info("marshallComponenet End...");
    }
    return Response.Builder.noContent().build();
  }

  // public PortletPreferencesSet getPreferences()

  // public Gadgets getGadgets(String id) throws Exception {
  // String[] fragments = id.split("::");
  // if (fragments.length < 2) {
  // throw new Exception("Invalid Gadgets Id: " + "[" + id + "]");
  // }
  // String gadgetsPath = getApplicationRegistryPath(fragments[0], fragments[1])
  // + "/"
  // + GADGETS_CONFIG_FILE_NAME;
  // SessionProvider sessionProvider = SessionProvider.createSystemProvider();
  // RegistryEntry gadgetsEntry;
  // try {
  // gadgetsEntry = regService_.getEntry(sessionProvider, gadgetsPath);
  // } catch (PathNotFoundException ie) {
  // return null;
  // } finally {
  // sessionProvider.close();
  // }
  // Gadgets gadgets = mapper_.toGadgets(gadgetsEntry.getDocument());
  // return gadgets;
  // }

  public void toXML(Object obj, String xmlPath) {
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(obj.getClass());
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      mctx.marshalDocument(obj, "UTF-8", null, new FileOutputStream(xmlPath));
    } catch (Exception ie) {
      log.error("Cannot convert the object to xml", ie);
    }
  }
}
