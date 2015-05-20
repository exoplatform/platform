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

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.oauth.OAuthConst;
import org.exoplatform.services.organization.DisabledUserException;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileHandler;
import org.exoplatform.services.organization.UserStatus;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.web.application.AbstractApplicationMessage;
import org.exoplatform.web.security.AuthenticationRegistry;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.PasswordStringLengthValidator;
import org.exoplatform.webui.form.validator.PersonalNameValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;
import org.exoplatform.webui.form.validator.UserConfigurableValidator;
import org.exoplatform.webui.form.validator.Validator;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.gatein.security.oauth.common.OAuthConstants;
import org.gatein.security.oauth.exception.OAuthException;
import org.gatein.security.oauth.exception.OAuthExceptionCode;
import org.gatein.security.oauth.spi.OAuthPrincipal;
import org.gatein.security.oauth.spi.OAuthProviderType;
import org.gatein.security.oauth.utils.OAuthUtils;

import javax.security.auth.login.LoginException;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author <a href="mailto:tuyennt@exoplatform.com">Tuyen Nguyen The</a>.
 */
public class OAuthLoginServletFilter extends OAuthAbstractFilter {
    private static Logger log = LoggerFactory.getLogger(OAuthLoginServletFilter.class);

    public static final String CONTROLLER_PARAM_NAME = "login_controller";
    public static final String CANCEL_OAUTH = "oauth_cancel";
    public static final String CONFIRM_ACCOUNT = "confirm_account";
    public static final String REGISTER_NEW_ACCOUNT = "register";
    public static final String CONFIRM_REGISTER_ACCOUNT = "submit_register";

