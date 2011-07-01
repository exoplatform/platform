package org.exoplatform.setting.client;

public class WizardUtility {

  
  public static int getToStepId(String toStepId) {
    int stepId = 0;
    
    // "toStep12"
    String strStepId = toStepId.replace("toStep", "");
    stepId = Integer.valueOf(strStepId);
    
    return stepId;
  }
  
}
