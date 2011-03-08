package org.exoplatform.trial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.exoplatform.commons.platform.info.PlatformInfo;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.picocontainer.Startable;

public class TrialService implements Startable {

  private static final String PRODUCT_NAME = "Platform";
  public static Calendar remindDate;
  private ScheduledExecutorService executor;

  public TrialService(PlatformInfo platformInfo, InitParams params) {
    Utils.productNameAndVersion = PRODUCT_NAME + " " + platformInfo.getVersion();
    Utils.registrationFormUrl = ((ValueParam) params.get("registrationFormUrl")).getValue();
    Utils.KEY_CONTENT = ((ValueParam) params.get("KeyContent")).getValue().trim();
    String tmpValue = ((ValueParam) params.get("delayPeriod")).getValue();
    Utils.delayPeriod = tmpValue == null ? Utils.DEFAULT_DELAY_PERIOD : Integer.parseInt(tmpValue);
  }

  public void start() {
    Properties properties = new Properties();
    if (new File(Utils.CONFIG_FILE_LOCATION).exists()) {
      InputStream inputStream;
      try {
        inputStream = new FileInputStream(Utils.CONFIG_FILE_LOCATION);
      } catch (FileNotFoundException exception) {
        throw new RuntimeException(Utils.CONFIG_FILE_LOCATION + " file couldn't be found.", exception);
      }
      try {
        properties.loadFromXML(inputStream);
      } catch (InvalidPropertiesFormatException exception) {
        throw new RuntimeException("File format error: " + Utils.CONFIG_FILE_LOCATION, exception);
      } catch (IOException exception) {
        throw new RuntimeException("Error while reading file: " + Utils.CONFIG_FILE_LOCATION, exception);
      }
      try {
        inputStream.close();
      } catch (IOException exception) {
        throw new RuntimeException("Error while closing stream after reading file: " + Utils.CONFIG_FILE_LOCATION, exception);
      }
    } else {
      if (!new File(Utils.EXO_HOME_FOLDER).exists()) {
        new File(Utils.EXO_HOME_FOLDER).mkdirs();
      }
      properties.put(Utils.LEAD_CAPTURE_KEY, "");
      properties.put(Utils.REMIND_DATE, "");
      OutputStream outputStream;
      try {
        outputStream = new FileOutputStream(Utils.CONFIG_FILE_LOCATION);
        properties.storeToXML(outputStream, Utils.CONFIG_FILE_LOCATION);
      } catch (FileNotFoundException exception) {
        throw new RuntimeException("Error while creating file: " + Utils.CONFIG_FILE_LOCATION + ". The file may be locked.",
            exception);
      } catch (IOException exception) {
        throw new RuntimeException("Error while storing entries in file: " + Utils.CONFIG_FILE_LOCATION
            + ". The file may be locked.", exception);
      }
      return;
    }

    InputStream propertiesInputStream;
    try {
      propertiesInputStream = new FileInputStream(Utils.CONFIG_FILE_LOCATION);
      properties.loadFromXML(propertiesInputStream);
    } catch (FileNotFoundException exception) {
      throw new RuntimeException("Error while creating file: " + Utils.CONFIG_FILE_LOCATION + ". The file may be locked.",
          exception);
    } catch (InvalidPropertiesFormatException exception) {
      throw new RuntimeException("File format error: " + Utils.CONFIG_FILE_LOCATION, exception);
    } catch (IOException exception) {
      throw new RuntimeException("Error while reading file: " + Utils.CONFIG_FILE_LOCATION, exception);
    }
    try {
      propertiesInputStream.close();
    } catch (IOException exception) {
      throw new RuntimeException("Error while closing stream after reading file: " + Utils.CONFIG_FILE_LOCATION, exception);
    }

    String hashMD5Added = "";
    if (!properties.containsKey(Utils.LEAD_CAPTURE_KEY)) {
      if (!properties.containsKey(Utils.productNameAndVersion)) {
        Enumeration<Object> keys = properties.keys();
        if (keys.hasMoreElements()) {
          hashMD5Added = properties.getProperty((String) keys.nextElement());
        }
      } else {
        hashMD5Added = properties.getProperty(Utils.productNameAndVersion);
      }
    } else {
      hashMD5Added = properties.getProperty(Utils.LEAD_CAPTURE_KEY);
    }
    String keyContent;
    try {
      keyContent = Utils.getModifiedMD5Code(Utils.KEY_CONTENT.getBytes());
    } catch (NoSuchAlgorithmException exception) {
      throw new RuntimeException("Error while encoding the key.", exception);
    }
    TrialFilter.unlocked = hashMD5Added != null && !hashMD5Added.equals("") && hashMD5Added.equals(keyContent);
    if (!TrialFilter.unlocked) {
      String remindDateString = properties.getProperty(Utils.REMIND_DATE);
      if (remindDateString == null || remindDateString.equals("")) {
        return;
      } else {
        try {
          remindDate = Utils.parseDateBase64(remindDateString);
        } catch (ParseException exception) {
          throw new RuntimeException("Error while decoding the file content.", exception);
        }
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new Runnable() {
          public void run() {
            TrialService.this.computeUnlockedInformation();
          }
        }, 0, 1, TimeUnit.DAYS);
      }
    }
  }

  private void computeUnlockedInformation() {
    Calendar today = Calendar.getInstance();
    today.set(Calendar.HOUR, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    today.set(Calendar.MILLISECOND, 0);
    if (remindDate.compareTo(today) < 0 || Utils.delayPeriod <= 0) { // Reminder Date is outdated
      Utils.outdated = true;
      TrialFilter.unlocked = false;
    } else { // Reminder Date is not yet outdated
      TrialFilter.unlocked = true;
    }
  }

  public void stop() {
    if (executor != null) {
      executor.shutdown();
    }
  }
}