    public static final String SESSION_ATTR_REGISTER_NEW_ACCOUNT = "__oauth_create_new_account";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // This is workaround to fix bug:
        // - error message is shown after user cancel his oauth login then do login with username and password
        ((HttpServletRequest)request).getSession().removeAttribute(OAuthConstants.ATTRIBUTE_EXCEPTION_OAUTH);
        super.doFilter(request, response, chain);
    }

    @Override
    protected void executeFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");

        AuthenticationRegistry authReg = getService(AuthenticationRegistry.class);
        ResourceBundleService service = getService(ResourceBundleService.class);
        ResourceBundle bundle = service.getResourceBundle(service.getSharedResourceBundleNames(), req.getLocale()) ;
        MessageResolver messageResolver = new MessageResolver(bundle);
        ServletContext context = getContext();

        User portalUser = (User) authReg.getAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_PORTAL_USER);
        User detectedUser = (User)authReg.getAttributeOfClient(req, OAuthConst.ATTRIBUTE_AUTHENTICATED_PORTAL_USER_DETECTED);
        req.setAttribute("portalUser", portalUser);
        if (detectedUser != null) {
            req.setAttribute("detectedUser", detectedUser);
        }

        final String controller = req.getParameter(CONTROLLER_PARAM_NAME);
        if (CANCEL_OAUTH.equalsIgnoreCase(controller)) {
            cancelOauth(req, res, authReg);
            return;
        } else if (CONFIRM_ACCOUNT.equalsIgnoreCase(controller)) {
            confirmExistingAccount(req, res, messageResolver, authReg);
            return;
        } else if (CONFIRM_REGISTER_ACCOUNT.equalsIgnoreCase(controller)) {
            processCreateNewAccount(req, res, messageResolver, authReg, portalUser);
            return;
        } else if (REGISTER_NEW_ACCOUNT.equalsIgnoreCase(controller)) {
            req.getSession(true).setAttribute(SESSION_ATTR_REGISTER_NEW_ACCOUNT, new Integer(1));
        }


        Integer createNewAccount = (Integer)req.getSession().getAttribute(SESSION_ATTR_REGISTER_NEW_ACCOUNT);
        if(detectedUser != null && createNewAccount == null) {
            RequestDispatcher invitation = context.getRequestDispatcher("/login/jsp/oauth_invitation.jsp");
            if(invitation != null) {
                invitation.forward(req, res);
                return;
            }
        } else {
            RequestDispatcher register = context.getRequestDispatcher("/login/jsp/oauth_register.jsp");
            if(register != null) {
                register.forward(req, res);
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private void cancelOauth(HttpServletRequest req, HttpServletResponse res, AuthenticationRegistry authReg) throws IOException {
        req.getSession().removeAttribute(SESSION_ATTR_REGISTER_NEW_ACCOUNT);
        authReg.removeAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_OAUTH_PRINCIPAL);
        authReg.removeAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_PORTAL_USER);
        authReg.removeAttributeOfClient(req, OAuthConst.ATTRIBUTE_AUTHENTICATED_PORTAL_USER_DETECTED);

        //. Redirect to last URL
        String initialURL = OAuthUtils.getURLToRedirectAfterLinkAccount(req, req.getSession());
        if(initialURL == null) {
            initialURL = req.getServletPath();
        }
        res.sendRedirect(res.encodeRedirectURL(initialURL));
    }

    private void confirmExistingAccount(HttpServletRequest req, HttpServletResponse res, MessageResolver bundle,
                                        AuthenticationRegistry authReg) throws IOException, ServletException {
        String username;
        User detectedUser = (User)authReg.getAttributeOfClient(req, OAuthConst.ATTRIBUTE_AUTHENTICATED_PORTAL_USER_DETECTED);
        if(detectedUser != null) {
            username = detectedUser.getUserName();
        } else {
            req.setAttribute("invitationConfirmError", bundle.resolve("UIOAuthInvitationForm.message.not-in-oauth-login"));
            getContext().getRequestDispatcher("/login/jsp/oauth_invitation.jsp").forward(req, res);
            return;
        }

        String password = req.getParameter("password");
        Credential[] credentials =
                new Credential[]{new UsernameCredential(username), new PasswordCredential(password)};
        Authenticator authenticator = getService(Authenticator.class);
        try {
            if (password == null || password.trim().isEmpty()) {
                throw new LoginException("Password must not empty");
            }
            OrganizationService orgService = getService(OrganizationService.class);
            String user = authenticator.validateUser(credentials);
            if(user != null && !user.isEmpty()) {
                //Update authentication
                OAuthPrincipal principal = (OAuthPrincipal)authReg.getAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_OAUTH_PRINCIPAL);
                OAuthProviderType providerType = principal.getOauthProviderType();

                UserProfileHandler profileHandler = orgService.getUserProfileHandler();
                UserProfile newUserProfile = profileHandler.findUserProfileByName(user);
                if (newUserProfile == null) {
                    newUserProfile = orgService.getUserProfileHandler().createUserProfileInstance(user);
                }

                newUserProfile.setAttribute(providerType.getUserNameAttrName(), principal.getUserName());
                profileHandler.saveUserProfile(newUserProfile, true);

                //. Redirect to last URL
                authReg.removeAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_PORTAL_USER);
                authReg.removeAttributeOfClient(req, OAuthConst.ATTRIBUTE_AUTHENTICATED_PORTAL_USER_DETECTED);
                String initURL = OAuthUtils.getURLToRedirectAfterLinkAccount(req, req.getSession());
                if(initURL == null) {
                    initURL = req.getServletPath();
                }
                res.sendRedirect(res.encodeRedirectURL(initURL));
            }
        } catch (LoginException ex) {
            Exception e = authenticator.getLastExceptionOnValidateUser();
            if (e != null && e instanceof DisabledUserException) {
                req.setAttribute("invitationConfirmError", bundle.resolve("UIOAuthInvitationForm.message.userDisabled"));
            } else {
                req.setAttribute("invitationConfirmError", bundle.resolve("UIOAuthInvitationForm.message.loginFailure"));
            }
        } catch (Exception ex) {
            req.setAttribute("invitationConfirmError", bundle.resolve("UIOAuthInvitationForm.message.loginUnknowException"));
            log.warn("Exception while checking password of user", ex);
        }

        getContext().getRequestDispatcher("/login/jsp/oauth_invitation.jsp").forward(req, res);
    }

    private void processCreateNewAccount(HttpServletRequest req, HttpServletResponse res,
                                            MessageResolver bundle, AuthenticationRegistry authReg,
                                            User portalUser) throws IOException, ServletException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String password2 = req.getParameter("password2");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String displayName = req.getParameter("displayName");
        String email = req.getParameter("email");

        portalUser.setUserName(username);
        portalUser.setPassword(password);
        portalUser.setFirstName(firstName);
        portalUser.setLastName(lastName);
        portalUser.setDisplayName(displayName);
        portalUser.setEmail(email);

        List<String> errors = new ArrayList<String>();
        Set<String> errorFields = new HashSet<String>();
        OrganizationService orgService = getService(OrganizationService.class);

        validateUser(portalUser, password2, orgService, bundle, errors, errorFields);

        if(errors.size() == 0) {
            try {
                orgService.getUserHandler().createUser(portalUser, true);
                UserProfileHandler profileHandler = orgService.getUserProfileHandler();
                UserProfile newUserProfile = profileHandler.findUserProfileByName(portalUser.getUserName());
                if (newUserProfile == null) {
                    newUserProfile = orgService.getUserProfileHandler().createUserProfileInstance(portalUser.getUserName());
                }
                OAuthPrincipal oauthPrincipal = (OAuthPrincipal)authReg.getAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_OAUTH_PRINCIPAL);
                newUserProfile.setAttribute(oauthPrincipal.getOauthProviderType().getUserNameAttrName(), oauthPrincipal.getUserName());

                try {
                    profileHandler.saveUserProfile(newUserProfile, true);

                    authReg.removeAttributeOfClient(req, OAuthConstants.ATTRIBUTE_AUTHENTICATED_PORTAL_USER);
                    authReg.removeAttributeOfClient(req, OAuthConst.ATTRIBUTE_AUTHENTICATED_PORTAL_USER_DETECTED);

                    // Successfully to register new account
                    // Just refresh then oauth lifecycle will continue process
                    res.sendRedirect(getContext().getContextPath());
                    return;

                } catch (OAuthException gtnOAuthException) {
                    // Show warning message if user with this facebookUsername (or googleUsername) already exists
                    // NOTE: It could happen only in case of parallel registration of same oauth user from more browser windows
                    if (gtnOAuthException.getExceptionCode() == OAuthExceptionCode.DUPLICATE_OAUTH_PROVIDER_USERNAME) {

                        // Drop new user
                        orgService.getUserHandler().removeUser(portalUser.getUserName(), true);

                        // Clear previous message about successful creation of user because we dropped him. Add message about duplicate oauth username
                        errors.add(bundle.resolve("UIAccountSocial.msg.failed-registration",
                                gtnOAuthException.getExceptionAttribute(OAuthConstants.EXCEPTION_OAUTH_PROVIDER_USERNAME),
                                gtnOAuthException.getExceptionAttribute(OAuthConstants.EXCEPTION_OAUTH_PROVIDER_NAME)));
                    } else {
                        log.warn("Unknown oauth error", gtnOAuthException);
                        errors.add(bundle.resolve("UIAccountSocial.msg.oauth-error"));
                    }
                }
            } catch (Exception ex) {
                log.warn("Exception when create new account for user", ex);
                errors.add(bundle.resolve("UIAccountInputSet.msg.fail.create.user"));
            }
        }
        req.setAttribute("register_errors", errors);
        req.setAttribute("register_error_fields", errorFields);
        getContext().getRequestDispatcher("/login/jsp/oauth_register.jsp").forward(req, res);
    }

    private void validateUser(User user, String password2, OrganizationService orgService,
                              MessageResolver bundle, List<String> errorMessages, Set<String> errorFields) {
        Validator validator;
        Validator mandatory = new MandatoryValidator();
        Validator stringLength;
        ResourceBundle rb = bundle.getBundle();
        //
        String username = user.getUserName();
        validator = new UserConfigurableValidator(UserConfigurableValidator.USERNAME,
                                                        UserConfigurableValidator.DEFAULT_LOCALIZATION_KEY);
        validate("username", username, new Validator[]{mandatory, validator}, rb, errorMessages, errorFields);
        if (!errorFields.contains("username")) {
            try {
                if (orgService.getUserHandler().findUserByName(username, UserStatus.ANY) != null) {
                    errorFields.add("username");
                    errorMessages.add(bundle.resolve("UIAccountInputSet.msg.user-exist", username));
                }
            } catch (Exception ex) {
                log.warn("Can not check username exist or not for: " + username);
            }
        }

        //
        String password = user.getPassword();
        validator = new PasswordStringLengthValidator(6, 30);
        validate("password", password, new Validator[]{mandatory, validator}, rb, errorMessages, errorFields);
        if (!errorFields.contains("password")) {
            if (!password.equals(password2)) {
                errorMessages.add(bundle.resolve("UIAccountForm.msg.password-is-not-match"));
                errorFields.add("password2");
            }
        }

        stringLength = new StringLengthValidator(1, 45);
        validator = new PersonalNameValidator();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        validate("firstName", firstName, new Validator[]{mandatory, stringLength, validator}, rb, errorMessages, errorFields);
        validate("lastName", lastName, new Validator[]{mandatory, stringLength, validator}, rb, errorMessages, errorFields);

        stringLength = new StringLengthValidator(0, 90);
        validator = new UserConfigurableValidator("displayname", UserConfigurableValidator.KEY_PREFIX + "displayname", false);
        String displayName = user.getDisplayName();
        validate("displayName", displayName, new Validator[]{stringLength, validator}, rb, errorMessages, errorFields);

        //
        validator = new UserConfigurableValidator(UserConfigurableValidator.EMAIL);
        String email = user.getEmail();
        validate("email", email, new Validator[]{mandatory, validator}, rb, errorMessages, errorFields);
        if (!errorFields.contains("email")) {
            try {
                Query query = new Query();
                query.setEmail(email);
                ListAccess<User> users = orgService.getUserHandler().findUsersByQuery(query, UserStatus.ANY);
                if (users != null && users.getSize() > 0) {
                    errorFields.add("email");
                    errorMessages.add(bundle.resolve("UIAccountInputSet.msg.email-exist", email));
                }
            } catch (Exception ex) {
                log.warn("Can not check email exist or not for: " + email);
            }
        }
    }

    private void validate(String field, String value, Validator[] validators, ResourceBundle bundle,
                                                        List<String> errorMessages, Set<String> errorFields) {
        try {
            for(Validator validator : validators) {
                validator.validate(new UIFormStringInput(field, field, value));
            }
        } catch (Exception e) {
            errorFields.add(field);
            if (e instanceof MessageException) {
                MessageException mex = (MessageException)e;
                AbstractApplicationMessage msg = mex.getDetailMessage();
                msg.setResourceBundle(bundle);
                errorMessages.add(msg.getMessage());
            } else {
                log.debug(e);
                errorMessages.add(field + " error");
            }
        }
    }

    public static class MessageResolver {
        private final ResourceBundle bundle;

        public MessageResolver(ResourceBundle bundle) {
            this.bundle = bundle;
        }

        public String resolve(String key, Object... args) {
            try {
                String message = bundle.getString(key);
                if (message != null && args != null) {
                    for (int i = 0; i < args.length; i++) {
                        final Object messageArg = args[i];
                        if (messageArg != null) {
                            String arg = messageArg.toString();
                            message = message.replace("{" + i + "}", arg);
                        }
                    }
                }
                return message;
            } catch (Exception ex) {
                return key;
            }
        }

        public ResourceBundle getBundle() {
            return bundle;
        }
    }
}
