package org.exoplatform.setting.shared.data;



/**
 * For each property configured by user, there is an enum here.
 * 
 * @author Clement
 *
 */
public enum SetupWizardData {
  
  // Super User
  SU_USERNAME ("exo.super.user"),
  SU_PASSWORD ("exo.super.user.password"),
  SU_EMAIL    ("exo.super.user.email"),
  
  JCR_DATA_SOURCE   ("gatein.jcr.datasource.name"),
  STORE_FILES_IN_DB ("gatein.jcr.store.files.db"),
  
  IDM_DATA_SOURCE ("gatein.idm.datasource.name"),

  LDAP_SERVER_TYPE  ("ldap.server.type"),
  LDAP_PROVIDER_URL ("ldap.provider.url"),
  LDAP_BASE_DN      ("ldap.base.dn"),
  LDAP_ROOT_DN      ("ldap.root.dn"),
  LDAP_PASSWORD     ("ldap.password"),
  
  FS_LOGS        ("filesystem.logs"),
  FS_INDEX       ("filesystem.index"),
  FS_DATA_VALUES ("filesystem.data.values"),
  
  SMTP_HOST               ("gatein.email.smtp.host"),
  SMTP_PORT               ("gatein.email.smtp.port"),
  SMTP_SECURED_CONNECTION ("gatein.email.smtp.auth"),
  SMTP_USERNAME           ("gatein.email.smtp.username"),
  SMTP_PASSWORD           ("gatein.email.smtp.password"),
  SMTP_EMAIL              ("gatein.email.smtp.from"),
  
  CHAT_IP   ("exo.chat.server"),
  CHAT_PORT ("exo.chat.port"),
  
  WS_BLANK   ("exo.website.blank"),
  WS_SAMPLES ("exo.website.samples");

  private String propertyName;
  
  private SetupWizardData(String propertyName) {
    this.propertyName = propertyName;
  }
  
  public String getPropertyName() {
    return this.propertyName;
  }
}