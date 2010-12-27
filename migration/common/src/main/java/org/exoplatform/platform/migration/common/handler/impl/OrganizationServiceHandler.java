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
package org.exoplatform.platform.migration.common.handler.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.ExternalComponentPlugins;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.platform.migration.common.component.Logger;
import org.exoplatform.platform.migration.common.constants.Constants;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationConfig;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class OrganizationServiceHandler extends ComponentHandler {

  private OrganizationService organizationService;

  private int maxUsersPerFile = 0;
  
  private Logger logger_ = null;

  public OrganizationServiceHandler(InitParams initParams) {
    ValueParam valueParam = initParams.getValueParam(Constants.MAX_USERS_IN_FILE_PARAM_NAME);
    if ((valueParam == null) || (valueParam.getValue().length() == 0)) {
      throw new IllegalStateException(Constants.MAX_USERS_IN_FILE_PARAM_NAME + " init param is missing");
    }
    maxUsersPerFile = Integer.parseInt(valueParam.getValue());
    if (maxUsersPerFile == 0) {
      maxUsersPerFile = Constants.DEFAULT_MAX_USERS_IN_FILE_PARAM_NAME;
    }
    super.setTargetComponentName(OrganizationService.class.getName());
  }

  @Override
  public Entry invoke(Component component, ExoContainer container) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(out);

    organizationService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
    logger_ = (Logger) container.getComponentInstanceOfType(Logger.class);
    logger_.setLogger(this.getClass());
    if(logger_.isDebugEnabled()){
      logger_.debug("Invoking handler for component: " + organizationService.getClass().getName());
    }
    writeProfiles(zos);
    writeUsers(zos);

    Configuration configuration = new Configuration();
    writeOrganizationModelData(zos, configuration);

    configuration.addComponent(component);
    zos.putNextEntry(new ZipEntry(component.getKey() + ".xml"));
    zos.write(toXML(configuration));
    zos.closeEntry();
    zos.close();

    Entry entry = new Entry(component.getKey());
    entry.setType(EntryType.ZIP);
    entry.setContent(out.toByteArray());
    return entry;
  }

  private void writeOrganizationModelData(ZipOutputStream zos, Configuration configuration) throws Exception {
    {// groups & membershipTypes entry
      Configuration organizationServiceConfiguration = buildOrganizationServiceConfiguration(getOrganizationConfig(getAllGroups(), getAllMembershipTypes(), null));
      addEntry(zos, toXML(organizationServiceConfiguration), "OrganizationDataModel/Groups-MembershipTypes.xml");
      configuration.addImport("OrganizationDataModel/Groups-MembershipTypes.xml");
    }

    {// Write Users
      PageList pageList = organizationService.getUserHandler().findUsers(new Query());
      pageList.getPageSize();
      int entryNumber = 1;
      List<OrganizationConfig.User> orgConfigUsersInSigleFile = new ArrayList<OrganizationConfig.User>();
      int i = 1;
      while (i <= pageList.getAvailablePage()) {
        List users = pageList.getPage(i);
        if (orgConfigUsersInSigleFile.size() < maxUsersPerFile) {
          for (Object user : users) {
            OrganizationConfig.User orgConfigUser = convertUserToSerializableObject((User) user);
            orgConfigUsersInSigleFile.add(orgConfigUser);
          }
          i++;
        }
        if ((orgConfigUsersInSigleFile.size() >= maxUsersPerFile) || (i > pageList.getAvailablePage())) {
          Configuration organizationServiceConfiguration = buildOrganizationServiceConfiguration(getOrganizationConfig(null, null, orgConfigUsersInSigleFile));
          addEntry(zos, toXML(organizationServiceConfiguration), "OrganizationDataModel/Users" + entryNumber + ".xml");
          orgConfigUsersInSigleFile.clear();
          configuration.addImport("OrganizationDataModel/Users" + entryNumber + ".xml");
          entryNumber++;
        }
      }
    }
  }

  private OrganizationConfig.User convertUserToSerializableObject(User user) throws Exception {
    OrganizationConfig.User orgConfigUser = new OrganizationConfig.User();
    orgConfigUser.setEmail(user.getEmail());
    orgConfigUser.setFirstName(user.getFirstName());
    orgConfigUser.setLastName(user.getLastName());
    orgConfigUser.setPassword(user.getPassword());
    orgConfigUser.setUserName(user.getUserName());
    Collection memberships = organizationService.getMembershipHandler().findMembershipsByUser(user.getUserName());
    String groups = "";
    for (Object ob : memberships) {
      groups += ((Membership) ob).getMembershipType() + ":" + ((Membership) ob).getGroupId() + ",";
      groups.substring(0, groups.lastIndexOf(","));
    }
    orgConfigUser.setGroups(groups);
    return orgConfigUser;
  }

  private void addEntry(ZipOutputStream zos, byte[] bytes, String entryName) throws IOException {
    zos.putNextEntry(new ZipEntry(entryName));
    zos.write(bytes);
    zos.closeEntry();
    if(logger_.isDebugEnabled()){
      logger_.debug("Adding entry: " + entryName);
    }
  }

  private Configuration buildOrganizationServiceConfiguration(OrganizationConfig organizationConfig) {
    Configuration configuration = new Configuration();
    ExternalComponentPlugins externalComponentPlugins = new ExternalComponentPlugins();
    ComponentPlugin componentPlugin = new ComponentPlugin();
    InitParams initParams = new InitParams();
    ValueParam valueParam1 = new ValueParam();
    ValueParam valueParam2 = new ValueParam();
    ObjectParameter objectParam = new ObjectParameter();

    valueParam1.setName("checkDatabaseAlgorithm");
    valueParam1.setValue("entry");
    valueParam2.setName("printInformation");
    valueParam2.setValue("true");
    objectParam.setName("configuration");
    objectParam.setObject(organizationConfig);

    initParams.addParam(valueParam1);
    initParams.addParam(valueParam2);
    initParams.addParameter(objectParam);

    externalComponentPlugins.setTargetComponent("org.exoplatform.services.organization.OrganizationService");
    componentPlugin.setName("init.organizationDataModel.listener");
    componentPlugin.setSetMethod("addListenerPlugin");
    componentPlugin.setType("org.exoplatform.services.organization.OrganizationDatabaseInitializer");
    componentPlugin.setInitParams(initParams);
    ArrayList<ComponentPlugin> componentPlugins = new ArrayList<ComponentPlugin>();
    componentPlugins.add(componentPlugin);
    externalComponentPlugins.setComponentPlugins(componentPlugins);
    configuration.addExternalComponentPlugins(externalComponentPlugins);
    return configuration;
  }

  private OrganizationConfig getOrganizationConfig(List groups, List membershipTypes, List users) {
    OrganizationConfig organizationConfig = new OrganizationConfig();
    organizationConfig.setGroup(groups);
    organizationConfig.setMembershipType(membershipTypes);
    organizationConfig.setUser(users);
    return organizationConfig;
  }

  @SuppressWarnings("unchecked")
  private List getAllGroups() throws Exception {

    List allGroups = new ArrayList<OrganizationConfig.Group>();
    try {
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

  @SuppressWarnings("unchecked")
  private List getAllMembershipTypes() throws Exception {

    List allMembershipTypes = new ArrayList<OrganizationConfig.MembershipType>();
    try {
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

  private void writeProfiles(ZipOutputStream zos) throws Exception {
    PageList usersPageList = organizationService.getUserHandler().findUsers(new Query());
    int pageCount = usersPageList.getAvailablePage();
    XStream xstream_ = new XStream(new XppDriver());
    for (int i = 1; i <= pageCount; i++) {
      List<User> usersList = usersPageList.getPage(i);
      for (User user : usersList) {
        UserProfile userProfile = organizationService.getUserProfileHandler().findUserProfileByName(user.getUserName());
        if ((userProfile != null) && (userProfile.getUserInfoMap() != null) && !userProfile.getUserInfoMap().isEmpty()) {
          xstream_.alias("user-profile", userProfile.getClass());
          String xml = xstream_.toXML(userProfile);
          zos.putNextEntry(new ZipEntry(Constants.PROFILES_FOLDER_NAME + userProfile.getUserName() + Constants.PROFILE_FILE_SUFFIX));
          zos.write(xml.getBytes());
          zos.closeEntry();
          if(logger_.isDebugEnabled()){
            logger_.debug("Adding entry for userProfile: " + userProfile.getUserName());
          }
        }
      }
    }
  }

  private void writeUsers(ZipOutputStream zos) throws Exception {
    PageList usersPageList = organizationService.getUserHandler().findUsers(new Query());
    int pageCount = usersPageList.getAvailablePage();
    XStream xstream_ = new XStream(new XppDriver());
    if(logger_.isDebugEnabled()){
      logger_.debug("Adding entries for each user, additional fields are missing: LastLoginTime & CreatedDate");
    }
    for (int i = 1; i <= pageCount; i++) {
      List<User> usersList = usersPageList.getPage(i);
      for (User user : usersList) {
        if (user != null) {
          xstream_.alias("user", user.getClass());
          String xml = xstream_.toXML(user);
          zos.putNextEntry(new ZipEntry(Constants.USERS_FOLDER_NAME + user.getUserName() + Constants.USER_FILE_SUFFIX));
          zos.write(xml.getBytes());
          zos.closeEntry();
          if(logger_.isDebugEnabled()){
            logger_.debug("Adding entry for user: " + user.getUserName());
          }
        }
      }
    }
  }
}
