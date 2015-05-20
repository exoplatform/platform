/*
 * Copyright (C) 2012 eXo Platform SAS.
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
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.webui.register.UIRegisterForm;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.services.organization.DisabledUserException;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileHandler;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.web.application.AbstractApplicationMessage;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.web.security.AuthenticationRegistry;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.PasswordStringLengthValidator;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.gatein.security.oauth.common.OAuthConstants;
import org.gatein.security.oauth.spi.OAuthPrincipal;
import org.gatein.security.oauth.spi.OAuthProviderType;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

@ComponentConfigs({
        @ComponentConfig(lifecycle = UIFormLifecycle.class, template = "system:/groovy/portal/webui/form/UIOAuthInvitationForm.gtmpl", events = {
            @EventConfig(listeners = UIOAuthInvitationForm.ConfirmActionListener.class),
            @EventConfig(listeners = UIOAuthInvitationForm.NewAccountActionListener.class, phase = Event.Phase.DECODE),
            @EventConfig(listeners = UIRegisterOAuth.CancelActionListener.class, phase = Event.Phase.DECODE)},
        initParams = { @ParamConfig(name= "skipCaptcha", value="false") })
})
public class UIOAuthInvitationForm extends UIForm {
    private static final Logger log = LoggerFactory.getLogger(UIOAuthInvitationForm.class);

    private static final String[] ACTIONS = {"Confirm", "NewAccount", "Cancel"};

    protected static String PASSWORD = "password";

    private String detectedUserName;

    public UIOAuthInvitationForm(InitParams params) throws Exception {

        addUIFormInput(new UIFormStringInput(PASSWORD, PASSWORD, null)
                .setType(UIFormStringInput.PASSWORD_TYPE)
                .addValidator(MandatoryValidator.class)
                .addValidator(PasswordStringLengthValidator.class, 6, 30));

        setActions(ACTIONS);
    }

    public String getDetectedUserName() {
        return detectedUserName;
    }

    public void setDetectedUserName(String detectedUserName) {
        this.detectedUserName = detectedUserName;
    }

    public static class ConfirmActionListener extends EventListener<UIOAuthInvitationForm> {
        @Override
        public void execute(Event<UIOAuthInvitationForm> event) throws Exception {
            PortalRequestContext portalRequestContext = Util.getPortalRequestContext();
            HttpServletRequest request = portalRequestContext.getRequest();

            UIOAuthInvitationForm form = event.getSource();
            UIFormStringInput passwordInput = form.getChild(UIFormStringInput.class);
            String password = passwordInput.getValue();
            String username = "";

            AuthenticationRegistry authRegistry = form.getApplicationComponent(AuthenticationRegistry.class);
            Authenticator authenticator = form.getApplicationComponent(Authenticator.class);

            User detectedUser = (User)authRegistry.getAttributeOfClient(request, OAuthConst.ATTRIBUTE_AUTHENTICATED_PORTAL_USER_DETECTED);
            if(detectedUser != null) {
                username = detectedUser.getUserName();
            }

            Credential[] credentials =
                    new Credential[]{new UsernameCredential(username), new PasswordCredential(password)};

            try {
                if (password == null || password.trim().isEmpty()) {
                    throw new LoginException("Password must not empty");
                }

                String user = authenticator.validateUser(credentials);

                if(user != null && !user.isEmpty()) {
                    //Update authentication
                    OrganizationService orgService = form.getApplicationComponent(OrganizationService.class);
                    OAuthPrincipal principal = (OAuthPrincipal)authRegistry.getAttributeOfClient(request, OAuthConstants.ATTRIBUTE_AUTHENTICATED_OAUTH_PRINCIPAL);
                    OAuthProviderType providerType = principal.getOauthProviderType();

                    UserProfileHandler profileHandler = orgService.getUserProfileHandler();
                    UserProfile newUserProfile = profileHandler.findUserProfileByName(user);
                    if (newUserProfile == null) {
                        newUserProfile = orgService.getUserProfileHandler().createUserProfileInstance(user);
                    }

                    newUserProfile.setAttribute(providerType.getUserNameAttrName(), principal.getUserName());
                    profileHandler.saveUserProfile(newUserProfile, true);

                    authRegistry.removeAttributeOfClient(request, OAuthConstants.ATTRIBUTE_AUTHENTICATED_PORTAL_USER);
                    authRegistry.removeAttributeOfClient(request, OAuthConst.ATTRIBUTE_AUTHENTICATED_PORTAL_USER_DETECTED);

                    // Close the registration popup
                    UIMaskWorkspace.CloseActionListener closePopupListener = new UIMaskWorkspace.CloseActionListener();
                    closePopupListener.execute((Event)event);

                    // Redirect to finish login with new user
                    SiteKey siteKey = portalRequestContext.getSiteKey();
                    NodeURL urlToRedirect = portalRequestContext.createURL(NodeURL.TYPE);
                    urlToRedirect.setResource(new NavigationResource(siteKey, portalRequestContext.getNodePath()));

                    portalRequestContext.getJavascriptManager().addJavascript("window.location = '" + urlToRedirect.toString() + "';");
                }
            } catch (LoginException ex) {
                UIApplication uiApp = portalRequestContext.getUIApplication();
                Exception e = authenticator.getLastExceptionOnValidateUser();
                if (e != null && e instanceof DisabledUserException) {
                    uiApp.addMessage(new ApplicationMessage("UIOAuthInvitationForm.message.userDisabled", new Object[0], AbstractApplicationMessage.ERROR));
                } else {
                    uiApp.addMessage(new ApplicationMessage("UIOAuthInvitationForm.message.loginFailure", new Object[0], AbstractApplicationMessage.ERROR));
                }
            } catch (Exception ex) {
                log.warn(ex);
                UIApplication uiApp = portalRequestContext.getUIApplication();
                uiApp.addMessage(new ApplicationMessage("UIOAuthInvitationForm.message.loginUnknowException", new Object[0], AbstractApplicationMessage.ERROR));
            }
        }
    }
    public static class NewAccountActionListener extends EventListener<UIOAuthInvitationForm> {
        @Override
        public void execute(Event<UIOAuthInvitationForm> event) throws Exception {
            UIRegisterOAuth uiRegisterOAuth = event.getSource().getParent();

            uiRegisterOAuth.getChild(UIOAuthInvitationForm.class).setRendered(false);
            uiRegisterOAuth.getChild(UIRegisterForm.class).setRendered(true);

            event.getRequestContext().addUIComponentToUpdateByAjax(uiRegisterOAuth);
        }
    }
}
