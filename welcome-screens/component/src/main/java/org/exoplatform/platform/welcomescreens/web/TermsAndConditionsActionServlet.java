/**
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
package org.exoplatform.platform.welcomescreens.web;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.platform.welcomescreens.service.TermsAndConditionsService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.*;
import org.exoplatform.container.component.RequestLifeCycle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="hzekri@exoplatform.com">hzekri</a>
 * @date 18/01/13
 */
public class TermsAndConditionsActionServlet extends HttpServlet {
    private static final long serialVersionUID = 6467955354840693802L;


    private static Log logger = ExoLogger.getLogger(TermsAndConditionsActionServlet.class);
    protected final static String USER_NAME_ACCOUNT = "username";
    protected final static String FIRST_NAME_ACCOUNT = "firstNameAccount";
    protected final static String LAST_NAME_ACCOUNT = "lastNameAccount";
    protected final static String EMAIL_ACCOUNT = "emailAccount";
    protected final static String USER_PASSWORD_ACCOUNT = "password";
    protected final static String ADMIN_FIRST_NAME = "root";
    protected final static String ADMIN_PASSWORD = "adminPassword";
    protected final static String PLATFORM_ADMINISTRATORS_GROUP = "/platform/administrators";
    protected final static String MEMBERSHIP_TYPE_Member = "member";
    protected final static String INTRANET_HOME = "/portal/intranet";

    private TermsAndConditionsService termsAndConditionsService;


    public TermsAndConditionsService getTermsAndConditionsService() {
        if (this.termsAndConditionsService == null) {
            termsAndConditionsService = (TermsAndConditionsService) PortalContainer.getInstance().getComponentInstanceOfType(TermsAndConditionsService.class);
        }
        return this.termsAndConditionsService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get usefull parameters
        String initialURI = request.getParameter(TermsAndConditionsViewServlet.INITIAL_URI_PARAM);
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

        getTermsAndConditionsService().checkTermsAndConditions();

        // Redirect to requested page
        String redirectURI = "/" + PortalContainer.getCurrentPortalContainerName() + "/login?" + "username=" + userNameAccount + "&password=" + userPasswordAccount + "&initialURI=" + INTRANET_HOME;
        response.sendRedirect(redirectURI);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}