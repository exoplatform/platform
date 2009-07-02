package org.exoplatform.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.security.ConversationState;

public class CacheUserProfileFilter implements Filter {

	/**
	 * "subject".
	 */
	public static final String USER_PROFILE = "UserProfile";

	/**
	 * Logger.
	 */
	private static Log log = ExoLogger
	.getLogger("core.security.SetCurrentIdentityFilter");

	private String portalContainerName;

	public void init(FilterConfig filterConfig) {
		portalContainerName = filterConfig.getInitParameter("portalContainerName");
		if (portalContainerName == null)
			portalContainerName = filterConfig.getServletContext()
			.getServletContextName();

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException {
		ConversationState state = ConversationState.getCurrent();
		try {
			if (state != null) {
				if (log.isDebugEnabled()) log.debug("Conversation State found, save user profile to Conversation State.");

				if (state.getAttribute(USER_PROFILE) == null) {
					ExoContainer portalContainer = ExoContainerContext.getCurrentContainer();
					OrganizationService orgService = (OrganizationService) portalContainer
					.getComponentInstanceOfType(OrganizationService.class);
					
					User user = orgService.getUserHandler().findUserByName(state.getIdentity().getUserId());
					state.setAttribute(USER_PROFILE, user);
					
				} 

			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			log.warn("An error occured while cache user profile", e);
		}

	}

	public void destroy() {
	}
}
