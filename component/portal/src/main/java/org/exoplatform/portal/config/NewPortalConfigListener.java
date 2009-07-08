/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.portal.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.application.PortletPreferences.PortletPreferencesSet;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Created by The eXo Platform SARL Author : Tuan Nguyen
 * tuan08@users.sourceforge.net May 22, 2006
 */

public class NewPortalConfigListener extends BaseComponentPlugin {

  private ConfigurationManager cmanager_;

  private DataStorage          pdcService_;

  private List<?>              configs;

  private PageTemplateConfig   pageTemplateConfig_;

  private String               defaultPortal;

  public NewPortalConfigListener(DataStorage pdcService,
                                 ConfigurationManager cmanager,
                                 InitParams params) throws Exception {
    cmanager_ = cmanager;
    pdcService_ = pdcService;

    ObjectParameter objectParam = params.getObjectParam("page.templates");
    if (objectParam != null)
      pageTemplateConfig_ = (PageTemplateConfig) objectParam.getObject();

    defaultPortal = "classic";
    ValueParam valueParam = params.getValueParam("default.portal");
    if (valueParam != null)
      defaultPortal = valueParam.getValue();
    if (defaultPortal == null || defaultPortal.trim().length() == 0)
      defaultPortal = "classic";
    configs = params.getObjectParamValues(NewPortalConfig.class);
  }

  public void run() throws Exception {
    if (isInitedDB(defaultPortal))
      return;
    for (Object ele : configs) {
      NewPortalConfig portalConfig = (NewPortalConfig) ele;
      if (portalConfig.getOwnerType().equals("user")) {
        initUserTypeDB(portalConfig);
      } else if (portalConfig.getOwnerType().equals(PortalConfig.GROUP_TYPE)) {
        initGroupTypeDB(portalConfig);
      } else {
        initPortalTypeDB(portalConfig);
      }
      portalConfig.getPredefinedOwner().clear();
    }
  }

  NewPortalConfig getPortalConfig(String ownerType) {
    for (Object object : configs) {
      NewPortalConfig portalConfig = (NewPortalConfig) object;
      if (portalConfig.getOwnerType().equals(ownerType))
        return portalConfig;
    }
    return null;
  }

  private boolean isInitedDB(String user) throws Exception {
    PortalConfig pconfig = pdcService_.getPortalConfig(user);
    return pconfig != null;
  }

  public void initUserTypeDB(NewPortalConfig config) throws Exception {
    HashSet<String> owners = config.getPredefinedOwner();
    Iterator<String> iter = owners.iterator();
    while (iter.hasNext()) {
      String owner = iter.next();
      createPage(config, owner);
      createPageNavigation(config, owner);
    }
  }

  public void initGroupTypeDB(NewPortalConfig config) throws Exception {
    HashSet<String> owners = config.getPredefinedOwner();
    Iterator<String> iter = owners.iterator();
    while (iter.hasNext()) {
      String owner = iter.next();
      createPage(config, owner);
      createPageNavigation(config, owner);
      createPortletPreferences(config, owner);
    }
  }

  public void initPortalTypeDB(NewPortalConfig config) throws Exception {
    HashSet<String> owners = config.getPredefinedOwner();
    Iterator<String> iter = owners.iterator();
    while (iter.hasNext()) {
      String owner = iter.next();
      createPortalConfig(config, owner);
      createPage(config, owner);
      createPageNavigation(config, owner);
      createPortletPreferences(config, owner);
    }
  }

  private void createPortalConfig(NewPortalConfig config, String owner) throws Exception {
    String xml = null;
    if (config.getTemplateOwner() == null || config.getTemplateOwner().trim().length() < 1) {
      xml = getDefaultConfig(config, owner, "portal");
    } else {
      xml = getTemplateConfig(config, owner, "portal");
    }
    PortalConfig pconfig = fromXML(xml, PortalConfig.class);
    pdcService_.create(pconfig);
  }

  private void createPage(NewPortalConfig config, String owner) throws Exception {
    String xml = null;
    if (config.getTemplateOwner() == null || config.getTemplateOwner().trim().length() < 1) {
      xml = getDefaultConfig(config, owner, "pages");
    } else {
      xml = getTemplateConfig(config, owner, "pages");
    }
    PageSet pageSet = fromXML(xml, PageSet.class);
    ArrayList<Page> list = pageSet.getPages();
    for (Page page : list) {
      pdcService_.create(page);
    }
  }

  private void createPageNavigation(NewPortalConfig config, String owner) throws Exception {
    String xml = null;
    if (config.getTemplateOwner() == null || config.getTemplateOwner().trim().length() < 1) {
      xml = getDefaultConfig(config, owner, "navigation");
    } else {
      xml = getTemplateConfig(config, owner, "navigation");
    }
    PageNavigation navigation = fromXML(xml, PageNavigation.class);
    if (pdcService_.getPageNavigation(navigation.getOwner()) == null) {
      pdcService_.create(navigation);
    } else {
      pdcService_.save(navigation);
    }
  }

  private void createPortletPreferences(NewPortalConfig config, String owner) throws Exception {
    String xml = null;
    if (config.getTemplateOwner() == null || config.getTemplateOwner().trim().length() < 1) {
      xml = getDefaultConfig(config, owner, "portlet-preferences");
    } else {
      xml = getTemplateConfig(config, owner, "portlet-preferences");
    }
    PortletPreferencesSet portletSet = fromXML(xml, PortletPreferencesSet.class);
    ArrayList<PortletPreferences> list = portletSet.getPortlets();
    for (PortletPreferences portlet : list) {
      pdcService_.save(portlet);
    }
  }

  private String getDefaultConfig(NewPortalConfig portalConfig, String owner, String dataType) throws Exception {
    String ownerType = portalConfig.getOwnerType();
    String path = "/" + ownerType + "/" + owner + "/" + dataType + ".xml";
    String location = portalConfig.getTemplateLocation();
    return IOUtil.getStreamContentAsString(cmanager_.getInputStream(location + path));
  }

  private String getTemplateConfig(NewPortalConfig portalConfig, String owner, String dataType) throws Exception {
    String ownerType = portalConfig.getOwnerType();
    String templateLoc = portalConfig.getTemplateLocation();
    String path = "/" + ownerType + "/template/" + portalConfig.getTemplateOwner() + "/" + dataType
        + ".xml";
    InputStream is = cmanager_.getInputStream(templateLoc + path);
    String template = IOUtil.getStreamContentAsString(is);
    return StringUtils.replace(template, "@owner@", owner);
  }

  public Page createPageFromTemplate(String temp) throws Exception {
    return fromXML(getTemplateConfig(temp, "page"), Page.class);
  }

  public PortletPreferencesSet createPortletPreferencesFromTemplate(String temp) throws Exception {
    return fromXML(getTemplateConfig(temp, "portlet-preferences"), PortletPreferencesSet.class);
  }

  private String getTemplateConfig(String name, String dataType) throws Exception {
    String path = pageTemplateConfig_.getLocation() + "/" + name + "/" + dataType + ".xml";
    InputStream is = cmanager_.getInputStream(path);
    return IOUtil.getStreamContentAsString(is);
  }

  private <T> T fromXML(String xml, Class<T> clazz) throws Exception {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
    IBindingFactory bfact = BindingDirectory.getFactory(clazz);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    return clazz.cast(uctx.unmarshalDocument(is, "UTF-8"));
  }

  String getDefaultPortal() {
    return defaultPortal;
  }

}
