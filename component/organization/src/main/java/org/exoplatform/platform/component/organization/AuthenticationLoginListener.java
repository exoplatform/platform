/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.platform.component.organization;

import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;

/**
 * After a user login first time, this listener gets his data initialized.
 * 
 * @author Boubaker KHANFIR
 */
public class AuthenticationLoginListener extends Listener<ConversationRegistry, ConversationState> {

  private static final Log LOG = ExoLogger.getLogger(AuthenticationLoginListener.class);

  private OrganizationIntegrationService organizationIntegrationService;

  public AuthenticationLoginListener(OrganizationIntegrationService organizationIntegrationService) throws Exception {
    this.organizationIntegrationService = organizationIntegrationService;
  }

  /**
   * {@inheritDoc}
   */
  public void onEvent(Event<ConversationRegistry, ConversationState> event) throws Exception {
    String userId = event.getData().getIdentity().getUserId();
    if (LOG.isDebugEnabled()) {
      LOG.debug("Apply listeners for user" + userId);
    }
    organizationIntegrationService.applyUserListeners(userId);
    if (LOG.isDebugEnabled()) {
      LOG.debug("User listeners applied for " + userId);
    }
  }
}