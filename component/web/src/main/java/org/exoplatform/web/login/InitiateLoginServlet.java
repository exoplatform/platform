/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.web.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.web.security.Credentials;
import org.exoplatform.web.security.security.AbstractTokenService;
import org.exoplatform.web.security.security.CookieTokenService;
import org.exoplatform.web.security.security.TransientTokenService;

/**
 * Initiate the login dance.
 * 
 * @author <a href="mailto:trong.tran@exoplatform.com">Tran The Trong</a>
 * @version $Revision$
 */
public class InitiateLoginServlet extends HttpServlet {
  public static final String COOKIE_NAME = "rememberme";

  public static final String CREDENTIALS = "credentials";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                                                                        IOException {
    HttpSession session = req.getSession();
    Credentials credentials = (Credentials) session.getAttribute(InitiateLoginServlet.CREDENTIALS);
    session.setAttribute("initialURI", req.getAttribute("javax.servlet.forward.request_uri"));

    if (credentials == null) {
      String token = getTokenCookie(req);

      if (token != null) {
        AbstractTokenService tokenService = AbstractTokenService.getInstance(CookieTokenService.class);
        credentials = tokenService.validateToken(token, false);
        if (credentials == null) {
          Cookie cookie = new Cookie(InitiateLoginServlet.COOKIE_NAME, "");
          cookie.setPath(req.getContextPath());
          cookie.setMaxAge(0);
          resp.addCookie(cookie);
          req.getRequestDispatcher("/login/jsp/login.jsp").include(req, resp);
          return;
        }
      } else {
        req.getRequestDispatcher("/login/jsp/login.jsp").include(req, resp);
        return;
      }
    } else {
      req.getSession().removeAttribute(InitiateLoginServlet.CREDENTIALS);
    }
    String token = null;
    for (Cookie cookie : req.getCookies()) {
      if (InitiateLoginServlet.COOKIE_NAME.equals(cookie.getName())) {
        token = cookie.getValue();
        break;
      }
    }
    if (token == null) {
      TransientTokenService tokenService = (TransientTokenService) ExoContainerContext.getCurrentContainer()
                                                                                      .getComponentInstanceOfType(TransientTokenService.class);
      token = tokenService.createToken(credentials);
    }

    sendAuth(resp, credentials.getUsername(), token);

  }

  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                                                                         IOException {
    doGet(req, resp);
  }

  private void sendAuth(HttpServletResponse resp, String jUsername, String jPassword) throws IOException {
    String url = "j_security_check?j_username=" + jUsername + "&j_password=" + jPassword;
    url = resp.encodeRedirectURL(url);

    resp.sendRedirect(url);
  }

  private String getTokenCookie(HttpServletRequest req) {
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (InitiateLoginServlet.COOKIE_NAME.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
