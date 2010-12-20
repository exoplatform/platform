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
package org.exoplatform.platform.migration.common.component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.ExternalComponentPlugins;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.migration.common.constants.Constants;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.platform.migration.common.handler.ComponentHandler.Entry;
import org.exoplatform.platform.migration.common.handler.ComponentHandler.EntryType;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

public class MarshallConfigurationService {

  Map<String, ComponentHandler> handlersMap = new HashMap<String, ComponentHandler>();
  
  private ContainerParamExtractor containerParamExtractor_ = null;

  public void addHandler(ComponentHandler componentHandler) {
    handlersMap.put(componentHandler.getTargetComponentName(), componentHandler);
  }

  public String generateHTMLContainersList() throws Exception {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    containerParamExtractor_ = (ContainerParamExtractor) container.getComponentInstanceOfType(ContainerParamExtractor.class);
    String containerId = containerParamExtractor_.getContainerId(container);
    String containerRestContextName = containerParamExtractor_.getContainerRestContext(container);
    StringBuffer urlSuffixBuffer = new StringBuffer("<a href='/" + containerId + "/" + containerRestContextName);
    urlSuffixBuffer.append(Constants.CLASS_URI_TEMPLE);
    urlSuffixBuffer.append(Constants.GET_CONTAINERS_METHOD_URI_TEMPLE);
    urlSuffixBuffer.append("?");
    urlSuffixBuffer.append(Constants.CONTAINER_ID_PARAM_NAME);
    urlSuffixBuffer.append("=");
    String componentsListURLSuffix = urlSuffixBuffer.toString();

    urlSuffixBuffer.delete(0, urlSuffixBuffer.length());
    urlSuffixBuffer.append("<a href='/" + containerId + "/" + containerRestContextName);
    urlSuffixBuffer.append(Constants.CLASS_URI_TEMPLE);
    urlSuffixBuffer.append(Constants.GET_CONTAINER_CONFIGURATION_URI_TEMPLE);
    urlSuffixBuffer.append("?");
    urlSuffixBuffer.append(Constants.CONTAINER_ID_PARAM_NAME);
    urlSuffixBuffer.append("=");
    String exportComponentsURLSuffix = urlSuffixBuffer.toString();

    StringBuffer responseStringBuffer = new StringBuffer();
    ExoContainer topContainer = ExoContainerContext.getTopContainer();

    {
      responseStringBuffer.append("<html xmlns='http://www.w3.org/1999/xhtml'><body xmlns='http://www.w3.org/1999/xhtml'>");

      responseStringBuffer.append("<fieldset>");
      responseStringBuffer.append("<legend>RootContainer</legend>");

      responseStringBuffer.append(componentsListURLSuffix);
      responseStringBuffer.append(topContainer.getContext().getName());
      responseStringBuffer.append("'>");
      responseStringBuffer.append("Components List");
      responseStringBuffer.append("</a>\r\n<br/>");

      responseStringBuffer.append(exportComponentsURLSuffix);
      responseStringBuffer.append(topContainer.getContext().getName());
      responseStringBuffer.append("'>");
      responseStringBuffer.append("Export all Components");
      responseStringBuffer.append("</a>\r\n<br/>");

      responseStringBuffer.append("</fieldset><br/>");
    }

    List<PortalContainer> portalContainers = topContainer.getComponentInstancesOfType(PortalContainer.class);
    for (PortalContainer portalContainer : portalContainers) {
      responseStringBuffer.append("<fieldset>");
      responseStringBuffer.append("<legend>");
      responseStringBuffer.append("PortalContainer : ");
      responseStringBuffer.append(portalContainer.getContext().getName());
      responseStringBuffer.append("</legend>");

      responseStringBuffer.append(componentsListURLSuffix);
      responseStringBuffer.append(portalContainer.getContext().getName());
      responseStringBuffer.append("'>");
      responseStringBuffer.append("Components List");
      responseStringBuffer.append("</a>\r\n<br/>");

      responseStringBuffer.append(exportComponentsURLSuffix);
      responseStringBuffer.append(portalContainer.getContext().getName());
      responseStringBuffer.append("'>");
      responseStringBuffer.append("Export all Components");
      responseStringBuffer.append("</a>\r\n<br/>");

      responseStringBuffer.append("</fieldset><br/>");
    }
    responseStringBuffer.append("</body></html>");
    return responseStringBuffer.toString();
  }

  public String generateHTMLComponentsList(String containerId) throws Exception {
    ExoContainer container = null;
    if ((containerId == null) || containerId.equalsIgnoreCase(Constants.ROOT_CONTAINER)) {
      container = ExoContainerContext.getTopContainer();
    } else {
      container = ExoContainerContext.getContainerByName(containerId);
    }

    String containerName = containerParamExtractor_.getContainerId(container);
    String containerRestContextName = containerParamExtractor_.getContainerRestContext(container);

    StringBuffer responseStringBuffer = new StringBuffer();
    Collection<?> components = ((ConfigurationManager) container.getComponentInstanceOfType(ConfigurationManager.class)).getComponents();

    responseStringBuffer.append("<html xmlns='http://www.w3.org/1999/xhtml'><body xmlns='http://www.w3.org/1999/xhtml'>");
    StringBuffer urlSuffixBuffer = new StringBuffer("<a href='/" + containerName + "/" + containerRestContextName);
    urlSuffixBuffer.append(Constants.CLASS_URI_TEMPLE);
    urlSuffixBuffer.append(Constants.GET_COMPONENT_METHOD_URI_TEMPLE);
    urlSuffixBuffer.append("?");
    urlSuffixBuffer.append(Constants.CONTAINER_ID_PARAM_NAME);
    urlSuffixBuffer.append("=");
    urlSuffixBuffer.append(containerName);
    urlSuffixBuffer.append("&");
    urlSuffixBuffer.append(Constants.COMONENT_KEY_PARAM_NAME);
    urlSuffixBuffer.append("=");
    String urlSuffix = urlSuffixBuffer.toString();
    for (Object component : components) {
      responseStringBuffer.append(urlSuffix);
      responseStringBuffer.append(((Component) component).getKey());
      responseStringBuffer.append("'>");
      responseStringBuffer.append(((Component) component).getKey());
      responseStringBuffer.append("</a>\r\n<br/>");
    }
    responseStringBuffer.append("</body></html>");
    return responseStringBuffer.toString();
  }

