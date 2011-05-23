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
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.security.security.AbstractTokenService;
import org.exoplatform.web.security.security.CookieTokenService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Manages an error on login 
 * 
 * Created by The eXo Platform SAS
 * Author : Nicolas Filotto 
 *          nicolas.filotto@exoplatform.com
 * 4 oct. 2009  
 */
public class ErrorLoginServlet extends AbstractHttpServlet
{

   /**
    * Serial version ID
    */
   private static final long serialVersionUID = -1565579389217147072L;
     public static final String WASReqURL_COOKIE = "WASReqURL";

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger(ErrorLoginServlet.class.getName());

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      PortalContainer pContainer = PortalContainer.getInstance();
      ServletContext context = pContainer.getPortalContext();
      // Unregister the token cookie
      unregisterTokenCookie(req);
      // Clear the token cookie
      clearTokenCookie(req, resp);

      resp.setContentType("text/html; charset=UTF-8");
 
      // This allows the customer to define another login page without changing the portal
      showLoginForm(req, resp);
   }
      
   private void showLoginForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      String initialURI = "";

        //Get all cookies
        Cookie[] cookies = req.getCookies();
        for (int index = 0; index <= cookies.length; index++) {
            String cookieName = cookies[index].getName();
            if (WASReqURL_COOKIE.equals(cookieName)) {
                //TODO If you get a wrong URL (pattern : http://:<port>/portal) ,you could fix it by : https://www-304.ibm.com/support/docview.wss?rs=203&ca=portall2&uid=swg21259747
                      initialURI= cookies[index].getValue();
                break;
            }
        }

      int jsecurityIndex = initialURI.lastIndexOf("/j_security_check");
      if (jsecurityIndex != -1) 
      {
         initialURI = initialURI.substring(0, jsecurityIndex);
      }
      
      try
      {
         req.setAttribute("org.gatein.portal.login.initial_uri", initialURI);
         getServletContext().getRequestDispatcher("/login/jsp/login.jsp").include(req, resp);
      }
      finally
      {
         req.removeAttribute("org.gatein.portal.login.initial_uri");
      }
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      doGet(req, resp);
   }

   private void clearTokenCookie(HttpServletRequest req, HttpServletResponse resp)
   {
      Cookie cookie = new Cookie(InitiateLoginServlet.COOKIE_NAME, "");
      cookie.setPath(req.getContextPath());
      cookie.setMaxAge(0);
      resp.addCookie(cookie);
   }

   private void unregisterTokenCookie(HttpServletRequest req)
   {
      String tokenId = getTokenCookie(req);
      if (tokenId != null)
      {
         try
         {
            AbstractTokenService tokenService = AbstractTokenService.getInstance(CookieTokenService.class);
            tokenService.deleteToken(tokenId);
         }
         catch (Exception e)
         {
            LOG.warn("Cannot delete the token '" + tokenId + "'", e);
         }
      }
   }

   private String getTokenCookie(HttpServletRequest req)
   {
      Cookie[] cookies = req.getCookies();
      if (cookies != null)
      {
         for (Cookie cookie : cookies)
         {
            if (InitiateLoginServlet.COOKIE_NAME.equals(cookie.getName()))
            {
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
   protected boolean requirePortalEnvironment()
   {
      return true;
   }
}
