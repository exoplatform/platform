package org.exoplatform.platform.common.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Servlet responsible of the first display of Terms&Conditions
 * @author Clement
 *
 */
public class TermsAndConditionsViewServlet extends HttpServlet {
  private static final long serialVersionUID = 6467955354840693802L;
  
  private static Log logger = ExoLogger.getLogger(TermsAndConditionsFilter.class);
  
  protected final static String INITIAL_URI_PARAM = "tacURI";
  private final static String TC_JSP_RESOURCE = "/jsp/termsandconditions.jsp";
  private final static String INITIAL_URI_ATTRIBUTE = "org.gatein.portal.login.initial_uri";
  
  private final static String TC_JSP_RESOURCE_TEST0 = "/WEB-INF/jsp/admin/termsandconditions.jsp";

  private final static String TC_JSP_RESOURCE_TEST1 = "/WEB-INF/jsp/admin/test1-termsandconditions.jsp";
  private final static String TC_JSP_RESOURCE_TEST2 = "/WEB-INF/jsp/admin/test2-termsandconditions.jsp";
  private final static String TC_JSP_RESOURCE_TEST3 = "/WEB-INF/jsp/admin/test3-termsandconditions.jsp";
  private final static String TC_JSP_RESOURCE_TEST4 = "/WEB-INF/jsp/admin/test4-termsandconditions.jsp";
  private final static String TC_JSP_RESOURCE_TEST5 = "/WEB-INF/jsp/admin/test5-termsandconditions.jsp";
  private final static String TC_JSP_RESOURCE_TEST6 = "/WEB-INF/jsp/admin/test6-termsandconditions.jsp";

  private final static String TC_JSP_RESOURCE_TEST7 = "/jsp/admin/termsandconditions.jsp";
  
  private final static String TC_JSP_RESOURCE_TEST11 = "/jsp/admin/test1-termsandconditions.jsp";
  private final static String TC_JSP_RESOURCE_TEST12 = "/jsp/admin/test2-termsandconditions.jsp";
  private final static String TC_JSP_RESOURCE_TEST13 = "/jsp/admin/test3-termsandconditions.jsp";
  private final static String TC_JSP_RESOURCE_TEST14 = "/jsp/admin/test4-termsandconditions.jsp";
  private final static String TC_JSP_RESOURCE_TEST15 = "/jsp/admin/test5-termsandconditions.jsp";
  private final static String TC_JSP_RESOURCE_TEST16 = "/jsp/admin/test6-termsandconditions.jsp";

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    String initialURI = request.getParameter(INITIAL_URI_PARAM);
    
    String sTestNumber = request.getParameter("t");
    int testNumber = -1;
    try {
      testNumber = Integer.valueOf(sTestNumber);
      if(testNumber < 0 || testNumber > 6) {
        testNumber = -1;
      }
    }
    catch(Exception e) {
      logger.error("Problem with parameter: t", e);
    }

    // Include JSP page
    try {
      request.setAttribute(INITIAL_URI_ATTRIBUTE, initialURI);
      
      String jspResource = TC_JSP_RESOURCE;
      if(testNumber != -1) {
        switch(testNumber) {
          case 0: jspResource = TC_JSP_RESOURCE_TEST0;break;
          case 1: jspResource = TC_JSP_RESOURCE_TEST1;break;
          case 2: jspResource = TC_JSP_RESOURCE_TEST2;break;
          case 3: jspResource = TC_JSP_RESOURCE_TEST3;break;
          case 4: jspResource = TC_JSP_RESOURCE_TEST4;break;
          case 5: jspResource = TC_JSP_RESOURCE_TEST5;break;
          case 6: jspResource = TC_JSP_RESOURCE_TEST6;break;
          case 7: jspResource = TC_JSP_RESOURCE_TEST7;break;
          case 11: jspResource = TC_JSP_RESOURCE_TEST11;break;
          case 12: jspResource = TC_JSP_RESOURCE_TEST12;break;
          case 13: jspResource = TC_JSP_RESOURCE_TEST13;break;
          case 14: jspResource = TC_JSP_RESOURCE_TEST14;break;
          case 15: jspResource = TC_JSP_RESOURCE_TEST15;break;
          case 16: jspResource = TC_JSP_RESOURCE_TEST16;break;
          default: break;
        }
      }
      getServletContext().getRequestDispatcher(jspResource).include(request, response);
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
