/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.upgrade.plugins;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.UserStatus;
import org.exoplatform.services.organization.idm.PicketLinkIDMOrganizationServiceImpl;
import org.exoplatform.services.organization.idm.PicketLinkIDMService;
import org.exoplatform.services.organization.idm.UserDAOImpl;
import org.exoplatform.services.organization.impl.UserImpl;
import org.picketlink.idm.api.Attribute;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.impl.api.SimpleAttribute;

public class DisableUserUpgradePlugin extends UpgradeProductPlugin {

  private static final Log LOG = ExoLogger.getLogger(DisableUserUpgradePlugin.class.getName());
  
  private OrganizationService service;  
  
  private int threadNum = 20;
  
  private int batchSize = 100;
  
  public static final String THREAD_CONFIG = "numberOfThreads";
  
  public static final String BATCH_CONFIG = "batchSize";

  private static final String VERSION = "4.3";

  public DisableUserUpgradePlugin(OrganizationService service, InitParams initParams) {
    super(initParams);
    if (initParams.containsKey(THREAD_CONFIG)) {
      int tn = threadNum;
      try {
        tn = Integer.parseInt(initParams.getValueParam(THREAD_CONFIG).getValue());
      } catch (Exception ex) {        
      }
      if (tn > 0) {
        threadNum = tn;
      }
    }
    if (initParams.containsKey(BATCH_CONFIG)) {
      int b = threadNum;
      try {
        b = Integer.parseInt(initParams.getValueParam(BATCH_CONFIG).getValue());
      } catch (Exception ex) {        
      }
      if (b > 0) {
        batchSize = b;
      }
    }
    this.service = service;    
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    LOG.info("Start {} .............", this.getClass().getName());
    
    if (service instanceof PicketLinkIDMOrganizationServiceImpl) {
      final PicketLinkIDMOrganizationServiceImpl impl = (PicketLinkIDMOrganizationServiceImpl)service;
      if (!impl.getConfiguration().isDisableUserActived()) {
        LOG.info("Ignore upgrade user due to disable-user feature is not activated");
        return;
      }
      
      final UserHandler handler = service.getUserHandler();
      ListAccess<User> users = null;
      int size = 0;
      try {
        RequestLifeCycle.begin(impl);
        users = handler.findAllUsers(UserStatus.ANY);
        size = users.getSize();
      } catch (Exception ex) {
        LOG.error(ex);
      } finally {
        RequestLifeCycle.end();
      }
      
      batchSize = batchSize > size ? size : batchSize;
      int tSize = size / threadNum;
      tSize = tSize > 0 ? tSize : size;
      final int odd = tSize == size ? 0 : size % threadNum;
      
      LOG.info("start upgrading {} users by {} threads, batchSize: {}", size, threadNum, batchSize);      
      //
      final AtomicLong count = new AtomicLong();
      List<Future<Boolean>> results = new LinkedList<Future<Boolean>>();      
      final ExoContainer container = ExoContainerContext.getCurrentContainer();
      final PicketLinkIDMService idmService = container.getComponentInstanceOfType(PicketLinkIDMService.class);
      final int threadSize = tSize;
      final ListAccess<User> usrs = users;
      final int totalSize = size;
      
      ExecutorService execService = Executors.newFixedThreadPool(threadNum);
      try {
        //
        for (int i = 0; i < threadNum; i++) {
          final int idx = i;
          //
          results.add(execService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
              int start = idx * threadSize;
              int end = start + threadSize;
              if (idx == threadNum -1) {
                end += odd;
              }
              
              ExoContainerContext.setCurrentContainer(container);
              
              int startBatch = start;
              int endBatch = startBatch + batchSize;
              endBatch = endBatch > end ? end : endBatch;
              while (endBatch <= totalSize && endBatch <= end && startBatch < endBatch) {
                LOG.info("{} start: {}, end: {}", Thread.currentThread(), startBatch, endBatch);        
                RequestLifeCycle.begin(impl);
                IdentitySession session = idmService.getIdentitySession();
                User[] tmp = usrs.load(startBatch, endBatch - startBatch);
                try {
                  for (User u : tmp) {
                    enableUser(u, session);
                  }
                  count.addAndGet(endBatch - startBatch);
                  startBatch = endBatch;
                  endBatch = startBatch + batchSize;
                  endBatch = endBatch > end ? end : endBatch;
                  LOG.info("{} finished successfully!", Thread.currentThread());
                } catch (Exception e) {
                  LOG.error("An unexpected error occurs when migrating pages:", e);        
                  return false;
                } finally {
                  RequestLifeCycle.end();
                }
              }
              
              return true;
            }
  
          }));
        }
      } finally {
        execService.shutdown();
      }
      
      try {
        for (Future<Boolean> r : results) {
          if (!r.get()) {
            LOG.error("Disable user migration completed with errors");
          }
        }
        LOG.info("Finish upgrading {} users", count.get());
      } catch (Exception ex) {
        LOG.error(ex);
      }
    } else {
      LOG.info("Ignore upgrade user due to not using PicketLinkIDMOrganizationServiceImpl");
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isBefore(previousVersion, VERSION);
  }
  
  private void enableUser(User u, IdentitySession session) {
    if (u instanceof UserImpl) {
      ((UserImpl) u).setEnabled(true);

      Attribute[] attrs = new Attribute[] { new SimpleAttribute(UserDAOImpl.USER_ENABLED, String.valueOf(true)) };
      AttributesManager am = session.getAttributesManager();
      try {
        am.updateAttributes(u.getUserName(), attrs);
      } catch (Exception e) {
        LOG.error(e);
      }
    }
  }
}
