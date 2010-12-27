/*
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
package org.exoplatform.platform.migration.aio.backup;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.exoplatform.platform.migration.aio.backup.exporter.CollaborationWorkspaceStreamExporter;
import org.exoplatform.platform.migration.aio.backup.exporter.SystemWorkspaceStreamExporter;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.core.WorkspaceContainerFacade;
import org.exoplatform.services.jcr.datamodel.ItemData;
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.jcr.ext.backup.BackupConfig;
import org.exoplatform.services.jcr.ext.backup.BackupJob;
import org.exoplatform.services.jcr.ext.backup.impl.fs.FileNameProducer;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.jcr.impl.core.NamespaceRegistryImpl;
import org.exoplatform.services.jcr.impl.core.SessionDataManager;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.jcr.impl.core.value.ValueFactoryImpl;
import org.exoplatform.services.jcr.impl.dataflow.persistent.LocalWorkspaceDataManagerStub;
import org.exoplatform.services.jcr.impl.util.io.WorkspaceFileCleanerHolder;
import org.exoplatform.services.jcr.impl.xml.exporting.BaseXmlExporter;
import org.exoplatform.services.log.ExoLogger;
import org.xml.sax.SAXException;

public class BackupService {

  private static final String SYSTEM_WS_NAME = "system";

  private static final String COLLABORATION_WS_NAME = "collaboration";

  protected RepositoryService repoService;

  public BackupService(RepositoryService repoService) {
    this.repoService = repoService;
  }

  public final int getType() {
    return BackupJob.FULL;
  }

  protected static Log log = ExoLogger.getLogger(BackupService.class);

  protected URL createStorage(BackupConfig config, Calendar timeStamp) throws FileNotFoundException, IOException {
    FileNameProducer fnp = new FileNameProducer(config.getRepository(), config.getWorkspace(), config.getBackupDir().getAbsolutePath(), timeStamp, true);
    return new URL("file:" + fnp.getNextFile().getAbsolutePath());
  }

  public void startBackupWorkspace(final BackupConfig config) {
    new Thread() {
      @Override
      public void run() {
        try {
          log.info(config.getWorkspace() + " backup init!");
          Calendar timeStamp = Calendar.getInstance();
          URL url = createStorage(config, timeStamp);
          final FileOutputStream fos = new FileOutputStream(url.getFile());
          exportWorkspace(fos, config.getRepository(), config.getWorkspace(), false, false);
          fos.close();
          log.info(config.getWorkspace() + " backup done successfully!");
        } catch (RepositoryException e) {
          log.error("Full backup initialization failed ", e);
        } catch (IOException e) {
          log.error("Full backup failed for workspace : " + config.getWorkspace(), e);
        }
      }
    }.start();
  }

  private void exportWorkspace(OutputStream out, String repositoryName, String workspaceName, boolean skipBinary, boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException {
    try {
      ManageableRepository repository = (ManageableRepository) repoService.getRepository(repositoryName);
      SessionImpl session = (SessionImpl) repository.getSystemSession(workspaceName);
      WorkspaceContainerFacade workspaceContainer = repository.getWorkspaceContainer(workspaceName);
      LocalWorkspaceDataManagerStub workspaceDataManager = (LocalWorkspaceDataManagerStub) workspaceContainer.getComponent(LocalWorkspaceDataManagerStub.class);
      SessionDataManager dataManager = new SessionDataManager(session, workspaceDataManager);

      LocationFactory factory = new LocationFactory(((NamespaceRegistryImpl) repository.getNamespaceRegistry()));
      WorkspaceEntry wsConfig = (WorkspaceEntry) workspaceContainer.getComponent(WorkspaceEntry.class);
      WorkspaceFileCleanerHolder cleanerHolder = (WorkspaceFileCleanerHolder) workspaceContainer.getComponent(WorkspaceFileCleanerHolder.class);
      ValueFactoryImpl valueFactoryImpl = new ValueFactoryImpl(factory, wsConfig, cleanerHolder);

      XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
      XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(out, Constants.DEFAULT_ENCODING);
      BaseXmlExporter exporter = null;
      if (workspaceName.equals(COLLABORATION_WS_NAME)) {
        exporter = new CollaborationWorkspaceStreamExporter(streamWriter, dataManager, repository.getNamespaceRegistry(), valueFactoryImpl, skipBinary, noRecurse);
      } else if (workspaceName.equals(SYSTEM_WS_NAME)) {
        exporter = new SystemWorkspaceStreamExporter(streamWriter, dataManager, repository.getNamespaceRegistry(), valueFactoryImpl, skipBinary, noRecurse);
      } else {
        throw new RuntimeException("Unknown Workspace name. No backup Job launched for workspace : " + workspaceName);
      }
      ItemData srcItemData = dataManager.getItemData(Constants.ROOT_UUID);
      if (srcItemData == null) {
        throw new PathNotFoundException("Root node not found");
      }
      exporter.export((NodeData) srcItemData);
    } catch (XMLStreamException e) {
      throw new IOException(e.getLocalizedMessage());
    } catch (SAXException e) {
      throw new IOException(e.getLocalizedMessage());
    } catch (RepositoryConfigurationException e) {
      log.error("Full backup initialization failed ", e);
    }
  }

}