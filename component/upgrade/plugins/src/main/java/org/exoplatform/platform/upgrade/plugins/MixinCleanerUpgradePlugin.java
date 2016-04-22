/*
 * Copyright (C) 2003-2016 eXo Platform SAS.
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
package org.exoplatform.platform.upgrade.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer.PortalContainerPostInitTask;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.transaction.TransactionService;

/**
 * Created by The eXo Platform SAS Author : Boubaker Khanfir
 * bkhanfir@exoplatform.com April 16, 2016
 */
public class MixinCleanerUpgradePlugin extends UpgradeProductPlugin {

  public static final String        DEFAULT_WORKSPACE_NAME         = "social";

  private static final String       MIGRATION_STATUS               = "migration.status";

  public static final int           UPDATE_LAST_NODE_FREQ          = 1000;

  private static final int          TRANSACTION_TIMEOUT_IN_SECONDS = 86400;

  private static final Log          LOG                            = ExoLogger.getLogger(MixinCleanerUpgradePlugin.class);

  private static final int          NODES_IN_ONE_TRANSACTION       = 100;

  private final PortalContainer     portalContainer;

  private final RepositoryService   repositoryService;

  private final TransactionService  txService;

  private String                    workspaceName;

  private long                      totalCount                     = 0;

  private Map<String, List<String>> mixinNames                     = null;

  private String                    jcrRootPath;

  private long                      maxTreatedNodes                = 0;

  private boolean                   upgradeFinished                = false;

  /**
   * @param portalContainer
   * @param repositoryService
   * @param txService
   * @param initParams workspace: workspace on which the operation will start.
   *          mixins.to.clean, mixins.clean.exception
   */
  public MixinCleanerUpgradePlugin(PortalContainer portalContainer,
                                   RepositoryService repositoryService,
                                   TransactionService txService,
                                   SettingService settingService,
                                   ChromatticManager chromatticManager,
                                   InitParams initParams) {
    super(settingService, chromatticManager, initParams);
    this.repositoryService = repositoryService;
    this.txService = txService;
    this.portalContainer = portalContainer;
    ValueParam workspaceValueParam = initParams.getValueParam("workspace");
    if (workspaceValueParam != null) {
      workspaceName = workspaceValueParam.getValue();
    }
    if (StringUtils.isBlank(workspaceName)) {
      workspaceName = DEFAULT_WORKSPACE_NAME;
    }
    ValueParam pathParam = initParams.getValueParam("path");
    if (pathParam != null) {
      jcrRootPath = pathParam.getValue();
    }
    if (StringUtils.isBlank(jcrRootPath)) {
      jcrRootPath = "/";
    }

    ValuesParam mixinsValueParam = initParams.getValuesParam("mixins.to.clean");
    if (mixinsValueParam != null) {
      List<String> mixins = mixinsValueParam.getValues();
      mixinNames = new HashMap<String, List<String>>();
      for (String mixin : mixins) {
        if (!StringUtils.isBlank(mixin)) {
          mixinNames.put(mixin, null);
        }
      }
    }
    ValueParam maxNodesParam = initParams.getValueParam("max.nodes.to.treat");
    if (maxNodesParam != null) {
      try {
        maxTreatedNodes = Long.parseLong(maxNodesParam.getValue());
        if (maxTreatedNodes < 0) {
          throw new IllegalArgumentException("'maxTreatedNodes' parameter should be a positive integer.");
        }
      } catch (Exception e) {
        LOG.error("Parameter '" + maxNodesParam.getName() + "' is not a valid number.", e);
      }
    }
    ValuesParam mixinsExceptionsValueParam = initParams.getValuesParam("mixins.clean.exception");
    if (mixinsExceptionsValueParam != null) {
      List<String> mixins = mixinsExceptionsValueParam.getValues();
      // Values with value pattern MIXIN_TYPE;EXCEPTIONAL_NODE_TYPE_NAME
      // where EXCEPTIONAL_NODE_TYPE_NAME is the nodeType for which we shouldn't
      // clean up the mixin
      for (String mixinException : mixins) {
        String[] mixinExceptionParams = mixinException.split(";");
        String mixinName = mixinExceptionParams[0];
        String typeName = mixinExceptionParams[1];

        if (mixinNames.containsKey(mixinName)) {
          List<String> mixinExceptions = mixinNames.get(mixinName);
          if (mixinExceptions == null) {
            mixinExceptions = new ArrayList<String>();
            mixinNames.put(mixinName, mixinExceptions);
          }
          mixinExceptions.add(typeName);
        }
      }
    }
  }

