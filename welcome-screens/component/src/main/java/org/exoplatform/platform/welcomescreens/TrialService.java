package org.exoplatform.platform.welcomescreens;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.common.http.client.HttpURLConnection;
import org.exoplatform.commons.info.MissingProductInformationException;
import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 1/17/13
 */
public class TrialService implements Startable {


    private static final Log LOG = ExoLogger.getExoLogger(TrialService.class);
    private static String registrationFormUrl = null;
    private static String extendFormUrl = null;
    private static String pingBackUrl = null;
    private static String calledUrl = null;
    private static String productNameAndVersion = null;
    private static String productCode = null;
    private static String KEY_CONTENT = null;

    /* A verifier si elle sert à quelque chose dans la nouvelle spec*/
    private static boolean loopfuseFormDisplayed = false;
    private static boolean outdated = false;

    /*plus besoin de cette information (elle etait faite pour afficher le bouton continuer ou startEvaluation dans registration.jsp )  */
    //private static boolean dismissed = false;
    //private static boolean firstStart = false;
    private static int delayPeriod = Utils.DEFAULT_DELAY_PERIOD;
    private static int nbDaysBeforeExpiration = 0;
    private static int nbDaysAfterExpiration = 0;
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
            /*Dans la nouvelle spec il y a plus le bouton startEvaluation
            * avant en cliquant sur startEvaluation on ecrit pour la premierer fois le REMIND_DATE
            * maintenant le premier accées au portail déclenche le compteur et les 30jours d'evaluation
            * */
            String rdate = TrialService.computeRemindDateFromTodayBase64();

            productCode = generateProductCode();
            Utils.writeToFile(Utils.PRODUCT_NAME, productNameAndVersionHashed, Utils.HOME_CONFIG_FILE_LOCATION);
            Utils.writeToFile(Utils.PRODUCT_CODE, productCode, Utils.HOME_CONFIG_FILE_LOCATION);
            /*
            ecrire la REMIND_DATE à la creation fu fichier d'évaluation
             */
            Utils.writeToFile(Utils.REMIND_DATE, rdate, Utils.HOME_CONFIG_FILE_LOCATION);
            Utils.writeToFile(Utils.LOOP_FUSE_FORM_DISPLAYED, "false", Utils.HOME_CONFIG_FILE_LOCATION);

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
        remindDate = Utils.parseDateBase64(remindDateString);
        computeUnlockedInformation();
        // Compute delay period every day
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                computeUnlockedInformation();
            }
        }, 1, 1, TimeUnit.MINUTES);
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

    public static int getNbDaysAfterExpiration() {
        return nbDaysAfterExpiration;
    }

    public static boolean isLandingPageDisplayed() {
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

    private static void computeUnlockedInformation() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        if (remindDate.compareTo(today) <= 0 || delayPeriod <= 0) { // Reminder
            // Date is
            // outdated
            nbDaysBeforeExpiration=0;
            nbDaysAfterExpiration= nbDaysAfterExpiration + (int) TimeUnit.MILLISECONDS.toDays(today.getTimeInMillis()-remindDate.getTimeInMillis());
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
    /*
    public static class TrialFilter implements Filter {

        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                ServletException {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            boolean isIgnoringRequest = isIgnoredRequest(httpServletRequest.getSession(true).getServletContext(),
                    httpServletRequest.getRequestURI());
            if ((!outdated) || isIgnoringRequest) {
                chain.doFilter(request, response);
                return;
            }
            if (TrialService.calledUrl == null) {
                TrialService.calledUrl = httpServletRequest.getRequestURI();
            }
            chain.doFilter(request, response);
            return;
        }

        private boolean isIgnoredRequest(ServletContext context, String url) {
            String fileName = url.substring(url.indexOf("/"));
            String mimeType = context.getMimeType(fileName);
            return mimeType != null;
        }
    } */

    public static class UnlockServlet extends HttpServlet {
        private static final long serialVersionUID = -4806814673109318163L;

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

                String rdate=null;
                String hashMD5Added = request.getParameter("hashMD5");
                if (hashMD5Added != null) {
                try {
                    delayPeriod = decodeEvaluationKey(productCode, hashMD5Added);
                } catch (Exception exception) {
                    delayPeriod = 0;
                }
                if (delayPeriod <= 0) {
                    outdated = true;
                    request.setAttribute("errorMessage", "Sorry this evaluation key is not valid.");
                    request.getRequestDispatcher("/jsp/unlockTrial.jsp").include(request, response);
                    return;
                }
                productCode = generateProductCode();
                Utils.writeToFile(Utils.PRODUCT_CODE, productCode, Utils.HOME_CONFIG_FILE_LOCATION);
                outdated = false;
                rdate = computeRemindDateFromTodayBase64();
            try {
                remindDate = Utils.parseDateBase64(rdate);
                computeUnlockedInformation();
                if (!outdated) {
                    Utils.writeRemindDate(rdate, Utils.HOME_CONFIG_FILE_LOCATION);
                }
                response.sendRedirect(TrialService.calledUrl);
                return;
            } catch (Exception exception) {
                delayPeriod = 0;
                outdated = true;
                // rdate is malformed, may be this value is entered manually,
                // which mean that it's a hack
                response.sendRedirect(TrialService.calledUrl);
                return;
            }
        }
            TrialService.calledUrl=request.getRequestURI();
            request.getRequestDispatcher("WEB-INF/jsp/unlockTrial.jsp").include(request, response);
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
                LOG.error("LeadCapture : Error creating HTTP connection to the server : " + pingServerURL);

            } catch (IOException e) {
                LOG.error("LeadCapture : Error creating HTTP connection to the server : " + pingServerURL);
            }
            return false;
        }
    }

}