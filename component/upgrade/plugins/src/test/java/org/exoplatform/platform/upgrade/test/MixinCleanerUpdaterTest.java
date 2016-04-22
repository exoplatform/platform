/*
 * Copyright (C) 2003-2016 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.upgrade.test;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.platform.upgrade.plugins.MixinCleanerUpgradePlugin;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.transaction.TransactionService;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;

/**
 * Created by The eXo Platform SAS Author : Boubaker Khanfir
 * bkhanfir@exoplatform.com April 16, 2016
 */
public class MixinCleanerUpdaterTest extends BaseExoTestCase {
  private static final String    OLD_VERSION = "1.0";

  private static final String    NEW_VERSION = "1.1";

  private static final String    WS_NAME     = "portal-test";

  private final Log              LOG         = ExoLogger.getLogger(MixinCleanerUpdaterTest.class);

  private Set<ExoSocialActivity> tearDownActivityList;

  private Set<Identity>          tearDownIdentityList;

  private IdentityManager        identityManager;

  private ActivityManager        activityManager;

  private RepositoryService      repositoryService;

  private TransactionService     transactionService;

  private SettingService         settingService;

  private ChromatticManager      chromatticManager;

  protected Session              session;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    identityManager = getContainer().getComponentInstanceOfType(IdentityManager.class);
    activityManager = getContainer().getComponentInstanceOfType(ActivityManager.class);
    repositoryService = getContainer().getComponentInstanceOfType(RepositoryService.class);
    transactionService = getContainer().getComponentInstanceOfType(TransactionService.class);
    settingService = getContainer().getComponentInstanceOfType(SettingService.class);
    chromatticManager = getContainer().getComponentInstanceOfType(ChromatticManager.class);

    tearDownActivityList = new HashSet<ExoSocialActivity>();
    tearDownIdentityList = new HashSet<Identity>();

    for (int i = 0; i < 5; i++) {
      RequestLifeCycle.begin(PortalContainer.getInstance());
      try {
        createActivity("jame");
        createActivity("paul");
        createActivity("root");
        createActivity("john");
        createActivity("mary");
        createActivity("demo");
        createActivity("ghost");
        createActivity("raul");
      } finally {
        RequestLifeCycle.end();
      }
    }

