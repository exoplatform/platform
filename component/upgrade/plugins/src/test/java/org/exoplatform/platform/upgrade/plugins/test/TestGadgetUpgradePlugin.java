/**
 * Copyright (C) 2009 eXo Platform SAS.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.application.gadget.EncodingDetector;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.gadget.Source;
import org.exoplatform.application.gadget.SourceStorage;
import org.exoplatform.application.gadget.impl.GadgetDefinition;
import org.exoplatform.application.gadget.impl.GadgetRegistryServiceImpl;
import org.exoplatform.application.gadget.impl.LocalGadgetData;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.commons.upgrade.UpgradeProductService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.platform.upgrade.plugins.LocalGadgetImporter;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.test.BasicTestCase;
import org.gatein.common.io.IOTools;

public class TestGadgetUpgradePlugin extends BasicTestCase {
  private static final Log log = ExoLogger.getLogger(TestGadgetUpgradePlugin.class);

  private GadgetRegistryServiceImpl gadgetRegistryService;

  private static final String OLD_PRODUCT_INFORMATIONS_FILE = "classpath:/conf/product_old.properties";

  private static final String OLD_GADGET_URL = "classpath:/conf/gadgets/gadget1/test.xml";
  private static final String NEW_GADGET_URL = "classpath:/conf/gadgets/gadget2/test.xml";

  private static final String GADGET_NAME = "test";

  protected final String REPO_NAME = "repository";

  protected final String COLLABORATION_WS = "collaboration";

  protected PortalContainer container;

  protected RepositoryService repositoryService;

  protected ProductInformations productInformations;

  protected ConfigurationManager configurationManager;

  protected SourceStorage sourceStorage;

  public void setUp() throws Exception {
    container = PortalContainer.getInstance();
    repositoryService = getService(RepositoryService.class);
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
    sourceStorage = (SourceStorage) container.getComponentInstanceOfType(SourceStorage.class);
    gadgetRegistryService = (GadgetRegistryServiceImpl) container.getComponentInstanceOfType(GadgetRegistryService.class);
    ChromatticLifeCycle lifeCycle = gadgetRegistryService.getChromatticLifeCycle();
    lifeCycle.openContext();

    LocalGadgetImporter gadgetImporter = new LocalGadgetImporter(GADGET_NAME, gadgetRegistryService.getRegistry(),
        OLD_GADGET_URL, configurationManager);
    gadgetImporter.doImport();

    Source source = null;
    int i = 0;
//    while (i++ < 100) {
      try {
        GadgetDefinition gadgetDefinition = gadgetRegistryService.getRegistry().getGadget(GADGET_NAME);
        log.info("Gadget name : " + gadgetDefinition.getName());
        log.info("Gadget Reference URL : " + gadgetDefinition.getReferenceURL());
        log.info("Gadget file name : " + ((LocalGadgetData) gadgetDefinition.getData()).getFileName());
        log.info("Gadget children : " + ((LocalGadgetData) gadgetDefinition.getData()).getResources().getChildren());
        log.info("Gadget source : " + ((LocalGadgetData) gadgetDefinition.getData()).getSource());

        source = sourceStorage.getSource(gadgetRegistryService.getGadget(GADGET_NAME));
      } catch (Exception exception) {
        log.error("Can't get gadget content, iteration = " + i);
      }
//    }
    if (source == null) {
      throw new IllegalStateException("Can't get gadget content");
    }
    assertEquals(getFileContent(OLD_GADGET_URL), source.getTextContent());

    lifeCycle.closeContext(true);

    // invoke productInformations() explicitly to store the new version in the JCR
    productInformations.start();
    upgradeService.start();
  }

  public void testUpgrade() throws Exception {
    ChromatticLifeCycle lifeCycle = gadgetRegistryService.getChromatticLifeCycle();
    lifeCycle.openContext();

    Source source = sourceStorage.getSource(gadgetRegistryService.getGadget(GADGET_NAME));
    assertEquals(getFileContent(NEW_GADGET_URL), source.getTextContent());

    lifeCycle.closeContext(true);
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

  public String getFileContent(String filePath) throws IOException {
    InputStream in;
    try {
      if (!filePath.startsWith("classpath:") && !filePath.startsWith("jar:") && !filePath.startsWith("war:")
          && !filePath.startsWith("system:")) {
        filePath = "file:/" + filePath;
      }
      in = configurationManager.getInputStream(filePath);
      byte[] bytes = IOTools.getBytes(in);
      String encoding = EncodingDetector.detect(new ByteArrayInputStream(bytes));
      return new String(bytes, encoding);
    } catch (Exception exception) {
      return null;
    }
  }

}