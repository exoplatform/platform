package org.exoplatform.platform.common.account.setup.web;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 3/1/13
 */
public class AccountSetup extends HttpServlet {
    private static final long serialVersionUID = 6467955354840693802L;
    public final static String ACCOUNT_SETUP_NODE = "accountSetup";
    private static Log logger = ExoLogger.getLogger(AccountSetup.class);
    private final static String USER_NAME_ACCOUNT = "username";
    private final static String FIRST_NAME_ACCOUNT = "firstNameAccount";
    private final static String LAST_NAME_ACCOUNT = "lastNameAccount";
    private final static String EMAIL_ACCOUNT = "emailAccount";
    private final static String USER_PASSWORD_ACCOUNT = "password";
    private final static String ADMIN_FIRST_NAME = "root";
    private final static String ADMIN_PASSWORD = "adminPassword";
    private final static String PLATFORM_USERS_GROUP = "/platform/users";
    private final static String MEMBERSHIP_TYPE_MANAGER = "manager";
    private final static String INTRANET_HOME = "/portal/intranet";     //A verifier
    private final static String INITIAL_URI_PARAM = "initialURI";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String initialURI = request.getParameter(INITIAL_URI_PARAM);
        String userNameAccount = request.getParameter(USER_NAME_ACCOUNT);
        String firstNameAccount = request.getParameter(FIRST_NAME_ACCOUNT);
        String lastNameAccount = request.getParameter(LAST_NAME_ACCOUNT);
        String emailAccount = request.getParameter(EMAIL_ACCOUNT);
        String userPasswordAccount = request.getParameter(USER_PASSWORD_ACCOUNT);
        String adminPassword = request.getParameter(ADMIN_PASSWORD);
        OrganizationService orgService;
        UserHandler userHandler;
        SettingService settingService_;
        User user;

        try {
            orgService = (OrganizationService) PortalContainer.getInstance().getComponentInstanceOfType(OrganizationService.class);
            RequestLifeCycle.begin((ComponentRequestLifecycle) orgService);
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
                logger.error("Can not create User", e);
            }

            // Assign the membership "manager:/platform/users"  to the created user
            try {
                Group group = orgService.getGroupHandler().findGroupById(PLATFORM_USERS_GROUP);
                MembershipTypeHandler membershipTypeHandler = orgService.getMembershipTypeHandler();
                MembershipType membershipType = membershipTypeHandler.findMembershipType(MEMBERSHIP_TYPE_MANAGER);
                orgService.getMembershipHandler().linkMembership(user, group, membershipType, true);
            } catch (Exception e) {
                logger.error("Can not assign member:/platform/administrators membership to the created user", e);
            }

            // Set password for admin user
            try {
                User adminUser = userHandler.findUserByName(ADMIN_FIRST_NAME);
                adminUser.setPassword(adminPassword);
                orgService.getUserHandler().saveUser(adminUser, false);
            } catch (Exception e) {
                logger.error("Can not set password to the created user", e);
            }
        } finally {
            settingService_ =  (SettingService) PortalContainer.getInstance().getComponentInstanceOfType(SettingService.class);
            settingService_.set(Context.GLOBAL, Scope.GLOBAL, ACCOUNT_SETUP_NODE, SettingValue.create("setup over:" + "true"));
            RequestLifeCycle.end();
        }
        // Redirect to requested page
        String redirectURI = "/" + PortalContainer.getCurrentPortalContainerName() + "/login?" + "username=" + userNameAccount + "&password=" + userPasswordAccount + "&initialURI=" + INTRANET_HOME;
        response.sendRedirect(redirectURI);
        }
    //}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}