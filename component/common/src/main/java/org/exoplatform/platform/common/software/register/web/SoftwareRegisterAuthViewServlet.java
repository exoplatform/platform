package org.exoplatform.platform.common.software.register.web;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.common.software.register.model.SoftwareRegistration;
import org.exoplatform.platform.common.software.register.service.SoftwareRegistrationService;

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
public class SoftwareRegisterAuthViewServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private final static String SR_JSP_RESOURCE = "/WEB-INF/jsp/software-registration/softwareregister-success.jsp";

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    SettingService settingService = PortalContainer.getInstance().getComponentInstanceOfType(SettingService.class);
    SoftwareRegistrationService softwareRegistrationService = PortalContainer.getInstance().getComponentInstanceOfType(SoftwareRegistrationService.class);

    String code = request.getParameter("code");
    SoftwareRegistration softwareRegistration = softwareRegistrationService.getAccessToken(code);
    if(softwareRegistration!=null && softwareRegistration.getAccess_token()!=null) {
      settingService.set(Context.GLOBAL, Scope.GLOBAL, SoftwareRegistrationService.SOFTWARE_REGISTRATION_NODE, SettingValue.create("Software registered:" + "true"));
      softwareRegistrationService.checkSoftwareRegistration();
      getServletContext().setAttribute("status", "success");
    }else {
      getServletContext().setAttribute("status", "failed");
    }
    getServletContext().getRequestDispatcher(SR_JSP_RESOURCE).forward(request, response);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }
}
