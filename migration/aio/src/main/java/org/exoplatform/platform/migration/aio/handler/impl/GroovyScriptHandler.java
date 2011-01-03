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
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
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
import org.exoplatform.services.cms.impl.ResourceConfig;
import org.exoplatform.services.cms.impl.ResourceConfig.Resource;
import org.exoplatform.services.cms.scripts.ScriptService;
import org.exoplatform.services.cms.scripts.impl.ScriptPlugin;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;

public class GroovyScriptHandler extends ComponentHandler {

  private static final String AUTO_CREATE_IN_NEW_REPOSITORY = "autoCreateInNewRepository";

  private static final String SCRIPTS_LOCATION = "scripts/";

  private Log log = ExoLogger.getLogger(this.getClass());

  StringBuffer nodeTypePathTmp_ = new StringBuffer();

  public GroovyScriptHandler() {
    super.setTargetComponentName(ScriptService.class.getName());
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

      ComponentPlugin templatesComponentPlugin = new ComponentPlugin();
      templatesComponentPlugin.setName("manage.script.plugin");
      templatesComponentPlugin.setSetMethod("addScriptPlugin");
      templatesComponentPlugin.setType(ScriptPlugin.class.getName());

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
      valueParam.setName("predefinedScriptsLocation");
      valueParam.setValue(SCRIPTS_LOCATION);
      templatesPluginInitParams.addParam(valueParam);

      RepositoryService repositoryService = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class));
      ManageableRepository repository = repositoryService.getDefaultRepository();
      String defaumtRepositoryName = repository.getConfiguration().getName();

      valueParam = new ValueParam();
      valueParam.setName("repository");
      valueParam.setValue(defaumtRepositoryName);
      templatesPluginInitParams.addParam(valueParam);
      componentPluginsList.add(templatesComponentPlugin);

      ResourceConfig resourceConfig = new ResourceConfig();
      List<Resource> resources = new ArrayList<Resource>();
      resourceConfig.setRessources(resources);

      // Add nodeType templates definition into the InitParams of the Component
      ObjectParameter objectParam = new ObjectParameter();
      objectParam.setName("predefined.scripts");
      objectParam.setObject(resourceConfig);
      templatesPluginInitParams.addParam(objectParam);

      ScriptService scriptService = ((ScriptService) container.getComponentInstanceOfType(ScriptService.class));
      SessionProvider systemSessionProvider = SessionProvider.createSystemProvider();

      Node cbScriptHome = scriptService.getCBScriptHome(defaumtRepositoryName, systemSessionProvider);
      if (cbScriptHome.hasNodes()) {
        generateScriptsConfiguration(zos, resources, cbScriptHome.getNodes());
      }
      List<Node> ecmActionScripts = scriptService.getECMActionScripts(defaumtRepositoryName, systemSessionProvider);
      generateScriptsConfiguration(zos, resources, ecmActionScripts.iterator());

      List<Node> ecmInterceptorScripts = scriptService.getECMInterceptorScripts(defaumtRepositoryName, systemSessionProvider);
      generateScriptsConfiguration(zos, resources, ecmInterceptorScripts.iterator());

      List<Node> ecmWidgetScripts = scriptService.getECMWidgetScripts(defaumtRepositoryName, systemSessionProvider);
      generateScriptsConfiguration(zos, resources, ecmWidgetScripts.iterator());

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

  private void generateScriptsConfiguration(ZipOutputStream zos, List<Resource> resources, Iterator<?> scriptsNodeIterator) throws RepositoryException, ValueFormatException, PathNotFoundException, IOException {
    while (scriptsNodeIterator.hasNext()) {
      Node templateNode = (Node) scriptsNodeIterator.next();
      Resource resource = new Resource();
      String resourcePath = templateNode.getPath().replace("/exo:ecm/scripts/", "");
      resource.setName(resourcePath);
      resources.add(resource);

      String scriptData = templateNode.getProperty("jcr:data").getString();
      zos.putNextEntry(new ZipEntry(SCRIPTS_LOCATION + resourcePath));
      zos.write(scriptData.getBytes());
      zos.closeEntry();
    }
  }

  @SuppressWarnings("unchecked")
  private List<ComponentPlugin> cleanComponentPlugins(Component component) {
    List<ComponentPlugin> componentPluginsList = component.getComponentPlugins();
    int i = 0;
    while (i < componentPluginsList.size()) {
      ComponentPlugin componentPlugin = componentPluginsList.get(i);
      if (componentPlugin.getType().equals(ScriptPlugin.class.getName())) {
        componentPluginsList.remove(i);
      } else {
        i++;
      }
    }
    return componentPluginsList;
  }

}
