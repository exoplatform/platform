package org.exoplatform.platform.account.setup.web;

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

    private static Log logger = ExoLogger.getLogger(AccountSetup.class);
    private final static String USER_NAME_ACCOUNT = "username";
    private final static String FIRST_NAME_ACCOUNT = "firstNameAccount";
    private final static String LAST_NAME_ACCOUNT = "lastNameAccount";
    private final static String EMAIL_ACCOUNT = "emailAccount";
    private final static String USER_PASSWORD_ACCOUNT = "password";
    private final static String ADMIN_FIRST_NAME = "root";
    private final static String ADMIN_PASSWORD = "adminPassword";
    private final static String PLATFORM_ADMINISTRATORS_GROUP = "/platform/administrators";
    private final static String MEMBERSHIP_TYPE_Member = "member";
    private final static String INTRANET_HOME = "/portal/intranet/welcome";     //A verifier
    private final static String INITIAL_URI_PARAM = "initialURI";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get usefull parameters
        /*String fromTermsAndCondition = request.getParameter("termsAndCondition");
        if(fromTermsAndCondition!=null && !fromTermsAndCondition.equals("")){
            response.sendRedirect("/platform-extension/WEB-INF/jsp/accountSetup.jsp");
        }
        else{    */
        String initialURI = request.getParameter(INITIAL_URI_PARAM);
        String userNameAccount = request.getParameter(USER_NAME_ACCOUNT);
        String firstNameAccount = request.getParameter(FIRST_NAME_ACCOUNT);
        String lastNameAccount = request.getParameter(LAST_NAME_ACCOUNT);
        String emailAccount = request.getParameter(EMAIL_ACCOUNT);
        String userPasswordAccount = request.getParameter(USER_PASSWORD_ACCOUNT);
        String adminPassword = request.getParameter(ADMIN_PASSWORD);
        OrganizationService orgService;
        UserHandler userHandler;
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

            // Assign the membership "member:/platform/administrators"  to the created user
            try {
                Group group = orgService.getGroupHandler().findGroupById(PLATFORM_ADMINISTRATORS_GROUP);
                MembershipTypeHandler membershipTypeHandler = orgService.getMembershipTypeHandler();
                MembershipType membershipType = membershipTypeHandler.findMembershipType(MEMBERSHIP_TYPE_Member);
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