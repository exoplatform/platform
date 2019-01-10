package org.exoplatform.platform.common.software.register;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.picocontainer.Startable;

import org.exoplatform.commons.info.MissingProductInformationException;
import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.platform.common.account.setup.web.PingBackService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 */
public class UnlockService implements Startable {

    private static final Log LOG = ExoLogger.getExoLogger(UnlockService.class);

    private String registrationFormUrl;
    private String defaultPingBackUrl;
    private String extendFormUrl;
    private String subscriptionUrl;
    private String calledUrl = null;
    private String productCode = null;
    private String keyContent;
    private boolean unlocked = false;
    private boolean showTermsandConditions = true;
    private boolean outdated = false;
    private int delayPeriod;
    private int nbDaysBeforeExpiration = 0;
    private int nbDaysAfterExpiration = 0;
    private Calendar remindDate;
    private ScheduledExecutorService executor;
    public String ERROR = "";

    private ProductInformations productInformations;

    private PingBackService pingBackService;

    public UnlockService(ProductInformations productInformations, PingBackService pingBackService, InitParams params)
            throws MissingProductInformationException {
        this.productInformations = productInformations;
        this.pingBackService = pingBackService;
        registrationFormUrl = ((ValueParam) params.get("registrationFormUrl")).getValue();
        defaultPingBackUrl = ((ValueParam) params.get("defaultPingBackUrl")).getValue();
        extendFormUrl = ((ValueParam) params.get("extendFormUrl")).getValue();
        subscriptionUrl = ((ValueParam) params.get("subscriptionUrl")).getValue();
        keyContent = ((ValueParam) params.get("KeyContent")).getValue().trim();
        String tmpValue = ((ValueParam) params.get("delayPeriod")).getValue();
        delayPeriod = (tmpValue == null || tmpValue.isEmpty()) ? Utils.DEFAULT_DELAY_PERIOD : Integer.parseInt(tmpValue);
        String licensePath = params.getValueParam("exo.license.path").getValue();
        Utils.HOME_CONFIG_LOCATION = Utils.EXO_HOME_FOLDER + "/" + Utils.PRODUCT_NAME;
        Utils.HOME_CONFIG_FILE_LOCATION = Utils.HOME_CONFIG_LOCATION + "/" + Utils.LICENSE_FILE;
        if(StringUtils.isNotBlank(licensePath) && !StringUtils.equals(licensePath, Utils.HOME_CONFIG_FILE_LOCATION)) {
            checkCustomizeFolder(licensePath);
        }

    }

