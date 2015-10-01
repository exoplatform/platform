package org.exoplatform.platform.common.software.register.model;

/**
 * Created by The eXo Platform SEA
 * Author : eXoPlatform
 * toannh@exoplatform.com
 * On 10/1/15
 * Software Registration model
 */
public class SoftwareRegistration {

  private String token_type;
  private String expires_in;
  private String access_token;
  private String error_code;
  private String error_msg;

  public String getError_code() {
    return error_code;
  }

  public void setError_code(String error_code) {
    this.error_code = error_code;
  }

  public String getError_msg() {
    return error_msg;
  }

  public void setError_msg(String error_msg) {
    this.error_msg = error_msg;
  }

  public SoftwareRegistration(){}

  public SoftwareRegistration(String token_type, String expires_in, String access_token) {
    this.token_type = token_type;
    this.expires_in = expires_in;
    this.access_token = access_token;
  }

  public String getToken_type() {
    return token_type;
  }

  public void setToken_type(String token_type) {
    this.token_type = token_type;
  }

  public String getExpires_in() {
    return expires_in;
  }

  public void setExpires_in(String expires_in) {
    this.expires_in = expires_in;
  }

  public String getAccess_token() {
    return access_token;
  }

  public void setAccess_token(String access_token) {
    this.access_token = access_token;
  }
}
