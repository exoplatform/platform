package org.exoplatform.platform.common.software.register.web;

import org.exoplatform.platform.common.software.register.UnlockService;
import org.exoplatform.platform.common.software.register.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

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
    Utils.writeToFile(Utils.SW_REG_STATUS, String.valueOf("true"), Utils.HOME_CONFIG_FILE_LOCATION);
    UnlockService.setIsSkip(true);
    response.sendRedirect(redirectURI);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }
}
