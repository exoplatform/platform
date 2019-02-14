package org.exoplatform.platform.common.rest.services.CalendarPortlet;

import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.services.organization.*;
import org.exoplatform.services.organization.impl.GroupImpl;

import java.util.Collection;
import java.util.Collections;

public class MockOrganizationService implements OrganizationService {
    @Override
    public UserHandler getUserHandler() {
        return null;
    }

    @Override
    public UserProfileHandler getUserProfileHandler() {
        return null;
    }

    @Override
    public GroupHandler getGroupHandler() {
        return new MockGroupHandler();
    }

    @Override
    public MembershipTypeHandler getMembershipTypeHandler() {
        return null;
    }

    @Override
    public MembershipHandler getMembershipHandler() {
        return null;
    }

    @Override
    public void addListenerPlugin(ComponentPlugin componentPlugin) throws Exception {

    }

    class MockGroupHandler implements GroupHandler {
        @Override
        public Group createGroupInstance() {
            return null;
        }

        @Override
        public void createGroup(Group group, boolean b) throws Exception {

        }

        @Override
        public void addChild(Group group, Group group1, boolean b) throws Exception {

        }

        @Override
        public void saveGroup(Group group, boolean b) throws Exception {

        }

        @Override
        public Group removeGroup(Group group, boolean b) throws Exception {
            return null;
        }

        @Override
        public Collection<Group> findGroupByMembership(String s, String s1) throws Exception {
            return null;
        }

        @Override
        public Collection<Group> resolveGroupByMembership(String s, String s1) throws Exception {
            return null;
        }

        @Override
        public Group findGroupById(String s) throws Exception {
            return null;
        }

        @Override
        public Collection<Group> findGroups(Group group) throws Exception {
            return null;
        }

        @Override
        public Collection<Group> findGroupsOfUser(String s) throws Exception {
            Group group = new GroupImpl();
            group.setId("/platform/users");
            group.setParentId("/platform");
            group.setGroupName("users");
            group.setLabel("Users");
            return Collections.singleton(group);
        }

        @Override
        public Collection<Group> getAllGroups() throws Exception {
            return null;
        }

        @Override
        public void addGroupEventListener(GroupEventListener groupEventListener) {

        }

        @Override
        public void removeGroupEventListener(GroupEventListener groupEventListener) {

        }
    }
}
