package org.exoplatform.setting.shared.data;



/**
 * For each property configured by user, there is an enum here.
 * 
 * @author Clement
 *
 */
public enum SetupWizardData {
  
  // Super User
  USERNAME ("exo.super.user"),
  PASSWORD ("exo.super.user.password"),
  EMAIL    ("exo.super.user.email");

  private String propertyName;
  
  private SetupWizardData(String propertyName) {
    this.propertyName = propertyName;
  }
  
  public String getPropertyName() {
    return this.propertyName;
  }
}