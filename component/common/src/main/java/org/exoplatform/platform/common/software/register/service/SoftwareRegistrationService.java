package org.exoplatform.platform.common.software.register.service;

import org.exoplatform.platform.common.software.register.model.SoftwareRegistration;

/**
 * Created by The eXo Platform SEA
 * Author : eXoPlatform
 * toannh@exoplatform.com
 * On 9/30/15
 * Software register to Tribe service
 */
public interface SoftwareRegistrationService {

  public final static String SOFTWARE_REGISTRATION_NODE = "softwareRegistrationNode";
  public final static String SOFTWARE_REGISTRATION_SKIPPED = "softwareRegistrationSkipped";
  public final static String SOFTWARE_REGISTRATION_HOST = "http://192.168.3.36:8080";
  public final static String SOFTWARE_REGISTRATION_PATH = "/portal/authorize";
  public final static String SOFTWARE_REGISTRATION_RETURN_URL = "http://{0}:{1}/registrationPLF/software-register-auth";
  public final static String SOFTWARE_REGISTRATION_CLIENT_ID = "client_id=x6iCo6YWmw";
  public final static String SOFTWARE_REGISTRATION_RESPONSE_TYPE = "response_type=code";
  public final static String SOFTWARE_REGISTRATION_SKIP = "accountsetup.register.skip";

  /**
   * Check has your software registered to Tribe
   * @return boolean value
   */
  public boolean isSoftwareRegistered();

  /**
   * Check is registered and create if not exist
   */
  public void checkSoftwareRegistration();

  /**
   * get Skipped number
   * max is 2
   * @return
   */
  public int getSkippedNumber();

  public void updateSkippedNumber();

  /**
   * Get access token from community side
   * @param code
   * @return
   */
  public SoftwareRegistration registrationPLF(String code, String returnURL);

  /**
   * Check configuration allow skip platform register
   * @return
   */
  public boolean isSkipPlatformRegistration();
}
