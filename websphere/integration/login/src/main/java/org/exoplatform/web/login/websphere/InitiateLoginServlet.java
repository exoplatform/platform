/**
 * Copyright (C) 2009 eXo Platform SAS.
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

package org.exoplatform.web.login.websphere;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.web.AbstractHttpServlet;
import org.exoplatform.web.security.Credentials;
import org.exoplatform.web.security.security.AbstractTokenService;
import org.exoplatform.web.security.security.CookieTokenService;
import org.exoplatform.web.security.security.TransientTokenService;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Initiate the login dance.
 *
 * @author <a href="mailto:trong.tran@exoplatform.com">Tran The Trong</a>
 * @version $Revision$
 */
public class InitiateLoginServlet extends AbstractHttpServlet {

    /**
     * .
     */
    private static final Logger log = LoggerFactory.getLogger(InitiateLoginServlet.class);

    /**
     * .
     */
    public static final String COOKIE_NAME = "rememberme";

    /**
     * .
     */
    public static final String CREDENTIALS = "credentials";

    public static final String WASReqURL_COOKIE = "WASReqURL";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        HttpSession session = req.getSession();

        // Looking for credentials stored in the session
        Credentials credentials = (Credentials) session.getAttribute(InitiateLoginServlet.CREDENTIALS);

        //
        if (credentials == null) {
            PortalContainer pContainer = PortalContainer.getInstance();
            ServletContext context = pContainer.getPortalContext();

            //
            String token = getRememberMeTokenCookie(req);
            if (token != null) {
                AbstractTokenService tokenService = AbstractTokenService.getInstance(CookieTokenService.class);
                credentials = tokenService.validateToken(token, false);
                if (credentials == null) {
                    log.debug("Login initiated with no credentials in session but found token an invalid " + token + " " +
                            "that will be cleared in next response");

                    // We clear the cookie in the next response as it was not valid
                    Cookie cookie = new Cookie(InitiateLoginServlet.COOKIE_NAME, "");
                    cookie.setPath(req.getContextPath());
                    cookie.setMaxAge(0);
                    resp.addCookie(cookie);

                    // This allows the customer to define another login page without
                    // changing the portal
                    showLoginForm(req, resp);
                } else {
                    // Send authentication request
                    log.debug("Login initiated with no credentials in session but found token " + token + " with existing credentials, " +
                            "performing authentication");
                    sendAuth(req, resp, credentials.getUsername(), token);
                }
            } else {
                // This allows the customer to define another login page without
                // changing the portal
                log.debug("Login initiated with no credentials in session and no token cookie, redirecting to login page");
                showLoginForm(req, resp);
            }
        } else {
            // We create a temporary token just for the login time
            TransientTokenService tokenService = AbstractTokenService.getInstance(TransientTokenService.class);
            String token = tokenService.createToken(credentials);
            req.getSession().removeAttribute(InitiateLoginServlet.CREDENTIALS);

            // Send authentication request
            log.debug("Login initiated with credentials in session, performing authentication");
            sendAuth(req, resp, credentials.getUsername(), token);
        }
    }

    private void showLoginForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String initialURI = getInitialURI(req);
        try {
            String queryString = (String) req.getAttribute("javax.servlet.forward.query_string");
            if ((String) req.getAttribute("javax.servlet.forward.query_string") != null) {
                initialURI = initialURI + "?" + queryString;
            }
            req.setAttribute("org.gatein.portal.login.initial_uri", initialURI);
            getServletContext().getRequestDispatcher("/login/jsp/login.jsp").include(req, resp);
        } finally {
            req.removeAttribute("org.gatein.portal.login.initial_uri");
        }
    }

    private String getInitialURI(HttpServletRequest req) {
        String initialURI = "";
        //find  WASReqURL cookie
        Cookie[] cookies = req.getCookies();
        for (int index = 0; index <= cookies.length; index++) {
            String cookieName = cookies[index].getName();
            if (WASReqURL_COOKIE.equals(cookieName)) {
                 //TODO If you get a wrong URL (pattern : http://:<port>/portal) ,you could fix it by : https://www-304.ibm.com/support/docview.wss?rs=203&ca=portall2&uid=swg21259747
                      initialURI= cookies[index].getValue();
                break;
            }
        }
        return initialURI;
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void sendAuth(HttpServletRequest req, HttpServletResponse resp, String jUsername, String jPassword) throws IOException {
        String initialURI = getInitialURI(req);
        if (!initialURI.endsWith("/")) {
            initialURI += "/";
        }
        String url = initialURI + "j_security_check?j_username=" + jUsername + "&j_password=" + jPassword;
        url = resp.encodeRedirectURL(url);
        resp.sendRedirect(url);
    }

    /**
     * Extract the remember me token from the request or returns null.
     *
     * @param req the incoming request
     * @return the token
     */
    public static String getRememberMeTokenCookie(HttpServletRequest req) {
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

    /**
     * @see org.exoplatform.container.web.AbstractHttpServlet#requirePortalEnvironment()
     */
    @Override
    protected boolean requirePortalEnvironment() {
        return true;
    }
}
