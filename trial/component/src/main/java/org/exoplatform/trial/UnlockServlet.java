package org.exoplatform.trial;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.platform.common.Utils;

public class UnlockServlet extends HttpServlet {
  private static final long serialVersionUID = -4806814673109318163L;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String rdate = request.getParameter("rdate");
    if (rdate == null || rdate.equals("")) { // UnlockRequest
      String hashMD5Added = request.getParameter("hashMD5");
      if (hashMD5Added == null) {
        response.sendRedirect(TrialFilter.calledUrl);
      }
      try {
        TrialFilter.unlocked = hashMD5Added.equals(Utils.getModifiedMD5Code(Utils.KEY_CONTENT.getBytes()));
      } catch (NoSuchAlgorithmException exception) {
        throw new RuntimeException("Error while encoding the key.", exception);
      }
      if (TrialFilter.unlocked) {
        Utils.writeTrialKey(hashMD5Added, Utils.HOME_CONFIG_FILE_LOCATION);
        Cookie cookie = new Cookie("plf-lcf", "true");
        cookie.setPath("/");
        ((HttpServletResponse) response).addCookie(cookie);
        response.sendRedirect(TrialFilter.calledUrl);
        return;
      } else {
        response.sendRedirect("/trial/validation/unlock.jsp");
      }
    } else { // Add a reminder request into eXo Profile file
      try {
        Utils.parseDateBase64(rdate);
      } catch (Exception exception) {
        // rdate is malformed, may be this value is entered manually, which mean that it's a hack
        response.sendRedirect(TrialFilter.calledUrl);
        return;
      }
      Utils.writeRemindDate(rdate, Utils.HOME_CONFIG_FILE_LOCATION);
      Cookie cookie = new Cookie("plf-lcf", "true");
      cookie.setMaxAge(Utils.delayPeriod * 86400);
      cookie.setPath("/");
      ((HttpServletResponse) response).addCookie(cookie);
      TrialFilter.unlocked = true;
      response.sendRedirect(TrialFilter.calledUrl);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

}
