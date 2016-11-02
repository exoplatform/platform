/*
 * Copyright (C) 2015 eXo Platform SAS.
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

package org.exoplatform.oauth.filter;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.filter.Filter;
import org.exoplatform.web.security.AuthenticationRegistry;
import org.gatein.security.oauth.common.OAuthConstants;
import org.gatein.security.oauth.spi.OAuthProviderTypeRegistry;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public abstract class OAuthAbstractFilter implements Filter {

    public static final String COOKIE_LAST_LOGIN = "last_login_username";

    protected ThreadLocal<PortalContainer> container = new ThreadLocal<PortalContainer>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getRemoteUser() != null) {
            // User already loggedIn
            Cookie cookie = new Cookie(COOKIE_LAST_LOGIN, req.getRemoteUser());
            cookie.setPath(req.getContextPath());
            cookie.setMaxAge(3600); // 1 hours = 60 * 60 seconds
            cookie.setHttpOnly(true);
            res.addCookie(cookie);

            chain.doFilter(request, response);
            return;
        }

        PortalContainer c = PortalContainer.getCurrentInstance(request.getServletContext());
        if (c == null) {
            chain.doFilter(req, res);
            return;
        }

        try {
            this.container.set(c);
            if (!this.isOauthEnable()) {
                chain.doFilter(req, res);
                return;
            }

            AuthenticationRegistry authReg = getService(AuthenticationRegistry.class);
            User authenticated = (User)authReg.getAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_PORTAL_USER_FOR_JAAS);
            if (authenticated != null) {
                // Found user mapped with oauth-user, let LoginModule continue process login
                chain.doFilter(req, res);
                return;
            }

            User oauthAuthenticatedUser = (User) authReg.getAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_PORTAL_USER);
            if (oauthAuthenticatedUser == null) {
                // Not in oauth process, do not need to process here
                chain.doFilter(req, res);
                return;
            }

            //. In oauth process
            this.executeFilter(req, res, chain);

        } finally {
            this.container.set(null);
        }
    }

    protected abstract void executeFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException;


    private Boolean oauthEnable = null;
    protected boolean isOauthEnable() {
        if (oauthEnable == null) {
            OAuthProviderTypeRegistry registry = getService(OAuthProviderTypeRegistry.class);
            oauthEnable = registry.isOAuthEnabled();
        }
        return this.oauthEnable;
    }

    protected <T> T getService(Class<T> clazz) {
        return container.get().getComponentInstanceOfType(clazz);
    }

    protected ServletContext getContext() {
        return container.get().getPortalContext();
    }
}
