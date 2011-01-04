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

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.services.cms.folksonomy.FolksonomyService;
import org.exoplatform.services.cms.folksonomy.impl.TagStyleConfig;
import org.exoplatform.services.cms.folksonomy.impl.TagStylePlugin;
import org.exoplatform.services.cms.folksonomy.impl.TagStyleConfig.HtmlTagStyle;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.log.ExoLogger;

public class FolksonomyHandler extends ComponentHandler {

  private static final String AUTO_CREATE_IN_NEW_REPOSITORY = "autoCreateInNewRepository".intern();

  private static final String RANGE_PROP = "exo:styleRange";

  private static final String HTML_STYLE_PROP = "exo:htmlStyle";

  private Log log = ExoLogger.getLogger(this.getClass());

  public FolksonomyHandler() {
    super.setTargetComponentName(FolksonomyService.class.getName());
  }

  @Override
  public Entry invoke(Component component, ExoContainer container) {
    Session dmsWorkspaceSession = null;
    try {
      if (log.isDebugEnabled()) {
        log.debug("Handler invoked for component: " + component.getKey());
      }
      List<ComponentPlugin> componentPluginsList = cleanComponentPlugins(component, TagStylePlugin.class);

      ComponentPlugin templatesComponentPlugin = new ComponentPlugin();
      templatesComponentPlugin.setName("query.plugin");
      templatesComponentPlugin.setSetMethod("setQueryPlugin");
      templatesComponentPlugin.setType(TagStylePlugin.class.getName());

      InitParams templatesPluginInitParams = new InitParams();
      templatesComponentPlugin.setInitParams(templatesPluginInitParams);
      componentPluginsList.add(templatesComponentPlugin);

      TagStyleConfig tagStyleConfig = new TagStyleConfig();
      // Add nodeType templates definition into the InitParams of the Component
      ObjectParameter objectParam = new ObjectParameter();
      objectParam.setName("htmStyleForTag.configuration");
      objectParam.setObject(tagStyleConfig);
      templatesPluginInitParams.addParam(objectParam);

      if (component.getInitParams() != null) {
        boolean autoCreateInNewRepository = false;
        ValueParam originalParam = component.getInitParams().getValueParam(AUTO_CREATE_IN_NEW_REPOSITORY);
        if (originalParam != null) {
          autoCreateInNewRepository = Boolean.parseBoolean(originalParam.getValue());
        }
        tagStyleConfig.setAutoCreatedInNewRepository(autoCreateInNewRepository);
      }

      RepositoryService repositoryService = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class));
      ManageableRepository repository = repositoryService.getDefaultRepository();
      String defaumtRepositoryName = repository.getConfiguration().getName();

      tagStyleConfig.setRepository(defaumtRepositoryName);
      List<HtmlTagStyle> htmlTagStyleList = new ArrayList<HtmlTagStyle>();
      tagStyleConfig.setTagStyleList(htmlTagStyleList);

      FolksonomyService folksonomyService = ((FolksonomyService) container.getComponentInstanceOfType(FolksonomyService.class));

      List<Node> tagStyleNodes = folksonomyService.getAllTagStyle(defaumtRepositoryName);
      for (Node node : tagStyleNodes) {
        HtmlTagStyle htmlTagStyle = new HtmlTagStyle();
        htmlTagStyle.setName(node.getName());
        htmlTagStyle.setHtmlStyle(getHtmlStyleOfStyle(node));
        htmlTagStyle.setName(getRangeOfStyle(node));

        htmlTagStyleList.add(htmlTagStyle);
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

  public String getRangeOfStyle(Node tagStyle) throws Exception {
    return tagStyle.getProperty(RANGE_PROP).getValue().getString();
  }

  public String getHtmlStyleOfStyle(Node tagStyle) throws Exception {
    return tagStyle.getProperty(HTML_STYLE_PROP).getValue().getString();
  }

}