    session = getSession();
  }

  public void testUpgrade() throws Exception {
    assertEquals("Used workspace is different", session.getWorkspace().getName(), WS_NAME);

    // Get the number of nodes that wasn't updated
    NodeIterator nodeIterator = getQueryResult("select * from soc:profiledefinition", Query.SQL);
    // exceptional nodes = (soc:profiledefinition COUNT)
    long exceptionalNodesCount = nodeIterator.getSize();
    LOG.info("Nodes of type soc:profiledefinition = {}", nodeIterator.getSize());

    // Get nodes count with mixin exo:sortable
    nodeIterator = getQueryResult("select * from exo:sortable", Query.SQL);
    assertTrue("No nodes was found with mixin exo:sortable", nodeIterator.getSize() > exceptionalNodesCount);
    long mixinNodesCount = nodeIterator.getSize() - exceptionalNodesCount;

    LOG.info("Cleanup '{}' social nodes.", nodeIterator.getSize() - exceptionalNodesCount);
    String pluginName = "SocialMixinCleanerUpgradePlugin";

    MixinCleanerUpgradePlugin socialMixinCleanerUpgradePlugin = new MixinCleanerUpgradePlugin(PortalContainer.getInstance(),
                                                                                              repositoryService,
                                                                                              transactionService,
                                                                                              settingService,
                                                                                              chromatticManager,
                                                                                              setInitParams(100));

    socialMixinCleanerUpgradePlugin.setName(pluginName);
    socialMixinCleanerUpgradePlugin.processUpgrade(OLD_VERSION, NEW_VERSION);

    assertTrue("Should process to upgrade is 'False'",
               socialMixinCleanerUpgradePlugin.shouldProceedToUpgrade(NEW_VERSION, OLD_VERSION));

    // Wait until the upgrade is asynchronously finished
    while (!socialMixinCleanerUpgradePlugin.isUpgradeFinished()) {
      Thread.sleep(1000);
    }

    assertEquals("The number of treated nodes is not as expected", socialMixinCleanerUpgradePlugin.getTotalCount(), 100);
    mixinNodesCount -= 100;

    String migrationStatus = socialMixinCleanerUpgradePlugin.getValue(MixinCleanerUpgradePlugin.MIGRATION_STATUS);
    assertNotSame("Migration status is not coherent. ", migrationStatus, MixinCleanerUpgradePlugin.UPGRADE_COMPLETED_STATUS);

    socialMixinCleanerUpgradePlugin = new MixinCleanerUpgradePlugin(PortalContainer.getInstance(),
                                                                    repositoryService,
                                                                    transactionService,
                                                                    settingService,
                                                                    chromatticManager,
                                                                    setInitParams(0));
    socialMixinCleanerUpgradePlugin.setName(pluginName);

    assertTrue("Should process to upgrade is 'False'",
               socialMixinCleanerUpgradePlugin.shouldProceedToUpgrade(NEW_VERSION, OLD_VERSION));

    socialMixinCleanerUpgradePlugin.processUpgrade(OLD_VERSION, NEW_VERSION);

    // Wait until the upgrade is asynchronously finished
    while (!socialMixinCleanerUpgradePlugin.isUpgradeFinished()) {
      Thread.sleep(1000);
    }

    migrationStatus = socialMixinCleanerUpgradePlugin.getValue(MixinCleanerUpgradePlugin.MIGRATION_STATUS);
    assertEquals("Migration status is not coherent. ", migrationStatus, MixinCleanerUpgradePlugin.UPGRADE_COMPLETED_STATUS);

    // Get nodes of type exo:sortable count again
    nodeIterator = getQueryResult("select * from exo:sortable", Query.SQL);
    long nodesCount = nodeIterator.getSize();
    // FIXME: workaround JCR-2443
    if (nodeIterator.getSize() != exceptionalNodesCount) {
      while (nodeIterator.hasNext()) {
        Node node = nodeIterator.nextNode();
        if (!node.isNodeType("exo:sortable")) {
          // LOG.warn("Bug JCR-2443 happened on node '{}', node type '{}'.",
          // node.getPath(), node.getPrimaryNodeType().getName());
          nodesCount--;
        }
      }
    }

    nodeIterator = getQueryResult("select * from exo:sortable", Query.SQL);
    // FIXME: workaround JCR-2443
    if (nodeIterator.getSize() != exceptionalNodesCount) {
      while (nodeIterator.hasNext()) {
        Node node = nodeIterator.nextNode();
        if (node.isNodeType("exo:sortable") && !node.isNodeType("soc:profiledefinition")) {
          LOG.error("Node wasn't cleaned up '{}', node type '{}'.", node.getPath(), node.getPrimaryNodeType().getName());
        }
      }
    }

    assertEquals("Social nodes wasn't cleaned up. It seems that there are some remaining nodes that uses exo:sortable",
                 exceptionalNodesCount,
                 nodesCount);
    assertTrue("Treated nodes are different from what was initially predicted",
               mixinNodesCount <= socialMixinCleanerUpgradePlugin.getTotalCount());
  }

  @Override
  public void tearDown() throws Exception {
    for (ExoSocialActivity activity : tearDownActivityList) {
      activityManager.deleteActivity(activity.getId());
    }

    for (Identity identity : tearDownIdentityList) {
      identityManager.deleteIdentity(identity);
    }
  }

  @SuppressWarnings("deprecation")
  private void createActivity(String userId) {
    Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, true);
    tearDownIdentityList.add(identity);

    ExoSocialActivity activity = new ExoSocialActivityImpl();
    // test for reserving order of map values for i18n activity
    Map<String, String> templateParams = new LinkedHashMap<String, String>();
    templateParams.put("key1", "value 1");
    templateParams.put("key2", "value 2");
    templateParams.put("key3", "value 3");
    activity.setTemplateParams(templateParams);
    activity.setTitle("Activity Test for " + userId);
    activity.setUserId(identity.getId());

    //
    activity.isHidden(false);
    activity.isLocked(true);

    activity = activityManager.saveActivity(identity, activity);
    tearDownActivityList.add(activity);
  }

  private NodeIterator getQueryResult(String statement, String type) throws InvalidQueryException, RepositoryException {
    Query query = session.getWorkspace().getQueryManager().createQuery(statement, type);
    NodeIterator nodeIterator = query.execute().getNodes();
    return nodeIterator;
  }

  private InitParams setInitParams(long maxNodesToTreat) {
    InitParams initParams = new InitParams();
    ValueParam workspaceParam = new ValueParam();
    workspaceParam.setName("workspace");
    workspaceParam.setValue(WS_NAME);

    ValueParam maxNodesToTreatParam = new ValueParam();
    maxNodesToTreatParam.setName("max.nodes.to.treat");
    maxNodesToTreatParam.setValue("" + maxNodesToTreat);

    ValueParam groupIdParam = new ValueParam();
    groupIdParam.setName("product.group.id");
    groupIdParam.setValue("org.exoplatform.social");

    ValuesParam mixinsParam = new ValuesParam();
    mixinsParam.setName("mixins.to.clean");
    mixinsParam.setValues(Collections.singletonList("exo:sortable"));

    ValuesParam mixinsExceptionParam = new ValuesParam();
    mixinsExceptionParam.setName("mixins.clean.exception");
    mixinsExceptionParam.setValues(Collections.singletonList("exo:sortable;soc:profiledefinition"));

    initParams.addParam(workspaceParam);
    initParams.addParam(maxNodesToTreatParam);
    initParams.addParam(groupIdParam);
    initParams.addParam(mixinsParam);
    initParams.addParam(mixinsExceptionParam);

    return initParams;
  }

  private Session getSession() throws RepositoryException {
    PortalContainer container = PortalContainer.getInstance();
    RepositoryService repositoryService = (RepositoryService) container.getComponentInstance(RepositoryService.class);
    ManageableRepository repository = repositoryService.getCurrentRepository();
    return repository.getSystemSession("portal-test");
  }

}
