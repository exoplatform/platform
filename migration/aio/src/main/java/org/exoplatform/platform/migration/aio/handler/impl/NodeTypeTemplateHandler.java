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
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.services.cms.templates.TemplateService;
import org.exoplatform.services.cms.templates.impl.TemplateConfig;
import org.exoplatform.services.cms.templates.impl.TemplatePlugin;
import org.exoplatform.services.cms.templates.impl.TemplateConfig.Template;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;

public class NodeTypeTemplateHandler extends ComponentHandler {

  private static final String GTMPL_EXTENSION = ".gtmpl";

  private static final String TEMPLATES_NODETYPE_LOCATION = "nodetype-templates";

  private static final String CSS_EXTENSION = ".css";

  private Log log = ExoLogger.getLogger(this.getClass());

  StringBuffer nodeTypePathTmp_ = new StringBuffer();

  public NodeTypeTemplateHandler() {
    super.setTargetComponentName(TemplateService.class.getName());
  }

  @Override
  public Entry invoke(Component component, ExoContainer container) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(out);
      if (log.isDebugEnabled()) {
        log.debug("Handler invoked for component: " + component.getKey());
      }
      TemplateService templateService = ((TemplateService) container.getComponentInstanceOfType(TemplateService.class));
      List<ComponentPlugin> componentPluginsList = cleanComponentPlugins(component);

      ComponentPlugin templatesComponentPlugin = new ComponentPlugin();
      templatesComponentPlugin.setName("addTemplates");
      templatesComponentPlugin.setSetMethod("addTemplates");
      templatesComponentPlugin.setType(TemplatePlugin.class.getName());
      InitParams templatesPluginInitParams = new InitParams();
      templatesComponentPlugin.setInitParams(templatesPluginInitParams);

      ValueParam valueParam = new ValueParam();
      valueParam.setName("autoCreateInNewRepository");
      valueParam.setValue("true");
      templatesPluginInitParams.addParam(valueParam);

      valueParam = new ValueParam();
      valueParam.setName("storedLocation");
      valueParam.setValue(TEMPLATES_NODETYPE_LOCATION);
      templatesPluginInitParams.addParam(valueParam);

      valueParam = new ValueParam();
      valueParam.setName("repository");
      valueParam.setValue("repository");
      templatesPluginInitParams.addParam(valueParam);
      componentPluginsList.add(templatesComponentPlugin);

