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
package org.exoplatform.platform.upgrade.plugins.test;

import java.io.InputStream;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.commons.upgrade.UpgradeProductService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.organization.integration.Util;
import org.exoplatform.platform.upgrade.plugins.UpgradeOrganizationIntegrationDataPlugin;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.test.BasicTestCase;

/**
 * @author <a href="kmenzli@exoplatform.com">Kmenzli</a>
 * @date 08/07/12
 */
public class TestOrganizationIntegrationUpgradePlugin extends BasicTestCase {

    private static final String ORGANIZATION_INITIALIZATIONS = "OrganizationIntegrationService";
    private static final String OLD_PRODUCT_INFORMATIONS_FILE = "classpath:/conf/product_old2.properties";

    private static final String COLLABORATION_WS = "collaboration";

    protected PortalContainer container;

    protected RepositoryService repositoryService;

    protected ProductInformations productInformations;

    protected ConfigurationManager configurationManager;

    public void setUp() throws Exception {
        container = PortalContainer.getInstance();
        repositoryService = getService(RepositoryService.class);
        productInformations = getService(ProductInformations.class);

        configurationManager = getService(ConfigurationManager.class);

        // replace PLF version by an old one in the jcr.
        Session session = null;
        try {
            InputStream oldVersionsContentIS = configurationManager.getInputStream(OLD_PRODUCT_INFORMATIONS_FILE);
            byte[] binaries = new byte[oldVersionsContentIS.available()];
            oldVersionsContentIS.read(binaries);
            String oldVersionsContent = new String(binaries);

            session = repositoryService.getCurrentRepository().getSystemSession(COLLABORATION_WS);

            Node plfVersionDeclarationNode = getProductVersionNode(session);
            Node plfVersionDeclarationContentNode = plfVersionDeclarationNode.getNode("jcr:content");
            plfVersionDeclarationContentNode.setProperty("jcr:data", oldVersionsContent);

            session.save();
            session.refresh(true);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
        // PLF version replaced by an old one in the jcr.

        importOrgIntegData();
    }

    public void testUpgrade() throws Exception {
        // replace PLF version by an old one in the jcr.
        Session session = null;
        try {
            session = repositoryService.getCurrentRepository().getSystemSession(COLLABORATION_WS);
            {
                assertTrue(session.itemExists(Util.HOME_PATH));
                Node homeNode = (Node) session.getItem(Util.HOME_PATH);
                assertTrue(homeNode.hasNode(Util.ORGANIZATION_INITIALIZATIONS));
                Node orgIntegrationParentNode = homeNode.getNode(Util.ORGANIZATION_INITIALIZATIONS);

                assertTrue(orgIntegrationParentNode.hasNode(Util.USERS_FOLDER));
                Node usersNode = orgIntegrationParentNode.getNode(Util.USERS_FOLDER);
                assertTrue(usersNode.hasNode(Util.MEMBERSHIPS_LIST_NODE_NAME));

                assertTrue(orgIntegrationParentNode.hasNode(Util.GROUPS_FOLDER));
                Node groupsNode = orgIntegrationParentNode.getNode(Util.GROUPS_FOLDER);
                assertTrue(groupsNode.hasNode(Util.MEMBERSHIPS_LIST_NODE_NAME));

                assertTrue(orgIntegrationParentNode.hasNode(Util.PROFILES_FOLDER));

                assertTrue(orgIntegrationParentNode.hasNode(Util.MEMBERSHIPS_FOLDER));
            }
            {
                assertTrue(session.itemExists(Util.HOME_PATH));
                Node homeNode = (Node) session.getItem(Util.HOME_PATH);
                assertFalse(homeNode.hasNode(ORGANIZATION_INITIALIZATIONS));
            }
            session.save();
            session.refresh(true);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    private void importOrgIntegData() throws Exception {
        Session session = null;
        try {
            session = repositoryService.getCurrentRepository().getSystemSession(COLLABORATION_WS);

            InputStream inputStream = configurationManager.getInputStream("classpath:/conf/jcr/OrgIntegData.xml");
            session.importXML("/", inputStream, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
            session.save();

            // Inject OrganizationIntegrationService and
            // UpgradeOrganizationIntegrationDataPlugin into PortalContainer on
            // runtime
            configurationManager.addConfiguration("classpath:/conf/organization-integration-configuration.xml");
            InitParams params = configurationManager.getComponent(UpgradeOrganizationIntegrationDataPlugin.class).getInitParams();
            UpgradeOrganizationIntegrationDataPlugin organizationIntegrationDataPlugin = container.createComponent(
                    UpgradeOrganizationIntegrationDataPlugin.class, params);
            organizationIntegrationDataPlugin.setName("Upgrade-OrganizationItegration");
            UpgradeProductService upgradeProductService = (UpgradeProductService) container
                    .getComponentInstanceOfType(UpgradeProductService.class);
            upgradeProductService.addUpgradePlugin(organizationIntegrationDataPlugin);
        } finally {
            if (session != null) {
                session.logout();
            }
        }

        // Upgrade the OrganizationIntegrationService data
        UpgradeProductService upgradeService = getService(UpgradeProductService.class);
        productInformations.start();
        upgradeService.start();
    }

    protected <T> T getService(Class<T> clazz) {
        return clazz.cast(container.getComponentInstanceOfType(clazz));
    }

    private Node getProductVersionNode(Session session) throws PathNotFoundException, RepositoryException {
        Node plfVersionDeclarationNodeContent = ((Node) session
                .getItem("/Application Data/" + ProductInformations.UPGRADE_PRODUCT_SERVICE_NODE_NAME + "/"
                        + ProductInformations.PRODUCT_VERSION_DECLARATION_NODE_NAME));
        return plfVersionDeclarationNodeContent;
    }
}
