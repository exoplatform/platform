package org.exoplatform.platform.common.software.register;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Properties;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 */

public class Utils {
    public static final String UNLIMITED="UNLIMITED";
    private static final Log LOG = ExoLogger.getLogger(Utils.class);
    public static final int DEFAULT_DELAY_PERIOD = 30;
    public static final String REMIND_DATE = "remindDate";
    //this information would be put in the license.xml file, not problem even if user force it to true (hack tentation)
    // it will only hide the bar
    public static final String IS_EXTENDED = "extension";
    public static final String PRODUCT_KEY = "license";
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String EXO_HOME_FOLDER = USER_HOME + "/.eXo";
    public static final String PRODUCT_NAME = "Platform";
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final String PRODUCT_CODE = "ProductCode";
    public static String HOME_CONFIG_FILE_LOCATION;
    public static String HOME_CONFIG_LOCATION;
    public static final String LICENSE_FILE = "license.xml";
    public static final String SW_REG_SKIPPED = "skipped";
    public static final String SW_REG_STATUS = "status";
    public static final String SW_REG_PLF_VERSION = "version";

    public static String getModifiedMD5Code(byte[] dataToHash) {
        Security.addProvider(new BouncyCastleProvider());
        Provider provBC = Security.getProvider("BC");
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5", provBC);
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
        digest.update(dataToHash);
        byte[] hashMD5 = digest.digest(dataToHash);
        StringBuffer hashMD5String = new StringBuffer();
        for (int i = 0; i < hashMD5.length; i++) {
            hashMD5[i] %= 26;
            hashMD5[i] = (byte) Math.abs(hashMD5[i]);
            hashMD5[i] += ((byte) 'A' - 1);
            hashMD5String.append(((char) hashMD5[i]));
        }
        return hashMD5String.toString();
    }

    public static String readFromFile(String key, String fileLocation) {
        if (fileLocation == null || fileLocation.isEmpty() || !new File(fileLocation).exists()) {
            throw new IllegalArgumentException("Illegal file Location parameter: " + fileLocation);
        }
        try {
            Properties properties = new Properties();
            InputStream inputStream = new FileInputStream(fileLocation);
            properties.loadFromXML(inputStream);
            inputStream.close();
            return (String) properties.get(key);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static String formatDate(Calendar calendar) {
        return LocalDateTime.ofInstant(calendar.getTime().toInstant(), calendar.getTimeZone().toZoneId()).format(dateFormat);
    }

    public static void writeRemindDate(String remindDateStringBase64, String fileLocation) {
        writeToFile(REMIND_DATE, remindDateStringBase64, fileLocation);
    }

    public static Calendar parseDateBase64(String dateString) {
        try {
            dateString = new String(Base64.decodeBase64(dateString.getBytes()));
            Calendar calendar = Calendar.getInstance();
            Instant instant = LocalDate.parse(dateString, dateFormat).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
            calendar.setTime(Date.from(instant));
            return calendar;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static String formatDateBase64(Calendar date) {
        return new String(Base64.encodeBase64(formatDate(date).getBytes()));
    }

    public static void writeToFile(String key, String value, String fileLocation) {
        if (fileLocation == null || fileLocation.isEmpty()) {
            throw new IllegalArgumentException("Illegal empty file Location parameter.");
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Properties properties = new Properties();
            File file = new File(fileLocation);
            if (file.exists()) {
                inputStream = new FileInputStream(fileLocation);
                properties.loadFromXML(inputStream);
                inputStream.close();
            } else {
                verifyAndCreateParentFolder(fileLocation);
            }
            properties.put(key, value);
            outputStream = new FileOutputStream(fileLocation);
            properties.storeToXML(outputStream, "");
            outputStream.close();
        } catch (Exception exception) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ioException) {
                    LOG.error("Error during close outputStream ", ioException);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioException) {
                    LOG.error("Error during close inputStream ", ioException);
                }
            }
        }
    }

    private static void verifyAndCreateParentFolder(String fileLocation) {
        String parentFolderPath = fileLocation.replace("\\", "/");
        int parentFolderPathEndIndex = fileLocation.lastIndexOf("/");
        if (parentFolderPathEndIndex >= 0) {
            parentFolderPath = fileLocation.substring(0, parentFolderPathEndIndex);
        }
        if (!new File(parentFolderPath).exists()) {
            new File(parentFolderPath).mkdirs();
        }
    }
}
