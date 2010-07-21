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
package org.exoplatform.platform.migration.handlers.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.logging.Log;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.platform.migration.handlers.ComponentHandler;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.application.PortletPreferences.PortletPreferencesSet;
import org.exoplatform.portal.config.NewPortalConfig;
import org.exoplatform.portal.config.jcr.DataMapper;
import org.exoplatform.portal.config.model.Gadgets;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform
 * haikel.thamri@exoplatform.com 15 juil. 2010
 */
public class UserPortalConfigHandler implements ComponentHandler {
  private Log                 log                         = ExoLogger.getLogger(this.getClass());

  final private static String PORTAL_DATA                 = "MainPortalData";

  final private static String USER_DATA                   = "UserPortalData";

  final private static String GROUP_DATA                  = "SharedPortalData";

  final private static String PORTAL_CONFIG_FILE_NAME     = "portal-xml";

  final private static String NAVIGATION_CONFIG_FILE_NAME = "navigation-xml";

  final private static String GADGETS_CONFIG_FILE_NAME    = "gadgets-xml";

  final private static String EXO_REGISTRY                = "exo:registry";

  final private static String EXO_REGISTRYENTRY_NT        = "exo:registryEntry";

  final private static String EXO_DATA_TYPE               = "exo:dataType";

  private RegistryService     regService_;

  private PortalContainer     portalContainer;

  private OrganizationService organizationService;

  private DataMapper          mapper_                     = new DataMapper();

  public void invoke(Component component, String rootConfDir) {
    portalContainer = PortalContainer.getInstance();
    organizationService = (OrganizationService) portalContainer.getComponentInstanceOfType(OrganizationService.class);
    regService_ = (RegistryService) portalContainer.getComponentInstanceOfType(RegistryService.class);
    preMarshallComponent(component, rootConfDir);

    Configuration configuration = new Configuration();
    configuration.addComponent(component);
    marshall(configuration, rootConfDir + File.separator + "portal" + File.separator
        + component.getKey()+ ".xml");
  }

  private void preMarshallComponent(Component component, String rootConfDir) {
    try {
      ArrayList<String> portalNames = getPortalNames();
      String portalConfDir = rootConfDir + File.separator + "portal";
      String navigationConfDir = portalConfDir + File.separator + "portal-navigation";
      for (String portalName : portalNames) {
        File confSite = new File(navigationConfDir + File.separator + "portal" + File.separator
            + portalName);
        confSite.mkdirs();
        PortalConfig portalConfig = getPortalConfig(portalName);
        marshall(portalConfig, confSite.getPath() + File.separator + "portal.xml");

        PageSet pageSet = getPages(PortalConfig.PORTAL_TYPE, portalName);
        marshall(pageSet, confSite.getPath() + File.separator + "pages.xml");

        PageNavigation navigation = getPageNavigation(PortalConfig.PORTAL_TYPE, portalName);
        marshall(navigation, confSite.getPath() + File.separator + "navigation.xml");

        PortletPreferencesSet portletPreferencesSet = getPortletPreferencesSet(PortalConfig.PORTAL_TYPE,
                                                                               portalName);
        marshall(portletPreferencesSet, confSite.getPath() + File.separator
            + "portlet-preferences.xml");
      }

      ArrayList<String> groups = getAllGroups();
      for (String group : groups) {

        File confGroup = new File(navigationConfDir + File.separator + "group" + File.separator
            + group.replace("/", File.separator));
        confGroup.mkdirs();

        PageSet pageSet = getPages(PortalConfig.GROUP_TYPE, group);
        if (pageSet != null) {
          marshall(pageSet, confGroup.getPath() + File.separator + "pages.xml");
        }

        PageNavigation navigation = getPageNavigation(PortalConfig.GROUP_TYPE, group);
        if (navigation != null) {
          marshall(navigation, confGroup.getPath() + File.separator + "navigation.xml");
        }
        PortletPreferencesSet portletPreferencesSet = getPortletPreferencesSet(PortalConfig.GROUP_TYPE,
                                                                               group);
        if (portletPreferencesSet != null) {
          marshall(portletPreferencesSet, confGroup.getPath() + File.separator
              + "portlet-preferences.xml");
        }
      }
      ArrayList<String> allUsers = getAllUsers();
      for (String user : allUsers) {
        File confUser = new File(navigationConfDir + File.separator + "user" + File.separator
            + user);
        confUser.mkdirs();
        PageNavigation navigation = getPageNavigation(PortalConfig.USER_TYPE, user);
        if (navigation != null) {
          marshall(navigation, confUser.getPath() + File.separator + "navigation.xml");
        }
        PageSet pageSet = getPages(PortalConfig.USER_TYPE, user);
        if (pageSet != null) {
          marshall(pageSet, confUser.getPath() + File.separator + "pages.xml");
        }

        PortletPreferencesSet portletPreferencesSet = getPortletPreferencesSet(PortalConfig.USER_TYPE,
                                                                               user);
        if (portletPreferencesSet != null) {
          marshall(portletPreferencesSet, confUser.getPath() + File.separator
              + "portlet-preferences.xml");
        }
      }

      // Modify the templatelocation field in the UserPortalConfigHandler
      List<ComponentPlugin> componentPlugins = component.getComponentPlugins();
      for (ComponentPlugin componentPlugin : componentPlugins) {
        if (componentPlugin.getName().equals("new.portal.config.user.listener")) {
          ObjectParameter objectParameter = componentPlugin.getInitParams()
                                                           .getObjectParam("portal.configuration");
          NewPortalConfig newPortalConfig = (NewPortalConfig) objectParameter.getObject();
          newPortalConfig.setTemplateLocation("portal-navigation");
          objectParameter = componentPlugin.getInitParams().getObjectParam("group.configuration");
          newPortalConfig = (NewPortalConfig) objectParameter.getObject();
          newPortalConfig.setTemplateLocation("portal-navigation");
          objectParameter = componentPlugin.getInitParams().getObjectParam("user.configuration");
          newPortalConfig = (NewPortalConfig) objectParameter.getObject();
          newPortalConfig.setTemplateLocation("portal-navigation");
          break;
        }
      }

    } catch (Exception ie) {
      log.error("problem in the preMarshall Process", ie);
    }
  }

