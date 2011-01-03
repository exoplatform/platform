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

import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.PropertyDefinition;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.Property;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.impl.AddNamespacesPlugin;
import org.exoplatform.services.jcr.impl.AddNodeTypePlugin;
import org.exoplatform.services.log.ExoLogger;

public class NodeTypeManagerConfigHandler extends ComponentHandler {

  private Log log = ExoLogger.getLogger(this.getClass());

  StringBuffer nodeTypePathTmp_ = new StringBuffer();

  public NodeTypeManagerConfigHandler() {
    super.setTargetComponentName(RepositoryService.class.getName());
  }

  @Override
  public Entry invoke(Component component, ExoContainer container) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(out);
      if (log.isDebugEnabled()) {
        log.debug("Handler invoked for component: " + component.getKey());
      }
      RepositoryService repositoryService = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class));
      NodeTypeManager ntManager = repositoryService.getDefaultRepository().getNodeTypeManager();
      NodeTypeIterator nodeTypeIter = ntManager.getAllNodeTypes();

      List<ComponentPlugin> componentPluginsList = cleanComponentPlugins(component);

      addNamespaceComponentPlugin(repositoryService, componentPluginsList);
      generateNodeTypesConfiguration(zos, nodeTypeIter, componentPluginsList);

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

  private void generateRepositoryConfiguration(RepositoryService repositoryService, ZipOutputStream zos) throws IOException, Exception {
    zos.putNextEntry(new ZipEntry("repository-configuration.xml"));
    zos.write(toXML(repositoryService.getConfig()));
    zos.closeEntry();
    zos.close();
  }

  private void generateNodeTypesConfiguration(ZipOutputStream zos, NodeTypeIterator nodeTypeIter, List<ComponentPlugin> componentPluginsList) {
    ValuesParam nodeTypesValuesParam = new ValuesParam();
    nodeTypesValuesParam.setName("autoCreatedInNewRepository");
    nodeTypesValuesParam.setDescription("Node types configuration file");
    ArrayList<String> nodeTypesPathsList = new ArrayList<String>();
    nodeTypesValuesParam.setValues(nodeTypesPathsList);

    InitParams nodeTypePluginInitParams = new InitParams();
    nodeTypePluginInitParams.addParam(nodeTypesValuesParam);
    while (nodeTypeIter.hasNext()) {
      NodeType nodeType = nodeTypeIter.nextNodeType();
      addNodeTypeXML(nodeType, zos);
      nodeTypesPathsList.add(getNodeTypePath(nodeType));
    }
    ComponentPlugin addNodeTypePlugin = new ComponentPlugin();
    addNodeTypePlugin.setName("add.nodeType");
    addNodeTypePlugin.setInitParams(nodeTypePluginInitParams);
    addNodeTypePlugin.setSetMethod("addPlugin");
    addNodeTypePlugin.setType(AddNodeTypePlugin.class.getName());
    componentPluginsList.add(addNodeTypePlugin);
  }

  private void addNamespaceComponentPlugin(RepositoryService repositoryService, List<ComponentPlugin> componentPluginsList) throws RepositoryException, RepositoryConfigurationException, NamespaceException {
    NamespaceRegistry namespaceRegistry = repositoryService.getDefaultRepository().getNamespaceRegistry();
    PropertiesParam namespacesParam = new PropertiesParam();
    namespacesParam.setName("namespaces");
    String[] uris = namespaceRegistry.getURIs();
    for (String uri : uris) {
      namespacesParam.addProperty(new Property(namespaceRegistry.getPrefix(uri), uri));
    }
    InitParams namespacesInitParams = new InitParams();
    namespacesInitParams.addParam(namespacesParam);
    ComponentPlugin addNamespacesPlugin = new ComponentPlugin();
    addNamespacesPlugin.setName("add.nodeType");
    addNamespacesPlugin.setInitParams(namespacesInitParams);
    addNamespacesPlugin.setSetMethod("addPlugin");
    addNamespacesPlugin.setType(AddNamespacesPlugin.class.getName());
    componentPluginsList.add(addNamespacesPlugin);
  }

  @SuppressWarnings("unchecked")
  private List<ComponentPlugin> cleanComponentPlugins(Component component) {
    List<ComponentPlugin> componentPluginsList = component.getComponentPlugins();
    int i = 0;
    while (i < componentPluginsList.size()) {
      ComponentPlugin componentPlugin = componentPluginsList.get(i);
      if (componentPlugin.getType().equals(AddNodeTypePlugin.class.getName()) || componentPlugin.getType().equals(AddNamespacesPlugin.class.getName())) {
        componentPluginsList.remove(i);
      } else {
        i++;
      }
    }
    return componentPluginsList;
  }

  private String addNodeTypeXML(NodeType nodeType, ZipOutputStream zos) {
    StringBuffer nodeTypeXML = new StringBuffer();
    nodeTypeXML.append("<nodeTypes xmlns:nt=\"http://www.jcp.org/jcr/nt/1.5\" ");
    nodeTypeXML.append("xmlns:mix=\"http://www.jcp.org/jcr/mix/1.5\" ");
    nodeTypeXML.append("xmlns:jcr=\"http://www.jcp.org/jcr/1.5\" >\n");
    nodeTypeXML.append("<nodeType ");
    nodeTypeXML.append("name=\"").append(nodeType.getName()).append("\" ");
    nodeTypeXML.append("isMixin=\"").append(nodeType.isMixin()).append("\" ");
    nodeTypeXML.append("hasOrderableChildNodes=\"").append(nodeType.hasOrderableChildNodes()).append("\" ");
    String primaryItemName = "";
    if (nodeType.getPrimaryItemName() != null)
      primaryItemName = nodeType.getPrimaryItemName();
    nodeTypeXML.append("primaryItemName=").append("\"").append(primaryItemName).append("\" >\n");
    // represent supertypes
    String representSuperType = representSuperTypes(nodeType);
    nodeTypeXML.append(representSuperType);
    // represent PropertiesDefinition
    String representPropertiesXML = representPropertyDefinition(nodeType);
    nodeTypeXML.append(representPropertiesXML);
    // represent ChildNodeDefinition
    String representChildXML = representChildNodeDefinition(nodeType);
    nodeTypeXML.append(representChildXML);
    nodeTypeXML.append("</nodeType>").append("\n");
    nodeTypeXML.append("</nodeTypes>");

    try {
      zos.putNextEntry(new ZipEntry(getNodeTypePath(nodeType)));
      zos.write(nodeTypeXML.toString().getBytes());
      zos.closeEntry();
    } catch (Exception exception) {
      log.error("Error while adding nodetype : " + nodeType.getName(), exception);
    }
    return nodeTypeXML.toString();
  }

  private String getNodeTypePath(NodeType nodeType) {
    nodeTypePathTmp_.delete(0, nodeTypePathTmp_.length());
    nodeTypePathTmp_.append("nodeTypes/");
    nodeTypePathTmp_.append(nodeType.getName().replace(":", "/"));
    nodeTypePathTmp_.append(".xml");
    return nodeTypePathTmp_.toString();
  }

  private String representSuperTypes(NodeType nodeType) {
    StringBuilder superTypeXML = new StringBuilder();
    NodeType[] superType = nodeType.getDeclaredSupertypes();
    if (superType != null && superType.length > 0) {
      superTypeXML.append("<supertypes>").append("\n");
      for (int i = 0; i < superType.length; i++) {
        String typeName = superType[i].getName();
        superTypeXML.append("<supertype>").append(typeName).append("</supertype>").append("\n");
      }
      superTypeXML.append("</supertypes>").append("\n");
    }
    return superTypeXML.toString();
  }

  private String representPropertyDefinition(NodeType nodeType) {
    String[] requireType = { "undefined", "String", "Binary", "Long", "Double", "Date", "Boolean", "Name", "Path", "Reference" };
    String[] onparentVersion = { "", "COPY", "VERSION", "INITIALIZE", "COMPUTE", "IGNORE", "ABORT" };
    StringBuilder propertyXML = new StringBuilder();
    propertyXML.append("<propertyDefinitions>").append("\n");
    PropertyDefinition[] proDef = nodeType.getPropertyDefinitions();
    for (int j = 0; j < proDef.length; j++) {
      propertyXML.append("<propertyDefinition ");
      propertyXML.append("name=").append("\"").append(proDef[j].getName()).append("\" ");
      String requiredValue = null;
      if (proDef[j].getRequiredType() == 100)
        requiredValue = "Permission";
      else
        requiredValue = requireType[proDef[j].getRequiredType()];
      propertyXML.append("requiredType=").append("\"").append(requiredValue).append("\" ");
      String autoCreate = String.valueOf(proDef[j].isAutoCreated());
      propertyXML.append("autoCreated=").append("\"").append(autoCreate).append("\" ");
      String mandatory = String.valueOf(proDef[j].isMandatory());
      propertyXML.append("mandatory=").append("\"").append(mandatory).append("\" ");
      String onVersion = onparentVersion[proDef[j].getOnParentVersion()];
      propertyXML.append("onParentVersion=").append("\"").append(onVersion).append("\" ");
      String protect = String.valueOf(proDef[j].isProtected());
      propertyXML.append("protected=").append("\"").append(protect).append("\" ");
      String multiple = String.valueOf(proDef[j].isMultiple());
      propertyXML.append("multiple=").append("\"").append(multiple).append("\" >").append("\n");
      String[] constraints = proDef[j].getValueConstraints();
      if (constraints != null && constraints.length > 0) {
        propertyXML.append("<valueConstraints>").append("\n");
        for (int k = 0; k < constraints.length; k++) {
          String cons = constraints[k].toString();
          propertyXML.append("<valueConstraint>").append(cons).append("</valueConstraint>");
          propertyXML.append("\n");
        }
        propertyXML.append("</valueConstraints>").append("\n");
      } else {
        propertyXML.append("<valueConstraints/>").append("\n");
      }
      propertyXML.append("</propertyDefinition>").append("\n");
    }
    propertyXML.append("</propertyDefinitions>").append("\n");
    return propertyXML.toString();
  }

  private String representChildNodeDefinition(NodeType nodeType) {
    String[] onparentVersion = { "", "COPY", "VERSION", "INITIALIZE", "COMPUTE", "IGNORE", "ABORT" };
    StringBuilder childNodeXML = new StringBuilder();
    NodeDefinition[] childDef = nodeType.getChildNodeDefinitions();
    if (childDef != null && childDef.length > 0) {
      childNodeXML.append("<childNodeDefinitions>").append("\n");
      for (int j = 0; j < childDef.length; j++) {
        childNodeXML.append("<childNodeDefinition ");
        childNodeXML.append("name=").append("\"").append(childDef[j].getName()).append("\" ");
        NodeType defaultType = childDef[j].getDefaultPrimaryType();
        if (defaultType != null) {
          String defaultName = defaultType.getName();
          childNodeXML.append("defaultPrimaryType=").append("\"").append(defaultName).append("\" ");
        } else {
          childNodeXML.append("defaultPrimaryType=").append("\"").append("\" ");
        }
        String autoCreate = String.valueOf(childDef[j].isAutoCreated());
        childNodeXML.append("autoCreated=").append("\"").append(autoCreate).append("\" ");
        String mandatory = String.valueOf(childDef[j].isMandatory());
        childNodeXML.append("mandatory=").append("\"").append(mandatory).append("\" ");
        String onVersion = onparentVersion[childDef[j].getOnParentVersion()];
        childNodeXML.append("onParentVersion=").append("\"").append(onVersion).append("\" ");
        String protect = String.valueOf(childDef[j].isProtected());
        childNodeXML.append("protected=").append("\"").append(protect).append("\" ");
        String sameName = String.valueOf(childDef[j].allowsSameNameSiblings());
        childNodeXML.append("sameNameSiblings=").append("\"").append(sameName).append("\" >");
        childNodeXML.append("\n");
        NodeType[] requiredType = childDef[j].getRequiredPrimaryTypes();
        if (requiredType != null && requiredType.length > 0) {
          childNodeXML.append("<requiredPrimaryTypes>").append("\n");
          for (int k = 0; k < requiredType.length; k++) {
            String requiredName = requiredType[k].getName();
            childNodeXML.append("<requiredPrimaryType>").append(requiredName);
            childNodeXML.append("</requiredPrimaryType>").append("\n");
          }
          childNodeXML.append("</requiredPrimaryTypes>").append("\n");
        }
        childNodeXML.append("</childNodeDefinition>").append("\n");
      }
      childNodeXML.append("</childNodeDefinitions>").append("\n");
    }
    return childNodeXML.toString();
  }

}
