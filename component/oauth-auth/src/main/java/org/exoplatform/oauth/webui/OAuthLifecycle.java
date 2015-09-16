/*
 * JBoss, a division of Red Hat
 * Copyright 2013, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
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

package org.exoplatform.oauth.webui;

import org.exoplatform.oauth.OAuthConst;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.application.ApplicationLifecycle;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.web.application.RequestFailure;
import org.exoplatform.web.security.AuthenticationRegistry;
import org.exoplatform.webui.core.UIComponent;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.gatein.security.oauth.common.OAuthConstants;
import org.gatein.security.oauth.exception.OAuthException;
import org.gatein.security.oauth.exception.OAuthExceptionCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This class is clone from org.exoplatform.portal.application.oauth.OAuthLifecycle,
 * because the feature signup-on-fly need other UIRegisterOAuth component to show invitation/registration form
 * when user do authentication via oauth
 *
 * This lifecycle is used to update WebUI state based on OAuth events from Http filters
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OAuthLifecycle implements ApplicationLifecycle<PortalRequestContext> {

    /** . */
    private final Logger log = LoggerFactory.getLogger(OAuthLifecycle.class);

    private AuthenticationRegistry authRegistry;

    @Override
    public void onInit(Application app) throws Exception {
        this.authRegistry = app.getApplicationServiceContainer().getComponentInstanceOfType(AuthenticationRegistry.class);
    }

    @Override
    public void onStartRequest(Application app, PortalRequestContext context) throws Exception {
        HttpServletRequest httpRequest = context.getRequest();
        HttpSession httpSession = httpRequest.getSession();
        UIPortalApplication uiApp = Util.getUIPortalApplication();

        Boolean isOnFlyError = (Boolean)httpRequest.getSession().getAttribute(OAuthConst.SESSION_KEY_ON_FLY_ERROR);
        if (isOnFlyError != null) {
            httpRequest.getSession().removeAttribute(OAuthConst.SESSION_KEY_ON_FLY_ERROR);
        }

        User oauthAuthenticatedUser = (User)authRegistry.getAttributeOfClient(httpRequest, OAuthConstants.ATTRIBUTE_AUTHENTICATED_PORTAL_USER);

        // Display Registration form after successful OAuth authentication.
        if (oauthAuthenticatedUser != null) {
            UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);

            if (log.isTraceEnabled()) {
                log.trace("Found user, which has been authenticated through OAuth. Username is " + oauthAuthenticatedUser.getUserName());
            }

            if (!uiMaskWS.isShow() || !uiMaskWS.getUIComponent().getClass().equals(UIRegisterOAuth.class)) {
                if (log.isTraceEnabled()) {
                    log.trace("Showing registration form for OAuth registration");
                }
                UIComponent uiRegisterOauth = uiMaskWS.createUIComponent(UIRegisterOAuth.class, null, null);
                uiMaskWS.setCssClasses("TransparentMask");
                uiMaskWS.setWindowSize(-1, -1);
                uiMaskWS.setUIComponent(uiRegisterOauth);

                if (isOnFlyError != null && isOnFlyError) {
                    ApplicationMessage msg = new ApplicationMessage("UIRegisterForm.message.signUpOnFlyError", new Object[0], ApplicationMessage.WARNING);
                    msg.setArgsLocalized(false);
                    uiApp.addMessage(msg);
                }
            }
        }

        // Show message about successful social account linking
        String socialNetworkName = (String)httpSession.getAttribute(OAuthConstants.ATTRIBUTE_LINKED_OAUTH_PROVIDER);
        if (socialNetworkName != null) {
            httpSession.removeAttribute(OAuthConstants.ATTRIBUTE_LINKED_OAUTH_PROVIDER);

            ApplicationMessage msg = new ApplicationMessage("UIAccountSocial.msg.successful-link", new Object[] {socialNetworkName, context.getRemoteUser()});
            msg.setArgsLocalized(false);
            uiApp.addMessage(msg);
        }

        // Show message about failed social account linking
        OAuthException gtnOAuthException = (OAuthException)httpSession.getAttribute(OAuthConstants.ATTRIBUTE_EXCEPTION_AFTER_FAILED_LINK);
        if (gtnOAuthException != null) {
            httpSession.removeAttribute(OAuthConstants.ATTRIBUTE_EXCEPTION_AFTER_FAILED_LINK);

            Object[] args = new Object[] {gtnOAuthException.getExceptionAttribute(OAuthConstants.EXCEPTION_OAUTH_PROVIDER_USERNAME),
                    gtnOAuthException.getExceptionAttribute(OAuthConstants.EXCEPTION_OAUTH_PROVIDER_NAME)};
            ApplicationMessage appMessage = new ApplicationMessage("UIAccountSocial.msg.failed-link", args, ApplicationMessage.WARNING);
            appMessage.setArgsLocalized(false);
            uiApp.addMessage(appMessage);
        }

        // Show message about failed OAuth2 flow
        gtnOAuthException = (OAuthException)httpSession.getAttribute(OAuthConstants.ATTRIBUTE_EXCEPTION_OAUTH);
        if (gtnOAuthException != null) {
            httpSession.removeAttribute(OAuthConstants.ATTRIBUTE_EXCEPTION_OAUTH);

            String key;
            if (gtnOAuthException.getExceptionCode() == OAuthExceptionCode.USER_DENIED_SCOPE) {
                key = "UIAccountSocial.msg.access-denied";
            } else {
                key = "UIAccountSocial.msg.oauth-error";

                log.error("Unspecified error during OAuth flow", gtnOAuthException);
            }

            ApplicationMessage appMessage = new ApplicationMessage(key, null, ApplicationMessage.WARNING);
            uiApp.addMessage(appMessage);
        }
    }

    @Override
    public void onFailRequest(Application app, PortalRequestContext context, RequestFailure failureType) {
    }

    @Override
    public void onEndRequest(Application app, PortalRequestContext context) throws Exception {
    }

    @Override
    public void onDestroy(Application app) throws Exception {
    }
}
