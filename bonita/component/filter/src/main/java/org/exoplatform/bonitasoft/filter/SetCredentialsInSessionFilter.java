package org.exoplatform.bonitasoft.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bonitasoft.console.security.server.LoginServlet;
import org.bonitasoft.console.security.server.api.ICredentialsEncryptionAPI;
import org.bonitasoft.console.security.server.api.SecurityAPIFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class SetCredentialsInSessionFilter implements Filter {

  protected ICredentialsEncryptionAPI credentialsEncryptionAPI;
  private static final Log logger = ExoLogger.getLogger(SetCredentialsInSessionFilter.class.getName());

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException,
      IOException {
    try {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      String username = httpRequest.getRemoteUser();
      if ((username != null) && (username.length() > 0)) {
        String encryptedCredentials = this.credentialsEncryptionAPI.encryptCredential(username + ":");
        HttpSession session = httpRequest.getSession();
        session.setAttribute(LoginServlet.USER_CREDENTIALS_SESSION_PARAM_KEY, encryptedCredentials);
      } else {
        if (logger.isDebugEnabled()) {
          logger
              .debug("The HttpServletRequest remoteUser should be initialized in order for the SetCredentialsInSessionFilter to work");
          logger.debug("Redirect to portal");
        }
        httpRequest.getSession().invalidate();

        String url = System.getProperty("org.exoplatform.runtime.conf.cas.server.name") + "/portal";
        httpResponse.sendRedirect(url);
        return;

      }
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Error while setting the credentials in session");
      }
      throw new ServletException(e);
    }
    filterChain.doFilter(request, response);
  }

  public void init(FilterConfig filterConfig) throws ServletException {
    this.credentialsEncryptionAPI = SecurityAPIFactory.getCredentialsEncryptionAPI();
  }

  public void destroy() {}

}
