package org.exoplatform.platform.common;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Utils {
  public static String productNameAndVersion = "";
  public static String pingServerURL = "";
  public static String registrationFormUrl;
  public final static int DEFAULT_DELAY_PERIOD = 30;
  public static int delayPeriod = DEFAULT_DELAY_PERIOD;
  public static int daysBeforeExpire = 0;
  public static boolean loopfuseFormDisplayed = false;
  public static String KEY_CONTENT;
  public static boolean outdated = false;
  public static String pingBackUrl;
  public static final String LEAD_CAPTURE_KEY = "UnlockKey";
  public static final String REMIND_DATE = "remindDate";
  public static final String LOOP_FUSE_FORM_DISPLAYED = "formDisplayed";
  public static final String LAST_START_DATE = "LSTD";
  public final static String USER_HOME = System.getProperty("user.home");
  public final static String EXO_HOME_FOLDER = USER_HOME + "/.eXo";
  public final static String HOME_CONFIG_FILE_LOCATION = EXO_HOME_FOLDER + "/exokey.xml";
  private final static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

  public static String getModifiedMD5Code(byte[] dataToHash) throws NoSuchAlgorithmException {
    Security.addProvider(new BouncyCastleProvider());
    Provider provBC = Security.getProvider("BC");
    MessageDigest digest = MessageDigest.getInstance("MD5", provBC);
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

  public static String formatDate(Calendar date) {
    return dateFormat.format(date.getTime());
  }

  public static Calendar parseDate(String dateString) throws ParseException {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateFormat.parse(dateString));
    return calendar;
  }

  public static String readFromFile(String key, String fileLocation) throws InvalidPropertiesFormatException, IOException {
    if (fileLocation == null || fileLocation.isEmpty() || !new File(fileLocation).exists()) {
      throw new IllegalArgumentException("Illegal file Location parameter: " + fileLocation);
    }
    Properties properties = new Properties();
    InputStream inputStream = new FileInputStream(fileLocation);
    properties.loadFromXML(inputStream);
    inputStream.close();
    return (String) properties.get(key);
  }

  public static void writeToFile(String key, String value, String fileLocation, boolean addDefaultTrialKeyIfNotExists)
      throws InvalidPropertiesFormatException, IOException {
    if (fileLocation == null || fileLocation.isEmpty()) {
      throw new IllegalArgumentException("Illegal empty file Location parameter.");
    }
    Properties properties = new Properties();
    if (new File(fileLocation).exists()) {
      InputStream inputStream = new FileInputStream(fileLocation);
      properties.loadFromXML(inputStream);
      inputStream.close();
    } else {
      verifyAndCreateParentFolder(fileLocation);
      if (addDefaultTrialKeyIfNotExists) {
        properties.put(LEAD_CAPTURE_KEY, "");
      }
      OutputStream outputStream = new FileOutputStream(fileLocation);
      properties.storeToXML(outputStream, fileLocation);
      outputStream.close();
    }
    properties.put(key, value);
    OutputStream outputStream = new FileOutputStream(fileLocation);
    properties.storeToXML(outputStream, "");
    outputStream.close();
  }

  public static void writeTrialKey(String hashMD5, String fileLocation) throws InvalidPropertiesFormatException, IOException {
    writeToFile(LEAD_CAPTURE_KEY, hashMD5, fileLocation, true);
  }

  public static void writeRemindDate(String remindDateStringBase64, String fileLocation) throws InvalidPropertiesFormatException,
      IOException {
    writeToFile(REMIND_DATE, remindDateStringBase64, fileLocation, true);
  }

  public static void writePingBackFormDisplayed(String fileLocation) throws InvalidPropertiesFormatException, IOException {
    writeToFile(LOOP_FUSE_FORM_DISPLAYED, Boolean.toString(Utils.loopfuseFormDisplayed), fileLocation, true);
  }

  public static Calendar parseDateBase64(String dateString) throws ParseException {
    return parseDate(new String(Base64.decodeBase64(dateString.getBytes())));
  }

  public static String formatDateBase64(Calendar date) {
    return new String(Base64.encodeBase64(formatDate(date).getBytes()));
  }

  public static String computeRemindDateFromTodayBase64() {
    if (delayPeriod <= 0 || outdated) {
      return "";
    }
    Calendar remindDate = Calendar.getInstance();
    remindDate.add(Calendar.DAY_OF_MONTH, delayPeriod);
    return formatDateBase64(remindDate);
  }

  private static void verifyAndCreateParentFolder(String fileLocation) {
    String parentFolderPath = fileLocation;
    int parentFolderPathEndIndex = fileLocation.lastIndexOf("/");
    if (parentFolderPathEndIndex < 0) {
      parentFolderPathEndIndex = fileLocation.lastIndexOf("\\");
      if (parentFolderPathEndIndex >= 0) {
        parentFolderPath = fileLocation.substring(0, parentFolderPathEndIndex);
      }
    }
    if (!new File(parentFolderPath).exists()) {
      new File(parentFolderPath).mkdirs();
    }
  }
}
