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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.ExternalComponentPlugins;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com 28
 * juin 2010
 */

@URITemplate("/marshall/")
public class MarshallConfiguration implements ResourceContainer {
  private Log log = ExoLogger.getLogger(this.getClass());

  public MarshallConfiguration() {
  }

  @HTTPMethod("GET")
  @URITemplate("/configuration/")
  public Response marshallComponenet() throws JiBXException, FileNotFoundException {
    log.info("marshallComponenet Start...");
    Iterator it;
   //Config of Root Container
    {
      
      ExoContainer rootContainer = ExoContainerContext.getTopContainer();
      ConfigurationManager rootManager = (ConfigurationManager) rootContainer.getComponentInstanceOfType(ConfigurationManager.class);
      for ( it = rootManager.getConfiguration().getComponentIterator(); it.hasNext();) {
        Component component = (Component) it.next();
        ExternalComponentPlugins externalComponentPlugins = rootManager.getConfiguration()
                                                                       .getExternalComponentPlugins(component.getKey());
        if (externalComponentPlugins != null
            && externalComponentPlugins.getComponentPlugins() != null)

        if (component.getComponentPlugins() != null)
        if (component.getComponentPlugins() != null && externalComponentPlugins != null
            && externalComponentPlugins.getComponentPlugins() != null) {
          component.getComponentPlugins().addAll(externalComponentPlugins.getComponentPlugins());
        } else {
          if (component.getComponentPlugins() == null && externalComponentPlugins != null
              && externalComponentPlugins.getComponentPlugins() != null) {

            component.setComponentPlugins((ArrayList) externalComponentPlugins.getComponentPlugins());
          }
        }
        Configuration configuration = new Configuration();
        configuration.addComponent(component);
        IBindingFactory bfact = BindingDirectory.getFactory(Configuration.class);
        IMarshallingContext mctx = bfact.createMarshallingContext();
        mctx.setIndent(2);
        mctx.marshalDocument(configuration, "UTF-8", null, new FileOutputStream(component.getKey()
            + ".xml"));

      }

    }
//Config of portal container
    {
      PortalContainer portalContainer = PortalContainer.getInstance();
      ConfigurationManager portalManager = (ConfigurationManager) portalContainer.getComponentInstanceOfType(ConfigurationManager.class);
      for ( it = portalManager.getConfiguration().getComponentIterator(); it.hasNext();) {
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
        Configuration configuration = new Configuration();
        configuration.addComponent(component);
        IBindingFactory bfact = BindingDirectory.getFactory(Configuration.class);
        IMarshallingContext mctx = bfact.createMarshallingContext();
        mctx.setIndent(2);
        mctx.marshalDocument(configuration, "UTF-8", null, new FileOutputStream("portal-"
            + component.getKey() + ".xml"));

      }
      log.info("marshallComponenet End...");
    }
    return Response.Builder.noContent().build();
  }
}
