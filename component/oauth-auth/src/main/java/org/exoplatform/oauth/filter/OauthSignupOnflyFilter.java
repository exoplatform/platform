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

import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.oauth.OAuthConst;
import org.exoplatform.oauth.service.OAuthRegistrationServices;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.security.AuthenticationRegistry;
import org.gatein.security.oauth.common.OAuthConstants;
import org.gatein.security.oauth.spi.OAuthPrincipal;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class OauthSignupOnflyFilter extends OAuthAbstractFilter {
    static final String SESSION_KEY_SIGNUP_ON_FLY_ERROR = "__onfly_error";

    @Override
    protected void executeFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        AuthenticationRegistry authReg = getService(AuthenticationRegistry.class);
        User detectedUser = (User)authReg.getAttributeOfClient(req, OAuthConst.ATTRIBUTE_AUTHENTICATED_PORTAL_USER_DETECTED);
        if(detectedUser != null) {
            chain.doFilter(req, res);
            return;
        }

        OAuthPrincipal principal = (OAuthPrincipal) authReg.getAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_OAUTH_PRINCIPAL);
        OAuthRegistrationServices regService = getService(OAuthRegistrationServices.class);
        boolean isOnFly = regService != null && regService.isRegistrationOnFly(principal.getOauthProviderType());
        if (isOnFly) {
            String oauth = principal.getOauthProviderType().getKey() + "_" + principal.getUserName();
            String onFlyError = (String)req.getSession().getAttribute(SESSION_KEY_SIGNUP_ON_FLY_ERROR);
            if (onFlyError != null) {
                if (oauth.equals(onFlyError)) {
                    //. Did not detect and auto create user for this oauth-user, just show registration form
                    chain.doFilter(req, res);
                    return;

                } else {
                    req.getSession().removeAttribute(SESSION_KEY_SIGNUP_ON_FLY_ERROR);
                }
            }

            detectedUser = regService.detectGateInUser(req, principal);
            if (detectedUser != null) {
                authReg.setAttributeOfClient(req, OAuthConst.ATTRIBUTE_AUTHENTICATED_PORTAL_USER_DETECTED, detectedUser);

            } else {
                OrganizationService orgService = getService(OrganizationService.class);
                if (orgService instanceof ComponentRequestLifecycle) {
                    RequestLifeCycle.begin((ComponentRequestLifecycle)orgService);
                }
                User newUser = regService.createGateInUser(principal);
                if (orgService instanceof ComponentRequestLifecycle) {
                    RequestLifeCycle.end();
                }

                if (newUser != null) {
                    authReg.removeAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_PORTAL_USER);
                    // send redirect to continue oauth login
                    res.sendRedirect(getContext().getContextPath());
                    return;
                } else {
                    req.getSession().setAttribute(SESSION_KEY_SIGNUP_ON_FLY_ERROR, oauth);
                    req.getSession().setAttribute(OAuthConst.SESSION_KEY_ON_FLY_ERROR, Boolean.TRUE);
                }
            }
        }
        chain.doFilter(req, res);
    }
}
