/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ecms.upgrade.test;

import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;

import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.commons.upgrade.UpgradeProductService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.portal.webui.util.SessionProviderFactory;
import org.exoplatform.services.cms.templates.TemplateService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.test.BasicTestCase;

/**
 * @author Boubaker KHANFIR
 */
public class TestECMSUpgrade extends BasicTestCase {
  private static final String OLD_PRODUCT_INFORMATIONS_FILE = "classpath:/conf/product_old.properties";

  private static final String OLD_VERSION = "1.0-old";

  protected final String REPO_NAME = "repository";

  protected final String COLLABORATION_WS = "collaboration";

  protected PortalContainer container;

  protected RepositoryService repositoryService;

  protected TemplateService templateService;

  protected ProductInformations productInformations;

  protected ConfigurationManager configurationManager;
  
  public void setUp() throws Exception {
    container = PortalContainer.getInstance();
    repositoryService = getService(RepositoryService.class);
    templateService = getService(TemplateService.class);
    productInformations = getService(ProductInformations.class);
    UpgradeProductService upgradeService = getService(UpgradeProductService.class);
    configurationManager = getService(ConfigurationManager.class);
    Session session = null;
    try {
      InputStream oldVersionsContentIS = configurationManager.getInputStream(OLD_PRODUCT_INFORMATIONS_FILE);
      byte[] binaries = new byte[oldVersionsContentIS.available()];
      oldVersionsContentIS.read(binaries);
      String oldVersionsContent = new String(binaries);

      session = repositoryService.getRepository(REPO_NAME).getSystemSession(COLLABORATION_WS);

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
    // invoke productInformations() explicitly to store the new version in the JCR
    productInformations.start();
    upgradeService.start();
  }

  public void testUpgradeNodeTypesTemplates() throws Exception {
    templateService.getAllDocumentNodeTypes();
    NodeIterator iter = templateService.getAllTemplatesOfNodeType(true, "nt:file", SessionProviderFactory.createSystemProvider());
    while (iter.hasNext()) {
      Node node = (Node) iter.next();
      assertTrue(node.isNodeType(ProductInformations.MIX_VERSIONABLE));

      Version version = node.getBaseVersion();
      assertNotNull(version);

      String[] versionLabels = node.getVersionHistory().getVersionLabels(version);
      assertNotNull(versionLabels);
      assertEquals(versionLabels.length, 1);
      assertEquals(versionLabels[0], OLD_VERSION);
    }
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