    public void start() {
        if (!new File(Utils.HOME_CONFIG_FILE_LOCATION).exists()) {
            if (checkLicenceInJcr()) return;
            String rdate = computeRemindDateFromTodayBase64();
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
                    pingBackService.writePingBackFormDisplayed(true);
                } catch (MissingProductInformationException e) {
                    LOG.error("Product Information not found", e);

                }
                outdated = false;
                unlocked = true;
                showTermsandConditions = false;
                return;
            }
        }
        if (checkLicenceInJcr()) return;

        //Read if extended
        String isExtendedString = Utils.readFromFile(Utils.IS_EXTENDED, Utils.HOME_CONFIG_FILE_LOCATION);
        if (isExtendedString != null && !isExtendedString.isEmpty()) {
            isExtendedString = new String(Base64.decodeBase64(isExtendedString.getBytes())) ;
            unlocked = Boolean.parseBoolean(isExtendedString);
        }
        // Read: Remind date
        String remindDateString = Utils.readFromFile(Utils.REMIND_DATE, Utils.HOME_CONFIG_FILE_LOCATION);
        try {
            remindDate = Utils.parseDateBase64(remindDateString);
        } catch (Exception e){
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
        executor.scheduleWithFixedDelay(() -> {
            computeUnlockedInformation();
            if (outdated && unlocked) {
                unlocked = false;
                Utils.writeToFile(Utils.IS_EXTENDED, new String(Base64.encodeBase64("false".getBytes())) ,
                        Utils.HOME_CONFIG_FILE_LOCATION);
            }
        }, 1, 1, TimeUnit.MINUTES);
    }


    /**
     * Check and update customize path
     * @param lisensePath
     */
    private void checkCustomizeFolder(String lisensePath){
        File lisenseFile = new File(lisensePath);
        if(!StringUtils.endsWith(lisensePath, Utils.LICENSE_FILE)) {
            if(lisenseFile.exists() && lisenseFile.mkdirs()) {
                LOG.error("The customize lisense.xml path cannot be use, default value will be applied.");
                return;
            }
            if(lisenseFile.isFile()){
                if(lisenseFile.canWrite()) {
                    Utils.HOME_CONFIG_LOCATION = lisenseFile.getParent();
                    Utils.HOME_CONFIG_FILE_LOCATION = lisenseFile.getPath();
                }
            } else {
                Utils.HOME_CONFIG_LOCATION = lisenseFile.getPath();
                Utils.HOME_CONFIG_FILE_LOCATION = Utils.HOME_CONFIG_LOCATION + "/" + Utils.LICENSE_FILE;
            }
        }else {
            if ((lisenseFile.getParentFile().exists() && lisenseFile.canWrite())
                || lisenseFile.getParentFile().mkdirs()) {
                Utils.HOME_CONFIG_LOCATION = lisenseFile.getParent();
                Utils.HOME_CONFIG_FILE_LOCATION = lisenseFile.getPath();
            }
        }
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
                    unlocked = true;
                    pingBackService.writePingBackFormDisplayed(true);
                    showTermsandConditions = false;
                    Utils.writeToFile(Utils.PRODUCT_CODE, productCode, Utils.HOME_CONFIG_FILE_LOCATION);
                    Utils.writeToFile(Utils.PRODUCT_KEY, unlockKey, Utils.HOME_CONFIG_FILE_LOCATION);
                    return true;
                }
            }
        } catch (MissingProductInformationException e) {
            LOG.info("");
        }
        return false;
    }

    public void stop() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public String computeRemindDateFromTodayBase64() {
        Calendar remindDate = Calendar.getInstance();
        remindDate.set(Calendar.HOUR, 23);
        remindDate.set(Calendar.MINUTE, 59);
        remindDate.set(Calendar.SECOND, 59);
        remindDate.set(Calendar.MILLISECOND, 59);
        remindDate.add(Calendar.DAY_OF_MONTH, delayPeriod);
        return Utils.formatDateBase64(remindDate);
    }

    public ProductInformations getProductInformations() {
        return productInformations;
    }

    public String getRegistrationFormUrl() {
        return registrationFormUrl;
    }

    public String getDefaultPingBackUrl() {
        return defaultPingBackUrl;
    }

    public int getNbDaysBeforeExpiration() {
        return nbDaysBeforeExpiration;
    }

    public int getNbDaysAfterExpiration() {
        return nbDaysAfterExpiration;
    }

    public boolean showTermsAndConditions(){
         return showTermsandConditions;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    public int getDelayPeriod() {
        return delayPeriod;
    }

    public void setDelayPeriod(int delayPeriod) {
        this.delayPeriod = delayPeriod;
    }

    public String getCalledUrl() {
        return calledUrl;
    }

    public void setCalledUrl(String calledUrl) {
        this.calledUrl = calledUrl;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Calendar getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(Calendar remindDate) {
        this.remindDate = remindDate;
    }

    public String generateProductCode() {
        String productCode = Utils.PRODUCT_NAME + Math.random() + keyContent;
        return Utils.getModifiedMD5Code(productCode.getBytes());
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void computeUnlockedInformation() {
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
        if (outdated && unlocked) {
            unlocked = false;
            Utils.writeToFile(Utils.IS_EXTENDED, new String(Base64.encodeBase64("false".getBytes())),
                    Utils.HOME_CONFIG_FILE_LOCATION);
        }
    }

    public int decodeKey(String productCode, String Key) {
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

    private void persistInfo(String edition, String nbUser, String keyDate, String duration, String productCode, String key) {
        try {
            if(productInformations.getProductKey()==null || productInformations.getProductKey().equals("")
                    || !productCode.equals(productInformations.getProductCode())
                    || !key.equals(productInformations.getProductKey())) {
                Properties p = new Properties();
                p.setProperty(ProductInformations.EDITION, edition);
                p.setProperty(ProductInformations.NB_USERS, nbUser);
                p.setProperty(ProductInformations.KEY_GENERATION_DATE, keyDate);
                p.setProperty(ProductInformations.DELAY, duration);
                p.setProperty(ProductInformations.PRODUCT_CODE, productCode);
                p.setProperty(ProductInformations.PRODUCT_KEY, key);
                productInformations.setUnlockInformation(p);
                productInformations.storeProductInformation(productInformations.getProductInformation());
            }
        } catch (MissingProductInformationException e) {
            LOG.error("Product Information not found", e);
        }
    }
}