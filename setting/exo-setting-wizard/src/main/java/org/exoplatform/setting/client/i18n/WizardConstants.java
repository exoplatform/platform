package org.exoplatform.setting.client.i18n;

import com.google.gwt.i18n.client.Constants;

public interface WizardConstants extends Constants {


  /*=======================================================================
   * Screen System info
   *======================================================================*/
  
  @DefaultStringValue("Setup")
  String systemInfoTitle();
  
  @DefaultStringValue("We have detected the following environement on your server")
  String systemInfoDescription();
  
  @DefaultStringValue("English")
  String english();
  
  @DefaultStringValue("Français")
  String francais();
  
  @DefaultStringValue("Choose wizard language")
  String chooseLanguage();
  
  @DefaultStringValue("Start")
  String start();
  

  /*=======================================================================
   * Screen Apply Settings
   *======================================================================*/
  
  @DefaultStringValue("Apply settings")
  String applySettings();
  
  @DefaultStringValue("Please wait a few minutes while your server is restarting.<br />You can watch the log file at /var/logs/exo/startup.log")
  String applySettingsDescription();
  
  @DefaultStringValue("Finish")
  String finish();
  

  /*=======================================================================
   * Screen Setup Type
   *======================================================================*/

  @DefaultStringValue("Select a setup type.")
  String selectSetupType();
  
  @DefaultStringValue("Standard (Recommended)")
  String standard();
  
  @DefaultStringValue("Advanced")
  String advanced();
  
  @DefaultStringValue("Display standard options that most administrators have to configure.")
  String displayStandard();
  
  @DefaultStringValue("Display all advanced options, like JCR cache & indexer.")
  String displayAll();
  

  /*=======================================================================
   * Screen Setup Summary
   *======================================================================*/

  @DefaultStringValue("Setup summary")
  String setupSummary();

  @DefaultStringValue("We are ready to setup your portal. Please review the information below. <br />We will apply these settings and restart your server.")
  String weAreReady();

  @DefaultStringValue("Apply")
  String apply();

  @DefaultStringValue("Not yet implemented")
  String notYetImplemented();
  

  /*=======================================================================
   * Screen Super User
   *======================================================================*/

  @DefaultStringValue("Super user")
  String superUser();

  @DefaultStringValue("The super user will have all privileges on your portal")
  String superUserDescription();

  @DefaultStringValue("User name:")
  String userName();

  @DefaultStringValue("Password:")
  String password();

  @DefaultStringValue("Confirm password:")
  String confirmPassword();

  @DefaultStringValue("Email:")
  String email();

  @DefaultStringValue("Invalid user name")
  String invalidUserName();

  @DefaultStringValue("Invalid password")
  String invalidPassword();

  @DefaultStringValue("Passwords are different")
  String differentPasswords();

  @DefaultStringValue("Invalid email")
  String invalidMail();
  

  /*=======================================================================
   * Screen Database JCR
   *======================================================================*/

  @DefaultStringValue("JCR Database setup")
  String jcrDbSetup();

  @DefaultStringValue("The Database will be used to host the JCR.<br /><span style=\"color: red\">/!\\ Attention, you need to install your Database driver.</span>")
  String jcrDbSetupDesc();
  
  @DefaultStringValue("Choose a DataSource:")
  String chooseDs();
  
  @DefaultStringValue("Set your own DataSource:")
  String setYourDs();
  
  @DefaultStringValue("Store files in Database")
  String storeFilesInDb();
  
  @DefaultStringValue("Please indicate your data source:")
  String indicateYourDs();
  
  @DefaultStringValue("No DataSource configured")
  String noDs();
  

  /*=======================================================================
   * Screen IDM
   *======================================================================*/

  @DefaultStringValue("IDM Setup")
  String idmSetup();

  @DefaultStringValue("IDM (Organization Model)")
  String idmSetupDesc();

  @DefaultStringValue("Database")
  String database();

  @DefaultStringValue("LDAP")
  String ldap();
  

  /*=======================================================================
   * Screen Database IDM
   *======================================================================*/

  @DefaultStringValue("IDM Database setup")
  String idmDbSetup();

  @DefaultStringValue("The Database will be used to host the IDM.<br /><span style=\"color: red\">/!\\ Attention, you need to install your Database driver.</span>")
  String idmDbSetupDesc();
  

  /*=======================================================================
   * Screen LDAP config
   *======================================================================*/

  @DefaultStringValue("LDAP Configuration")
  String ldapConfig();
  
  @DefaultStringValue("Configuration where eXo will find users, groups and roles")
  String ldapConfigDesc();

  @DefaultStringValue("Server type:")
  String serverType();

  @DefaultStringValue("Provider URL:")
  String providerUrl();

  @DefaultStringValue("Base DN:")
  String baseDN();

  @DefaultStringValue("Root DN:")
  String rootDN();

  @DefaultStringValue("Invalid Provider URL")
  String invalidProviderUrl();

  @DefaultStringValue("Invalid Base DN")
  String invalidBaseDn();

  @DefaultStringValue("Invalid Root DN")
  String invalidRootDn();
  

  /*=======================================================================
   * Screen FileSystem
   *======================================================================*/

  @DefaultStringValue("Filesystem Setup")
  String fileSystemSetup();
  
  @DefaultStringValue("eXo requires several filesystem directories to work")
  String fileSystemSetupDesc();

  @DefaultStringValue("Logs:")
  String logs();

  @DefaultStringValue("Index:")
  String index();

  @DefaultStringValue("Data values:")
  String dataValues();

  @DefaultStringValue("Invalid logs directory")
  String invalidLogsDirectory();

  @DefaultStringValue("Invalid index directory")
  String invalidIndexDirectory();

  @DefaultStringValue("Invalid data values directory")
  String invalidDataValuesDirectory();
  

  /*=======================================================================
   * Screen MailSetting
   *======================================================================*/

  @DefaultStringValue("Mail settings")
  String mailSettings();
  
  @DefaultStringValue("The mail settings are used by the portal to send notifications")
  String mailSettingDesc();

  @DefaultStringValue("SMTP Host:")
  String smtpHost();

  @DefaultStringValue("Port:")
  String port();

  @DefaultStringValue("Use a secured connection")
  String securedConnection();

  @DefaultStringValue("Invalid SMTP Host")
  String invalidSmtpHost();

  @DefaultStringValue("Invalid port")
  String invalidPort();


  /*=======================================================================
   * Screen Chat Server
   *======================================================================*/

  @DefaultStringValue("Chat Server")
  String chatServer();
  
  @DefaultStringValue("Configure your chat server")
  String configureChatServer();

  @DefaultStringValue("IP or HostName:")
  String ipHostName();

  @DefaultStringValue("Invalid IP or HostName")
  String invalidIpHostName();


  /*=======================================================================
   * Screen Install website
   *======================================================================*/

  @DefaultStringValue("Install a WebSite")
  String installWebsite();
  
  @DefaultStringValue("Install a WebSite to your portal. You can choose a ready-made sample or start fresh with a blank WebSite.")
  String installWebsiteToYourPortal();

  @DefaultStringValue("Start with a blank portal")
  String startBlankPortal();

  @DefaultStringValue("Install a sample")
  String installSample();
  
  

  /*=======================================================================
   * GENERIC
   *======================================================================*/

  @DefaultStringValue("Message")
  String message();

  @DefaultStringValue("Ok")
  String ok();

  @DefaultStringValue("Error")
  String error();

  @DefaultStringValue("Next")
  String next();

  @DefaultStringValue("Previous")
  String previous();
}
