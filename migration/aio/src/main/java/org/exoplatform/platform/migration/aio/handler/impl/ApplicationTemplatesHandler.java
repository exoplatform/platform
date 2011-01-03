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
package org.exoplatform.platform.migration.aio.handler.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.services.cms.BasePath;
import org.exoplatform.services.cms.impl.DMSConfiguration;
import org.exoplatform.services.cms.views.ApplicationTemplateManagerService;
import org.exoplatform.services.cms.views.PortletTemplatePlugin;
import org.exoplatform.services.cms.views.PortletTemplatePlugin.PortletTemplateConfig;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;

public class ApplicationTemplatesHandler extends ComponentHandler {

  private static final String PORTLET_TEMPLATE_PATH = "portlet.template.path";

  private static final String PORTLET_NAME = "portletName";

  private static final String TEMPLATES_LOCATION = "application-templates/";

  private Log log = ExoLogger.getLogger(this.getClass());

  StringBuffer nodeTypePathTmp_ = new StringBuffer();

  public ApplicationTemplatesHandler() {
    super.setTargetComponentName(ApplicationTemplateManagerService.class.getName());
  }

  @Override
  public Entry invoke(Component component, ExoContainer container) {
    Session dmsWorkspaceSession = null;
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(out);
      if (log.isDebugEnabled()) {
        log.debug("Handler invoked for component: " + component.getKey());
      }
      List<ComponentPlugin> componentPluginsList = cleanComponentPlugins(component);

      RepositoryService repositoryService = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class));
      ManageableRepository repository = repositoryService.getDefaultRepository();
      String defaumtRepositoryName = repository.getConfiguration().getName();

      DMSConfiguration dmsConfiguration = ((DMSConfiguration) container.getComponentInstanceOfType(DMSConfiguration.class));
      String dmsSystemWorkspace = dmsConfiguration.getConfig(defaumtRepositoryName).getSystemWorkspace();

      NodeHierarchyCreator hierarchyCreator = ((NodeHierarchyCreator) container.getComponentInstanceOfType(NodeHierarchyCreator.class));
      String cmsViewTemplatesPath = hierarchyCreator.getJcrPath(BasePath.CMS_VIEWTEMPLATES_PATH);

      dmsWorkspaceSession = repository.getSystemSession(dmsSystemWorkspace);
      Node basedTemplateHome = (Node) dmsWorkspaceSession.getItem(cmsViewTemplatesPath);

      ApplicationTemplateManagerService applicationTemplateService = ((ApplicationTemplateManagerService) container.getComponentInstanceOfType(ApplicationTemplateManagerService.class));
      List<String> applicationNames = applicationTemplateService.getAllManagedPortletName(defaumtRepositoryName);
      for (String applicationName : applicationNames) {
        Node portletTemplateHome = basedTemplateHome.getNode(applicationName);

        ComponentPlugin templatesComponentPlugin = new ComponentPlugin();
        templatesComponentPlugin.setName("templates.plugin");
        templatesComponentPlugin.setSetMethod("addPlugin");
        templatesComponentPlugin.setType(PortletTemplatePlugin.class.getName());
        InitParams templatesPluginInitParams = new InitParams();
        templatesComponentPlugin.setInitParams(templatesPluginInitParams);
        componentPluginsList.add(templatesComponentPlugin);

        ValueParam valueParam = new ValueParam();
        valueParam.setName(PORTLET_NAME);
        valueParam.setValue(portletTemplateHome.getName());
        templatesPluginInitParams.addParam(valueParam);

        valueParam = new ValueParam();
        valueParam.setName(PORTLET_TEMPLATE_PATH);
        valueParam.setValue(TEMPLATES_LOCATION + portletTemplateHome.getName());
        templatesPluginInitParams.addParam(valueParam);

        NodeIterator categoriesNodeIterator = portletTemplateHome.getNodes();
        while (categoriesNodeIterator.hasNext()) {
          Node categoryNode = categoriesNodeIterator.nextNode();

          NodeIterator templateNodeIterator = categoryNode.getNodes();
          while (templateNodeIterator.hasNext()) {
            Node templateNode = templateNodeIterator.nextNode();
            PortletTemplateConfig portletTemplateConfig = new PortletTemplateConfig();
            portletTemplateConfig.setCategory(categoryNode.getName());
            portletTemplateConfig.setTemplateName(templateNode.getName());

            String templateData = templateNode.getProperty("exo:templateFile").getString();
            zos.putNextEntry(new ZipEntry(TEMPLATES_LOCATION + applicationName + "/" + categoryNode.getName() + "/" + templateNode.getName()));
            zos.write(templateData.getBytes());
            zos.closeEntry();

            // Add Application template initParam definition
            ObjectParameter objectParam = new ObjectParameter();
            String templateName = templateNode.getName().replace(".gtmpl", "");
            objectParam.setName(templateName + ".application.template.configuration");
            objectParam.setObject(portletTemplateConfig);
            templatesPluginInitParams.addParam(objectParam);
          }
        }
      }
      dmsWorkspaceSession.logout();

      Configuration configuration = new Configuration();
      configuration.addComponent(component);

      zos.putNextEntry(new ZipEntry(component.getKey() + ".xml"));
      zos.write(toXML(configuration));
      zos.closeEntry();
      zos.close();

      Entry entry = new Entry(component.getKey());
      entry.setType(EntryType.ZIP);
      entry.setContent(out.toByteArray());
      return entry;
    } catch (Exception ie) {
      log.error("Error while invoking handler for component: " + component.getKey(), ie);
      return null;
    } finally {
      if (dmsWorkspaceSession != null)
        dmsWorkspaceSession.logout();
    }

  }

  @SuppressWarnings("unchecked")
  private List<ComponentPlugin> cleanComponentPlugins(Component component) {
    List<ComponentPlugin> componentPluginsList = component.getComponentPlugins();
    int i = 0;
    while (i < componentPluginsList.size()) {
      ComponentPlugin componentPlugin = componentPluginsList.get(i);
      if (componentPlugin.getType().equals(PortletTemplatePlugin.class.getName())) {
        componentPluginsList.remove(i);
      } else {
        i++;
      }
    }
    return componentPluginsList;
  }

}