      RepositoryService repositoryService = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class));
      String repositoryName = repositoryService.getDefaultRepository().getConfiguration().getName();

      TemplateConfig templateConfig = new TemplateConfig();
      List<TemplateConfig.NodeType> documentTypeList = new ArrayList<TemplateConfig.NodeType>();
      templateConfig.setNodeTypes(documentTypeList);

      NodeTypeManager ntManager = repositoryService.getDefaultRepository().getNodeTypeManager();
      NodeTypeIterator nodeTypeIter = ntManager.getAllNodeTypes();
      List<String> documentTypeTemplateNames = templateService.getAllDocumentNodeTypes(repositoryName);
      SessionProvider systemSessionProvider = SessionProvider.createSystemProvider();
      while (nodeTypeIter.hasNext()) {
        NodeType nodeType = nodeTypeIter.nextNodeType();
        boolean isManagedNodeType = templateService.isManagedNodeType(nodeType.getName(), repositoryName);
        if (isManagedNodeType) {
          TemplateConfig.NodeType nodeTypeTemplates = new TemplateConfig.NodeType();
          nodeTypeTemplates.setLabel(templateService.getTemplateLabel(nodeType.getName(), repositoryName));
          nodeTypeTemplates.setNodetypeName(nodeType.getName());
          nodeTypeTemplates.setDocumentTemplate(documentTypeTemplateNames.contains(nodeType.getName()));
          documentTypeList.add(nodeTypeTemplates);
          NodeIterator dialogsNodeIterator = templateService.getAllTemplatesOfNodeType(true, nodeType.getName(), repositoryName, systemSessionProvider);
          List<Template> dialogs = new ArrayList<Template>();
          while (dialogsNodeIterator.hasNext()) {
            Node templateNode = dialogsNodeIterator.nextNode();
            Template dialogTemplate = new TemplateConfig.Template();
            String templateLocation = "/" + nodeType.getName() + "/" + TemplateService.DIALOGS + "/" + templateNode.getName();
            dialogTemplate.setTemplateFile(templateLocation);
            Value[] values = templateNode.getProperty(TemplateService.EXO_ROLES_PROP).getValues();
            String roles = "";
            for (int i = 0; i < values.length; i++) {
              Value value = values[i];
              roles += value.getString();
              if (i < (values.length - 1)) {
                roles += ",";
              }
            }
            dialogTemplate.setRoles(roles);
            dialogs.add(dialogTemplate);
            String template = templateNode.getProperty(TemplateService.EXO_TEMPLATE_FILE_PROP).getString();

            zos.putNextEntry(new ZipEntry(TEMPLATES_NODETYPE_LOCATION + templateLocation + GTMPL_EXTENSION));
            zos.write(template.getBytes());
            zos.closeEntry();
          }
          nodeTypeTemplates.setReferencedDialog(dialogs);

          List<Template> views = new ArrayList<Template>();
          NodeIterator viewsNodeIterator = templateService.getAllTemplatesOfNodeType(false, nodeType.getName(), repositoryName, systemSessionProvider);
          while (viewsNodeIterator.hasNext()) {
            Node templateNode = viewsNodeIterator.nextNode();
            Template viewTemplate = new TemplateConfig.Template();
            String templateLocation = "/" + nodeType.getName() + "/" + TemplateService.VIEWS + "/" + templateNode.getName();
            viewTemplate.setTemplateFile(templateLocation);
            Value[] values = templateNode.getProperty(TemplateService.EXO_ROLES_PROP).getValues();
            String roles = "";
            for (int i = 0; i < values.length; i++) {
              Value value = values[i];
              roles += value.getString();
              if (i < (values.length - 1)) {
                roles += ",";
              }
            }
            viewTemplate.setRoles(roles);
            views.add(viewTemplate);

            String template = templateNode.getProperty(TemplateService.EXO_TEMPLATE_FILE_PROP).getString();

            zos.putNextEntry(new ZipEntry(TEMPLATES_NODETYPE_LOCATION + templateLocation + GTMPL_EXTENSION));
            zos.write(template.getBytes());
            zos.closeEntry();
          }
          nodeTypeTemplates.setReferencedView(views);

          Node nodeTypeNode = templateService.getTemplatesHome(repositoryName, systemSessionProvider).getNode(nodeType.getName());
          if (nodeTypeNode.hasNode(TemplateService.SKINS)) {
            List<Template> skins = new ArrayList<Template>();
            NodeIterator skinsNodeIterator = nodeTypeNode.getNode(TemplateService.SKINS).getNodes();
            while (skinsNodeIterator.hasNext()) {
              Node templateNode = skinsNodeIterator.nextNode();
              Template skinTemplate = new TemplateConfig.Template();
              String templateLocation = "/" + nodeType.getName() + "/" + TemplateService.SKINS + "/" + templateNode.getName();
              skinTemplate.setTemplateFile(templateLocation);
              Value[] values = templateNode.getProperty(TemplateService.EXO_ROLES_PROP).getValues();
              String roles = "";
              for (int i = 0; i < values.length; i++) {
                Value value = values[i];
                roles += value.getString();
                if (i < (values.length - 1)) {
                  roles += ",";
                }
              }
              skinTemplate.setRoles(roles);
              skins.add(skinTemplate);

              String template = templateNode.getProperty(TemplateService.EXO_TEMPLATE_FILE_PROP).getString();

              zos.putNextEntry(new ZipEntry(TEMPLATES_NODETYPE_LOCATION + templateLocation + CSS_EXTENSION));
              zos.write(template.getBytes());
              zos.closeEntry();
            }
            nodeTypeTemplates.setReferencedSkin(skins);
          }
        }
      }
      ObjectParameter objectParam = new ObjectParameter();
      objectParam.setName("template.configuration");
      objectParam.setObject(templateConfig);
      templatesPluginInitParams.addParam(objectParam);

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
    }

  }

  @SuppressWarnings("unchecked")
  private List<ComponentPlugin> cleanComponentPlugins(Component component) {
    List<ComponentPlugin> componentPluginsList = component.getComponentPlugins();
    int i = 0;
    while (i < componentPluginsList.size()) {
      ComponentPlugin componentPlugin = componentPluginsList.get(i);
      if (componentPlugin.getType().equals(TemplatePlugin.class.getName())) {
        componentPluginsList.remove(i);
      } else {
        i++;
      }
    }
    return componentPluginsList;
  }

}
