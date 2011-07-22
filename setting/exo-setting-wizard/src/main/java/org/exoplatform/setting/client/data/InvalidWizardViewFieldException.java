package org.exoplatform.setting.client.data;

public class InvalidWizardViewFieldException extends Exception {
  
  private String message;

  public InvalidWizardViewFieldException() {
    this.message = "Invalid field";
  }
  
  public InvalidWizardViewFieldException(String message) {
    this.message = message;
  }
  
  public String getWizardMessage() {
    return this.message;
  }
}
