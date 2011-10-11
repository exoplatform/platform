package org.exoplatform.platform.organization.injector;

import java.util.List;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationConfig;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;

public class DataPlugin extends BaseComponentPlugin {
  private static final Log logger_ = ExoLogger.getLogger(DataPlugin.class);

  private OrganizationService organizationService;
  private OrganizationConfig config;

  public DataPlugin(OrganizationService organizationService, InitParams params) throws Exception {
    config = (OrganizationConfig) params.getObjectParamValues(OrganizationConfig.class).get(0);
    this.organizationService = organizationService;
  }

  public OrganizationConfig getConfig() {
    return this.config;
  }

  public void init() throws Exception {
    logger_.info("=======> Initialize the  organization data");
    createGroups();
    createMembershipTypes();
    createUsers();
    logger_.info("<=======");
  }

  private void createGroups() throws Exception {
    logger_.info("  Init Group Data injection");

    @SuppressWarnings("unchecked")
    List<OrganizationConfig.Group> groups = config.getGroup();
    if (groups == null || groups.isEmpty()) {
      return;
    }
    for (OrganizationConfig.Group data : groups) {
      String groupId = null;
      String parentId = data.getParentId();
      if (parentId == null || parentId.length() == 0)
        groupId = "/" + data.getName();
      else
        groupId = data.getParentId() + "/" + data.getName();

      if (organizationService.getGroupHandler().findGroupById(groupId) == null) {
        logger_.info("    Creating Group " + groupId);
        Group group = organizationService.getGroupHandler().createGroupInstance();
        group.setGroupName(data.getName());
        group.setDescription(data.getDescription());
        group.setLabel(data.getLabel());
        if (parentId == null || parentId.length() == 0) {
          organizationService.getGroupHandler().addChild(null, group, false);
        } else {
          Group parentGroup = organizationService.getGroupHandler().findGroupById(parentId);
          organizationService.getGroupHandler().addChild(parentGroup, group, false);
        }
      } else {
        logger_.info("    Ignoring existing Group " + groupId);
      }
    }
  }

  private void createMembershipTypes() throws Exception {
    logger_.info("  Init  MembershipType  Data ijection");

    @SuppressWarnings("unchecked")
    List<OrganizationConfig.MembershipType> types = config.getMembershipType();
    if (types == null || types.isEmpty()) {
      return;
    }
    for (OrganizationConfig.MembershipType data : types) {
      if (organizationService.getMembershipTypeHandler().findMembershipType(data.getType()) == null) {
        logger_.info("    Creating MembershipType " + data.getType());
        MembershipType type = organizationService.getMembershipTypeHandler().createMembershipTypeInstance();
        type.setName(data.getType());
        type.setDescription(data.getDescription());
        organizationService.getMembershipTypeHandler().createMembershipType(type, false);
      } else {
        logger_.info("    Ignoring existing MembershipType " + data.getType());
      }
    }
  }

  @SuppressWarnings({ "unchecked" })
  private void createUsers() throws Exception {
    logger_.info("  Init  User  Data injection");
    List<OrganizationConfig.User> users = config.getUser();
    if (users == null || users.isEmpty()) {
      return;
    }
    MembershipHandler mhandler = organizationService.getMembershipHandler();
    for (int i = 0; i < users.size(); i++) {
      OrganizationConfig.User data = (OrganizationConfig.User) users.get(i);
      User user = organizationService.getUserHandler().createUserInstance(data.getUserName());
      user.setPassword(data.getPassword());
      user.setFirstName(data.getFirstName());
      user.setLastName(data.getLastName());
      user.setEmail(data.getEmail());

      if (organizationService.getUserHandler().findUserByName(data.getUserName()) == null) {
        logger_.info("    Creating user " + data.getUserName());
        organizationService.getUserHandler().createUser(user, false);
      } else {
        logger_.info("    Ignoring existing User " + data.getUserName());
      }

      String groups = data.getGroups();
      String[] entry = groups.split(",");
      for (int j = 0; j < entry.length; j++) {
        String[] temp = entry[j].trim().split(":");
        String membership = temp[0];
        String groupId = temp[1];
        if (mhandler.findMembershipByUserGroupAndType(data.getUserName(), groupId, membership) == null) {
          Group group = organizationService.getGroupHandler().findGroupById(groupId);
          MembershipType mt = organizationService.getMembershipTypeHandler().createMembershipTypeInstance();
          mt.setName(membership);
          mhandler.linkMembership(user, group, mt, false);
          logger_.info("    Creating membership " + data.getUserName() + ", " + groupId + ", " + membership);
        } else {
          logger_.info("    Ignoring existing membership " + data.getUserName() + ", " + groupId + ", " + membership);
        }
      }
    }
  }
}
