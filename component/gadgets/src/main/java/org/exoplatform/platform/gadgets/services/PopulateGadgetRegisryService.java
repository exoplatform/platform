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
package org.exoplatform.platform.gadgets.services;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.gadgets.listeners.InitNewUserDashboardListener;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

/**
 * @author <a href="mailto:anouar.chattouna@exoplatform.com">Anouar Chattouna</a>
 * @version $Revision$
 */
public class PopulateGadgetRegisryService implements Startable {

  private static final String DEFAULT_GADGETS_CATEGORY_NAME = "Gadgets";
  private static String CATEGORY_NAME;
  private static String GADGETS_CATEGORY_ACCESS_PERMISSION;
  private static Log logger = ExoLogger.getExoLogger(InitNewUserDashboardListener.class);
  private GadgetRegistryService gadgetRegistryService = null;
  private ApplicationRegistryService applicationRegistryService = null;
  private ChromatticManager chromatticManager = null;
  private List<Gadget> gadgets;

  public PopulateGadgetRegisryService(ChromatticManager chromatticManager, GadgetRegistryService gadgetRegistryService,
      ApplicationRegistryService applicationRegistryService, InitParams initParams) {
    CATEGORY_NAME = initParams.getValueParam("gadgetsCategoryName").getValue();
    if (CATEGORY_NAME == null) {
      CATEGORY_NAME = DEFAULT_GADGETS_CATEGORY_NAME;
      if (logger.isDebugEnabled()) {
        logger.debug("Failed to retrieve " + initParams.getValueParam("gadgetsCategoryName").getName()
            + " init param. Default category name will be used: " + DEFAULT_GADGETS_CATEGORY_NAME);
      }
    }
    GADGETS_CATEGORY_ACCESS_PERMISSION = initParams.getValueParam("gadgetsCategoryAccessPermission").getValue();
    if (GADGETS_CATEGORY_ACCESS_PERMISSION == null) {
      GADGETS_CATEGORY_ACCESS_PERMISSION = UserACL.EVERYONE;
      if (logger.isDebugEnabled()) {
        logger.debug("Failed to retrieve " + initParams.getValueParam("gadgetsCategoryAccessPermission").getName()
            + " init param. Default access permission will be used: " + UserACL.EVERYONE);
      }
    }
    gadgets = initParams.getObjectParamValues(Gadget.class);
    this.gadgetRegistryService = gadgetRegistryService;
    this.applicationRegistryService = applicationRegistryService;
    this.chromatticManager = chromatticManager;

  }

  /**
   * Saves the gadget read from configuration file in the appropriate application category.
   */
  public void start() {
    for (Gadget gadget : gadgets) {
      // if(!gadget.isLocal()){
      // Test if there is an open Chromattic request else open new session
      boolean beginRequest = false;
      try {
        if (chromatticManager.getSynchronization() == null) {
          chromatticManager.beginRequest();
          beginRequest = true;
        }
      } catch (Exception e) {
        if (logger.isDebugEnabled()) {
          logger.debug("An exception has occurred while trying to begin the chromatticManager request: " + e.getMessage());
        }
      }
      try {
        // save the gadget via the GadgetRegistryService
        // check if the gadget was saved elsewhere
        if (gadgetRegistryService.getGadget(gadget.getName()) == null) {
          gadgetRegistryService.saveGadget(gadget);
        }
        ArrayList<String> permissions = new ArrayList<String>();
        String[] permissionEntry = GADGETS_CATEGORY_ACCESS_PERMISSION.split(",");
        for (String entry : permissionEntry) {
          permissions.add(entry);
        }
        // creates the registry application
        org.exoplatform.application.registry.Application registryApplication = new org.exoplatform.application.registry.Application();
        registryApplication.setApplicationName(gadget.getName());
        registryApplication.setType(ApplicationType.GADGET);
        registryApplication.setDisplayName(gadget.getTitle());
        registryApplication.setContentId(gadget.getName());
        String description = (gadget.getDescription() == null || gadget.getDescription().length() < 1) ? gadget.getName()
            : gadget.getDescription();
        registryApplication.setDescription(description);
        registryApplication.setAccessPermissions(permissions);
        registryApplication.setCategoryName(CATEGORY_NAME);
        if (applicationRegistryService.getApplicationCategory(CATEGORY_NAME) == null) {
          // creates the application category
          ApplicationCategory category = new ApplicationCategory();
          category.setName(CATEGORY_NAME);
          category.setDisplayName(CATEGORY_NAME);
          category.setDescription(CATEGORY_NAME);
          category.setAccessPermissions(permissions);
          applicationRegistryService.save(category, registryApplication);
        } else {
          applicationRegistryService.save(applicationRegistryService.getApplicationCategory(CATEGORY_NAME), registryApplication);
        }

      } catch (Exception e) {
        if (logger.isDebugEnabled()) {
          logger.debug("Error while saving gadget: " + gadget.getName() + " with " + CATEGORY_NAME + " application category: "
              + e.getMessage());
        }
      }
      // Test if Chromattic session is opened and try to end it
      if (beginRequest) {
        try {
          chromatticManager.endRequest(true);
        } catch (Exception e) {
          if (logger.isDebugEnabled()) {
            logger.debug("An exception has occurred while trying to end the chromatticManager request: " + e.getMessage());
          }
        }
      }
      // }
    }

  }

  public void stop() {}

}
