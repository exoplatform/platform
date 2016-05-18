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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer.PortalContainerPostInitTask;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.platform.upgrade.plugins.util.Utils;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.core.WorkspaceContainerFacade;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.jcr.impl.storage.jdbc.JDBCWorkspaceDataContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.transaction.TransactionService;

/**
 * Created by The eXo Platform SAS Author : Boubaker Khanfir
 * bkhanfir@exoplatform.com April 16, 2016
 */
public class MixinCleanerUpgradePlugin extends UpgradeProductPlugin {

  public static final String        MIGRATION_STATUS               = "migration.status";

  public static final int           UPDATE_LAST_NODE_FREQ          = 1000;

  private static final int          TRANSACTION_TIMEOUT_IN_SECONDS = 86400;

  private static final Log          LOG                            = ExoLogger.getLogger(MixinCleanerUpgradePlugin.class);

  private static final int          NODES_IN_ONE_TRANSACTION       = 100;

  private final PortalContainer     portalContainer;

  private final RepositoryService   repositoryService;

  private final TransactionService  txService;

  private String                    workspaceName;

  private long                      totalCount                     = 0;

  private Map<String, List<String>> mixinNames                     = new HashMap<String, List<String>>();

  private String                    jcrRootPath;

  private long                      maxTreatedNodes                = 0;

  private boolean                   upgradeFinished                = false;

  private boolean                   continueOnError                = true;

  private String                    migrationStatus                = null;

  private int                       queryLimitSize                 = 1000;

  private Set<String>               nodePathsInError               = new HashSet<String>();

