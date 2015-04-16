package org.exoplatform.platform.welcomescreens.service;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.commons.info.MissingProductInformationException;
import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.platform.common.account.setup.web.PingBackServlet;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.filter.Filter;
import org.picocontainer.Startable;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 1/17/13
 */
public class UnlockService implements Startable {


    private static final Log LOG = ExoLogger.getExoLogger(UnlockService.class);
    private static String registrationFormUrl = null;
    private static String defaultPingBackUrl = null;
    private static String extendFormUrl = null;
    private static String subscriptionUrl = null;
    private static String calledUrl = null;
    private static String productCode = null;
    private static String KEY_CONTENT = null;
    private static boolean isUnlocked = false;
    private static boolean showTermsandConditions = true;
    private static boolean outdated = false;
    private static int delayPeriod = Utils.DEFAULT_DELAY_PERIOD;
    private static int nbDaysBeforeExpiration = 0;
    private static int nbDaysAfterExpiration = 0;
    private static Calendar remindDate;
    public static String restContext;
    private static ScheduledExecutorService executor;
    private static ProductInformations productInformations;
    public static String ERROR = "";

    public UnlockService(ProductInformations productInformations, InitParams params) throws MissingProductInformationException {
        restContext = ExoContainerContext.getCurrentContainer().getContext().getRestContextName();
        this.productInformations = productInformations;
        registrationFormUrl = ((ValueParam) params.get("registrationFormUrl")).getValue();
        defaultPingBackUrl = ((ValueParam) params.get("defaultPingBackUrl")).getValue();
        extendFormUrl = ((ValueParam) params.get("extendFormUrl")).getValue();
        subscriptionUrl = ((ValueParam) params.get("subscriptionUrl")).getValue();
        KEY_CONTENT = ((ValueParam) params.get("KeyContent")).getValue().trim();
        String tmpValue = ((ValueParam) params.get("delayPeriod")).getValue();
        delayPeriod = (tmpValue == null || tmpValue.isEmpty()) ? Utils.DEFAULT_DELAY_PERIOD : Integer.parseInt(tmpValue);
        Utils.HOME_CONFIG_FILE_LOCATION = Utils.EXO_HOME_FOLDER + "/" + Utils.PRODUCT_NAME + "/license.xml";
    }

