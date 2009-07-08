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
package org.exoplatform.portal.config.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserACLMetaData;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.test.BasicTestCase;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class AbstractTestUserACL extends BasicTestCase {

  UserACL ua;
  User root, administrator, manager, user, guest;

  @Override
  protected void setUp() throws Exception {
    UserACLMetaData md = new UserACLMetaData();
    md.setSuperUser("root");
    md.setGuestsGroups("/platform/guests");
    md.setPortalCreateGroups("*:/platform/administrators,*:/organization/management/executive-board");
    md.setNavigationCreatorMembershipType("manager");
    UserACL ua = new UserACL(md);
    User root = new User("root");
    User administrator = new User("administrator");
    administrator.addMembership("whatever", "/platform/administrators");
    User manager = new User("manager");
    manager.addMembership("manager", "/manageable");
    User user = new User("user");
    User guest = new User(null);

    //
    this.ua = ua;
    this.root = root;
    this.administrator = administrator;
    this.manager = manager;
    this.user = user;
    this.guest = guest;
  }

  public class User {

    private final Identity identity;

    private User(String id) {
      if (id != null) {
        Collection<String> roles = Collections.emptySet();
        Set<MembershipEntry> memberships = new HashSet<MembershipEntry>();
        identity = new Identity(id, memberships, roles);
      } else {
        identity = null;
      }
    }

    private String getId() {
      return identity != null ? identity.getUserId() : null;
    }

    public void addMembership(String type, String group) {
      identity.getMemberships().add(new MembershipEntry(group, type));
    }

    public void removeMembership(String type, String group) {
      for (Iterator<MembershipEntry> i = identity.getMemberships().iterator();i.hasNext();) {
        MembershipEntry membership = i.next();
        if (type == null || type.equals(membership.getMembershipType())) {
          if (group == null || group.equals(membership.getGroup())) {
            i.remove();
          }
        }
      }
    }

    public void removeMembershipByType(String type) {
      removeMembership(type, null);
    }

    public void removeMembershipByGroup(String group) {
      removeMembership(null, group);
    }

    public void run(Runnable runnable) {
      ConversationState.setCurrent(new ConversationState(identity));
      try {
        runnable.run();
      } finally {
        ConversationState.setCurrent(null);
      }
    }

    public boolean hasEditPermission(PageNavigation nav) {
      ConversationState.setCurrent(new ConversationState(identity));
      try {
        return ua.hasEditPermission(nav);
      } finally {
        ConversationState.setCurrent(null);
      }
    }

    public boolean hasPermission(Page page) {
      ConversationState.setCurrent(new ConversationState(identity));
      try {
        return ua.hasPermission(page);
      } finally {
        ConversationState.setCurrent(null);
      }
    }

    public boolean hasEditPermission(Page page) {
      ConversationState.setCurrent(new ConversationState(identity));
      try {
        return ua.hasEditPermission(page);
      } finally {
        ConversationState.setCurrent(null);
      }
    }

    public boolean hasPermission(PortalConfig portal) {
      ConversationState.setCurrent(new ConversationState(identity));
      try {
        return ua.hasPermission(portal);
      } finally {
        ConversationState.setCurrent(null);
      }
    }

    public boolean hasEditPermission(PortalConfig portal) {
      ConversationState.setCurrent(new ConversationState(identity));
      try {
        return ua.hasEditPermission(portal);
      } finally {
        ConversationState.setCurrent(null);
      }
    }

    public boolean hasCreatePortalPermission() {
      ConversationState.setCurrent(new ConversationState(identity));
      try {
        return ua.hasCreatePortalPermission();
      } finally {
        ConversationState.setCurrent(null);
      }
    }
  }
}
