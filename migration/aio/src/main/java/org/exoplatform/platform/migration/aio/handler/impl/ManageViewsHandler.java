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

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
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
import org.exoplatform.services.cms.templates.impl.TemplatePlugin;
import org.exoplatform.services.cms.views.ManageViewService;
import org.exoplatform.services.cms.views.TemplateConfig;
import org.exoplatform.services.cms.views.ViewConfig;
import org.exoplatform.services.cms.views.ViewConfig.Tab;
import org.exoplatform.services.cms.views.impl.ManageViewPlugin;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;

public class ManageViewsHandler extends ComponentHandler {

  private final static String AUTO_CREATE_IN_NEW_REPOSITORY = "autoCreateInNewRepository";

  private final static String GTMPL_EXTENSION = ".gtmpl";

  private final static String TEMPLATES_LOCATION = "templates/";

  private final static String CB_PATH_TEMPLATE = "pathTemplate".intern();

  private final static String CB_QUERY_TEMPLATE = "queryTemplate".intern();

  private final static String CB_DETAIL_VIEW_TEMPLATE = "detailViewTemplate".intern();

  private final static String CB_SCRIPT_TEMPLATE = "scriptTemplate".intern();

  private final static String ECM_EXPLORER_TEMPLATE = "ecmExplorerTemplate".intern();

  private final static String EXO_PERMISSIONS = "exo:accessPermissions".intern();

  private final static String BUTTON_PROP = "exo:buttons".intern();

  private Log log = ExoLogger.getLogger(this.getClass());

