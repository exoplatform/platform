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
package org.exoplatform.services.security.j2ee.weblogic;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.exoplatform.portal.application.PortalRequestContext;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;

/**
 * Created by The eXo Platform SAS.
 * 
 * WeblogicLogoutFilter for logout.
 * 
 * @author <a href="mailto:thomas.delhomenie@exoplatform.com">Thomas
 *         Delhomenie</a>
 */
public class WeblogicLogoutFilter implements Filter {

	/**
	 * Exo logger.
	 */
    private static final Logger log = LoggerFactory.getLogger(WeblogicLogoutFilter.class);

	/**
	 * Destroy.
	 */
	public void destroy() {
	}

	/**
	 * Do filter. Logout if the current action is "Logout". It performs 2
	 * actions : - invalidate the session. This was necessary because the
	 * session was not invalidated and therefore the ConversationState was not
	 * removed from the ConversationRegistry (so a further login in the same
	 * browser with another user kept the former user's conversation state) -
	 * log out of WebLogic. Without this action, the remote user is not cleared.
	 * 
	 * This filter does not seem to be very clean. It is a dirty solution,
	 * waiting for a better solution.
	 * 
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		String actionParam = httpRequest.getParameter(PortalRequestContext.UI_COMPONENT_ACTION);
		if ("Logout".equals(actionParam)) {
			httpRequest.getSession().invalidate();
			weblogic.servlet.security.ServletAuthentication.logout(httpRequest);
		}

		chain.doFilter(request, response);
	}

	/**
	 * Initialization.
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
