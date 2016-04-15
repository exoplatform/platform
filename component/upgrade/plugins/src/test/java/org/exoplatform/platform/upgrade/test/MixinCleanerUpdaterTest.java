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

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;

import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.container.PortalContainer;
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
  private static final String    WS_NAME = "portal-test";

  private final Log              LOG     = ExoLogger.getLogger(MixinCleanerUpdaterTest.class);

  private Set<ExoSocialActivity> tearDownActivityList;

  private Set<Identity>          tearDownIdentityList;

  private IdentityManager        identityManager;

  private ActivityManager        activityManager;

  private RepositoryService      repositoryService;

  private TransactionService     transactionService;

  protected Session              session;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    identityManager = getContainer().getComponentInstanceOfType(IdentityManager.class);
    activityManager = getContainer().getComponentInstanceOfType(ActivityManager.class);
    repositoryService = getContainer().getComponentInstanceOfType(RepositoryService.class);
    transactionService = getContainer().getComponentInstanceOfType(TransactionService.class);

    session = getSession();

    tearDownActivityList = new HashSet<ExoSocialActivity>();
    tearDownIdentityList = new HashSet<Identity>();

    createActivity("root");
    createActivity("john");
    createActivity("mary");
    createActivity("demo");
    createActivity("ghost");
    createActivity("raul");
    createActivity("jame");
    createActivity("paul");
  }

  public void testUpgrade() throws Exception {
    assertEquals("Used workspace is different", session.getWorkspace().getName(), WS_NAME);

    // Get the number of nodes that wasn't updated
    NodeIterator nodeIterator = getQueryResult("select * from soc:profiledefinition");
    // exceptional nodes = (soc:profiledefinition COUNT)
    long exceptionalNodesCount = nodeIterator.getSize();
    LOG.info("Nodes of type soc:profiledefinition = {}", nodeIterator.getSize());

    // Get nodes count with mixin exo:sortable
    nodeIterator = getQueryResult("select * from exo:sortable");
    assertTrue("No nodes was found with mixin exo:sortable", nodeIterator.getSize() > exceptionalNodesCount);

    LOG.info("Cleanup '{}' social nodes.", nodeIterator.getSize());
    MixinCleanerUpgradePlugin socialMixinCleanerUpgradePlugin = new MixinCleanerUpgradePlugin(PortalContainer.getInstance(),
                                                                                              repositoryService,
                                                                                              transactionService,
                                                                                              setInitParams());
    socialMixinCleanerUpgradePlugin.processUpgrade(null, null);

    // Wait until the upgrade is asynchronously finished
    while (!socialMixinCleanerUpgradePlugin.isUpgradeFinished()) {
      Thread.sleep(1000);
    }

    // Get the nodes with mixin count, after clean up
    nodeIterator = getQueryResult("select * from exo:sortable");
    LOG.info("Not cleaned up social nodes: '{}'.", nodeIterator.getSize());

    assertEquals("Social nodes wasn't cleaned up. It seems that there are some remaining nodes that uses exo:sortable",
                 nodeIterator.getSize(),
                 exceptionalNodesCount);
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
    activityManager.saveActivityNoReturn(identity, activity);
    tearDownActivityList.add(activity);
  }

  private NodeIterator getQueryResult(String statement) throws InvalidQueryException, RepositoryException {
    Query query = session.getWorkspace().getQueryManager().createQuery(statement, Query.SQL);
    NodeIterator nodeIterator = query.execute().getNodes();
    return nodeIterator;
  }

  private InitParams setInitParams() {
    InitParams initParams = new InitParams();
    ValueParam workspaceParam = new ValueParam();
    workspaceParam.setName("workspace");
    workspaceParam.setValue(WS_NAME);

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
