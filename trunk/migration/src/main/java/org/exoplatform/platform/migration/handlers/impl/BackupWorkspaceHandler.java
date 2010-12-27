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
package org.exoplatform.platform.migration.handlers.impl;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.platform.migration.handlers.ComponentHandler;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.backup.BackupConfig;
import org.exoplatform.services.jcr.ext.backup.BackupManager;
import org.exoplatform.services.log.ExoLogger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform
 * haikel.thamri@exoplatform.com 4 août 2010
 */
public class BackupWorkspaceHandler implements ComponentHandler {

	private PortalContainer portalContainer;

	private RepositoryService repositoryService;

	private BackupManager backupManager;

	private Log log = ExoLogger.getLogger(this.getClass());

	public void invoke(Component component, String rootConfDir) {
		try {

			portalContainer = PortalContainer.getInstance();
			repositoryService = (RepositoryService) portalContainer
					.getComponentInstanceOfType(RepositoryService.class);
			backupManager = (BackupManager) portalContainer
					.getComponentInstanceOfType(BackupManager.class);
			preMarshallComponent(component, rootConfDir);
			Configuration configuration = new Configuration();
			configuration.addComponent(component);
			marshall(configuration, rootConfDir + File.separator + "portal"
					+ File.separator + component.getKey() + ".xml");
		} catch (Exception ie) {
			// TODO Auto-generated catch block
			log.error("error in the invoke method", ie);
		}
	}

	private void preMarshallComponent(Component component, String rootConfDir) {
		try {
			String backupPath = rootConfDir + File.separator + "backup";
			File backupDirectory = new File(backupPath);
			backupDirectory.mkdirs();

			String[] workspaceNames = repositoryService.getCurrentRepository()
					.getWorkspaceNames();
			for (String workspaceName : workspaceNames) {
				BackupConfig config = new BackupConfig();
				config.setBuckupType(BackupManager.FULL_AND_INCREMENTAL);
				config.setRepository(repositoryService.getCurrentRepository()
						.getConfiguration().getName());
				config.setWorkspace(workspaceName);
				config.setBackupDir(backupDirectory);
				config.setIncrementalJobPeriod(5000);
				backupManager.startBackup(config);
			}
		} catch (Exception ie) {
			log.error("problem in the preMarshall Process", ie);
		}
	}

	private void marshall(Object obj, String xmlPath) {
		try {
			IBindingFactory bfact = BindingDirectory.getFactory(obj.getClass());
			IMarshallingContext mctx = bfact.createMarshallingContext();
			mctx.setIndent(2);
			mctx.marshalDocument(obj, "UTF-8", null, new FileOutputStream(
					xmlPath));
		} catch (Exception ie) {
			log.error("Cannot convert the object to xml", ie);
		}
	}
}
