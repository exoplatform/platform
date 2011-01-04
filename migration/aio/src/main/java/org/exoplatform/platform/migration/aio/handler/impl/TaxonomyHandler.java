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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
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
import org.exoplatform.services.cms.actions.ActionServiceContainer;
import org.exoplatform.services.cms.actions.impl.ActionConfig;
import org.exoplatform.services.cms.actions.impl.ActionConfig.TaxonomyAction;
import org.exoplatform.services.cms.taxonomy.TaxonomyService;
import org.exoplatform.services.cms.taxonomy.impl.TaxonomyConfig;
import org.exoplatform.services.cms.taxonomy.impl.TaxonomyPlugin;
import org.exoplatform.services.cms.taxonomy.impl.TaxonomyConfig.Permission;
import org.exoplatform.services.cms.taxonomy.impl.TaxonomyConfig.Taxonomy;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.log.ExoLogger;

public class TaxonomyHandler extends ComponentHandler {

  private static final String EXO_PERMISSIONS = "exo:permissions";

  private static final String AUTO_CREATE_IN_NEW_REPOSITORY = "autoCreateInNewRepository".intern();

  private Log log = ExoLogger.getLogger(this.getClass());

  public TaxonomyHandler() {
    super.setTargetComponentName(TaxonomyService.class.getName());
  }

  @Override
  public Entry invoke(Component component, ExoContainer container) {
    Session dmsWorkspaceSession = null;
    try {
      if (log.isDebugEnabled()) {
        log.debug("Handler invoked for component: " + component.getKey());
      }
      List<ComponentPlugin> componentPluginsList = cleanComponentPlugins(component, TaxonomyPlugin.class);

      RepositoryService repositoryService = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class));
      ManageableRepository repository = repositoryService.getDefaultRepository();
      String defaumtRepositoryName = repository.getConfiguration().getName();

      TaxonomyService taxonomyService = ((TaxonomyService) container.getComponentInstanceOfType(TaxonomyService.class));
      ActionServiceContainer actionServiceContainer = ((ActionServiceContainer) container.getComponentInstanceOfType(ActionServiceContainer.class));
      List<Node> taxonomyTreeNodes = taxonomyService.getAllTaxonomyTrees(defaumtRepositoryName);

