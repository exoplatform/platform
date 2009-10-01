/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.portal.initializer.organization;

import java.util.List;

import org.exoplatform.services.organization.OrganizationConfig.Group;
import org.exoplatform.services.organization.OrganizationConfig.User;

/**
 * Created by The eXo Platform SAS Author : Pham Thanh Tung
 * thanhtungty@gmail.com Mar 6, 2009
 */
public class OrganizationConfig {

  List<GroupsConfig> groups;

  List<UsersConfig>  users;

  public List<GroupsConfig> getGroups() {
    return groups;
  }

  public void setGroups(List<GroupsConfig> groups) {
    this.groups = groups;
  }

  public List<UsersConfig> getUsers() {
    return users;
  }

  public void setUsers(List<UsersConfig> users) {
    this.users = users;
  }

  static public class GroupsConfig {

    private Group  group;

    private String from;

    private String to;

    public GroupsConfig() {
      group = new Group();
    }

    public Group getGroup() {
      return group;
    }

    public void setGroup(Group group) {
      this.group = group;
    }

    public String getDescription() {
      return group.getDescription();
    }

    public void setDescription(String description) {
      group.setDescription(description);
    }

    public String getName() {
      return group.getName();
    }

    public void setName(String name) {
      group.setName(name);
    }

    public String getParentId() {
      return group.getParentId();
    }

    public void setParentId(String parentId) {
      group.setParentId(parentId);
    }

    public String getLabel() {
      return group.getLabel();
    }

    public void setLabel(String label) {
      group.setLabel(label);
    }

    public String getFrom() {
      return from;
    }

    public void setFrom(String from) {
      this.from = from;
    }

    public String getTo() {
      return to;
    }

    public void setTo(String to) {
      this.to = to;
    }

  }

  static public class UsersConfig {

    private User   user;

    private String from;

    private String to;

    public User getUser() {
      return user;
    }

    public void setUser(User user) {
      this.user = user;
    }

    public String getFrom() {
      return from;
    }

    public void setFrom(String from) {
      this.from = from;
    }

    public String getTo() {
      return to;
    }

    public void setTo(String to) {
      this.to = to;
    }

  }

}