  public ManageViewsHandler() {
    super.setTargetComponentName(ManageViewService.class.getName());
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
      List<ComponentPlugin> componentPluginsList = cleanComponentPlugins(component, ManageViewPlugin.class);

      ComponentPlugin templatesComponentPlugin = new ComponentPlugin();
      templatesComponentPlugin.setName("addPlugins");
      templatesComponentPlugin.setSetMethod("addPlugins");
      templatesComponentPlugin.setType(ManageViewPlugin.class.getName());

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
      valueParam.setName("predefinedViewsLocation");
      valueParam.setValue(TEMPLATES_LOCATION);
      templatesPluginInitParams.addParam(valueParam);

      RepositoryService repositoryService = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class));
      ManageableRepository repository = repositoryService.getDefaultRepository();
      String defaumtRepositoryName = repository.getConfiguration().getName();

      valueParam = new ValueParam();
      valueParam.setName("repository");
      valueParam.setValue(defaumtRepositoryName);
      templatesPluginInitParams.addParam(valueParam);
      componentPluginsList.add(templatesComponentPlugin);

      DMSConfiguration dmsConfiguration = ((DMSConfiguration) container.getComponentInstanceOfType(DMSConfiguration.class));
      String dmsSystemWorkspace = dmsConfiguration.getConfig(defaumtRepositoryName).getSystemWorkspace();

      NodeHierarchyCreator hierarchyCreator = ((NodeHierarchyCreator) container.getComponentInstanceOfType(NodeHierarchyCreator.class));

      dmsWorkspaceSession = repository.getSystemSession(dmsSystemWorkspace);

      extractTemplates(zos, templatesPluginInitParams, repository, dmsWorkspaceSession, hierarchyCreator, ECM_EXPLORER_TEMPLATE, BasePath.ECM_EXPLORER_TEMPLATES, "");
      extractTemplates(zos, templatesPluginInitParams, repository, dmsWorkspaceSession, hierarchyCreator, CB_DETAIL_VIEW_TEMPLATE, BasePath.CB_DETAIL_VIEW_TEMPLATES, "content-browser/");
      extractTemplates(zos, templatesPluginInitParams, repository, dmsWorkspaceSession, hierarchyCreator, CB_PATH_TEMPLATE, BasePath.CB_PATH_TEMPLATES, "content-browser/");
      extractTemplates(zos, templatesPluginInitParams, repository, dmsWorkspaceSession, hierarchyCreator, CB_QUERY_TEMPLATE, BasePath.CB_QUERY_TEMPLATES, "content-browser/");
      extractTemplates(zos, templatesPluginInitParams, repository, dmsWorkspaceSession, hierarchyCreator, CB_SCRIPT_TEMPLATE, BasePath.CB_SCRIPT_TEMPLATES, "content-browser/");

      List<ViewConfig> viewConfigList = getAllViews(defaumtRepositoryName, hierarchyCreator, dmsWorkspaceSession);
      for (ViewConfig viewConfig : viewConfigList) {
        ObjectParameter objectParam = new ObjectParameter();
        objectParam.setName(viewConfig.getName());
        objectParam.setObject(viewConfig);
        templatesPluginInitParams.addParam(objectParam);
      }

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

  /**
   * {@inheritDoc}
   */
  public List<ViewConfig> getAllViews(String repository, NodeHierarchyCreator hierarchyCreator, Session dmsWorkspaceSession) throws Exception {
    List<ViewConfig> viewList = new ArrayList<ViewConfig>();
    ViewConfig view = null;
    Node viewNode = null;
    String viewsPath = hierarchyCreator.getJcrPath(BasePath.CMS_VIEWS_PATH);
    try {
      Node viewHome = (Node) dmsWorkspaceSession.getItem(viewsPath);
      for (NodeIterator iter = viewHome.getNodes(); iter.hasNext();) {
        view = new ViewConfig();
        viewNode = iter.nextNode();
        view.setName(viewNode.getName());
        view.setPermissions(viewNode.getProperty(EXO_PERMISSIONS).getString());
        view.setTemplate(viewNode.getProperty(TemplatePlugin.EXO_TEMPLATE).getString());
        List<Tab> tabList = new ArrayList<Tab>();
        for (NodeIterator tabsIterator = viewNode.getNodes(); tabsIterator.hasNext();) {
          Node tabNode = tabsIterator.nextNode();
          Tab tab = new Tab();
          tab.setTabName(tabNode.getName());
          if (tabNode.hasProperty(BUTTON_PROP)) {
            tab.setButtons(tabNode.getProperty(BUTTON_PROP).getString());
          }
          tabList.add(tab);
        }
        view.setTabList(tabList);
        viewList.add(view);
      }
    } catch (AccessDeniedException ace) {
      return new ArrayList<ViewConfig>();
    }
    return viewList;
  }

  private void extractTemplates(ZipOutputStream zos, InitParams templatesPluginInitParams, ManageableRepository repository, Session dmsWorkspaceSession, NodeHierarchyCreator hierarchyCreator, String type, String basePath, String prefixPath) throws RepositoryException, PathNotFoundException, ValueFormatException, IOException {
    String templatesPath = hierarchyCreator.getJcrPath(basePath);

    Node templatesHome = (Node) dmsWorkspaceSession.getItem(templatesPath);

    NodeIterator ecmExplorerTemplatesIterator = templatesHome.getNodes();
    while (ecmExplorerTemplatesIterator.hasNext()) {
      Node templateNode = ecmExplorerTemplatesIterator.nextNode();

      // Generates the ObjectParameter "templateConfig" that will contains the Metadata Templates Definition
      TemplateConfig templateConfig = new TemplateConfig();
      templateConfig.setName(templateNode.getName());
      templateConfig.setTemplateType(type);
      String templatePath = prefixPath + templatesHome.getName() + "/" + templateNode.getName() + GTMPL_EXTENSION;
      templateConfig.setWarPath(templatePath);

      String template = templateNode.getProperty(TemplatePlugin.EXO_TEMPLATE_FILE_PROP).getString();

      zos.putNextEntry(new ZipEntry(TEMPLATES_LOCATION + templatePath));
      zos.write(template.getBytes());
      zos.closeEntry();

      // Add nodeType templates definition into the InitParams of the Component
      ObjectParameter objectParam = new ObjectParameter();
      objectParam.setName(templateNode.getName());
      objectParam.setObject(templateConfig);
      templatesPluginInitParams.addParam(objectParam);
    }
  }
}
