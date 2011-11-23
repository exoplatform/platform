package org.exoplatform.trial;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.commons.info.MissingProductInformationException;
import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.web.filter.Filter;
import org.picocontainer.Startable;

public class TrialService implements Startable {

  private static String registrationFormUrl = null;
  private static String extendFormUrl = null;
  private static String pingBackUrl = null;
  private static String calledUrl = null;
  private static String productNameAndVersion = null;
  private static String productCode = null;
  private static String KEY_CONTENT = null;
  private static boolean loopfuseFormDisplayed = false;
  private static boolean outdated = false;
  private static boolean dismissed = false;
  private static boolean firstStart = false;
  private static int delayPeriod = Utils.DEFAULT_DELAY_PERIOD;
  private static int nbDaysBeforeExpiration = 0;
  private static Calendar remindDate;

  private ScheduledExecutorService executor;

  public TrialService(ProductInformations productInformations, InitParams params) throws MissingProductInformationException {
    productNameAndVersion = Utils.PRODUCT_NAME + productInformations.getVersion().trim();
    registrationFormUrl = ((ValueParam) params.get("registrationFormUrl")).getValue();
    extendFormUrl = ((ValueParam) params.get("extendFormUrl")).getValue();
    pingBackUrl = ((ValueParam) params.get("pingBackUrl")).getValue();
    KEY_CONTENT = ((ValueParam) params.get("KeyContent")).getValue().trim();
    String tmpValue = ((ValueParam) params.get("delayPeriod")).getValue();
    delayPeriod = (tmpValue == null || tmpValue.isEmpty()) ? Utils.DEFAULT_DELAY_PERIOD : Integer.parseInt(tmpValue);
    Utils.HOME_CONFIG_FILE_LOCATION = Utils.EXO_HOME_FOLDER + "/" + productNameAndVersion;
  }

  public void start() {
    String productNameAndVersionHashed = Utils.getModifiedMD5Code(productNameAndVersion.getBytes());
    if (!new File(Utils.HOME_CONFIG_FILE_LOCATION).exists()) {
      firstStart = true;
      productCode = generateProductCode();
      Utils.writeToFile(Utils.PRODUCT_NAME, productNameAndVersionHashed, Utils.HOME_CONFIG_FILE_LOCATION);
      Utils.writeToFile(Utils.PRODUCT_CODE, productCode, Utils.HOME_CONFIG_FILE_LOCATION);
      Utils.writeToFile(Utils.REMIND_DATE, "", Utils.HOME_CONFIG_FILE_LOCATION);
      Utils.writeToFile(Utils.LOOP_FUSE_FORM_DISPLAYED, "false", Utils.HOME_CONFIG_FILE_LOCATION);
      return;
    }
    // Test the file informations
    String productNameAndVersionReadFromFile = Utils.readFromFile(Utils.PRODUCT_NAME, Utils.HOME_CONFIG_FILE_LOCATION);
    if (!productNameAndVersionHashed.equals(productNameAndVersionReadFromFile)) {
      throw new IllegalStateException("Inconsistent product informations.");
    }

    // Read: Product code
    productCode = Utils.readFromFile(Utils.PRODUCT_CODE, Utils.HOME_CONFIG_FILE_LOCATION);

    // Read: loopfuse form displayed
    String loopfuseFormDisplayedString = Utils.readFromFile(Utils.LOOP_FUSE_FORM_DISPLAYED, Utils.HOME_CONFIG_FILE_LOCATION);
    if (loopfuseFormDisplayedString != null && !loopfuseFormDisplayedString.isEmpty()) {
      loopfuseFormDisplayed = Boolean.parseBoolean(loopfuseFormDisplayedString);
    }

    // Read: Remind date
    String remindDateString = Utils.readFromFile(Utils.REMIND_DATE, Utils.HOME_CONFIG_FILE_LOCATION);
    if (remindDateString == null || remindDateString.isEmpty()) {
      firstStart = true;
      // No trial delay was requested
      return;
    } else {
      // Trial delay was already requested
      remindDate = Utils.parseDateBase64(remindDateString);
      computeUnlockedInformation();

      // Copute delay period every day
      executor = Executors.newSingleThreadScheduledExecutor();
      executor.scheduleWithFixedDelay(new Runnable() {
        public void run() {
          // Have to click on dismiss each Day.
          dismissed = false;
          computeUnlockedInformation();
        }
      }, 1, 1, TimeUnit.DAYS);
    }
  }

  public void stop() {
    if (executor != null) {
      executor.shutdown();
    }
  }

  public static String computeRemindDateFromTodayBase64() {
    if (delayPeriod <= 0 || outdated) {
      return "";
    }
    Calendar remindDate = Calendar.getInstance();
    remindDate.add(Calendar.DAY_OF_MONTH, delayPeriod);
    return Utils.formatDateBase64(remindDate);
  }

  public static boolean isFirstStart() {
    return firstStart;
  }

  public static String getRegistrationFormUrl() {
    return registrationFormUrl;
  }

  public static String getProductNameAndVersion() {
    return productNameAndVersion;
  }

  public static int getDelayPeriod() {
    return delayPeriod;
  }

  public static int getNbDaysBeforeExpiration() {
    return nbDaysBeforeExpiration;
  }

  public static boolean isLoopfuseFormDisplayed() {
    return loopfuseFormDisplayed;
  }

  public static boolean isOutdated() {
    return outdated;
  }

  public static String getPingBackUrl() {
    return pingBackUrl;
  }

