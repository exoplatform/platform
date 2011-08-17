package org.exoplatform.setting.client.ui.model;

import org.exoplatform.setting.client.ui.controller.SetupWizardController;

/**
 * This class is used by controller to get all view datas
 * 
 * @author Clement
 *
 */
public abstract class WizardModel {
  
  protected SetupWizardController controller;
  protected int screenNumber;
  
  public WizardModel(SetupWizardController controller, int screenNumber) {
    this.controller = controller;
    this.screenNumber = screenNumber;
  }
  
  /**
   * Get datas before screen displaying
   */
  public abstract void initDatas();
}
