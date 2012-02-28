/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.platform.migration.bos.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.platform.migration.bos.exporter.SystemWorkspaceStreamExporter;
import org.exoplatform.platform.migration.bos.exporter.WorkspaceStreamExporter;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.core.WorkspaceContainerFacade;
import org.exoplatform.services.jcr.datamodel.ItemData;
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.jcr.ext.backup.BackupConfig;
import org.exoplatform.services.jcr.ext.backup.BackupManager;
import org.exoplatform.services.jcr.ext.backup.impl.FileNameProducer;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.jcr.impl.core.NamespaceRegistryImpl;
import org.exoplatform.services.jcr.impl.core.SessionDataManager;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.jcr.impl.core.value.ValueFactoryImpl;
import org.exoplatform.services.jcr.impl.dataflow.persistent.LocalWorkspaceDataManagerStub;
import org.exoplatform.services.jcr.impl.util.io.FileCleanerHolder;
import org.exoplatform.services.jcr.impl.xml.exporting.BaseXmlExporter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.xml.sax.SAXException;

/**
 * Created by The eXo Platform MEA Author : Anouar Chattouna anouar.chattouna@exoplatform.com June 03, 2011
 */

@Managed
@ManagedDescription("BOS Backup Service")
@NameTemplate( { @Property(key = "name", value = "BOSBackupService"), @Property(key = "service", value = "bonita-ext"),
    @Property(key = "type", value = "platform") })
public class BOSBackupService implements Startable {

  private RepositoryService repositoryService;
  private RepositoryEntry defaultRepositoryEntry;
  private String defaultRepositoryName;
  private final Log logger = ExoLogger.getLogger(this.getClass().getName());

  public BOSBackupService(RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  @Managed
  @ManagedDescription("Exports the repository data to a backup location")
  /**
   * Executes the repository backup.
   * 
   * @param backupLocation
   */
  public void doBackup(@ManagedName("backupLocation") String backupLocation) {

    logger.info("Starting the backup operation.. Repository: " + defaultRepositoryName);
    try {
      // in a standalone container there is one repository, the default one.
      ManageableRepository repository = repositoryService.getDefaultRepository();
      ArrayList<WorkspaceEntry> workspaceEntries = defaultRepositoryEntry.getWorkspaceEntries();
      String systemWorkspaceName = defaultRepositoryEntry.getSystemWorkspaceName();
      for (WorkspaceEntry workspace : workspaceEntries) {
        String workspaceName = workspace.getName();
        SessionImpl session = (SessionImpl) repository.getSystemSession(workspaceName);
        // set the backup configuration options
        BackupConfig config = new BackupConfig();
        config.setBackupType(BackupManager.FULL_BACKUP_ONLY);
        config.setRepository(defaultRepositoryEntry.getName());
        config.setWorkspace(workspaceName);
        // make sure that the backupLocation is well set.
        if (backupLocation != null) {
          config.setBackupDir(new File(backupLocation + File.separator + workspaceName));
        } else {
          logger.warn("Bachup operation aborted...");
          throw new IOException("The backupLocation field should not be empty!");
        }
        Calendar timeStamp = Calendar.getInstance();
        URL url = createStorage(config, timeStamp);
        FileOutputStream fos = new FileOutputStream(url.getFile());
        try {
          WorkspaceContainerFacade workspaceContainer = repository.getWorkspaceContainer(workspaceName);
          LocalWorkspaceDataManagerStub workspaceDataManager = (LocalWorkspaceDataManagerStub) workspaceContainer
              .getComponent(LocalWorkspaceDataManagerStub.class);
          SessionDataManager dataManager = new SessionDataManager(session, workspaceDataManager);
          LocationFactory factory = new LocationFactory(((NamespaceRegistryImpl) repository.getNamespaceRegistry()));
          WorkspaceEntry wsConfig = (WorkspaceEntry) workspaceContainer.getComponent(WorkspaceEntry.class);
          FileCleanerHolder cleanerHolder = (FileCleanerHolder) workspaceContainer.getComponent(FileCleanerHolder.class);
          ValueFactoryImpl valueFactoryImpl = new ValueFactoryImpl(factory, wsConfig, cleanerHolder);
          XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
          XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(fos, Constants.DEFAULT_ENCODING);
          ItemData srcItemData = dataManager.getItemData(Constants.ROOT_UUID);
          if (srcItemData == null) {
            throw new PathNotFoundException("Root node not found");
          }
          if (!workspace.getName().equals(systemWorkspaceName)) {
            // export data from the workspace as system view
            // session.exportWorkspaceSystemView(fos, false, false);
            BaseXmlExporter exporter = new WorkspaceStreamExporter(streamWriter, dataManager, repository.getNamespaceRegistry(),
                valueFactoryImpl, false, false);
            exporter.export((NodeData) srcItemData);        
          } else {
            // if the workspace is the system workspace, proceed differently:
            // not all contents will be exported, but only the Version History nodes
            BaseXmlExporter exporter = new SystemWorkspaceStreamExporter(streamWriter, dataManager, repository
                .getNamespaceRegistry(), valueFactoryImpl, false, false);
            exporter.export((NodeData) srcItemData);
          }
        } catch (XMLStreamException e) {
          logger.error("Full backup failed... " + e);
        } catch (SAXException e) {
          logger.error("Full backup failed... " + e);
        } finally {
          session.logout();
        }
        logger.info("Full backup succeeded: " + url.getPath());
      }
    } catch (IOException e) {
      logger.error("Full backup failed... " + e);
    } catch (RepositoryException e) {
      logger.error("Full backup failed... " + e);
    } catch (RepositoryConfigurationException e) {
      logger.error("Full backup failed... " + e);
    }
  }

  private URL createStorage(BackupConfig config, Calendar timeStamp) throws IOException {
    FileNameProducer fileNameProducer = new FileNameProducer(config.getRepository(), config.getWorkspace(), config.getBackupDir()
        .getAbsolutePath(), timeStamp, true);
    return new URL("file:" + fileNameProducer.getNextFile().getAbsolutePath());
  }

  public void start() {
    logger.info("BOS Backup Service started");
    defaultRepositoryName = repositoryService.getConfig().getDefaultRepositoryName();
    try {
      defaultRepositoryEntry = repositoryService.getConfig().getRepositoryConfiguration(defaultRepositoryName);
    } catch (RepositoryConfigurationException e) {
      logger.error("Cannot proceed BOS Backup Service initialization... " + e);
    }
  }

  public void stop() {
    logger.info("BOS Backup Service stopped");
  }
}
