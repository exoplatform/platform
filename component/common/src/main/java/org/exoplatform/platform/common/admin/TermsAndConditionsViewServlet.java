package org.exoplatform.platform.common.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible of the first display of Terms&Conditions
 * @author Clement
 *
 */
public class TermsAndConditionsViewServlet extends HttpServlet {
  private static final long serialVersionUID = 6467955354840693802L;
  
  protected final static String INITIAL_URI_PARAM = "tacURI";
  private final static String TC_JSP_RESOURCE = "/WEB-INF/jsp/admin/termsandconditions.jsp";
  private final static String INITIAL_URI_ATTRIBUTE = "org.gatein.portal.login.initial_uri";

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    String initialURI = request.getParameter(INITIAL_URI_PARAM);

    // Include JSP page
    try {
      request.setAttribute(INITIAL_URI_ATTRIBUTE, initialURI);
      getServletContext().getRequestDispatcher(TC_JSP_RESOURCE).include(request, response);
    }
    finally {
      request.removeAttribute(INITIAL_URI_ATTRIBUTE);
    }   
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

}
