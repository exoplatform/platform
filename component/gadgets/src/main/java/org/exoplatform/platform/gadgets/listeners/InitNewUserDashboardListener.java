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
package org.exoplatform.platform.gadgets.listeners;

import org.exoplatform.platform.gadgets.services.UserDashboardConfigurationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

/**
 * @author <a href="mailto:anouar.chattouna@exoplatform.com">Anouar
 *         Chattouna</a>
 * @version $Revision$
 */
public class InitNewUserDashboardListener extends UserEventListener {

  private static final Log logger = ExoLogger.getExoLogger(InitNewUserDashboardListener.class);
  private UserDashboardConfigurationService userDashboardConfigurationService = null;

  public InitNewUserDashboardListener(UserDashboardConfigurationService userDashboardConfigurationService) {
    this.userDashboardConfigurationService = userDashboardConfigurationService;
  }

  /**
   * This method is called after the user has been saved but not commited
   * yet. It initializes the concerned user's dashboard and prepopulates
   * some gadgets on it.
   * 
   * @param user
   *          The user instance has been saved.
   * @param isNew
   *          if the user is a new record in the database or not
   */
  @Override
  public void postSave(User user, boolean isNew) {
    if (!isNew) {
      return;
    }
    // prepopulate the created user dashborad
    try {
      userDashboardConfigurationService.prepopulateUserDashboard(user.getUserName());
    } catch (Exception e) {
      logger.error("Error while prepopulationg user dashboard: ", e);
    }
  }
}