  /**
   * @param portalContainer
   * @param repositoryService
   * @param txService
   * @param initParams workspace: workspace on which the operation will start.
   *          mixinsCleanup.includes, mixinsCleanup.excludes
   */
  public MixinCleanerUpgradePlugin(PortalContainer portalContainer,
                                   RepositoryService repositoryService,
                                   TransactionService txService,
                                   SettingService settingService,
                                   InitParams initParams) {
    super(settingService, initParams);
    this.repositoryService = repositoryService;
    this.txService = txService;
    this.portalContainer = portalContainer;
    ValueParam workspaceValueParam = initParams.getValueParam("workspace");
    if (workspaceValueParam != null) {
      workspaceName = workspaceValueParam.getValue();
    }
    if (StringUtils.isBlank(workspaceName)) {
      workspaceName = Utils.DEFAULT_WORKSPACE_NAME;
    }
    ValueParam pathParam = initParams.getValueParam("path");
    if (pathParam != null) {
      jcrRootPath = pathParam.getValue();
    }
    if (StringUtils.isBlank(jcrRootPath)) {
      jcrRootPath = "/";
    }

    ValuesParam mixinsValueParam = initParams.getValuesParam("mixinsCleanup.includes");
    if (mixinsValueParam != null) {
      List<String> mixins = mixinsValueParam.getValues();
      for (String mixin : mixins) {
        if (!StringUtils.isBlank(mixin)) {
          mixinNames.put(mixin, null);
        }
      }
    }
    if (mixinNames.isEmpty()) {
      LOG.warn("No mixins to cleanup, the mixins list is empty.");
    }
    ValueParam maxNodesParam = initParams.getValueParam("mixinsCleanup.maxNodes");
    if (maxNodesParam != null) {
      try {
        maxTreatedNodes = Long.parseLong(maxNodesParam.getValue());
        if (maxTreatedNodes < 0) {
          maxTreatedNodes = 0;
        }
      } catch (Exception e) {
        LOG.error("Parameter '" + maxNodesParam.getName() + "' is not a valid number.", e);
      }
    }
    ValueParam continueOnErrorParam = initParams.getValueParam("mixinsCleanup.continueOnError");
    if (continueOnErrorParam != null) {
      try {
        continueOnError = Boolean.parseBoolean(continueOnErrorParam.getValue());
      } catch (Exception e) {
        LOG.error("Parameter '" + continueOnErrorParam.getName() + "' is not a valid boolean.", e);
      }
    }
    ValuesParam mixinsExceptionsValueParam = initParams.getValuesParam("mixinsCleanup.excludes");
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
    migrationStatus = getValue(MIGRATION_STATUS);

    if (mixinNames.isEmpty()) {
      LOG.warn("Mixin types are empty for upgrade plugin {}", getName());
      return false;
    }

    return (VersionComparator.isAfter(newVersion, oldVersion) || VersionComparator.isSame(newVersion, oldVersion))
        && (migrationStatus == null || !migrationStatus.equals(UPGRADE_COMPLETED_STATUS));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processUpgrade(final String oldVersion, final String newVersion) {
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

  public void setQueryLimitSize(int queryLimitSize) {
    this.queryLimitSize = queryLimitSize;
  }

  private void doMigration() {
    LOG.info("Start migration, workspace = {}, root path = {}, maxNodes = {}", workspaceName, jcrRootPath, maxTreatedNodes);

    // Initialize counter
    totalCount = 0;

    Session session = null;
    UserTransaction transaction = null;
    Connection jdbcConn = null;
    SessionProvider sessionProvider = null;
    try {
      // Get JDBC Connection
      ManageableRepository currentRepository = repositoryService.getCurrentRepository();
      jdbcConn = getWorkspaceJDBCConnection(currentRepository);

      // Get JCR Session
      sessionProvider = SessionProvider.createSystemProvider();
      session = getJCRSession(sessionProvider, currentRepository);

      String query = Utils.getQuery(jdbcConn, currentRepository, workspaceName, mixinNames.keySet(), queryLimitSize);

      // Begin transaction
      transaction = beginTransaction();

      if (query != null) {
        // Get the list of items to cleanup, by SQL Query
        transaction = cleanNodesByQuery(query, session, transaction, jdbcConn, currentRepository);
      } else {
        // If Query is null, we will browse the JCR using API
        Node parentNode = (Node) session.getItem(jcrRootPath);
        transaction = cleanChildNodes(parentNode, session, transaction, true);
      }

      if (maxTreatedNodes > 0 && totalCount == maxTreatedNodes) {
        storeValueForPlugin(MIGRATION_STATUS, "" + totalCount);
      } else {
        storeValueForPlugin(MIGRATION_STATUS, UPGRADE_COMPLETED_STATUS);
      }
      LOG.info("Migration finished, proceeded nodes count = {}", totalCount);
    } catch (Exception e) {
      LOG.error("Migration interrupted because of the following error", e);
      try {
        if (session != null) {
          session.refresh(false);
        }
        transaction.rollback();
      } catch (Exception e1) {
        LOG.error("Error while rolling back transaction", e);
      }
    } finally {
      upgradeFinished = true;
      nodePathsInError.clear();
      if (session != null) {
        session.logout();
      }
      if (sessionProvider != null) {
        sessionProvider.close();
      }
      try {
        if (jdbcConn != null) {
          jdbcConn.close();
        }
      } catch (SQLException e) {
        LOG.error("Cannot close connection", e);
      }
    }
  }

  private UserTransaction cleanNodesByQuery(String query,
                                            Session session,
                                            UserTransaction transaction,
                                            Connection jdbcConn,
                                            ManageableRepository currentRepository) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    boolean continueSearching = true;
    do {
      long initialTreatedNodesCount = totalCount;
      long resultCount = 0;

      stmt = jdbcConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
      stmt.setQueryTimeout(TRANSACTION_TIMEOUT_IN_SECONDS);
      rs = stmt.executeQuery(query);
      try {
        while ((maxTreatedNodes == 0 || totalCount < maxTreatedNodes) && rs.next()) {
          resultCount++;
          String id = rs.getString(1);
          id = id.replaceAll(workspaceName, "");
          Node node = null;
          try {
            node = ((SessionImpl) session).getNodeByIdentifier(id);
          } catch (ItemNotFoundException e) {
            LOG.warn("Item not found with id: '{}'", id);
            continue;
          }
          // Avoid changing Root Node of Workspace
          if (node.getPath().equals("/")) {
            continue;
          }
          // Avoid changing Root Node of Workspace
          if (!node.getPath().startsWith(jcrRootPath)) {
            continue;
          }
          transaction = cleanNode(node, session, transaction, false);
        }
      } finally {
        try {
          if (rs != null) {
            rs.close();
          }
          if (stmt != null) {
            stmt.close();
          }
        } catch (SQLException e) {
          LOG.error("Cannot close JDBC statement", e);
        }
      }
      if (resultCount < queryLimitSize) {
        continueSearching = false;
      } else if (maxTreatedNodes > 0 && totalCount >= maxTreatedNodes) {
        continueSearching = false;
      } else if (((totalCount - initialTreatedNodesCount) * 2) < queryLimitSize) {
        queryLimitSize *= 2;
        query = Utils.getQuery(jdbcConn, currentRepository, workspaceName, mixinNames.keySet(), queryLimitSize);
      }
      // Commit the transaction before relaunching the SQL query
      transaction = commitTransaction(session, transaction, continueSearching);
    } while (continueSearching);
    return transaction;
  }

  private UserTransaction cleanChildNodes(Node parentNode, Session session, UserTransaction transaction, boolean treatChildren) throws Exception {
    NodeIterator nodeIterator = ((NodeImpl) parentNode).getNodesLazily(1);
    while (nodeIterator.hasNext() && (maxTreatedNodes == 0 || totalCount < maxTreatedNodes)) {
      Node node = nodeIterator.nextNode();
      transaction = cleanNode(node, session, transaction, treatChildren);
    }
    return commitTransaction(session, transaction, false);
  }

  private UserTransaction cleanNode(Node node, Session session, UserTransaction transaction, boolean treatChildren) throws Exception {
    boolean proceeded = false;
    String path = node.getPath();
    try {
      try {
        if (!nodePathsInError.contains(path)) {
          proceeded = cleanSingleNodeMixins(node);
        }
      } catch (Exception e) {
        nodePathsInError.add(path);

        if (node != null) {
          node.refresh(false);
        }
        if (continueOnError) {
          LOG.warn("An error occured while proceeding node: {}. Cause: {} ", path, e.getMessage());
          if (LOG.isTraceEnabled()) {
            LOG.error("Error while proceeding node", e);
          }
        } else {
          throw e;
        }
      }
      // Commit transaction for each 100 cleaned nodes
      if (proceeded && totalCount > 0 && totalCount % NODES_IN_ONE_TRANSACTION == 0) {
        transaction = commitTransaction(session, transaction, true);
      }
      if (treatChildren) {
        // Cleanup children nodes
        transaction = cleanChildNodes(node, session, transaction, treatChildren);
      }
    } catch (Exception e) {
      nodePathsInError.add(path);

      // Rollback transation and decrease the proceeded nodes count
      long canceledNodes = totalCount % NODES_IN_ONE_TRANSACTION;
      LOG.error("Rollback '" + canceledNodes + "'  cleaned nodes", e);

      session.refresh(false);
      transaction.rollback();
      totalCount -= canceledNodes;

      // Restart transaction
      transaction = beginTransaction();
    }
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
        LOG.debug("Ignore node: '{}', nodetype = '{}', remove mixin '{}'",
                  node.getPath(),
                  node.getPrimaryNodeType().getName(),
                  mixinName);
      } else {
        LOG.debug("Proceed node: '{}', nodetype = '{}', remove mixin '{}'",
                  node.getPath(),
                  node.getPrimaryNodeType().getName(),
                  mixinName);
        node.removeMixin(mixinName);
        node.save();
        proceeded = true;
      }
    }
    if (proceeded) {
      LOG.debug("Proceeded node: '{}', nodetype = '{}'", node.getPath(), node.getPrimaryNodeType().getName());
      totalCount++;
    }
    return proceeded;
  }

  private Session getJCRSession(SessionProvider sessionProvider, ManageableRepository currentRepository) throws LoginException,
                                                                                                        NoSuchWorkspaceException,
                                                                                                        RepositoryException {
    Session session;
    session = sessionProvider.getSession(workspaceName, currentRepository);
    ((SessionImpl) session).setTimeout(TRANSACTION_TIMEOUT_IN_SECONDS * 1000);
    if (!session.itemExists(jcrRootPath)) {
      throw new IllegalStateException("Cannot procced to upgrade, path doesn't exist:" + workspaceName + ":" + jcrRootPath);
    }
    return session;
  }

  private Connection getWorkspaceJDBCConnection(ManageableRepository currentRepository) throws RepositoryException {
    Connection jdbcConn;
    WorkspaceContainerFacade containerFacade = currentRepository.getWorkspaceContainer(workspaceName);
    if (containerFacade == null) {
      throw new IllegalStateException("Workspace with name '" + workspaceName + "' wasn't found");
    }
    JDBCWorkspaceDataContainer dataContainer = (JDBCWorkspaceDataContainer) containerFacade.getComponent(JDBCWorkspaceDataContainer.class);
    jdbcConn = dataContainer.connFactory.getJdbcConnection();
    return jdbcConn;
  }

  private UserTransaction commitTransaction(Session session, UserTransaction transaction, boolean reNew) throws Exception {
    // Commit mixin cleanup transaction
    session.save();
    if (transaction.getStatus() == Status.STATUS_ACTIVE) {
      transaction.commit();
    }
    LOG.info("Migration in progress, proceeded nodes count = {}", totalCount);
    if (reNew) {
      transaction = beginTransaction();
    }
    return transaction;
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
