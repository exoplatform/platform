package org.exoplatform.platform.common.account.setup.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 */
public class AccountSetupViewServlet extends HttpServlet {
  private final static String INTRANET_HOME_PAGE = "/portal/intranet";

  private final static String AS_JSP_RESOURCE    = "/WEB-INF/jsp/welcome-screens/accountSetup.jsp";

  private AccountSetupService accountSetupService;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    accountSetupService = PortalContainer.getInstance().getComponentInstanceOfType(AccountSetupService.class);

    if (accountSetupService.mustSkipAccountSetup()) {
        response.sendRedirect(INTRANET_HOME_PAGE);
    } else {
        getServletContext().getRequestDispatcher(AS_JSP_RESOURCE).forward(request, response);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

}
