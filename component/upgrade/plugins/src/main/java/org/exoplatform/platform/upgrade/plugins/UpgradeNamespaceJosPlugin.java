/*
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

import java.io.InputStream;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.nodetype.ExtendedNodeTypeManager;
import org.exoplatform.services.jcr.impl.core.ExtendedNamespaceRegistry;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="kmenzli@exoplatform.com">Khemais MENZLI</a>
 * Date: 30/08/12
 * Time: 12:36
 */
public class UpgradeNamespaceJosPlugin extends UpgradeProductPlugin {
    private static final Log LOG = ExoLogger.getLogger(UpgradeNamespaceJosPlugin.class);
    private RepositoryService repositoryService;
    private ConfigurationManager configurationManager;

    public UpgradeNamespaceJosPlugin(InitParams initParams, ConfigurationManager configurationManager,
                                     RepositoryService repositoryService) throws Exception {

        super(initParams);
        this.configurationManager = configurationManager;
        this.repositoryService = repositoryService;
    }

    @Override
    public void processUpgrade(String oldVersion, String newVersion) {
        LOG.info("Start UpgradeNamespaceJosPlugin ...");

        try {
            ExtendedNamespaceRegistry namespaceRegistry = (ExtendedNamespaceRegistry) repositoryService.getCurrentRepository().getNamespaceRegistry();
            ExtendedNodeTypeManager nodeTypeManager = (ExtendedNodeTypeManager) repositoryService.getCurrentRepository().getNodeTypeManager();

            String oldNamespaceURL = namespaceRegistry.getNamespaceURIByPrefix("jos");
            if(oldNamespaceURL.equalsIgnoreCase("http://www.exoplatform.com/jcr-services/organization-service/1.0/") == false)
            {
                boolean hasNodeType = false;
                //if there are not any nodetype use 'jos' namespace, unregister 'jos'
                try {
                    // Unregister old namespace
                    namespaceRegistry.unregisterNamespace("jos");
                    // Register new namespace
                    namespaceRegistry.registerNamespace("jos", "http://www.exoplatform.com/jcr-services/organization-service/1.0/");
                } catch (Exception e) {
                    hasNodeType = true;
                    LOG.info("Need unregister nodetypes first...");
                }
                if(hasNodeType){
                    // Unregister NodeTypes
                    try {
                        nodeTypeManager.unregisterNodeType("jos:membershipType");
                    } catch (Exception e) {
                        LOG.info("jos:membershipType does not exist...");
                    }
                    try {
                        nodeTypeManager.unregisterNodeType("jos:userMembership");
                    } catch (Exception e) {
                        LOG.info("jos:userMembership does not exist...");
                    }
                    try {
                        nodeTypeManager.unregisterNodeType("jos:profileAttributes");
                    } catch (Exception e) {
                        LOG.info("jos:profileAttributes does not exist...");
                    }
                    try {
                        nodeTypeManager.unregisterNodeType("jos:userProfile");
                    } catch (Exception e) {
                        LOG.info("jos:userMembership does not exist...");
                    }
                    try {
                        nodeTypeManager.unregisterNodeType("jos:user");
                    } catch (Exception e) {
                        LOG.info("jos:user does not exist...");
                    }
                    try {
                        nodeTypeManager.unregisterNodeType("jos:hierarchyGroup");
                    } catch (Exception e) {
                        LOG.info("jos:hierarchyGroup does not exist...");
                    }
                    try {
                        nodeTypeManager.unregisterNodeType("jos:organizationUsers");
                    } catch (Exception e) {
                        LOG.info("jos:userMembership does not exist...");
                    }
                    try {
                        nodeTypeManager.unregisterNodeType("jos:organizationGroups");
                    } catch (Exception e) {
                        LOG.info("jos:organizationGroups does not exist...");
                    }
                    try {
                        nodeTypeManager.unregisterNodeType("jos:organizationMembershipTypes");
                    } catch (Exception e) {
                        LOG.info("jos:organizationMembershipTypes does not exist...");
                    }
                    try {
                        nodeTypeManager.unregisterNodeType("jos:organizationStorage");
                    } catch (Exception e) {
                        LOG.info("jos:organizationStorage does not exist...");
                    }
                    try {
                        nodeTypeManager.unregisterNodeType("jos:group");
                    } catch (Exception e) {
                        LOG.info("jos:group does not exist...");
                    }

                    // Unregister old namespace
                    namespaceRegistry.unregisterNamespace("jos");
                    // Register new namespace
                    namespaceRegistry.registerNamespace("jos", "http://www.exoplatform.com/jcr-services/organization-service/1.0/");

                    // Re-register NodeTypes
                    InputStream inputStream = configurationManager.getInputStream("jar:/conf/organization-nodetypes.xml");
                    nodeTypeManager.registerNodeTypes(inputStream, ExtendedNodeTypeManager.REPLACE_IF_EXISTS, "text/xml");
                }

                LOG.info("Perform UpgradeNamespaceJosPlugin done...");
            }
            else
            {
                LOG.info("Nothing to run UpgradeNamespaceJosPlugin ...");
            }

            LOG.info("Finish UpgradeNamespaceJosPlugin ...");
        }
        catch (Exception e) {
            LOG.error("UpgradeNamespaceJosPlugin: Upgrade Jos namespace failure", e);
        }
    }

    @Override
    public boolean shouldProceedToUpgrade(String previousVersion, String newVersion) {
        return true;
    }
}
