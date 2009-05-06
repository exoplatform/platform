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

import org.exoplatform.web.security.Credentials;
import org.exoplatform.web.security.TokenStore;

/**
 * @author <a href="mailto:trong.tran@exoplatform.com">Tran The Trong</a>
 * @version $Revision$
 */
public class PortalLoginController extends HttpServlet {

  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    //
    String username = req.getParameter("username");
    String password = req.getParameter("password");
    Credentials credentials = new Credentials(username, password);
    req.getSession().setAttribute(InitiateLoginServlet.CREDENTIALS, credentials);

    String uri = (String) req.getSession().getAttribute("initialURI");
    if (uri == null || uri.length() == 0) {
      uri = req.getContextPath() + "/private" + req.getPathInfo();
    }

    //
    String rememberme = req.getParameter("rememberme");
    if ("true".equals(rememberme)) {
			boolean isRememeber = "true".equals(req.getParameter(InitiateLoginServlet.COOKIE_NAME));
			if (isRememeber) {
				int SECONDS_ONE_DAY = 60 * 60 * 24; 

				//Create token
				String cookieToken = TokenStore.COOKIE_STORE.createToken(1000 * SECONDS_ONE_DAY, credentials);
				Cookie cookie = new Cookie(InitiateLoginServlet.COOKIE_NAME, cookieToken);
				cookie.setPath(req.getContextPath());
				cookie.setMaxAge(SECONDS_ONE_DAY);
				resp.addCookie(cookie);
			}
    }
    resp.sendRedirect(uri);
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet(req, resp);
  }
}
