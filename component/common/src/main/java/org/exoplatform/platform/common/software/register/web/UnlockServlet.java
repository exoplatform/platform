package org.exoplatform.platform.common.software.register.web;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.commons.info.MissingProductInformationException;
import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.common.software.register.UnlockService;
import org.exoplatform.platform.common.software.register.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UnlockServlet extends HttpServlet {
  private static final long serialVersionUID = -4806814673109318163L;

  private static final Log  LOG              = ExoLogger.getExoLogger(UnlockServlet.class);

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    UnlockService unlockService = PortalContainer.getInstance().getComponentInstanceOfType(UnlockService.class);

    String rdate;
    String hashMD5Added = request.getParameter("hashMD5");
    String pc = request.getParameter("pc");
    int delay;
    boolean callPingBack = false;
    String productEdition;
    if (hashMD5Added != null) {
      try {
        if (pc != null && !pc.equals(unlockService.getProductCode())) {
          delay = unlockService.decodeKey(pc, hashMD5Added);
        } else {
          delay = unlockService.decodeKey(unlockService.getProductCode(), hashMD5Added);
        }
      } catch (Exception exception) {
        delay = 0;
      }
      if (delay > -1 && delay <= 0) {
        request.setAttribute("errorMessage", "Invalid unlock key.");
        request.getRequestDispatcher("WEB-INF/jsp/welcome-screens/unlockTrial.jsp").include(request, response);
        return;
      }
      try {
        productEdition = unlockService.getProductInformations().getEdition();
      } catch (MissingProductInformationException MPIE) {
        LOG.error("[Unlock Service] : cannot load le platform edition from JCR ", MPIE);
        productEdition = ProductInformations.EXPRESS_EDITION;
      }
      if (delay == -1) {
        // shutDown executor -> unlimited duration so no need to
        // computeUnlockInformation everyday
        // to check if it's outdated
        if ((pc != null) && (!pc.equals(unlockService.getProductCode()))) {
          unlockService.setProductCode(pc);
        }
        Utils.writeToFile(Utils.PRODUCT_KEY, hashMD5Added, Utils.HOME_CONFIG_FILE_LOCATION);
        Utils.writeToFile(Utils.PRODUCT_CODE, unlockService.getProductCode(), Utils.HOME_CONFIG_FILE_LOCATION);
        unlockService.setOutdated(false);
        unlockService.setUnlocked(true); // to disappear the trial banner
        // --- call the enterprise ping back URL (only when the registered edition is
        // and enterprise edition)
        if ((!callPingBack && (productEdition.equalsIgnoreCase(ProductInformations.ENTERPRISE_EDITION)))) {
          if (callPingBack()) {
            LOG.info("[Ping Back] : call to " + unlockService.getDefaultPingBackUrl().concat("-ent") + " is done succesfully ");
          }
        }
        if (unlockService.getExecutor() != null) {
          unlockService.getExecutor().shutdown();
        }
        response.sendRedirect(unlockService.getCalledUrl());
        return;
      }
      unlockService.setDelayPeriod(delay);
      unlockService.setProductCode(unlockService.generateProductCode());
      Utils.writeToFile(Utils.PRODUCT_CODE, unlockService.getProductCode(), Utils.HOME_CONFIG_FILE_LOCATION);
      unlockService.setOutdated(false);
      rdate = unlockService.computeRemindDateFromTodayBase64();
      try {
        unlockService.setRemindDate(Utils.parseDateBase64(rdate));
        unlockService.computeUnlockedInformation();
        if (!unlockService.isOutdated()) {
          Utils.writeRemindDate(rdate, Utils.HOME_CONFIG_FILE_LOCATION);
          Utils.writeToFile(Utils.IS_EXTENDED,
                            new String(Base64.encodeBase64("true".getBytes())),
                            Utils.HOME_CONFIG_FILE_LOCATION);
          unlockService.setUnlocked(true);
        }
        // --- call the enterprise ping back URL (only when the registered edition is
        // and enterprise edition)
        if ((!callPingBack && (productEdition.equalsIgnoreCase(ProductInformations.ENTERPRISE_EDITION)))) {
          if (callPingBack()) {
            LOG.info("[Ping Back] : call to " + unlockService.getDefaultPingBackUrl().concat("-ent") + " is done succesfully ");
          }
        }
        response.sendRedirect(unlockService.getCalledUrl());
        return;
      } catch (Exception exception) {
        response.sendRedirect(unlockService.getCalledUrl());
        return;
      }
    }
    if (!unlockService.isUnlocked()) {
      request.getRequestDispatcher("WEB-INF/jsp/welcome-screens/unlockTrial.jsp").include(request, response);
    } else {
      response.sendRedirect("/portal/intranet");
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  protected boolean callPingBack() {
    UnlockService unlockService = PortalContainer.getInstance().getComponentInstanceOfType(UnlockService.class);

    String pingServerURL = unlockService.getDefaultPingBackUrl().concat("-ent");
    HttpURLConnection urlConn = null;
    try {
      URL url = new URL(pingServerURL);
      urlConn = (HttpURLConnection) url.openConnection();
      urlConn.connect();
      return (HttpURLConnection.HTTP_NOT_FOUND != urlConn.getResponseCode());
    } catch (MalformedURLException e) {
      LOG.error("[Ping Back Call] : Error creating HTTP connection to  : " + pingServerURL);

    } catch (IOException e) {
      LOG.error("[Ping Back Call] : Error creating HTTP connection to : " + pingServerURL);
    } finally {
      urlConn.disconnect();
      LOG.info("[Ping Back Call] : connection to [" + pingServerURL + "] is released");
    }
    return false;
  }
}