  public Entry getComponentConfiguration(String containerId, String componentKey) throws Exception {
    ExoContainer container = ExoContainerContext.getContainerByName(containerId);
    ConfigurationManager configurationManager = (ConfigurationManager) container.getComponentInstanceOfType(ConfigurationManager.class);
    Component component = configurationManager.getConfiguration().getComponent(componentKey);
    ExternalComponentPlugins externalComponentPlugins = configurationManager.getConfiguration().getExternalComponentPlugins(component.getKey());
    if ((externalComponentPlugins != null) && (externalComponentPlugins.getComponentPlugins() != null)) {
      if (component.getComponentPlugins() == null) {
        component.setComponentPlugins((ArrayList) externalComponentPlugins.getComponentPlugins());
      } else {
        component.getComponentPlugins().addAll(externalComponentPlugins.getComponentPlugins());
      }
      if (component.getComponentPlugins() == null) {
        Collections.sort(component.getComponentPlugins(), componentPluginComparator);
      }
    }
    ComponentHandler handler = handlersMap.get(component.getKey());
    Entry configurationEntry = null;
    if (handler != null) {
      configurationEntry = handler.invoke(component, container);
    } else {
      Configuration configuration = new Configuration();
      configuration.addComponent(component);
      configurationEntry = new Entry(component.getKey());
      configurationEntry.setContent(toXML(configuration));
      configurationEntry.setType(EntryType.XML);
    }
    return configurationEntry;
  }

  public Entry getAllComponentsConfiguration(String containerId) throws Exception {
    ExoContainer container = ExoContainerContext.getContainerByName(containerId);
    ConfigurationManager configurationManager = (ConfigurationManager) container.getComponentInstanceOfType(ConfigurationManager.class);
    Collection<Component> components = configurationManager.getConfiguration().getComponents();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(out);
    for (Component component : components) {
      Entry entry = getComponentConfiguration(containerId, component.getKey());
      zos.putNextEntry(new ZipEntry(component.getKey() + entry.getType()));
      zos.write(entry.getContent());
      zos.closeEntry();
    }

    Collection componentLifecyclePlugins = configurationManager.getConfiguration().getComponentLifecyclePlugins();
    Collection containerLifecyclePlugins = configurationManager.getConfiguration().getContainerLifecyclePlugins();
    if ((containerLifecyclePlugins != null) && (componentLifecyclePlugins != null) && (componentLifecyclePlugins.size() > 0) && (containerLifecyclePlugins.size() > 0)) {
      Configuration configuration = new Configuration();
      for (Object componentLifecyclePlugin : componentLifecyclePlugins) {
        configuration.addComponentLifecyclePlugin(componentLifecyclePlugin);
      }
      for (Object containerLifecyclePlugin : containerLifecyclePlugins) {
        configuration.addContainerLifecyclePlugin(containerLifecyclePlugin);
      }
      zos.putNextEntry(new ZipEntry(Constants.LIFECYCLE_PLUGINS_XML_FILE_NAME));
      byte[] bytes = toXML(configuration);
      zos.write(bytes);
      zos.closeEntry();
    }
    zos.close();
    Entry entry = new Entry(Constants.CONTAINER_FILE_PREFIX + containerId);
    entry.setType(EntryType.ZIP);
    entry.setContent(out.toByteArray());
    return entry;
  }

  public int getInitParamsSize(InitParams initParams) throws JiBXException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    IBindingFactory bfact = BindingDirectory.getFactory(Configuration.class);
    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);
    mctx.marshalDocument(initParams, Constants.UTF_8, false, out);
    return out.size();
  }

  public byte[] toXML(Object obj) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(obj.getClass());
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      mctx.marshalDocument(obj, Constants.UTF_8, null, out);
      return out.toByteArray();
    } catch (Exception ie) {
      throw ie;
    }
  }

  private Comparator<ComponentPlugin> componentPluginComparator = new Comparator<ComponentPlugin>() {
    public int compare(ComponentPlugin o1, ComponentPlugin o2) {
      int compare = 0;
      if ((o1.getName() != null) && (o2.getName() != null)) {
        compare = o1.getName().compareTo(o2.getName());
      }
      if (compare == 0) {
        if ((o1.getType() != null) && (o2.getType() != null)) {
          compare = o1.getType().compareTo(o2.getType());
        }
        if (compare == 0) {
          if ((o1.getDescription() != null) && (o2.getDescription() != null)) {
            compare = o1.getDescription().compareTo(o2.getDescription());
          }
          if (compare == 0) {
            try {
              if ((o1.getInitParams() != null) || (o2.getInitParams() != null)) {
                if (o1.getInitParams() == o2.getInitParams()) { // null
                  compare = 0;
                } else if (o1.getInitParams() == null) {
                  compare = -1;
                } else if (o2.getInitParams() == null) {
                  compare = 1;
                } else {
                  compare = getInitParamsSize(o1.getInitParams()) - getInitParamsSize(o2.getInitParams());
                }
              }
            } catch (Exception exception) {
              exception.printStackTrace();
            };
          }
        }
      }
      return compare;
    }
  };
}
