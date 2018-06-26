package org.exoplatform.platform.component.organization.test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.platform.migration.ExternalStoreUpgradePlugin;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.organization.*;
import org.exoplatform.services.organization.externalstore.*;
import org.exoplatform.services.organization.externalstore.model.IDMEntityType;
import org.exoplatform.services.organization.idm.externalstore.IDMInMemoryQueueServiceImpl;
import org.exoplatform.services.organization.impl.GroupImpl;
import org.exoplatform.services.organization.impl.UserImpl;
import org.exoplatform.services.scheduler.JobSchedulerService;
import org.exoplatform.test.BasicTestCase;

public class TestExternalStoreUpgradePlugin extends BasicTestCase {

  IDMExternalStoreService       externalStoreService;

  IDMExternalStoreImportService externalStoreImportService;

  OrganizationService           organizationService;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    externalStoreService = mock(IDMExternalStoreService.class);
    Set<IDMEntityType<?>> entityTypes = new HashSet<>();
    entityTypes.add(IDMEntityType.USER);
    entityTypes.add(IDMEntityType.USER_MEMBERSHIPS);
    entityTypes.add(IDMEntityType.USER_PROFILE);
    entityTypes.add(IDMEntityType.GROUP);
    entityTypes.add(IDMEntityType.GROUP_MEMBERSHIPS);
    when(externalStoreService.getManagedEntityTypes()).thenReturn(entityTypes);
    organizationService = mock(OrganizationService.class);
    UserHandler userHandler = mock(UserHandler.class);
    GroupHandler groupHandler = mock(GroupHandler.class);
    when(organizationService.getUserHandler()).thenReturn(userHandler);
    when(organizationService.getGroupHandler()).thenReturn(groupHandler);
    IDMQueueService idmQueueService = new IDMInMemoryQueueServiceImpl(null);
    ListenerService listenerService = mock(ListenerService.class);
    JobSchedulerService jobSchedulerService = mock(JobSchedulerService.class);
    ExoContainer container = mock(ExoContainer.class);
    externalStoreImportService = new IDMExternalStoreImportService(container,
                                                                   organizationService,
                                                                   listenerService,
                                                                   externalStoreService,
                                                                   jobSchedulerService,
                                                                   idmQueueService,
                                                                   null);
  }

  public void testUpgradeEnabled() throws Exception {
    when(externalStoreService.isEnabled()).thenReturn(false);
    assertFalse(new ExternalStoreUpgradePlugin(externalStoreImportService, externalStoreService, createParams()).isEnabled());
  }

  public void testUpgradeUsers() throws Exception {
    Map<String, User> externalUsers = new HashMap<>();
    UserImpl testuser = new UserImpl("testuser");
    externalUsers.put("testuser", testuser);
    UserImpl testuser2 = new UserImpl("testuser2");
    UserImpl testuser3 = new UserImpl("testuser3");
    externalUsers.put("testuser3", testuser3);

    when(externalStoreService.getAllOfType(IDMEntityType.USER, null)).thenAnswer(new Answer<ListAccess<String>>() {
      @Override
      public ListAccess<String> answer(InvocationOnMock invocation) throws Throwable {
        return new ListAccessImpl(new ArrayList<>(externalUsers.keySet()));
      }
    });

    when(externalStoreService.getEntity(eq(IDMEntityType.USER), anyString())).thenAnswer(new Answer<User>() {
      @Override
      public User answer(InvocationOnMock invocation) throws Throwable {
        String username = invocation.getArgumentAt(1, String.class);
        return externalUsers.get(username);
      }
    });

    when(externalStoreService.isEntityPresent(eq(IDMEntityType.USER), anyString())).thenAnswer(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock invocation) throws Throwable {
        String username = invocation.getArgumentAt(1, String.class);
        return externalStoreService.getEntity(IDMEntityType.USER, username) != null;
      }
    });

    when(organizationService.getUserHandler().findUserByName(eq("testuser"))).thenReturn(testuser);
    when(organizationService.getUserHandler().findUserByName(eq("testuser2"))).thenReturn(testuser2);
    when(organizationService.getUserHandler().findUserByName(eq("testuser3"))).thenReturn(testuser3);

    when(externalStoreService.isEnabled()).thenReturn(true);

    new ExternalStoreUpgradePlugin(externalStoreImportService, externalStoreService, createParams()).processUpgrade(null, null);
    externalStoreImportService.processQueueEntries();

    assertFalse(testuser.isInternalStore());
    assertTrue(testuser2.isInternalStore());
    assertFalse(testuser3.isInternalStore());
  }

  public void testUpgradeGroups() throws Exception {
    Map<String, Group> externalGroups = new HashMap<>();
    Group testGroup = new GroupImpl("group");
    testGroup.setId("/group");
    externalGroups.put("/group", testGroup);
    Group testGroup2 = new GroupImpl("group2");
    testGroup2.setId("/group2");
    Group testGroup3 = new GroupImpl("group3");
    testGroup3.setId("/group3");
    externalGroups.put("/group3", testGroup3);

    when(externalStoreService.getAllOfType(IDMEntityType.GROUP, null)).thenAnswer(new Answer<ListAccess<String>>() {
      @Override
      public ListAccess<String> answer(InvocationOnMock invocation) throws Throwable {
        return new ListAccessImpl(new ArrayList<>(externalGroups.keySet()));
      }
    });

    when(externalStoreService.getEntity(eq(IDMEntityType.GROUP), anyString())).thenAnswer(new Answer<Group>() {
      @Override
      public Group answer(InvocationOnMock invocation) throws Throwable {
        String groupId = invocation.getArgumentAt(1, String.class);
        return externalGroups.get(groupId);
      }
    });

    when(externalStoreService.isEntityPresent(eq(IDMEntityType.GROUP), anyString())).thenAnswer(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock invocation) throws Throwable {
        String groupId = invocation.getArgumentAt(1, String.class);
        return externalStoreService.getEntity(IDMEntityType.GROUP, groupId) != null;
      }
    });

    when(organizationService.getGroupHandler().findGroupById(eq("/group"))).thenReturn(testGroup);
    when(organizationService.getGroupHandler().findGroupById(eq("/group2"))).thenReturn(testGroup2);
    when(organizationService.getGroupHandler().findGroupById(eq("/group3"))).thenReturn(testGroup3);

    when(externalStoreService.isEnabled()).thenReturn(true);

    new ExternalStoreUpgradePlugin(externalStoreImportService, externalStoreService, createParams()).processUpgrade(null, null);
    externalStoreImportService.processQueueEntries();

    assertFalse(testGroup.isInternalStore());
    assertTrue(testGroup2.isInternalStore());
    assertFalse(testGroup3.isInternalStore());
  }

  private InitParams createParams() {
    InitParams params = new InitParams();

    ValueParam valueParam = new ValueParam();
    valueParam.setName(UpgradeProductPlugin.PRODUCT_GROUP_ID);
    valueParam.setValue("platform");
    params.addParam(valueParam);
    return params;
  }

  public class ListAccessImpl implements ListAccess<String> {

    private final List<String> list;

    public ListAccessImpl(List<String> list) {
      this.list = list;
    }

    public String[] load(int index, int length) throws Exception {
      if (index + length > list.size()) {
        length = list.size() - index;
        if (length <= 0) {
          return (new String[0]);
        }
      }
      String[] array = (String[]) new String[length];
      list.subList(index, index + length).toArray(array);
      return array;
    }

    public int getSize() throws Exception {
      return list.size();
    }
  }

}
