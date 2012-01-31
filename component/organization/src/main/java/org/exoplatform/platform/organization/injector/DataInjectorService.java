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
package org.exoplatform.platform.organization.injector;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.ExternalComponentPlugins;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationConfig;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.idm.PicketLinkIDMOrganizationServiceImpl;
import org.exoplatform.services.organization.idm.PicketLinkIDMService;
import org.exoplatform.services.organization.impl.UserImpl;
import org.exoplatform.services.organization.impl.UserProfileImpl;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.picketlink.idm.impl.api.session.IdentitySessionImpl;
import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObject;
import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObjectCredential;
import org.picketlink.idm.impl.model.hibernate.HibernateIdentityObjectType;
import org.picketlink.idm.impl.repository.RepositoryIdentityStoreSessionImpl;
import org.picketlink.idm.spi.store.IdentityStoreInvocationContext;
import org.picocontainer.Startable;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class DataInjectorService implements Startable {
  private static final String CONFIGURATION_XML_SUFFIX = "-configuration.xml";

  private static final Log logger_ = ExoLogger.getLogger(DataInjectorService.class);

  private List<UserImpl> usersData = new ArrayList<UserImpl>();
  private List<UserProfileImpl> userProfilesData = new ArrayList<UserProfileImpl>();
  private OrganizationService organizationService;
  private PicketLinkIDMService picketLinkIDMService;
  private ConfigurationManager configurationManager;
  private List<DataPlugin> dataPlugins = new ArrayList<DataPlugin>();

  public DataInjectorService(OrganizationService organizationService, ConfigurationManager configurationManager) {
    this.organizationService = organizationService;
    this.configurationManager = configurationManager;
    this.picketLinkIDMService = (PicketLinkIDMService) PortalContainer.getInstance().getComponentInstanceOfType(
        PicketLinkIDMService.class);
  }

  @Override
  public void start() {
    try {
      doImport(false);
    } catch (Exception exception) {
      logger_.error("Cannot inject Organization Model Data", exception);
    }
  }

  @SuppressWarnings({ "unchecked", "deprecation" })
  public void doImport(boolean isArchive) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      for (DataPlugin dataPlugin : dataPlugins) {
        dataPlugin.init();
      }
      if (!isArchive) {
        for (DataPlugin dataPlugin : dataPlugins) {
          List<OrganizationConfig.User> users = dataPlugin.getConfig().getUser();
          if (users == null || users.isEmpty()) {
            continue;
          }
          for (OrganizationConfig.User injectedUser : users) {
            String xmlContent = getXMLContent(SerializationUtils.USERS_FOLDER_NAME + injectedUser.getUserName()
                + SerializationUtils.USER_FILE_SUFFIX);
            if (xmlContent != null) {
              XStream xstreamUser_ = new XStream(new XppDriver());
              xstreamUser_.alias("user", UserImpl.class);
              UserImpl userToImport = (UserImpl) xstreamUser_.fromXML(new String(xmlContent));
              User user = organizationService.getUserHandler().findUserByName(userToImport.getUserName());
              if (userToImport.getLastLoginTime() != null) {
                user.setLastLoginTime(userToImport.getLastLoginTime());
              }
              if (userToImport.getCreatedDate() != null) {
                user.setCreatedDate(userToImport.getCreatedDate());
              }
              organizationService.getUserHandler().saveUser(user, false);
            }

            xmlContent = getXMLContent(SerializationUtils.PROFILES_FOLDER_NAME + injectedUser.getUserName()
                + SerializationUtils.PROFILE_FILE_SUFFIX);
            if (xmlContent != null) {
              XStream xstreamProfile_ = new XStream(new XppDriver());
              xstreamProfile_.alias("user-profile", UserProfileImpl.class);
              UserProfileImpl userProfile = (UserProfileImpl) xstreamProfile_.fromXML(xmlContent);
              organizationService.getUserProfileHandler().saveUserProfile(userProfile, false);
            }
          }
        }
      } else {
        for (UserImpl importedUser : usersData) {
          User user = organizationService.getUserHandler().findUserByName(importedUser.getUserName());
          if (user != null) {
            if (importedUser.getLastLoginTime() != null) {
              user.setLastLoginTime(importedUser.getLastLoginTime());
            }
            if (importedUser.getCreatedDate() != null) {
              user.setCreatedDate(importedUser.getCreatedDate());
            }
            organizationService.getUserHandler().saveUser(user, false);
          } else {
            logger_.warn("user = " + importedUser.getUserName() + " doesn't exist");
          }
        }

        for (UserProfileImpl userProfile : userProfilesData) {
          User user = organizationService.getUserHandler().findUserByName(userProfile.getUserName());
          if (user != null) {
            organizationService.getUserProfileHandler().saveUserProfile(userProfile, true);
          } else {
            logger_.warn("userProfile = " + userProfile.getUserName() + " doesn't exist");
          }
        }
      }
    } finally {
      RequestLifeCycle.end();
    }
  }

  @Override
  public void stop() {}

  public void addDataPlugin(DataPlugin dataPlugin) {
    dataPlugins.add(dataPlugin);
  }

  public void readDataPlugins(String filePath) throws Exception {
    dataPlugins.clear();

    FileInputStream fin = new FileInputStream(filePath);
    ZipInputStream zin = new ZipInputStream(fin);
    ZipEntry ze = null;
    while ((ze = zin.getNextEntry()) != null) {
      if (ze.getName().equals("configuration.xml") || ze.getName().contains(CONFIGURATION_XML_SUFFIX)) {
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        for (int c = zin.read(); c != -1; c = zin.read()) {
          fout.write(c);
        }
        zin.closeEntry();

        Configuration tmpConfiguration = SerializationUtils.fromXML(fout.toByteArray(), Configuration.class);

        Component component = tmpConfiguration.getComponent(DataInjectorService.class.getName());
        ExternalComponentPlugins externalComponentPlugins = tmpConfiguration
            .getExternalComponentPlugins(DataInjectorService.class.getName());
        if (component != null && component.getComponentPlugins() != null && !component.getComponentPlugins().isEmpty()) {
          this.addComponentPlugins(component.getComponentPlugins());
        } else if (externalComponentPlugins != null && externalComponentPlugins.getComponentPlugins() != null
            && !externalComponentPlugins.getComponentPlugins().isEmpty()) {
          this.addComponentPlugins(externalComponentPlugins.getComponentPlugins());
        }
      }
    }
    zin.close();
  }

  private void addComponentPlugins(List<ComponentPlugin> plugins) {
    if (plugins == null || plugins.isEmpty()) {
      return;
    }
    for (ComponentPlugin plugin : plugins) {
      try {
        DataPlugin dataPlugin = PortalContainer.getInstance().createComponent(DataPlugin.class, plugin.getInitParams());
        dataPlugin.setName(plugin.getName());
        dataPlugin.setDescription(plugin.getDescription());

        this.addDataPlugin(dataPlugin);
      } catch (Exception e) {
        logger_.error("Failed to instanciate component plugin " + plugin.getName() + ", type=" + plugin.getClass(), e);
      }
    }
  }

  public void writeOrganizationModelData(ZipOutputStream zos) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      Configuration configuration = new Configuration();

      Component component = new Component();
      component.setType(DataInjectorService.class.getName());
      configuration.addComponent(component);

      {// groups & membershipTypes entry
        List<OrganizationConfig.Group> groups = getAllGroups();
        List<OrganizationConfig.MembershipType> membershipTypes = getAllMembershipTypes();
        Configuration organizationServiceConfiguration = SerializationUtils
            .buildOrganizationServiceConfiguration(SerializationUtils.getOrganizationConfig(groups, membershipTypes, null));
        SerializationUtils.addEntry(zos, SerializationUtils.toXML(organizationServiceConfiguration),
            "OrganizationDataModel/Groups-MembershipTypes-configuration.xml");
        configuration.addImport("OrganizationDataModel/Groups-MembershipTypes-configuration.xml");
      }

      {// Write Users
        ListAccess<User> usersListAccess = organizationService.getUserHandler().findAllUsers();
        List<OrganizationConfig.User> orgConfigUsersInSigleFile = new ArrayList<OrganizationConfig.User>();
        int i = 0;
        while (i <= usersListAccess.getSize()) {
          int length = i + SerializationUtils.MAX_USERS_IN_FILE_PARAM_NAME <= usersListAccess.getSize() ? SerializationUtils.MAX_USERS_IN_FILE_PARAM_NAME
              : usersListAccess.getSize() - i;
          User[] users = usersListAccess.load(i, length);
          for (User user : users) {
            OrganizationConfig.User orgConfigUser = convertUserToSerializableObject(user);
            orgConfigUsersInSigleFile.add(orgConfigUser);
          }
          Configuration organizationServiceConfiguration = SerializationUtils
              .buildOrganizationServiceConfiguration(SerializationUtils.getOrganizationConfig(null, null,
                  orgConfigUsersInSigleFile));
          SerializationUtils.addEntry(zos, SerializationUtils.toXML(organizationServiceConfiguration),
              "OrganizationDataModel/Users" + i + CONFIGURATION_XML_SUFFIX);
          orgConfigUsersInSigleFile.clear();
          configuration.addImport("OrganizationDataModel/Users" + i + CONFIGURATION_XML_SUFFIX);
          i += SerializationUtils.MAX_USERS_IN_FILE_PARAM_NAME;
        }
      }
      SerializationUtils.addEntry(zos, SerializationUtils.toXML(configuration), "configuration.xml");
    } finally {
      RequestLifeCycle.end();
    }
  }

  public void writeProfiles(ZipOutputStream zos) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      ListAccess<User> usersListAccess = organizationService.getUserHandler().findAllUsers();
      XStream xstream_ = new XStream(new XppDriver());
      int i = 0;
      while (i <= usersListAccess.getSize()) {
        int length = i + SerializationUtils.MAX_USERS_IN_FILE_PARAM_NAME <= usersListAccess.getSize() ? SerializationUtils.MAX_USERS_IN_FILE_PARAM_NAME
            : usersListAccess.getSize() - i;
        User[] users = usersListAccess.load(i, length);
        for (User user : users) {
          UserProfile userProfile = organizationService.getUserProfileHandler().findUserProfileByName(user.getUserName());
          if ((userProfile != null) && (userProfile.getUserInfoMap() != null) && !userProfile.getUserInfoMap().isEmpty()) {
            xstream_.alias("user-profile", userProfile.getClass());
            String xml = xstream_.toXML(userProfile);
            zos.putNextEntry(new ZipEntry(SerializationUtils.PROFILES_FOLDER_NAME + userProfile.getUserName()
                + SerializationUtils.PROFILE_FILE_SUFFIX));
            zos.write(xml.getBytes());
            zos.closeEntry();
            if (logger_.isDebugEnabled()) {
              logger_.debug("Adding entry for userProfile: " + userProfile.getUserName());
            }
          }
        }
        i += SerializationUtils.MAX_USERS_IN_FILE_PARAM_NAME;
      }
    } finally {
      RequestLifeCycle.end();
    }
  }

  public void writeUsers(ZipOutputStream zos) throws Exception {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      if (logger_.isDebugEnabled()) {
        logger_.debug("Adding entries for each user, additional fields are missing: LastLoginTime & CreatedDate");
      }
      ListAccess<User> usersListAccess = organizationService.getUserHandler().findAllUsers();
      XStream xstream_ = new XStream(new XppDriver());
      int i = 0;
      while (i <= usersListAccess.getSize()) {
        int length = i + SerializationUtils.MAX_USERS_IN_FILE_PARAM_NAME <= usersListAccess.getSize() ? SerializationUtils.MAX_USERS_IN_FILE_PARAM_NAME
            : usersListAccess.getSize() - i;
        User[] users = usersListAccess.load(i, length);
        for (User user : users) {
          if (user != null) {
            xstream_.alias("user", user.getClass());
            String xml = xstream_.toXML(user);
            zos.putNextEntry(new ZipEntry(SerializationUtils.USERS_FOLDER_NAME + user.getUserName()
                + SerializationUtils.USER_FILE_SUFFIX));
            zos.write(xml.getBytes());
            zos.closeEntry();
            if (logger_.isDebugEnabled()) {
              logger_.debug("Adding entry for user: " + user.getUserName());
            }
          }
        }
        i += SerializationUtils.MAX_USERS_IN_FILE_PARAM_NAME;
      }
    } finally {
      RequestLifeCycle.end();
    }
  }

  private OrganizationConfig.User convertUserToSerializableObject(User user) throws Exception {
    OrganizationConfig.User orgConfigUser = new OrganizationConfig.User();
    orgConfigUser.setEmail(user.getEmail());
    orgConfigUser.setFirstName(user.getFirstName());
    orgConfigUser.setLastName(user.getLastName());
    if (organizationService instanceof PicketLinkIDMOrganizationServiceImpl) {
      orgConfigUser.setPassword(readPasswordFromPicketLink(user));
    } else {
      orgConfigUser.setPassword(user.getPassword());
    }
    orgConfigUser.setUserName(user.getUserName());
    @SuppressWarnings("unchecked")
    Collection<Membership> memberships = organizationService.getMembershipHandler().findMembershipsByUser(user.getUserName());
    String groups = "";
    for (Membership membership : memberships) {
      groups += membership.getMembershipType() + ":" + membership.getGroupId() + ",";
      groups.substring(0, groups.lastIndexOf(","));
    }
    orgConfigUser.setGroups(groups);
    return orgConfigUser;
  }

  public void readUsersData(String filePath) throws Exception {
    usersData.clear();

    XStream xstreamUser_ = new XStream(new XppDriver());
    xstreamUser_.alias("user", UserImpl.class);

    FileInputStream fin = new FileInputStream(filePath);
    ZipInputStream zin = new ZipInputStream(fin);
    ZipEntry ze = null;
    while ((ze = zin.getNextEntry()) != null) {
      if (ze.getName().contains("_user.xml")) {
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        for (int c = zin.read(); c != -1; c = zin.read()) {
          fout.write(c);
        }
        zin.closeEntry();

        UserImpl importedUser = (UserImpl) xstreamUser_.fromXML(new String(fout.toByteArray()));
        usersData.add(importedUser);
      }
    }
    zin.close();
  }

  public void readUserProfilesData(String filePath) throws Exception {
    userProfilesData.clear();

    XStream xstreamProfile_ = new XStream(new XppDriver());
    xstreamProfile_.alias("user-profile", UserProfileImpl.class);

    FileInputStream fin = new FileInputStream(filePath);
    ZipInputStream zin = new ZipInputStream(fin);
    ZipEntry ze = null;
    while ((ze = zin.getNextEntry()) != null) {
      if (ze.getName().contains("_profile.xml")) {
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        for (int c = zin.read(); c != -1; c = zin.read()) {
          fout.write(c);
        }
        zin.closeEntry();

        UserProfileImpl userProfile = (UserProfileImpl) xstreamProfile_.fromXML(new String(fout.toByteArray()));
        userProfilesData.add(userProfile);
      }
    }
    zin.close();
  }

  private List<OrganizationConfig.Group> getAllGroups() throws Exception {

    List<OrganizationConfig.Group> allGroups = new ArrayList<OrganizationConfig.Group>();
    try {
      @SuppressWarnings("unchecked")
      Collection<Group> groups = organizationService.getGroupHandler().getAllGroups();
      for (Group group : groups) {
        OrganizationConfig.Group orgConfGroup = new OrganizationConfig.Group();
        orgConfGroup.setDescription(group.getDescription());
        orgConfGroup.setLabel(group.getLabel());
        orgConfGroup.setName(group.getGroupName());
        orgConfGroup.setParentId(group.getParentId());
        allGroups.add(orgConfGroup);
      }
    } catch (Exception e) {
      logger_.error("Error when recovering of all groups ... ", e);
      return null;
    }
    return allGroups;
  }

  private List<OrganizationConfig.MembershipType> getAllMembershipTypes() throws Exception {

    List<OrganizationConfig.MembershipType> allMembershipTypes = new ArrayList<OrganizationConfig.MembershipType>();
    try {
      @SuppressWarnings("unchecked")
      Collection<MembershipType> membershipTypes = organizationService.getMembershipTypeHandler().findMembershipTypes();
      for (MembershipType membershipType : membershipTypes) {
        OrganizationConfig.MembershipType orgConfMemberShipType = new OrganizationConfig.MembershipType();
        orgConfMemberShipType.setDescription(membershipType.getDescription());
        orgConfMemberShipType.setType(membershipType.getName());
        allMembershipTypes.add(orgConfMemberShipType);
      }
    } catch (Exception e) {
      logger_.error("Error when recovering of all membershipTypes ... ", e);
      return null;
    }
    return allMembershipTypes;
  }

  private String getXMLContent(String filePath) {
    String xmlContent = null;
    try {
      InputStream xmlInputStream = configurationManager.getInputStream(filePath);
      byte[] bytes = new byte[xmlInputStream.available()];
      xmlInputStream.read(bytes);
      xmlContent = new String(bytes);
    } catch (Exception exception) {
      logger_.error("file wasn't found ", exception);
    }
    return xmlContent;
  }

  private String readPasswordFromPicketLink(User user) throws Exception {
    PicketLinkIDMOrganizationServiceImpl orgService = (PicketLinkIDMOrganizationServiceImpl) organizationService;
    if (orgService.getConfiguration().isPasswordAsAttribute()) {
      return user.getPassword();
    } else {
      IdentitySession identitySession = picketLinkIDMService.getIdentitySession();
      IdentityStoreInvocationContext identitySessionContext = ((IdentitySessionImpl) identitySession).getSessionContext()
          .resolveStoreInvocationContext();
      Session session = ((Session) ((RepositoryIdentityStoreSessionImpl) identitySessionContext.getIdentityStoreSession())
          .getIdentityStoreSession("HibernateStore").getSessionContext());

      HibernateIdentityObjectType hibernateIdentityObjectType = (HibernateIdentityObjectType) session
          .createCriteria(HibernateIdentityObjectType.class).add(Restrictions.eq("name", "USER")).uniqueResult();

      HibernateIdentityObject hibernateUserObject = (HibernateIdentityObject) session
          .createCriteria(HibernateIdentityObject.class).add(Restrictions.eq("name", user.getUserName()))
          .add(Restrictions.eq("identityType", hibernateIdentityObjectType)).uniqueResult();

      HibernateIdentityObjectCredential hibernateCredential = (HibernateIdentityObjectCredential) session
          .createCriteria(HibernateIdentityObjectCredential.class).createAlias("type", "t")
          .add(Restrictions.eq("t.name", PasswordCredential.TYPE.getName()))
          .add(Restrictions.eq("identityObject", hibernateUserObject)).setCacheable(true).uniqueResult();
      if (hibernateCredential != null) {
        return hibernateCredential.getTextValue();
      } else {
        return "";
      }
    }
  }

}
