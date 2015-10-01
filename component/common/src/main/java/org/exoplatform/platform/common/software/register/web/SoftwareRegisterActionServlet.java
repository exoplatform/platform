package org.exoplatform.platform.common.software.register.web;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.platform.common.software.register.UnlockService;
import org.exoplatform.platform.common.software.register.Utils;
import org.exoplatform.platform.common.software.register.service.SoftwareRegistrationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
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
public class SoftwareRegisterActionServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Log logger = ExoLogger.getLogger(SoftwareRegisterActionServlet.class);

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String redirectURI = "/portal/";
    String value = request.getParameter("value");
    SoftwareRegistrationService softwareRegistrationService = WCMCoreUtils.getService(SoftwareRegistrationService.class);
    int xx = Integer.parseInt(Utils.readFromFile(Utils.SW_REG_SKIPPED, Utils.HOME_CONFIG_FILE_LOCATION));
    if(xx>10){
      //Utils.writeToFile(Utils.SW_REG_STATUS, String.valueOf("true"), Utils.HOME_CONFIG_FILE_LOCATION);
    }
    if(StringUtils.equals("skip", value)) {
      UnlockService.setIsSkip(true);
      softwareRegistrationService.updateSkippedNumber();
    }
    response.sendRedirect(redirectURI);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }
}
