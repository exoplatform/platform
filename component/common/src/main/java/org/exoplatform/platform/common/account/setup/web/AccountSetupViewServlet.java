package org.exoplatform.platform.common.account.setup.web;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.container.PortalContainer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 3/19/13
 */
public class AccountSetupViewServlet extends HttpServlet {
    private final static String INTRANET_HOME_PAGE = "/portal/intranet";
    private final static String AS_JSP_RESOURCE = "/WEB-INF/jsp/welcome-screens/accountSetup.jsp";
    SettingService settingService ;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean setupDone = false;
        settingService = (SettingService) PortalContainer.getInstance().getComponentInstanceOfType(SettingService.class);
        SettingValue accountSetupNode = settingService.get(Context.GLOBAL, Scope.GLOBAL, AccountSetup.ACCOUNT_SETUP_NODE);
        if(accountSetupNode != null)
            setupDone = true;
        if(setupDone) response.sendRedirect(INTRANET_HOME_PAGE);
        else getServletContext().getRequestDispatcher(AS_JSP_RESOURCE).include(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

}
