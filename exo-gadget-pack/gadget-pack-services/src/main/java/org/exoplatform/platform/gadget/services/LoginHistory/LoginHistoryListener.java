/***************************************************************************
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
 ***************************************************************************/
package org.exoplatform.platform.gadget.services.LoginHistory;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;


/**
 * Created by The eXo Platform SARL Author : Tung Vu Minh tungvm@exoplatform.com
 * Apr 21, 2011 6:19:21 PM
 */
public class LoginHistoryListener extends Listener<ConversationRegistry, ConversationState> {
	private static final Log LOG = ExoLogger.getLogger(LoginHistoryListener.class);
	private ExoContainerContext context;

	public LoginHistoryListener(ExoContainerContext context) throws Exception {
		this.context = context;
	}

	/**
	 * Log the time when user logging in 
	 * 
	 * @throws Exception
	 */	
	@Override
	public void onEvent(Event<ConversationRegistry, ConversationState> event) throws Exception {
		String userId = event.getData().getIdentity().getUserId();
		try {
			ExoContainer container = ExoContainerContext.getContainerByName(context.getPortalContainerName());
			LoginHistoryService loginHistoryService = (LoginHistoryService)container.getComponentInstanceOfType(LoginHistoryService.class);
			
			long now = System.currentTimeMillis();
			if(now - loginHistoryService.getLastLogin(userId) > 180000) {
				loginHistoryService.addLoginHistoryEntry(userId, now);
				LOG.info("User " + userId + " logged in.");
			}
		} catch (Exception e) {
			LOG.debug("Error while logging the login of user '" + userId + "': " + e.getMessage(), e);
		}
	}
}