  public static Calendar getRemindDate() {
    return remindDate;
  }

  public ScheduledExecutorService getExecutor() {
    return this.executor;
  }

  public static String getCalledUrl() {
    return calledUrl;
  }

  public static String getExtendFormUrl() {
    return extendFormUrl;
  }

  public static String getProductCode() {
    return productCode;
  }

  private static String generateProductCode() {
    String productCode = productNameAndVersion + Math.random() + KEY_CONTENT;
    return Utils.getModifiedMD5Code(productCode.getBytes());
  }

  public static boolean isDismissed() {
    return dismissed;
  }

  private static void computeUnlockedInformation() {
    Calendar today = Calendar.getInstance();
    today.set(Calendar.HOUR, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    today.set(Calendar.MILLISECOND, 0);
    if (remindDate.compareTo(today) <= 0 || delayPeriod <= 0) { // Reminder
                                                                // Date is
                                                                // outdated
      remindDate = today;
      delayPeriod = 0;
      outdated = true;
    } else { // Reminder Date is not yet outdated
      outdated = false;
      nbDaysBeforeExpiration = (int) TimeUnit.MILLISECONDS.toDays(remindDate.getTimeInMillis() - today.getTimeInMillis());
    }
  }

  private static int decodeEvaluationKey(String productCode, String evaluationKey) {
    StringBuffer evaluationKeyBuffer = new StringBuffer(new String(Base64.decodeBase64(evaluationKey.getBytes())));
    int length = Integer.parseInt(evaluationKeyBuffer.substring(4, 6));
    evaluationKeyBuffer.replace(4, 6, "");
    String productCodeHashed = evaluationKeyBuffer.substring(0, length);
    if (!productCodeHashed.equals(Utils.getModifiedMD5Code(productCode.getBytes()))) {
      return 0;
    }
    String periodString = evaluationKeyBuffer.substring(length);
    periodString = new String(Base64.decodeBase64(periodString.getBytes()));
    int period = Integer.parseInt(periodString) / 3;
    return period;
  }

  public static class TrialFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
        ServletException {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      boolean isIgnoringRequest = isIgnoredRequest(httpServletRequest.getSession(true).getServletContext(),
          httpServletRequest.getRequestURI());
      if ((!outdated && dismissed) || isIgnoringRequest) {
        chain.doFilter(request, response);
        return;
      }
      if (TrialService.calledUrl == null) {
        TrialService.calledUrl = httpServletRequest.getRequestURI();
      }
      httpServletResponse.sendRedirect("/trial/jsp/registration.jsp");
    }

    private boolean isIgnoredRequest(ServletContext context, String url) {
      String fileName = url.substring(url.indexOf("/"));
      String mimeType = context.getMimeType(fileName);
      return mimeType != null;
    }
  }

  public static class UnlockServlet extends HttpServlet {
    private static final long serialVersionUID = -4806814673109318163L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String dismiss = request.getParameter("dismiss");
      if (dismiss != null && dismiss.equals("true")) {
        if (!outdated && remindDate != null) {
          dismissed = true;
        }
        response.sendRedirect(TrialService.calledUrl);
        return;
      }
      String rdate = request.getParameter("rdate");
      if (rdate == null || rdate.isEmpty()) { // UnlockRequest
        String hashMD5Added = request.getParameter("hashMD5");
        if (hashMD5Added == null) {
          response.sendRedirect(TrialService.calledUrl);
          return;
        }
        try {
          delayPeriod = decodeEvaluationKey(productCode, hashMD5Added);
        } catch (Exception exception) {
          delayPeriod = 0;
        }
        if (delayPeriod <= 0) {
          outdated = true;
          request.setAttribute("errorMessage", "Sorry this evaluation key is not valid.");
          request.getRequestDispatcher("/jsp/extend.jsp").include(request, response);
          return;
        }
        outdated = false;
        rdate = computeRemindDateFromTodayBase64();
      }
      try {
        remindDate = Utils.parseDateBase64(rdate);
        computeUnlockedInformation();
        if (!outdated) {
          Utils.writeRemindDate(rdate, Utils.HOME_CONFIG_FILE_LOCATION);
        }
        response.sendRedirect(TrialService.calledUrl);
      } catch (Exception exception) {
        delayPeriod = 0;
        outdated = true;
        // rdate is malformed, may be this value is entered manually,
        // which mean that it's a hack
        response.sendRedirect(TrialService.calledUrl);
        return;
      }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doPost(request, response);
    }

  }

  public static class PingBackServlet extends HttpServlet {
    private static final long serialVersionUID = 6467955354840693802L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      if (isConnectedToInternet()) {
        loopfuseFormDisplayed = true;
        Utils.writePingBackFormDisplayed(Utils.HOME_CONFIG_FILE_LOCATION, loopfuseFormDisplayed);
      }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doPost(request, response);
    }

    public static boolean isConnectedToInternet() {
      // computes the Platform server URL, format http://server/
      String pingServerURL = pingBackUrl.substring(0, pingBackUrl.indexOf("/", "http://url".length()));
      try {
        URL url = new URL(pingServerURL);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.connect();
        return (HttpURLConnection.HTTP_NOT_FOUND != urlConn.getResponseCode());
      } catch (MalformedURLException e) {
        System.err.println("LeadCapture : Error creating HTTP connection to the server : " + pingServerURL);
      } catch (IOException e) {
        System.err.println("LeadCapture : Error creating HTTP connection to the server : " + pingServerURL);
      }
      return false;
    }
  }

}