  private void marshall(Object obj, String xmlPath) {
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(obj.getClass());
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      mctx.marshalDocument(obj, "UTF-8", null, new FileOutputStream(xmlPath));
    } catch (Exception ie) {
      log.error("Cannot convert the object to xml", ie);
    }
  }

  private PageSet getPages(String ownerType, String ownerId) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    PageSet pages = null;
    try {
      String registryNodePath = regService_.getRegistry(sessionProvider).getNode().getPath();
      String queryString = "select * from " + EXO_REGISTRYENTRY_NT + " where jcr:path like '"
          + registryNodePath + "/" + getApplicationRegistryPath(ownerType, ownerId) + "/%'"
          + " and " + EXO_DATA_TYPE + " like 'Page'";
      Session session = regService_.getRegistry(sessionProvider).getNode().getSession();

      QueryManager queryManager = session.getWorkspace().getQueryManager();
      javax.jcr.query.Query query = queryManager.createQuery(queryString, "sql");
      QueryResult result = query.execute();
      NodeIterator itr = result.getNodes();

      while (itr.hasNext()) {

        Node node = itr.nextNode();

        String entryPath = node.getPath().substring(registryNodePath.length() + 1);
        RegistryEntry pageEntry = regService_.getEntry(sessionProvider, entryPath);
        Page page = mapper_.toPageConfig(pageEntry.getDocument());
        if (pages == null)
          pages = new PageSet();
        pages.getPages().add(page);
      }
    } catch (Exception ie) {
      log.error("Cannot recovers pages from jcr", ie);
      return null;
    } finally {
      sessionProvider.close();
    }
    sessionProvider.close();
    return pages;
  }

  private PortletPreferencesSet getPortletPreferencesSet(String ownerType, String ownerId) throws Exception {

    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    PortletPreferencesSet portletPreferencesSet = null;
    try {
      ArrayList<PortletPreferences> portlets = new ArrayList<PortletPreferences>();
      String registryNodePath = regService_.getRegistry(sessionProvider).getNode().getPath();
      String queryString = "select * from " + EXO_REGISTRYENTRY_NT + " where jcr:path like '"
          + registryNodePath + "/" + getApplicationRegistryPath(ownerType, ownerId) + "/%'"
          + " and " + EXO_DATA_TYPE + " like 'PortletPreferences'";
      Session session = regService_.getRegistry(sessionProvider).getNode().getSession();

      QueryManager queryManager = session.getWorkspace().getQueryManager();
      javax.jcr.query.Query query = queryManager.createQuery(queryString, "sql");
      QueryResult result = query.execute();
      NodeIterator itr = result.getNodes();

      while (itr.hasNext()) {

        Node node = itr.nextNode();

        String entryPath = node.getPath().substring(registryNodePath.length() + 1);
        RegistryEntry pageEntry = regService_.getEntry(sessionProvider, entryPath);
        PortletPreferences portletPreferences = mapper_.toPortletPreferences(pageEntry.getDocument());
        portlets.add(portletPreferences);
        if (portletPreferencesSet == null)
          portletPreferencesSet = new PortletPreferencesSet();
        portletPreferencesSet.setPortlets(portlets);
      }
    } catch (Exception ie) {
      log.error("Cannot recovers preferences from jcr", ie);
      return null;
    } finally {
      sessionProvider.close();
    }
    return portletPreferencesSet;
  }

  // public PortletPreferencesSet getPreferences()
  private PortalConfig getPortalConfig(String portalName) throws Exception {
    String portalPath = getApplicationRegistryPath(PortalConfig.PORTAL_TYPE, portalName) + "/"
        + PORTAL_CONFIG_FILE_NAME;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    RegistryEntry portalEntry;
    try {
      portalEntry = regService_.getEntry(sessionProvider, portalPath);
    } catch (PathNotFoundException ie) {
      return null;
    } finally {
      sessionProvider.close();
    }
    PortalConfig config = mapper_.toPortalConfig(portalEntry.getDocument());

    return config;
  }

  private Gadgets getGadgets(String id) throws Exception {
    String[] fragments = id.split("::");
    if (fragments.length < 2) {
      throw new Exception("Invalid Gadgets Id: " + "[" + id + "]");
    }
    String gadgetsPath = getApplicationRegistryPath(fragments[0], fragments[1]) + "/"
        + GADGETS_CONFIG_FILE_NAME;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    RegistryEntry gadgetsEntry;
    try {
      gadgetsEntry = regService_.getEntry(sessionProvider, gadgetsPath);
    } catch (PathNotFoundException ie) {
      return null;
    } finally {
      sessionProvider.close();
    }
    Gadgets gadgets = mapper_.toGadgets(gadgetsEntry.getDocument());
    return gadgets;
  }

  private String getApplicationRegistryPath(String ownerType, String ownerId) {
    String path = "";
    if (PortalConfig.PORTAL_TYPE.equals(ownerType)) {
      path = RegistryService.EXO_APPLICATIONS + "/" + PORTAL_DATA + "/" + ownerId;
    } else if (PortalConfig.USER_TYPE.equals(ownerType)) {
      path = RegistryService.EXO_USERS + "/" + ownerId + "/" + USER_DATA;
    } else if (PortalConfig.GROUP_TYPE.equals(ownerType)) {
      if (ownerId.charAt(0) != '/')
        ownerId = "/" + ownerId;
      path = RegistryService.EXO_GROUPS + ownerId + "/" + GROUP_DATA;
    }

    return path;
  }

  private PageNavigation getPageNavigation(String ownerType, String id) throws Exception {
    String navigationPath = getApplicationRegistryPath(ownerType, id) + "/"
        + NAVIGATION_CONFIG_FILE_NAME;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    RegistryEntry navigationEntry;
    try {
      navigationEntry = regService_.getEntry(sessionProvider, navigationPath);
    } catch (PathNotFoundException ie) {
      return null;
    } finally {
      sessionProvider.close();
    }
    PageNavigation navigation = mapper_.toPageNavigation(navigationEntry.getDocument());
    return navigation;
  }

  private ArrayList<String> getPortalNames() throws RepositoryException {
    ArrayList<String> portalNames = new ArrayList<String>();
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    Session session = regService_.getRegistry(sessionProvider).getNode().getSession();
    Node node = (Node) session.getItem("/" + EXO_REGISTRY + "/" + RegistryService.EXO_APPLICATIONS
        + "/" + PORTAL_DATA);
    NodeIterator nodeIterator = node.getNodes();
    while (nodeIterator.hasNext()) {

      node = nodeIterator.nextNode();
      portalNames.add(node.getName());
    }
    return portalNames;
  }

  private ArrayList<String> getAllGroups() {
    ArrayList<String> allGroups = new ArrayList<String>();
    try {
      PortalContainer portalContainer = PortalContainer.getInstance();
      OrganizationService organizationService = (OrganizationService) portalContainer.getComponentInstanceOfType(OrganizationService.class);

      Collection<Group> groups = organizationService.getGroupHandler().getAllGroups();
      for (Group group : groups) {
        allGroups.add(group.getId());
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      log.error("Error when recovering of all groups", e);
      return null;
    }
    return allGroups;
  }

  private ArrayList<String> getAllUsers() {
    ArrayList<String> allUsers = new ArrayList<String>();
    try {
      Query query = new Query();
      query.setUserName("*");
      PageList users = organizationService.getUserHandler().findUsers(query);
      for (Object o : users.getAll()) {
        String userName = ((User) o).getUserName();
        allUsers.add(userName);
      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      log.error("Error when recovering of all users", e);
      return null;
    }
    return allUsers;
  }

}
