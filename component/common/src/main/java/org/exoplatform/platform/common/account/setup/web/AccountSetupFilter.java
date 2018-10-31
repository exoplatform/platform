package org.exoplatform.platform.common.account.setup.web;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.web.filter.Filter;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 */
public class AccountSetupFilter implements Filter {
  private static final String PLF_PLATFORM_EXTENSION_SERVLET_CTX = "/platform-extension";

  private static final String ACCOUNT_SETUP_SERVLET              = "/accountSetup";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;

    ExoContainer container = PortalContainer.getInstance();
    AccountSetupService accountSetupService = container.getComponentInstanceOfType(AccountSetupService.class);

    boolean setupDone = accountSetupService.mustSkipAccountSetup();

    String requestUri = httpServletRequest.getRequestURI();
    boolean isRestUri = requestUri.contains(container.getContext().getRestContextName());
    if (!setupDone && !isRestUri) {
      ServletContext platformExtensionContext = httpServletRequest.getSession()
                                                                  .getServletContext()
                                                                  .getContext(PLF_PLATFORM_EXTENSION_SERVLET_CTX);
      platformExtensionContext.getRequestDispatcher(ACCOUNT_SETUP_SERVLET).forward(httpServletRequest, httpServletResponse);
      return;
    }
    chain.doFilter(request, response);
  }
}
