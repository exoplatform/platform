package org.exoplatform.platform.common.software.register.web;

import org.exoplatform.platform.common.software.register.service.SoftwareRegistrationService;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by The eXo Platform SEA
 * Author : eXoPlatform
 * toannh@exoplatform.com
 * On 9/28/15
 * Software register to Tribe
 */
public class SoftwareRegisterViewServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private final static String SR_JSP_RESOURCE = "/WEB-INF/jsp/software-registration/softwareregister.jsp";

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    SoftwareRegistrationService registrationService = WCMCoreUtils.getService(SoftwareRegistrationService.class);
    String returnURL = SoftwareRegisterAuthViewServlet.getReturnURL(request);
    StringBuffer _registrationURL = new StringBuffer();
    _registrationURL.append(registrationService.getSoftwareRegistrationHost());
    _registrationURL.append(SoftwareRegistrationService.SOFTWARE_REGISTRATION_PATH);
    _registrationURL.append("?").append(SoftwareRegistrationService.SOFTWARE_REGISTRATION_CLIENT_ID);
    _registrationURL.append("&").append(SoftwareRegistrationService.SOFTWARE_REGISTRATION_RESPONSE_TYPE);
    _registrationURL.append("&redirect_uri=").append(returnURL);
    request.setAttribute("registrationURL", _registrationURL.toString());
    getServletContext().getRequestDispatcher(SR_JSP_RESOURCE).forward(request, response);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }
}
