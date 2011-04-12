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
package org.exoplatform.platform.common.space.plugin;

import java.io.InputStream;
import java.util.Iterator;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.services.cms.impl.Utils;
import org.exoplatform.services.deployment.DeploymentDescriptor;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.wcm.core.NodetypeConstant;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

public class XMLDeploymentPlugin extends SpaceListenerPlugin {

  final static private String GROUPS_PATH = "groupsPath";

  /** The init params. */
  private InitParams initParams;

  /** The configuration manager. */
  private ConfigurationManager configurationManager;

  /** The repository service. */
  private RepositoryService repositoryService;

  private String groupsPath;

  /** The log. */
  private Log log = ExoLogger.getLogger(this.getClass());

  /**
   * Instantiates a new XML deployment plugin.
   * 
   * @param initParams
   *          the init params
   * @param configurationManager
   *          the configuration manager
   * @param repositoryService
   *          the repository service
   * @param nodeHierarchyCreator
   *          the nodeHierarchyCreator service
   */
  public XMLDeploymentPlugin(InitParams initParams, ConfigurationManager configurationManager,
      RepositoryService repositoryService, NodeHierarchyCreator nodeHierarchyCreator) {
    this.initParams = initParams;
    this.configurationManager = configurationManager;
    this.repositoryService = repositoryService;
    groupsPath = nodeHierarchyCreator.getJcrPath(GROUPS_PATH);
    if (groupsPath.lastIndexOf("/") == groupsPath.length() - 1) {
      groupsPath = groupsPath.substring(0, groupsPath.lastIndexOf("/"));
    }
  }

  @Override
  public void spaceCreated(SpaceLifeCycleEvent lifeCycleEvent) {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      deploy(sessionProvider, lifeCycleEvent.getSpace().getGroupId());
    } catch (Exception e) {
      log.error("An unexpected problem occurs while deploying contents", e);
    } finally {
      sessionProvider.close();
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.deployment.DeploymentPlugin#deploy(org.exoplatform.services.jcr.ext.common.SessionProvider)
   */
  @SuppressWarnings("rawtypes")
  public void deploy(SessionProvider sessionProvider, String spaceId) throws Exception {
    Iterator iterator = initParams.getObjectParamIterator();
    while (iterator.hasNext()) {
      ObjectParameter objectParameter = (ObjectParameter) iterator.next();
      DeploymentDescriptor deploymentDescriptor = (DeploymentDescriptor) objectParameter.getObject();
      String sourcePath = deploymentDescriptor.getSourcePath();
      // sourcePath should start with: war:/, jar:/, classpath:/, file:/
      Boolean cleanupPublication = deploymentDescriptor.getCleanupPublication();

      InputStream inputStream = configurationManager.getInputStream(sourcePath);
      ManageableRepository repository = repositoryService.getCurrentRepository();
      Session session = sessionProvider.getSession(deploymentDescriptor.getTarget().getWorkspace(), repository);
      String targetNodePath = deploymentDescriptor.getTarget().getNodePath();
      if (targetNodePath.indexOf("/") == 0) {
        targetNodePath = targetNodePath.replaceFirst("/", "");
      }
      if (targetNodePath.lastIndexOf("/") == targetNodePath.length() - 1) {
        targetNodePath = targetNodePath.substring(0, targetNodePath.lastIndexOf("/"));
      }
      // if target path contains folders, then create them
      if (!targetNodePath.equals("")) {
        Node spaceRootNode = (Node) session.getItem(groupsPath + spaceId);
        Utils.makePath(spaceRootNode, targetNodePath, NodetypeConstant.NT_UNSTRUCTURED);
      }
      String fullTargetNodePath = groupsPath + spaceId + "/" + targetNodePath;
      session.importXML(fullTargetNodePath, inputStream, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);

      if (cleanupPublication) {
        /**
         * This code allows to cleanup the publication lifecycle in the target folder after importing the data. By using this, the publication live revision property will be re-initialized and the content will be set as published directly. Thus, the content will be visible in front side.
         */
        QueryManager manager = session.getWorkspace().getQueryManager();
        String statement = "select * from nt:base where jcr:path LIKE '" + fullTargetNodePath + "/%'";
        Query query = manager.createQuery(statement.toString(), Query.SQL);
        NodeIterator iter = query.execute().getNodes();
        while (iter.hasNext()) {
          Node node = iter.nextNode();
          if (node.hasProperty("publication:liveRevision") && node.hasProperty("publication:currentState")) {
            log.info("\"" + node.getName() + "\" publication lifecycle has been cleaned up");
            node.setProperty("publication:liveRevision", "");
            node.setProperty("publication:currentState", "published");
          }
        }
      }
      session.save();
      session.logout();
      if (log.isInfoEnabled()) {
        log.info(deploymentDescriptor.getSourcePath() + " is deployed succesfully into " + fullTargetNodePath);
      }
    }
  }

  @Override
  public void applicationActivated(SpaceLifeCycleEvent arg0) {}

  @Override
  public void applicationAdded(SpaceLifeCycleEvent arg0) {}

  @Override
  public void applicationDeactivated(SpaceLifeCycleEvent arg0) {}

  @Override
  public void applicationRemoved(SpaceLifeCycleEvent arg0) {}

  @Override
  public void grantedLead(SpaceLifeCycleEvent arg0) {}

  @Override
  public void joined(SpaceLifeCycleEvent arg0) {}

  @Override
  public void left(SpaceLifeCycleEvent arg0) {}

  @Override
  public void revokedLead(SpaceLifeCycleEvent arg0) {}

  @Override
  public void spaceRemoved(SpaceLifeCycleEvent arg0) {}
}
