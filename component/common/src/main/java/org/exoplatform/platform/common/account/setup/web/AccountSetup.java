package org.exoplatform.platform.common.account.setup.web;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.*;
import org.gatein.common.text.EntityEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 3/1/13
 */
public class AccountSetup extends HttpServlet {

    private static final Log LOG = ExoLogger.getLogger(AccountSetup.class);
    private static final long serialVersionUID = 6467955354840693802L;
    public final static String ACCOUNT_SETUP_NODE = "accountSetup";
    private final static String USER_NAME_ACCOUNT = "username";
    private final static String FIRST_NAME_ACCOUNT = "firstNameAccount";
    private final static String LAST_NAME_ACCOUNT = "lastNameAccount";
    private final static String EMAIL_ACCOUNT = "emailAccount";
    private final static String USER_PASSWORD_ACCOUNT = "password";
    private final static String ADMIN_FIRST_NAME = "root";
    private final static String ADMIN_PASSWORD = "adminPassword";
    private final static String PLATFORM_USERS_GROUP = "/platform/administrators";
    private final static String PLATFORM_WEB_CONTRIBUTORS_GROUP = "/platform/web-contributors";
    private final static String PLATFORM_DEVELOPERS_GROUP = "/developers";
    private final static String PLATFORM_PLATFORM_USERS_GROUP ="/platform/users";
    private final static String MEMBERSHIP_TYPE_MANAGER = "*";
    private final static String INTRANET_HOME = "/portal/intranet";
    private final static String INITIAL_URI_PARAM = "initialURI";
    private final static String ACCOUNT_SETUP_BUTTON = "setupbutton";
    private final static String SETUP_SKIP_BUTTON = "skipform";
    public static Boolean SETUP_SKIP = false;
    private static final String ACCOUNT_SETUP_SKIP_PROPERTY = "accountsetup.skip";
    SettingService settingService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String redirectURI = null;
        String accountsetupbutton = request.getParameter(ACCOUNT_SETUP_BUTTON);

        boolean isDevMod = PropertyManager.isDevelopping();
        // check accountsetup.skip flag in configuration.properties
        String propertySetupSkip =  PropertyManager.getProperty(ACCOUNT_SETUP_SKIP_PROPERTY);
        if (propertySetupSkip == null) {
          LOG.debug("Property accountsetup.skip not found in configuration.properties ");
          propertySetupSkip = "false";
        }
        settingService = (SettingService) PortalContainer.getInstance().getComponentInstanceOfType(SettingService.class);
        SettingValue accountSetupNode = settingService.get(Context.GLOBAL, Scope.GLOBAL, ACCOUNT_SETUP_NODE);
        if (accountsetupbutton.equals(SETUP_SKIP_BUTTON) || accountSetupNode != null || propertySetupSkip.equals("true") || isDevMod) {
          if (LOG.isWarnEnabled()) {
            LOG.warn("Direct access to Account Setup Form.");
          }
          SETUP_SKIP = true;
          redirectURI = "/" + PortalContainer.getCurrentPortalContainerName();
        } else {
        EntityEncoder encoder = EntityEncoder.FULL;
        String userNameAccount = request.getParameter(USER_NAME_ACCOUNT);
        String firstNameAccount = request.getParameter(FIRST_NAME_ACCOUNT);
        String lastNameAccount = request.getParameter(LAST_NAME_ACCOUNT);
        String emailAccount = request.getParameter(EMAIL_ACCOUNT);
        String userPasswordAccount = request.getParameter(USER_PASSWORD_ACCOUNT);
        String adminPassword = request.getParameter(ADMIN_PASSWORD);
        OrganizationService orgService;
        UserHandler userHandler;
        User user;
        Group group = null;
        MembershipType membershipType = null;

        try {
            orgService = (OrganizationService) PortalContainer.getInstance().getComponentInstanceOfType(OrganizationService.class);
            RequestLifeCycle.begin((ComponentRequestLifecycle) orgService);
            // --- Get MemberShipType Service
            MembershipTypeHandler membershipTypeHandler = orgService.getMembershipTypeHandler();

            // Create user account
            userHandler = orgService.getUserHandler();
            user = userHandler.createUserInstance(userNameAccount);
            user.setPassword(userPasswordAccount);
            user.setFirstName(firstNameAccount);
            user.setLastName(lastNameAccount);
            user.setEmail(emailAccount);

            try {
                userHandler.createUser(user, true);
            } catch (Exception e) {
                LOG.error("Can not create User", e);
            }

            // Assign the membership "*:/platform/administrators"  to the created user
            try {
                group = orgService.getGroupHandler().findGroupById(PLATFORM_USERS_GROUP);
                membershipType = membershipTypeHandler.findMembershipType(MEMBERSHIP_TYPE_MANAGER);
                orgService.getMembershipHandler().linkMembership(user, group, membershipType, true);
            } catch (Exception e) {
                LOG.error("Can not assign *:/platform/administrators membership to the created user", e);
            }

            // Assign the membership "*:/platform/web-contributors"  to the created user
            try {
                group = orgService.getGroupHandler().findGroupById(PLATFORM_WEB_CONTRIBUTORS_GROUP);
                membershipType = membershipTypeHandler.findMembershipType(MEMBERSHIP_TYPE_MANAGER);
                orgService.getMembershipHandler().linkMembership(user, group, membershipType, true);
            } catch (Exception e) {
                LOG.error("Can not assign *:/platform/web-contributors membership to the created user", e);
            }

            // Assign the membership "member:/developer"  to the created user
            try {
                group = orgService.getGroupHandler().findGroupById(PLATFORM_DEVELOPERS_GROUP);
                membershipType = membershipTypeHandler.findMembershipType(MEMBERSHIP_TYPE_MANAGER);
                orgService.getMembershipHandler().linkMembership(user, group, membershipType, true);
            } catch (Exception e) {
                LOG.error("Can not assign *:/developers membership to the created user", e);
            }

            // Assign the membership "*:/platform/users"  to the created user
            try {
                group = orgService.getGroupHandler().findGroupById(PLATFORM_PLATFORM_USERS_GROUP);
                membershipType = membershipTypeHandler.findMembershipType(MEMBERSHIP_TYPE_MANAGER);
                orgService.getMembershipHandler().linkMembership(user, group, membershipType, true);
            } catch (Exception e) {
                LOG.error("Can not assign *:/platform/users membership to the created user", e);
            }


            // Set password for admin user
            try {
                User adminUser = userHandler.findUserByName(ADMIN_FIRST_NAME);
                adminUser.setPassword(adminPassword);
                orgService.getUserHandler().saveUser(adminUser, false);
            } catch (Exception e) {
                LOG.error("Can not set password to the created user", e);
            }
        } finally {
            if (accountSetupNode == null)
              settingService.set(Context.GLOBAL, Scope.GLOBAL, ACCOUNT_SETUP_NODE, SettingValue.create("setup over:" + "true"));
            RequestLifeCycle.end();
        }
        // Redirect to requested page
        redirectURI = "/" + PortalContainer.getCurrentPortalContainerName() + "/login?" + "username=" + URLEncoder.encode(userNameAccount, "UTF-8") + "&password=" + userPasswordAccount + "&initialURI=" + INTRANET_HOME;
        }
        response.setCharacterEncoding("UTF-8");
        response.sendRedirect(redirectURI);
        }
    //}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
