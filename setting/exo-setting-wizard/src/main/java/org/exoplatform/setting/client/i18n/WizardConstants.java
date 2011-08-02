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
