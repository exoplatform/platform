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
import java.util.concurrent.atomic.AtomicLong;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;

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

  private static final int          TRANSACTION_TIMEOUT_IN_SECONDS = 86400;

  private static final String       PLUGIN_PROCEED_VERSION         = "4.3.0";

  private static final Log          log                            = ExoLogger.getLogger(MixinCleanerUpgradePlugin.class);

  private static final int          NODES_IN_ONE_TRANSACTION       = 100;

  private final PortalContainer     portalContainer;

  private final RepositoryService   repositoryService;

  private final TransactionService  txService;

  private String                    workspaceName;

  private AtomicLong                totalCount                     = new AtomicLong(0);

  private Map<String, List<String>> mixinNames                     = null;

  private String                    jcrPath;

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
                                   InitParams initParams) {
    super(initParams);
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
      jcrPath = pathParam.getValue();
    }
    if (StringUtils.isBlank(jcrPath)) {
      jcrPath = "/";
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
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion, PLUGIN_PROCEED_VERSION);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    PortalContainer.addInitTask(portalContainer.getPortalContext(), new PortalContainerPostInitTask() {
      @Override
      public void execute(ServletContext context, PortalContainer portalContainer) {
        // Execute the task in an asynchrounous way
        new Thread(new Runnable() {
          @Override
          public void run() {
            // Wait for 10 seconds until the server is fully started.
            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              // Nothing to do
            }
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

  private void doMigration() {
    log.info("Start migration");

    // Initialize counter
    totalCount.set(0);

    Session session = null;
    try {
      // Get JCR Session
      ManageableRepository currentRepository = repositoryService.getCurrentRepository();
      session = SessionProvider.createSystemProvider().getSession(workspaceName, currentRepository);
      ((SessionImpl) session).setTimeout(TRANSACTION_TIMEOUT_IN_SECONDS);

      // Begin transaction
      UserTransaction transaction = beginTransaction();
      if (!session.itemExists(jcrPath)) {
        throw new IllegalStateException("Cannot procced to upgrade, because doesn't exist in path " + workspaceName + ":"
            + jcrPath + "'");
      }
      Node parentNode = (Node) session.getItem(jcrPath);
      transaction = cleanUpChildreNodes(parentNode, session, transaction);
      session.save();
      transaction.commit();
      log.info("Migration finished, proceeded nodes count = {}", totalCount);
    } catch (Exception e) {
      log.error("Migration interrupted because of the following error", e);
    } finally {
      if (session != null) {
        session.logout();
      }
    }
    upgradeFinished = true;
  }

  private UserTransaction cleanUpChildreNodes(Node parentNode, Session session, UserTransaction transaction) throws Exception {
    NodeIterator nodeIterator = ((NodeImpl) parentNode).getNodesLazily(1);
    while (nodeIterator.hasNext()) {
      try {
        Node node = nodeIterator.nextNode();
        try {
          boolean proceeded = false;

          // Remove all mixins from nodes
          for (String mixinName : mixinNames.keySet()) {
            if (!node.isNodeType(mixinName)) {
              continue;
            }
            // Ignore deletion of 'exo:datetime' and 'exo:modify' from
            // nodes of type 'soc:spaceref'
            // Those mixins are used to sort spaces by access time
            if (isNodeType(node, mixinNames.get(mixinName))) {
              log.debug("Ignore id: '{}', nodetype = '{}', remove mixin '{}'", node.getPath(), node.getPrimaryNodeType()
                                                                                                   .getName(), mixinName);
            } else {
              log.debug("Proceed path: '{}', nodetype = '{}', remove mixin '{}'", node.getPath(), node.getPrimaryNodeType()
                                                                                                      .getName(), mixinName);
              node.removeMixin(mixinName);
              node.save();
              proceeded = true;
            }
          }
          if (proceeded) {
            totalCount.incrementAndGet();
          }
        } catch (Exception e) {
          log.warn("Error updating node "
              + (node == null ? "" : " with path " + node.getPath() + ", nodetype = " + node.getPrimaryNodeType().getName()), e);
          if (node != null) {
            node.refresh(false);
          }
        }
        if (totalCount.get() > 0 && totalCount.get() % NODES_IN_ONE_TRANSACTION == 0) {
          session.save();
          transaction.commit();
          log.info("Migration in progress, proceeded nodes count = {}", totalCount);

          transaction = beginTransaction();
        }
        transaction = cleanUpChildreNodes(node, session, transaction);
      } catch (Exception e) {
        transaction.rollback();
        long canceledNodes = totalCount.get() % NODES_IN_ONE_TRANSACTION;
        log.error("Rollback '" + canceledNodes + "'  cleaned nodes", e);
        totalCount.set(totalCount.get() - canceledNodes);
        transaction = beginTransaction();
      }
    }
    return transaction;
  }

  private boolean isNodeType(Node node, List<String> exceptionalNodeTypes) throws RepositoryException {
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

  private UserTransaction beginTransaction() throws SystemException, NotSupportedException {
    UserTransaction transaction;
    transaction = txService.getUserTransaction();
    transaction.setTransactionTimeout(TRANSACTION_TIMEOUT_IN_SECONDS);
    transaction.begin();
    return transaction;
  }

}