    public void start() {
        if (!new File(Utils.HOME_CONFIG_FILE_LOCATION).exists()) {
            if (checkLicenceInJcr()) return;
            String rdate = UnlockService.computeRemindDateFromTodayBase64();
            productCode = generateProductCode();
            Utils.writeToFile(Utils.PRODUCT_KEY, "", Utils.HOME_CONFIG_FILE_LOCATION);
            Utils.writeToFile(Utils.PRODUCT_CODE, productCode, Utils.HOME_CONFIG_FILE_LOCATION);
            Utils.writeToFile(Utils.REMIND_DATE, rdate, Utils.HOME_CONFIG_FILE_LOCATION);
        }
        productCode = Utils.readFromFile(Utils.PRODUCT_CODE, Utils.HOME_CONFIG_FILE_LOCATION);
        String unlockKey = Utils.readFromFile(Utils.PRODUCT_KEY, Utils.HOME_CONFIG_FILE_LOCATION);
        if ((unlockKey != null) && (!unlockKey.equals(""))) {
            int period = decodeKey(productCode, unlockKey);
            if (period == -1) {
                try {
                    PingBackServlet.writePingBackFormDisplayed(true);
                } catch (MissingProductInformationException e) {
                    LOG.error("Product Information not found ",e.getLocalizedMessage());

                }
                outdated = false;
                isUnlocked = true;
                showTermsandConditions = false;
                return;
            }
        }
        if (checkLicenceInJcr()) return;

        //Read if extended
        String isExtendedString = Utils.readFromFile(Utils.IS_EXTENDED, Utils.HOME_CONFIG_FILE_LOCATION);
        if (isExtendedString != null && !isExtendedString.isEmpty()) {
            isExtendedString = new String(Base64.decodeBase64(isExtendedString.getBytes())) ;
            isUnlocked = Boolean.parseBoolean(isExtendedString);
        }
        // Read: Remind date
        String remindDateString = Utils.readFromFile(Utils.REMIND_DATE, Utils.HOME_CONFIG_FILE_LOCATION);
        try{
            remindDate = Utils.parseDateBase64(remindDateString);
        }   catch (Exception e){
            //Added to not have NPE if user played with licence.xml given by sales
            //or user play with remindDate param in licence.xml
            remindDate = Calendar.getInstance();
            remindDate.set(Calendar.HOUR, 23);
            remindDate.set(Calendar.MINUTE, 59);
            remindDate.set(Calendar.SECOND, 59);
            remindDate.set(Calendar.MILLISECOND, 59);
            ERROR ="your license file is incorrect, please contact our support to fix the problem";
        }

        computeUnlockedInformation();
        // Compute delay period every day
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new Runnable() {
            public void run() {                computeUnlockedInformation();
                if (outdated && isUnlocked) {
                    isUnlocked = false;
                    Utils.writeToFile(Utils.IS_EXTENDED, new String(Base64.encodeBase64("false".getBytes())) , Utils.HOME_CONFIG_FILE_LOCATION);
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private boolean checkLicenceInJcr() {
        try {
            String unlockKey = "";
         String pc = productInformations.getProductCode();
         if ((pc != null) && (!pc.equals(""))) {
                unlockKey = productInformations.getProductKey();
            }
            if ((unlockKey != null) && (!unlockKey.equals(""))) {
                int period = decodeKey(pc, unlockKey);
                if (period == -1) {
                    productCode = pc;
                    outdated = false;
                    isUnlocked = true;
                    PingBackServlet.writePingBackFormDisplayed(true);
                    showTermsandConditions = false;
                    Utils.writeToFile(Utils.PRODUCT_CODE, productCode, Utils.HOME_CONFIG_FILE_LOCATION);
                    Utils.writeToFile(Utils.PRODUCT_KEY, unlockKey, Utils.HOME_CONFIG_FILE_LOCATION);
                    return true;
                }
            }
        }catch (MissingProductInformationException e) {
            LOG.info("");
        }
        return false;
    }

    public void stop() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public static String computeRemindDateFromTodayBase64() {
        Calendar remindDate = Calendar.getInstance();
        remindDate.set(Calendar.HOUR, 23);
        remindDate.set(Calendar.MINUTE, 59);
        remindDate.set(Calendar.SECOND, 59);
        remindDate.set(Calendar.MILLISECOND, 59);
        remindDate.add(Calendar.DAY_OF_MONTH, delayPeriod);
        return Utils.formatDateBase64(remindDate);
    }

    public static String getRegistrationFormUrl() {
        return registrationFormUrl;
    }

    public static int getNbDaysBeforeExpiration() {
        return nbDaysBeforeExpiration;
    }

    public static int getNbDaysAfterExpiration() {
        return nbDaysAfterExpiration;
    }

    public static boolean showTermsAndConditions(){
         return showTermsandConditions;
    }

    public static String getSubscriptionUrl() {
        return subscriptionUrl;
    }

    public static boolean isUnlocked() {
        return isUnlocked;
    }

    public static boolean isOutdated() {
        return outdated;
    }

    public static ScheduledExecutorService getExecutor() {
        return executor;
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
        String productCode = Utils.PRODUCT_NAME + Math.random() + KEY_CONTENT;
        return Utils.getModifiedMD5Code(productCode.getBytes());
    }

    private static void computeUnlockedInformation() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        if (remindDate.compareTo(today) < 0) { // Reminder
            // Date is
            // outdated
            nbDaysBeforeExpiration = 0;
            nbDaysAfterExpiration =  (int) TimeUnit.MILLISECONDS.toDays(today.getTimeInMillis() - remindDate.getTimeInMillis());
            delayPeriod = 0;
            outdated = true;
        } else { // Reminder Date is not yet outdated
            outdated = false;
            nbDaysAfterExpiration = 0;
            nbDaysBeforeExpiration = (int) TimeUnit.MILLISECONDS.toDays(remindDate.getTimeInMillis() - today.getTimeInMillis());
        }
        if (outdated && isUnlocked) {
            isUnlocked = false;
            Utils.writeToFile(Utils.IS_EXTENDED, new String(Base64.encodeBase64("false".getBytes())) , Utils.HOME_CONFIG_FILE_LOCATION);
        }
    }

    private static int decodeKey(String productCode, String Key) {
        try{
        StringBuffer keyBuffer = new StringBuffer(new String(Base64.decodeBase64(Key.getBytes())));
        String keyLengthString = keyBuffer.substring(8, 10);
        int length = Integer.parseInt(keyBuffer.substring(4, 6));
        keyBuffer.replace(4, 6, "");
        String productCodeHashed = keyBuffer.substring(0, length);
        if (!productCodeHashed.equals(Utils.getModifiedMD5Code(productCode.getBytes()))) {
            keyBuffer.replace(6, 8, "");
            productCodeHashed = keyBuffer.substring(0, length);
            if (!productCodeHashed.equals(Utils.getModifiedMD5Code(productCode.getBytes()))){
            return 0;
            }
        }
        String productInfoString = keyBuffer.substring(length);
        String[] productInfo = productInfoString.split(",");

        if ((productInfo.length == 3)) {
            int keyLength = Integer.parseInt(keyLengthString);
            boolean validLicence = (keyLength==keyBuffer.toString().length()+4);
            if(!validLicence)  return 0;
            String nbUser = productInfo[0];
            String duration = productInfo[1];
            String keyDate = productInfo[2];
            DateFormat d = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    d.parse(keyDate);
                } catch (ParseException e) {
                    LOG.info("UNVALID KEY");
                    return 0;
                }
            String edition = "";
            int period = 0;
            try{
            period = Integer.parseInt(duration);
            }catch(NumberFormatException exp) {
                LOG.info("INVALID KAY");
                return 0;
            }
            if (period == -1) {
                duration = Utils.UNLIMITED;
                nbUser = new String(Base64.decodeBase64(nbUser.getBytes()));
                int userNumber = 0;
                try{
                    userNumber = Integer.parseInt(nbUser) / 3;
                }catch(NumberFormatException exp) {
                    LOG.info("INVALID KAY");
                    return 0;
                }
                if (userNumber == -1) {
                    edition = ProductInformations.ENTERPRISE_EDITION;
                    nbUser = Utils.UNLIMITED;
                } else {
                    edition = ProductInformations.EXPRESS_EDITION;
                    nbUser = String.valueOf(userNumber);
                }
            }
            persistInfo(edition, nbUser, keyDate, duration, productCode, Key);
            return period;
        } else if((productInfo.length==1)||(productInfo.length==0)){
            String periodString = new String(Base64.decodeBase64(productInfoString.getBytes()));
            int period = Integer.parseInt(periodString) / 3;
            return period;
        }
        else return 0;
        }
        catch(Exception e){
            return 0;
        }

    }

    private static void persistInfo(String edition, String nbUser, String keyDate, String duration, String productCode, String key) {
        try {
            if(productInformations.getProductKey()==null||(productInformations.getProductKey().equals(""))||
                    (!productCode.equals(productInformations.getProductCode()))||(!key.equals(productInformations.getProductKey()))){
                Properties p = new Properties();
                p.setProperty(ProductInformations.EDITION, edition);
                p.setProperty(ProductInformations.NB_USERS, nbUser);
                p.setProperty(ProductInformations.KEY_GENERATION_DATE, keyDate);
                p.setProperty(ProductInformations.DELAY, duration);
                p.setProperty(ProductInformations.PRODUCT_CODE, productCode);
                p.setProperty(ProductInformations.PRODUCT_KEY, key);
                productInformations.setUnlockInformation(p);
                productInformations.storeUnlockInformation();
            }
        } catch (MissingProductInformationException e) {
            LOG.error("Product Information not found ",e.getLocalizedMessage());
        }
    }

    public static class UnlockFilter implements Filter {
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                ServletException {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;

            boolean isIgnoringRequest = isIgnoredRequest(httpServletRequest.getSession(true).getServletContext(),
                    httpServletRequest.getRequestURI());
            if (!isIgnoringRequest) {
                UnlockService.calledUrl = httpServletRequest.getRequestURI();
            }
            chain.doFilter(request, response);
        }

        private boolean isIgnoredRequest(ServletContext context, String url) {
            String fileName = url.substring(url.indexOf("/"));
            String mimeType = context.getMimeType(fileName);
            return ((mimeType != null) || (url.contains(restContext)));
        }
    }

    public static class UnlockServlet extends HttpServlet {
        private static final long serialVersionUID = -4806814673109318163L;

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            String rdate = null;
            String hashMD5Added = request.getParameter("hashMD5");
            String pc = request.getParameter("pc");
            int delay;
            boolean callPingBack = false;
            String productEdition = "";
            if (hashMD5Added != null) {
                try {
                    if((pc!=null)&&(!pc.equals(productCode)))
                    {
                        delay = decodeKey(pc, hashMD5Added);
                    }
                    else{
                        delay = decodeKey(productCode, hashMD5Added);
                    }
                } catch (Exception exception) {
                    delay = 0;
                }
                if (( delay > -1)&&(delay<=0)) {
                    request.setAttribute("errorMessage", "Sorry this key is not valid.");
                    request.getRequestDispatcher("WEB-INF/jsp/welcome-screens/unlockTrial.jsp").include(request, response);
                    return;
                }
                try {
                    productEdition = productInformations.getEdition();
                } catch (MissingProductInformationException MPIE) {
                    LOG.error("[Unlock Service] : cannot load le platform edition from JCR ",MPIE );
                    productEdition = ProductInformations.EXPRESS_EDITION;
                }
                if (delay == -1) {
                    //shutDown executor -> unlimited duration so no need to computeUnlockInformation everyday
                    //to check if it's outdated
                    if((pc!=null)&&(!pc.equals(productCode))){
                        productCode = pc;
                    }
                    Utils.writeToFile(Utils.PRODUCT_KEY, hashMD5Added, Utils.HOME_CONFIG_FILE_LOCATION);
                    Utils.writeToFile(Utils.PRODUCT_CODE, productCode, Utils.HOME_CONFIG_FILE_LOCATION);
                    outdated = false;
                    isUnlocked = true; // to disappear the trial banner
                   //--- call the enterprise ping back URL (only when the registered edition is and enterprise edition)
                   if ((!callPingBack && (productEdition.equalsIgnoreCase(ProductInformations.ENTERPRISE_EDITION)))) {
                       if(callPingBack()) {
                           LOG.info("[Ping Back] : call to "+defaultPingBackUrl.concat("-ent")+" is done succesfully ");
                           callPingBack = true;
                       }
                   }
                    if (UnlockService.getExecutor() != null)
                        executor.shutdown();
                    response.sendRedirect(UnlockService.calledUrl);
                    return;
                }
                delayPeriod = delay;
                productCode = generateProductCode();
                Utils.writeToFile(Utils.PRODUCT_CODE, productCode, Utils.HOME_CONFIG_FILE_LOCATION);
                outdated = false;
                rdate = computeRemindDateFromTodayBase64();
                try {
                    remindDate = Utils.parseDateBase64(rdate);
                    computeUnlockedInformation();
                    if (!outdated) {
                        Utils.writeRemindDate(rdate, Utils.HOME_CONFIG_FILE_LOCATION);
                        Utils.writeToFile(Utils.IS_EXTENDED, new String(Base64.encodeBase64("true".getBytes())) , Utils.HOME_CONFIG_FILE_LOCATION);
                        isUnlocked = true;
                    }
                    //--- call the enterprise ping back URL (only when the registered edition is and enterprise edition)
                    if ((!callPingBack && (productEdition.equalsIgnoreCase(ProductInformations.ENTERPRISE_EDITION)))) {
                        if(callPingBack()) {
                            LOG.info("[Ping Back] : call to "+defaultPingBackUrl.concat("-ent")+" is done succesfully ");
                            callPingBack = true;
                        }
                    }
                    response.sendRedirect(UnlockService.calledUrl);
                    return;
                } catch (Exception exception) {
                    response.sendRedirect(UnlockService.calledUrl);
                    return;
                }
            }
            if (!UnlockService.isUnlocked())
            request.getRequestDispatcher("WEB-INF/jsp/welcome-screens/unlockTrial.jsp").include(request, response);
            else response.sendRedirect("/portal/intranet");
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            doPost(request, response);
        }
        protected boolean callPingBack () {
            String pingServerURL = defaultPingBackUrl.concat("-ent");
            URL url = null;
            HttpURLConnection urlConn = null;
            try {
                url = new URL(pingServerURL);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.connect();
                return (HttpURLConnection.HTTP_NOT_FOUND != urlConn.getResponseCode());
            } catch (MalformedURLException e) {
                LOG.error("[Ping Back Call] : Error creating HTTP connection to  : " + pingServerURL);

            } catch (IOException e) {
                LOG.error("[Ping Back Call] : Error creating HTTP connection to : " + pingServerURL);
            } finally {
                urlConn.disconnect();
                LOG.info("[Ping Back Call] : connection to ["+pingServerURL+"] is released");
            }
            return false;
        }
    }
}