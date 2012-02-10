package org.exoplatform.trial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class Utils {

  private static Log logger = ExoLogger.getLogger(Utils.class);
  
  public static final int DEFAULT_DELAY_PERIOD = 30;
  public static final String REMIND_DATE = "remindDate";
  public static final String LOOP_FUSE_FORM_DISPLAYED = "formDisplayed";
  public static final String LAST_START_DATE = "LSTD";
  public static final String USER_HOME = System.getProperty("user.home");
  public static final String EXO_HOME_FOLDER = USER_HOME + "/.eXo";
  public static final String PRODUCT_NAME = "Platform";
  public static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
  public static final String PRODUCT_CODE = "ProductCode";
  public static String HOME_CONFIG_FILE_LOCATION = EXO_HOME_FOLDER + "/exokey.xml";

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

  public static String formatDate(Calendar date) {
    return dateFormat.format(date.getTime());
  }

  public static void writeRemindDate(String remindDateStringBase64, String fileLocation) {
    writeToFile(REMIND_DATE, remindDateStringBase64, fileLocation);
  }

  public static void writePingBackFormDisplayed(String fileLocation, boolean loopfuseFormDisplayed) {
    writeToFile(LOOP_FUSE_FORM_DISPLAYED, Boolean.toString(loopfuseFormDisplayed), fileLocation);
  }

  public static Calendar parseDateBase64(String dateString) {
    try {
      dateString = new String(Base64.decodeBase64(dateString.getBytes()));
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dateFormat.parse(dateString));
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
      if (file.exists() && file.exists()) {
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
          logger.error("Error during close outputStream ", ioException);
        }
      }
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException ioException) {
          logger.error("Error during close inputStream ", ioException);
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
