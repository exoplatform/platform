/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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
package org.exoplatform.platform.cloud.services.multitenancy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.exoplatform.cloudmanagement.multitenancy.TenantCreationException;
import org.exoplatform.cloudmanagement.multitenancy.TenantSuspendException;
import org.exoplatform.cloudmanagement.rest.TenantService;
import org.exoplatform.cloudmanagement.status.TenantStatus;
import org.exoplatform.cloudmanagement.status.TransientTenantStatus;
import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.RepositoryServiceConfiguration;
import org.exoplatform.services.jcr.ext.repository.creation.RepositoryCreationException;
import org.exoplatform.services.jcr.ext.repository.creation.RepositoryCreationService;
import org.exoplatform.services.naming.InitialContextInitializer;
import org.jibx.runtime.JiBXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reimplement suspend and resume methods to take in account JCR Value Storage.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:pnedonosko@exoplatform.com">Peter Nedonosko</a>
 * @version $Id: CreateNewRepositoryPlugin.java 00001 2011-09-05 10:06:10Z pnedonosko $
 */
public class CreateNewRepositoryPlugin extends
                                      org.exoplatform.cloudmanagement.multitenancy.CreateNewRepositoryPlugin {

  /**
   * Class logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(CreateNewRepositoryPlugin.class);


  /**
   * @param configurationService
   * @param repositoryCreationService
   * @param priority
   * @param repositoryConfigurationPath
   * @param repositoryService
   * @param initialContextInitializer
   */
  public CreateNewRepositoryPlugin(ConfigurationManager configurationService,
                                   RepositoryCreationService repositoryCreationService,
                                   int priority,
                                   String repositoryConfigurationPath,
                                   RepositoryService repositoryService,
                                   InitialContextInitializer initialContextInitializer) {
    super(configurationService,
          repositoryCreationService,
          priority,
          repositoryConfigurationPath,
          repositoryService,
          initialContextInitializer);
  }

  /**
   * @param params
   * @param repositoryCreationService
   * @param configurationService
   * @param repositoryService
   * @param initialContextInitializer
   * @throws ConfigurationException
   */
  public CreateNewRepositoryPlugin(InitParams params,
                                   RepositoryCreationService repositoryCreationService,
                                   ConfigurationManager configurationService,
                                   RepositoryService repositoryService,
                                   InitialContextInitializer initialContextInitializer) throws ConfigurationException {
    super(params,
          repositoryCreationService,
          configurationService,
          repositoryService,
          initialContextInitializer);
  }
  
  // ***** Reimplement suspend and resume to take JCR Value Storage in account *****
  
  public void resume(TransientTenantStatus tenantStatus, InputStream io) throws TenantCreationException
  {
     LOG.info("Tenant resuming. Create new repository stage for tenant '" + tenantStatus.getTenantName() + "'");
     InputStream configuration = null;
     try
     {
        configuration = configurationService.getInputStream(path);
        if (configuration == null)
        {
           configuration = CreateNewRepositoryPlugin.class.getResourceAsStream(path);
        }
        if (configuration == null)
        {
           throw new TenantCreationException("Fail to load tenant configuration from " + path);
        }

        Map<String, Object> props = new HashMap<String, Object>(2);
        props.put("tenant.repository.name", tenantStatus.getTenantName());
        props.put("gatein.tenant.repository.name", tenantStatus.getTenantName());

        RepositoryServiceConfiguration conf =
           TenantService.unmarshalRepositoryServiceConfiguration(configuration, props);
        RepositoryEntry repositoryEntry = conf.getRepositoryConfiguration(tenantStatus.getTenantName());

        // extract tenant resources
        PlatformTenantResourcesManager tenantResourcesManager = new PlatformTenantResourcesManager(repositoryEntry);
        tenantResourcesManager.deserialize(io, initialContextInitializer);

        repositoryService.createRepository(repositoryEntry);
        repositoryService.getConfig().retain();

     }
     catch (JiBXException e)
     {
        throw new TenantCreationException(e.getLocalizedMessage(), e);
     }
     catch (RepositoryConfigurationException e)
     {
        throw new TenantCreationException(e.getLocalizedMessage(), e);
     }
     catch (RepositoryCreationException e)
     {
        throw new TenantCreationException(e.getLocalizedMessage(), e);
     }
     catch (IOException e)
     {
        throw new TenantCreationException(e.getLocalizedMessage(), e);
     }
     catch (Exception e)
     {
        throw new TenantCreationException("Fail to resume tenant " + (tenantStatus != null ? tenantStatus.getTenantName() 
                + " on app server " + tenantStatus.getApServerAlias() : "<null>"), e);
     }
     finally
     {
        if (configuration != null)
        {
           try
           {
              configuration.close();
           }
           catch (IOException e)
           {
              throw new TenantCreationException(e.getLocalizedMessage(), e);
           }
        }
     }
  }

  @Override
  public void suspend(TransientTenantStatus tenantStatus) throws TenantSuspendException
  {
     try
     {
        RepositoryEntry repositoryEntry =
           repositoryService.getConfig().getRepositoryConfiguration(tenantStatus.getTenantName());

        // store repository DataSource configuration before its removing 
        PlatformTenantResourcesManager tenantResourcesManager = new PlatformTenantResourcesManager(repositoryEntry);
        tenantResourcesManager.initSerialization();
        tenantResourcesManager.serializeDataSource(initialContextInitializer);

        repositoryCreationService.removeRepository(tenantStatus.getTenantName(), true);

        // store repository index, values and logs 
        tenantResourcesManager.serializeIndex();
        tenantResourcesManager.serializeValues();
        tenantResourcesManager.serializeLogs();

        File indexZip = tenantResourcesManager.completeSerialization();
        tenantStatus.setProperty(TenantStatus.PROPERTY_RESOURCES_ZIP, indexZip.getPath());

        LOG.info("Removing resources for tenant " + tenantStatus.getTenantName());
        tenantResourcesManager.cleanTenantResources();
     }
     catch (RepositoryCreationException e)
     {
        throw new TenantSuspendException(e);
     }
     catch (RepositoryConfigurationException e)
     {
        throw new TenantSuspendException(e);
     }
     catch (IOException e)
     {
        throw new TenantSuspendException(e);
     }
     catch (ParserConfigurationException e)
     {
        throw new TenantSuspendException(e);
     }
     catch (TransformerException e)
     {
        throw new TenantSuspendException(e);
     }
     catch (Exception e)
     {
       throw new TenantSuspendException("Fail to suspend tenant " + (tenantStatus != null ? tenantStatus.getTenantName() 
                                                 + " from app server " + tenantStatus.getApServerAlias() : "<null>"), e); 
     }

  }

  
}
