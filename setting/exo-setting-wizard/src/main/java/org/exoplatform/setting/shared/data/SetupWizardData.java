package org.exoplatform.setting.shared.data;

/**
 * For each property configured by user, there is an enum here.
 * 
 * @author Clement
 *
 */
public enum SetupWizardData {
  
  // Super User
  USERNAME ("User name", "exo.super.user"),
  PASSWORD ("Password", "exo.super.user.password"),
  EMAIL    ("Email", "exo.super.user.email");

  private String name;
  private String propertyName;
  
  private SetupWizardData(String name, String propertyName) {
    this.name = name;
    this.propertyName = propertyName;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getPropertyName() {
    return this.propertyName;
  }
}