  /**
   * @return true if current version is uppper or equals to than 4.3.0, else
   *         return false
   */
  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String oldVersion) {
    String migrationStatus = getValue(MIGRATION_STATUS);

    return VersionComparator.isAfter(newVersion, oldVersion)
        && (migrationStatus == null || !migrationStatus.equals(UPGRADE_COMPLETED_STATUS));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processUpgrade(final String oldVersion, final String newVersion) {
    storeValueForPlugin(MIGRATION_STATUS, "0");

    PortalContainer.addInitTask(portalContainer.getPortalContext(), new PortalContainerPostInitTask() {
      @Override
      public void execute(ServletContext context, PortalContainer portalContainer) {
        // Execute the task in an asynchrounous way
        new Thread(new Runnable() {
          @Override
          public void run() {
            doMigration();
          }
        }, getName()).start();
      }
    });
  }

  /**
   * @return whether the operation is finished or not
   */
  public boolean isUpgradeFinished() {
    return upgradeFinished;
  }

  public long getTotalCount() {
    return totalCount;
  }

  private void doMigration() {
    LOG.info("Start migration, workspace = {}, root path = {}, maxNodes = {}", workspaceName, jcrRootPath, maxTreatedNodes);

    // Initialize counter
    totalCount = 0;

    Session session = null;
    UserTransaction transaction = null;
    try {
      // Get JCR Session
      ManageableRepository currentRepository = repositoryService.getCurrentRepository();
      session = SessionProvider.createSystemProvider().getSession(workspaceName, currentRepository);
      ((SessionImpl) session).setTimeout(TRANSACTION_TIMEOUT_IN_SECONDS);

      // Begin transaction
      transaction = beginTransaction();
      if (!session.itemExists(jcrRootPath)) {
        throw new IllegalStateException("Cannot procced to upgrade, path doesn't exist:" + workspaceName + ":" + jcrRootPath);
      }
      Node parentNode = (Node) session.getItem(jcrRootPath);

      transaction = cleanChildrenNodes(parentNode, session, transaction);
      session.save();
      transaction.commit();

      if (maxTreatedNodes > 0 && totalCount == maxTreatedNodes) {
        storeValueForPlugin(MIGRATION_STATUS, "" + totalCount);
      } else {
        storeValueForPlugin(MIGRATION_STATUS, UPGRADE_COMPLETED_STATUS);
      }

      LOG.info("Migration finished, proceeded nodes count = {}", totalCount);
    } catch (Exception e) {
      LOG.error("Migration interrupted because of the following error", e);
      try {
        session.refresh(false);
        transaction.rollback();
      } catch (Exception e1) {
        LOG.error("Error while rolling back transaction", e);
      }
    } finally {
      upgradeFinished = true;
      if (session != null) {
        session.logout();
      }
    }
  }

  private UserTransaction cleanChildrenNodes(Node parentNode, Session session, UserTransaction transaction) throws Exception {
    NodeIterator nodeIterator = ((NodeImpl) parentNode).getNodesLazily(1);
    while (nodeIterator.hasNext() && (maxTreatedNodes == 0 || totalCount < maxTreatedNodes)) {
      Node node = nodeIterator.nextNode();
      boolean proceeded = false;
      try {
        try {
          proceeded = cleanSingleNodeMixins(node);
        } catch (Exception e) {
          if (node != null) {
            node.refresh(false);
          }
          throw e;
        }
        // Commit transaction for each 100 cleaned nodes
        if (proceeded && totalCount > 0 && totalCount % NODES_IN_ONE_TRANSACTION == 0) {
          transaction = commitTransaction(node, session, transaction);
        }
        // Cleanup children nodes
        transaction = cleanChildrenNodes(node, session, transaction);
      } catch (Exception e) {
        // Rollback transation and decrease the proceeded nodes count
        long canceledNodes = totalCount % NODES_IN_ONE_TRANSACTION;
        LOG.error("Rollback '" + canceledNodes + "'  cleaned nodes", e);

        session.refresh(false);
        transaction.rollback();
        totalCount -= canceledNodes;

        // Restart transaction
        transaction = beginTransaction();
      }
    }
    return transaction;
  }

  private UserTransaction commitTransaction(Node node, Session session, UserTransaction transaction) throws Exception {
    // Commit mixin cleanup transaction
    session.save();
    transaction.commit();
    if (totalCount > 0 && totalCount % UPDATE_LAST_NODE_FREQ == 0) {
      // Store last updated node path each 1000 treated node
      storeValueForPlugin(MIGRATION_STATUS, "" + totalCount);
    }
    LOG.info("Migration in progress, proceeded nodes count = {}", totalCount);
    transaction = beginTransaction();
    return transaction;
  }

  private boolean cleanSingleNodeMixins(Node node) throws Exception {
    boolean proceeded = false;

    // Remove all mixins from nodes
    for (String mixinName : mixinNames.keySet()) {
      if (!node.isNodeType(mixinName)) {
        continue;
      }
      // Ignore deletion of mixins on exceptional node types (specified by
      // init-param)
      if (isExceptionalNodeType(node, mixinNames.get(mixinName))) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Ignore node: '{}', nodetype = '{}', remove mixin '{}'",
                    node.getPath(),
                    node.getPrimaryNodeType().getName(),
                    mixinName);
        }
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Proceed node: '{}', nodetype = '{}', remove mixin '{}'",
                    node.getPath(),
                    node.getPrimaryNodeType().getName(),
                    mixinName);
        }
        node.removeMixin(mixinName);
        node.save();
        proceeded = true;
      }
    }
    if (proceeded) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Proceeded node: '{}', nodetype = '{}'", node.getPath(), node.getPrimaryNodeType().getName());
      }
      totalCount++;
    }
    return proceeded;
  }

  private boolean isExceptionalNodeType(Node node, List<String> exceptionalNodeTypes) throws RepositoryException {
    if (exceptionalNodeTypes == null) {
      return false;
    }
    for (String nodeType : exceptionalNodeTypes) {
      if (node.isNodeType(nodeType)) {
        return true;
      }
    }
    return false;
  }

  private UserTransaction beginTransaction() throws Exception {
    UserTransaction transaction;
    transaction = txService.getUserTransaction();
    transaction.setTransactionTimeout(TRANSACTION_TIMEOUT_IN_SECONDS);
    transaction.begin();
    return transaction;
  }

}