      for (Node node : taxonomyTreeNodes) {
        ComponentPlugin templatesComponentPlugin = new ComponentPlugin();
        templatesComponentPlugin.setName("predefinedTaxonomyPlugin");
        templatesComponentPlugin.setSetMethod("addTaxonomyPlugin");
        templatesComponentPlugin.setType(TaxonomyPlugin.class.getName());

        InitParams templatesPluginInitParams = new InitParams();
        templatesComponentPlugin.setInitParams(templatesPluginInitParams);
        componentPluginsList.add(templatesComponentPlugin);

        ValueParam valueParam = new ValueParam();
        valueParam.setName(AUTO_CREATE_IN_NEW_REPOSITORY);
        valueParam.setValue("true");
        templatesPluginInitParams.addParam(valueParam);

        valueParam = new ValueParam();
        valueParam.setName("repository");
        valueParam.setValue(defaumtRepositoryName);
        templatesPluginInitParams.addParam(valueParam);

        valueParam = new ValueParam();
        valueParam.setName("treeName");
        valueParam.setValue(node.getName());
        templatesPluginInitParams.addParam(valueParam);

        Node taxonomyRootNode = taxonomyService.getTaxonomyTree(defaumtRepositoryName, node.getName(), true);

        if (node.hasProperty(EXO_PERMISSIONS)) {// setup permissions
          TaxonomyConfig permissionTaxonomyConfig = new TaxonomyConfig();
          List<Taxonomy> taxonomyList = new ArrayList<Taxonomy>();
          permissionTaxonomyConfig.setTaxonomies(taxonomyList);
          Taxonomy taxonomy = new Taxonomy();
          taxonomyList.add(taxonomy);

          Value[] permissionValues = node.getProperty(EXO_PERMISSIONS).getValues();
          taxonomy.setPermissions(getPermissions(permissionValues));

          ObjectParameter objectParam = new ObjectParameter();
          objectParam.setName("permission.configuration");
          objectParam.setObject(permissionTaxonomyConfig);
          templatesPluginInitParams.addParam(objectParam);
        }

        {// Setup taxonomy sub Tree definition
          TaxonomyConfig taxonomyConfiguration = new TaxonomyConfig();
          List<Taxonomy> taxonomyList = new ArrayList<Taxonomy>();
          taxonomyConfiguration.setTaxonomies(taxonomyList);

          String taxonomyRootPath = taxonomyRootNode.getPath();
          NodeIterator subNodes = taxonomyRootNode.getNodes();
          while (subNodes.hasNext()) {
            Node subNode = subNodes.nextNode();
            String subTaxonomyPath = subNode.getPath();
            Taxonomy taxonomy = new Taxonomy();
            taxonomyList.add(taxonomy);
            if (subNode.hasProperty(EXO_PERMISSIONS)) {
              Value[] permissionValues = subNode.getProperty(EXO_PERMISSIONS).getValues();
              taxonomy.setPermissions(getPermissions(permissionValues));
            }
            taxonomy.setName(subNode.getName());
            taxonomy.setPath(subTaxonomyPath.replace(taxonomyRootPath, ""));
          }

          ObjectParameter objectParam = new ObjectParameter();
          objectParam.setName("taxonomy.configuration");
          objectParam.setObject(taxonomyConfiguration);
          templatesPluginInitParams.addParam(objectParam);
        }

        {// setup taxonomy actions definition
          ActionConfig actionConfig = new ActionConfig();
          List<TaxonomyAction> taxonomyActionList = new ArrayList<TaxonomyAction>();
          actionConfig.setActions(taxonomyActionList);
          actionConfig.setAutoCreatedInNewRepository(false);

          List<Node> taxonomyActionNodes = actionServiceContainer.getActions(taxonomyRootNode);
          for (Node taxonomyActionNode : taxonomyActionNodes) {
            TaxonomyAction taxonomyAction = new TaxonomyAction();
            taxonomyActionList.add(taxonomyAction);

            taxonomyAction.setName(taxonomyActionNode.getName());
            if (taxonomyActionNode.hasProperty("exo:description")) {
              taxonomyAction.setDescription(taxonomyActionNode.getProperty("exo:description").getString());
            }
            if (taxonomyActionNode.hasProperty("exo:storeHomePath")) {
              taxonomyAction.setHomePath(taxonomyActionNode.getProperty("exo:storeHomePath").getString());
            }
            if (taxonomyActionNode.hasProperty("exo:lifecyclePhase")) {
              taxonomyAction.setLifecyclePhase(taxonomyActionNode.getProperty("exo:lifecyclePhase").getString());
            }

            List<String> mixins = new ArrayList<String>();
            taxonomyActionNode.getMixinNodeTypes();
            for (String mixin : mixins) {
              mixins.add(mixin);
            }
            taxonomyAction.setMixins(mixins);

            if (taxonomyActionNode.hasProperty("exo:role")) {
              Value[] roles = taxonomyActionNode.getProperty("exo:role").getValues();
              StringBuffer rolesStringBuffer = new StringBuffer();
              for (Value roleValue : roles) {
                rolesStringBuffer.append(roleValue.getString()).append(";");
              }
              if (rolesStringBuffer.length() > 0) {
                rolesStringBuffer.replace(rolesStringBuffer.length() - 1, rolesStringBuffer.length(), "");
              }
              taxonomyAction.setRoles(rolesStringBuffer.toString());
            }

            if (taxonomyActionNode.hasProperty("exo:targetPath")) {
              taxonomyAction.setTargetPath(taxonomyActionNode.getProperty("exo:targetPath").getString());
            }
            if (taxonomyActionNode.hasProperty("exo:targetWorkspace")) {
              taxonomyAction.setTargetWspace(taxonomyActionNode.getProperty("exo:targetWorkspace").getString());
            }
            taxonomyAction.setType(taxonomyActionNode.getPrimaryNodeType().getName());
          }

          ObjectParameter objectParam = new ObjectParameter();
          objectParam.setName("predefined.actions");
          objectParam.setObject(actionConfig);
          templatesPluginInitParams.addParam(objectParam);
        }

      }

      Configuration configuration = new Configuration();
      configuration.addComponent(component);

      Entry entry = new Entry(component.getKey());
      entry.setType(EntryType.XML);
      entry.setContent(toXML(configuration));
      return entry;
    } catch (Exception ie) {
      log.error("Error while invoking handler for component: " + component.getKey(), ie);
      return null;
    } finally {
      if (dmsWorkspaceSession != null)
        dmsWorkspaceSession.logout();
    }
  }

  public List<Permission> getPermissions(Value[] permissionValues) throws ValueFormatException, IllegalStateException, RepositoryException {
    Map<String, List<String>> permissionsMap = new HashMap<String, List<String>>();
    for (Value permission : permissionValues) {
      String[] permissionEntries = permission.getString().split(" ");
      List<String> permissionDetails = null;
      if (!permissionsMap.containsKey(permissionEntries[0])) {
        permissionDetails = new ArrayList<String>();
      } else {
        permissionDetails = permissionsMap.get(permissionEntries[0]);
      }
      permissionDetails.add(permissionEntries[1]);
      permissionsMap.put(permissionEntries[0], permissionDetails);
    }
    List<Permission> permissionList = new ArrayList<Permission>();
    for (Map.Entry<String, List<String>> permissionEntry : permissionsMap.entrySet()) {
      Permission permission = new Permission();
      permission.setIdentity(permissionEntry.getKey());
      for (String perm : permissionEntry.getValue()) {
        if (PermissionType.READ.equals(perm))
          permission.setRead("true");
        else if (PermissionType.ADD_NODE.equals(perm))
          permission.setAddNode("true");
        else if (PermissionType.SET_PROPERTY.equals(perm))
          permission.setSetProperty("true");
        else if (PermissionType.REMOVE.equals(perm))
          permission.setRemove("true");
      }
      permissionList.add(permission);
    }
    return permissionList;
  }

}
