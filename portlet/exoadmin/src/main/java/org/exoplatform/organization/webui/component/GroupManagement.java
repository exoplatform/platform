/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;

/**
 * Created by The eXo Platform SAS
 * Author : Huu-Dung Kieu	
 *          kieuhdung@gmail.com
 * 22 déc. 08  
 */
public class GroupManagement {
  
  public static OrganizationService getOrganizationService() {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    OrganizationService orgService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
    return orgService;
  }
  
  public static UserACL getUserACL() {
  	ExoContainer container = ExoContainerContext.getCurrentContainer() ;
    UserACL acl = (UserACL) container.getComponentInstanceOfType(UserACL.class) ;
    return acl;
  }
  public static boolean isMembershipOfGroup(String username, String membership, String groupId) throws Exception {
    boolean ret = false;
    if (username == null)
      username = org.exoplatform.portal.webui.util.Util.getPortalRequestContext().getRemoteUser();
    OrganizationService orgService = getOrganizationService();
    Collection groups = orgService.getGroupHandler().findGroupByMembership(username, membership);
    for(Object group: groups) {
      if (((Group)group).getId().equals(groupId)) {
        ret = true;
        break;
      }
    }
    return ret;
  }  
  
  public static boolean isManagerOfGroup(String username, String groupId) throws Exception {
    return isMembershipOfGroup(username, getUserACL().getAdminMSType(), groupId);
  }  
  
  public static boolean isMemberOfGroup(String username, String groupId) throws Exception {
    boolean ret = false;
    if (username == null)
      username = org.exoplatform.portal.webui.util.Util.getPortalRequestContext().getRemoteUser();
    OrganizationService orgService = getOrganizationService();
    Collection groups = orgService.getGroupHandler().findGroupsOfUser(username);
    for(Object group: groups) {
      if (((Group)group).getId().equals(groupId)) {
        ret = true;
        break;
      }
    }
    return ret;
  }
  
  public static boolean isRelatedOfGroup(String username, String groupId) throws Exception {
    boolean ret = false;
    if (username == null)
      username = org.exoplatform.portal.webui.util.Util.getPortalRequestContext().getRemoteUser();
    OrganizationService orgService = getOrganizationService();
    Collection groups = orgService.getGroupHandler().findGroupsOfUser(username);
    for(Object group: groups) {
      if (((Group)group).getId().startsWith(groupId)) {
        ret = true;
        break;
      }
    }
    return ret;
  }
  
  public static Collection getRelatedGroups(String username, Collection groups) throws Exception {
    if (username == null)
      username = org.exoplatform.portal.webui.util.Util.getPortalRequestContext().getRemoteUser();
    List relatedGroups = new ArrayList();
    OrganizationService orgService = getOrganizationService();
    Collection userGroups = orgService.getGroupHandler().findGroupsOfUser(username);
    for(Object group: groups) {
      if (isRelatedGroup((Group)group, userGroups))
        relatedGroups.add(group);
    }
    return relatedGroups;
  }
  
  private static boolean isRelatedGroup(Group group, Collection groups) {
    boolean ret = false;
    String groupId = group.getId();
    for(Object g: groups) {
      if (((Group)g).getId().startsWith(groupId)) {
        ret = true;
        break;
      }
    }
    return ret;
  }
  
  public static boolean isAdministrator(String username) throws Exception {
    if (username == null)
      username = org.exoplatform.portal.webui.util.Util.getPortalRequestContext().getRemoteUser();
    return isMemberOfGroup(username, getUserACL().getAdminGroups());
  }
  
//  public static boolean isSuperUser(String username) throws Exception {
//    if (username == null)
//      username = org.exoplatform.portal.webui.util.Util.getPortalRequestContext().getRemoteUser();
//    return isMemberOfGroup(username, getUserACL().getAdminGroups());
//  }
  
//  public static boolean isPlatformAdminGroup(String groupId) {
//    return groupId.equals(PLATFORM_ADMIN_GROUP);
//  }
//  
//  public static boolean isPlatformUsersGroup(String groupId) {
//    return groupId.equals(PLATFORM_USERS_GROUP);
//  }
  
  public static boolean isSuperUserOfGroup(String username, String groupId) {
    try {
//        return false;
      // 2nd the selected group must be a normal group
//      if (isPlatformAdminGroup(groupId) || isPlatformUsersGroup(groupId))
//        return false;
//      
      boolean ret = (GroupManagement.isManagerOfGroup(username, groupId) || (GroupManagement.isAdministrator(username)));
      // finally, user must be manager of that group
      return ret;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
}
