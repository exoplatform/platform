package org.exoplatform.setting.client;

public class WizardUtility {

  
  public static int getToStepId(String toStepId) {
    int stepId = 0;
    
    // "toStep12"
    String strStepId = toStepId.replace("toStep", "");
    stepId = Integer.valueOf(strStepId);
    
    return stepId;
  }
  
  public static int getNextStepId(int stepId) {
    return stepId + 1;
  }
  
  public static int getPreviousStepId(int stepId) {
    int newStepId = 0;
    if(stepId > 0) {
      newStepId = stepId - 1;
    }
    return newStepId;
  }
  
}
