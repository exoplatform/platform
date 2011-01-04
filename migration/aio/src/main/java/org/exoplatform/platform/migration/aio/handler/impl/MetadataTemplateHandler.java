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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

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
import org.exoplatform.services.cms.metadata.MetadataService;
import org.exoplatform.services.cms.templates.impl.TemplateConfig;
import org.exoplatform.services.cms.templates.impl.TemplatePlugin;
import org.exoplatform.services.cms.templates.impl.TemplateConfig.Template;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;

public class MetadataTemplateHandler extends ComponentHandler {

  private static final String AUTO_CREATE_IN_NEW_REPOSITORY = "autoCreateInNewRepository";

  private static final String GTMPL_EXTENSION = ".gtmpl";

  private static final String TEMPLATES_NODETYPE_LOCATION = "matadata-templates";

  private static final String CSS_EXTENSION = ".css";

  private Log log = ExoLogger.getLogger(this.getClass());

  public MetadataTemplateHandler() {
    super.setTargetComponentName(MetadataService.class.getName());
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
      List<ComponentPlugin> componentPluginsList = cleanComponentPlugins(component, TemplatePlugin.class);

      ComponentPlugin templatesComponentPlugin = new ComponentPlugin();
      templatesComponentPlugin.setName("addPlugins");
      templatesComponentPlugin.setSetMethod("addPlugins");
      templatesComponentPlugin.setType(TemplatePlugin.class.getName());

      InitParams templatesPluginInitParams = new InitParams();
      templatesComponentPlugin.setInitParams(templatesPluginInitParams);

      if (component.getInitParams() != null) {
        ValueParam valueParam = new ValueParam();
        valueParam.setName(AUTO_CREATE_IN_NEW_REPOSITORY);
        String autoCreateInNewRepository = "false";
        ValueParam originalParam = component.getInitParams().getValueParam(AUTO_CREATE_IN_NEW_REPOSITORY);
        if (originalParam != null) {
          autoCreateInNewRepository = originalParam.getValue();
        }
        valueParam.setValue(autoCreateInNewRepository);
        templatesPluginInitParams.addParam(valueParam);
      }

      ValueParam valueParam = new ValueParam();
      valueParam.setName("storedLocation");
      valueParam.setValue(TEMPLATES_NODETYPE_LOCATION);
      templatesPluginInitParams.addParam(valueParam);

      RepositoryService repositoryService = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class));
      ManageableRepository repository = repositoryService.getDefaultRepository();
      String defaumtRepositoryName = repository.getConfiguration().getName();

      valueParam = new ValueParam();
      valueParam.setName("repository");
      valueParam.setValue(defaumtRepositoryName);
      templatesPluginInitParams.addParam(valueParam);
      componentPluginsList.add(templatesComponentPlugin);

      // Generates the ObjectParameter "templateConfig" that will contains the Metadata Templates Definition
      TemplateConfig templateConfig = new TemplateConfig();
      List<TemplateConfig.NodeType> metadatsList = new ArrayList<TemplateConfig.NodeType>();
      templateConfig.setNodeTypes(metadatsList);

      DMSConfiguration dmsConfiguration = ((DMSConfiguration) container.getComponentInstanceOfType(DMSConfiguration.class));
      String dmsSystemWorkspace = dmsConfiguration.getConfig(defaumtRepositoryName).getSystemWorkspace();

      NodeHierarchyCreator hierarchyCreator = ((NodeHierarchyCreator) container.getComponentInstanceOfType(NodeHierarchyCreator.class));
      String metadataTemplatesPath = hierarchyCreator.getJcrPath(BasePath.METADATA_PATH);

      dmsWorkspaceSession = repository.getSystemSession(dmsSystemWorkspace);
      Node baseTemplateHome = (Node) dmsWorkspaceSession.getItem(metadataTemplatesPath);

      NodeIterator matadataNodesIterator = baseTemplateHome.getNodes();
      while (matadataNodesIterator.hasNext()) {
        Node matadataNode = matadataNodesIterator.nextNode();

        TemplateConfig.NodeType nodeTypeTemplates = new TemplateConfig.NodeType();
        metadatsList.add(nodeTypeTemplates);

        if (matadataNode.hasProperty(TemplatePlugin.TEMPLATE_LABEL)) {
          nodeTypeTemplates.setLabel(matadataNode.getProperty(TemplatePlugin.TEMPLATE_LABEL).getString());
        }
        nodeTypeTemplates.setNodetypeName(matadataNode.getName());
        if (matadataNode.hasProperty(TemplatePlugin.DOCUMENT_TEMPLATE_PROP)) {
          nodeTypeTemplates.setDocumentTemplate(matadataNode.getProperty(TemplatePlugin.DOCUMENT_TEMPLATE_PROP).getBoolean());
        }

        if (matadataNode.hasNode(TemplatePlugin.DIALOGS)) {
          // Generates Dialog Templates
          NodeIterator dialogsNodeIterator = matadataNode.getNode(TemplatePlugin.DIALOGS).getNodes();
          List<Template> dialogs = generateDialogTemplates(zos, matadataNode.getName(), dialogsNodeIterator);
          nodeTypeTemplates.setReferencedDialog(dialogs);
        }
        if (matadataNode.hasNode(TemplatePlugin.VIEWS)) {
          // Generates View Templates
          NodeIterator viewsNodeIterator = matadataNode.getNode(TemplatePlugin.VIEWS).getNodes();
          List<Template> views = generateViewTemplate(zos, matadataNode.getName(), viewsNodeIterator);
          nodeTypeTemplates.setReferencedView(views);
        }

        if (matadataNode.hasNode(TemplatePlugin.SKINS)) {
          // Generates Skin Templates
          NodeIterator skinsNodeIterator = matadataNode.getNode(TemplatePlugin.SKINS).getNodes();
          List<Template> skins = generateSkinTemplate(zos, matadataNode.getName(), skinsNodeIterator);
          nodeTypeTemplates.setReferencedSkin(skins);
        }
      }

      // Add nodeType templates definition into the InitParams of the Component
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
    } finally {
      if (dmsWorkspaceSession != null)
        dmsWorkspaceSession.logout();
    }

  }

  private List<Template> generateSkinTemplate(ZipOutputStream zos, String nodeType, NodeIterator skinsNodeIterator) throws RepositoryException, ValueFormatException, PathNotFoundException, IOException {
    List<Template> skins = new ArrayList<Template>();
    while (skinsNodeIterator.hasNext()) {
      Node templateNode = skinsNodeIterator.nextNode();
      Template skinTemplate = new TemplateConfig.Template();
      String templateLocation = "/" + nodeType + "/" + TemplatePlugin.SKINS + "/" + templateNode.getName();
      skinTemplate.setTemplateFile(templateLocation);
      Value[] values = templateNode.getProperty(TemplatePlugin.EXO_ROLES_PROP).getValues();
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

      String template = templateNode.getProperty(TemplatePlugin.EXO_TEMPLATE_FILE_PROP).getString();

      zos.putNextEntry(new ZipEntry(TEMPLATES_NODETYPE_LOCATION + templateLocation + CSS_EXTENSION));
      zos.write(template.getBytes());
      zos.closeEntry();
    }
    return skins;
  }

  private List<Template> generateViewTemplate(ZipOutputStream zos, String nodeType, NodeIterator viewsNodeIterator) throws RepositoryException, ValueFormatException, PathNotFoundException, IOException {
    List<Template> views = new ArrayList<Template>();
    while (viewsNodeIterator.hasNext()) {
      Node templateNode = viewsNodeIterator.nextNode();
      Template viewTemplate = new TemplateConfig.Template();
      String templateLocation = "/" + nodeType + "/" + TemplatePlugin.VIEWS + "/" + templateNode.getName();
      viewTemplate.setTemplateFile(templateLocation);
      Value[] values = templateNode.getProperty(TemplatePlugin.EXO_ROLES_PROP).getValues();
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

      String template = templateNode.getProperty(TemplatePlugin.EXO_TEMPLATE_FILE_PROP).getString();

      zos.putNextEntry(new ZipEntry(TEMPLATES_NODETYPE_LOCATION + templateLocation + GTMPL_EXTENSION));
      zos.write(template.getBytes());
      zos.closeEntry();
    }
    return views;
  }

  private List<Template> generateDialogTemplates(ZipOutputStream zos, String nodeType, NodeIterator dialogsNodeIterator) throws RepositoryException, ValueFormatException, PathNotFoundException, IOException {
    List<Template> dialogs = new ArrayList<Template>();
    while (dialogsNodeIterator.hasNext()) {
      Node templateNode = dialogsNodeIterator.nextNode();
      Template dialogTemplate = new TemplateConfig.Template();
      String templateLocation = "/" + nodeType + "/" + TemplatePlugin.DIALOGS + "/" + templateNode.getName();
      dialogTemplate.setTemplateFile(templateLocation);
      Value[] values = templateNode.getProperty(TemplatePlugin.EXO_ROLES_PROP).getValues();
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
      String template = templateNode.getProperty(TemplatePlugin.EXO_TEMPLATE_FILE_PROP).getString();

      zos.putNextEntry(new ZipEntry(TEMPLATES_NODETYPE_LOCATION + templateLocation + GTMPL_EXTENSION));
      zos.write(template.getBytes());
      zos.closeEntry();
    }
    return dialogs;
  }
}
