/**
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.core.nodetype.ExtendedNodeTypeManager;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

public class UpgradeNodeTypeSpaceAccessPlugin extends UpgradeProductPlugin {

    private static final Log LOG = ExoLogger.getLogger(UpgradeNodeTypeSpaceAccessPlugin.class);
    private static final String NT_PLF_SPACEACCESS = "plf:spaceaccess";
    private RepositoryService repositoryService;
    private static final String DEFAULT_WS = "collaboration";
    String queryStatement = "SELECT * FROM "+NT_PLF_SPACEACCESS+" WHERE (jcr:path LIKE '/Users/%') order by exo:dateModified ASC";


    public UpgradeNodeTypeSpaceAccessPlugin(InitParams initParams, RepositoryService repositoryService) throws Exception {

        super(initParams);
        this.repositoryService = repositoryService;
    }

    @Override
    public void processUpgrade(String oldVersion, String newVersion) {

        LOG.info("Start UpgradeNodeTypeSpaceAccessPlugin ...");

        try {

           ExtendedNodeTypeManager nodeTypeManager = (ExtendedNodeTypeManager) repositoryService.getCurrentRepository().getNodeTypeManager();

            try {

                //--- remove all nodes with nodeType : plf:spaceaccess
                removeSpaceAccessNodeInstances(queryStatement);

                //--- Unregister the node-type plf:spaceaccess
                nodeTypeManager.unregisterNodeType(NT_PLF_SPACEACCESS);

            }  catch (NoSuchNodeTypeException NSNTE) {

                LOG.error("There is no registered node type with the specified name[" + NT_PLF_SPACEACCESS + "]");

            } catch (RepositoryException RE) {

                LOG.error("Can not remove ["+NT_PLF_SPACEACCESS+"] nodetype, please check nodes instances with type ["+NT_PLF_SPACEACCESS+"]");

            } catch (Exception E) {

                LOG.error("The upgrade plugin can not proceed to remove the node type ["+NT_PLF_SPACEACCESS+"]",E);
            }

        }  catch (Exception e) {

            LOG.error("UpgradeNodeTypeSpaceAccessPlugin: Upgrade Space nodeTypes failure", e);
        }

        LOG.info("Finish UpgradeNodeTypeSpaceAccessPlugin ...");

    }

    @Override
    public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
        // --- return true anly for the first version of platform
        return VersionComparator.isAfter(newVersion,previousVersion);
    }

    /**
     * Remove all nodes creating using the nodetype plf:spaceaccess
     * @return
     */
    private boolean removeSpaceAccessNodeInstances (String theQuery) {

        boolean isNodesExists = true;

        QueryResult result = null;

        try {

            //--- Execute the search query
            result = getSpaceAccessNodeInstances(theQuery);

            //--- Remove each node found with Type plf:spaceAccess
            NodeIterator iter = result.getNodes();

            Node spaceAccessNode = null;

            if (iter.getSize() > 0 ) {

                LOG.info("Remove nodes registered using the Node-Type ["+NT_PLF_SPACEACCESS+"] ");

                while (iter.hasNext()) {
                    spaceAccessNode = iter.nextNode();
                    spaceAccessNode.remove();
                    spaceAccessNode.getSession().save();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Remove node [" + spaceAccessNode.getName() +"]");
                    }
                }
            }

        } catch (Exception E) {

            isNodesExists = false;

            LOG.error("Failed to remove nodes SpaceAccess ", E);

        }

        //--- return
        return isNodesExists;

    }

    /**
     * Get all node  created using the nodetype plf:spaceaccess
     * @param theQuery query to be performed
     * @return
     */
    private QueryResult getSpaceAccessNodeInstances (String theQuery) {

        Query query = null;

        QueryResult result = null;

        Session session = null;

        try {

            session = getSession(false);

            QueryManager manager = session.getWorkspace().getQueryManager();

            query = manager.createQuery(theQuery, Query.SQL);

            result = query.execute();

        } catch (Exception E) {

            LOG.error("Can not execute the jcr query ["+theQuery+"] ", E);

        }
        return result;

    }

    /**
     * Get jcr session (using the default workspace collaboration)
     * @param active
     * @return
     */
    private Session getSession (boolean active ) {

        Session session = null;

        try {
            SessionProvider sProvider = CommonsUtils.getSystemSessionProvider();

            ManageableRepository manageableRepository = repositoryService.getCurrentRepository();

            session = sProvider.getSession(DEFAULT_WS, manageableRepository);

        } catch (Exception E) {

            LOG.error("Can not get the jcr session ", E);

        }
        return session;

    }

}
