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
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.services.cms.queries.QueryService;
import org.exoplatform.services.cms.queries.impl.QueryData;
import org.exoplatform.services.cms.queries.impl.QueryPlugin;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;

public class QueryHandler extends ComponentHandler {

  private static final String AUTO_CREATE_IN_NEW_REPOSITORY = "autoCreateInNewRepository".intern();

  private static final String STATEMENT = "jcr:statement".intern();

  private static final String LANGUAGE = "jcr:language".intern();

  private static final String PERMISSIONS = "exo:accessPermissions".intern();

  private static final String CACHED_RESULT = "exo:cachedResult".intern();

  private Log log = ExoLogger.getLogger(this.getClass());

  public QueryHandler() {
    super.setTargetComponentName(QueryService.class.getName());
  }

  @Override
  public Entry invoke(Component component, ExoContainer container) {
    Session dmsWorkspaceSession = null;
    try {
      if (log.isDebugEnabled()) {
        log.debug("Handler invoked for component: " + component.getKey());
      }
      List<ComponentPlugin> componentPluginsList = cleanComponentPlugins(component, QueryPlugin.class);

      ComponentPlugin templatesComponentPlugin = new ComponentPlugin();
      templatesComponentPlugin.setName("query.plugin");
      templatesComponentPlugin.setSetMethod("setQueryPlugin");
      templatesComponentPlugin.setType(QueryPlugin.class.getName());

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

      RepositoryService repositoryService = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class));
      ManageableRepository repository = repositoryService.getDefaultRepository();
      String defaumtRepositoryName = repository.getConfiguration().getName();

      ValueParam valueParam = new ValueParam();
      valueParam.setName("repository");
      valueParam.setValue(defaumtRepositoryName);
      templatesPluginInitParams.addParam(valueParam);
      componentPluginsList.add(templatesComponentPlugin);

      QueryService queryService = ((QueryService) container.getComponentInstanceOfType(QueryService.class));
      SessionProvider systemSessionProvider = SessionProvider.createSystemProvider();

      List<Node> queryNodes = queryService.getSharedQueries(defaumtRepositoryName, systemSessionProvider);
      for (Node node : queryNodes) {
        QueryData queryData = new QueryData();
        queryData.setCacheResult(node.getProperty(CACHED_RESULT).getBoolean());
        queryData.setStatement(node.getProperty(STATEMENT).getString());
        queryData.setLanguage(node.getProperty(LANGUAGE).getString());
        Value[] vls = node.getProperty(PERMISSIONS).getValues();
        List<String> permissions = new ArrayList<String>();
        for (Value permissionValue : vls) {
          permissions.add(permissionValue.getString());
        }
        queryData.setPermissions(permissions);
        queryData.setName(node.getName());

        // Add nodeType templates definition into the InitParams of the Component
        ObjectParameter objectParam = new ObjectParameter();
        objectParam.setName(queryData.getName());
        objectParam.setObject(queryData);
        templatesPluginInitParams.addParam(objectParam);
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

}
