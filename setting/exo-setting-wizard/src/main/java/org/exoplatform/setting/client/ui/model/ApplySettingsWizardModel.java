package org.exoplatform.setting.client.ui.model;

import org.exoplatform.setting.client.ui.controller.SetupWizardController;

public class ApplySettingsWizardModel extends WizardModel {

  public ApplySettingsWizardModel(SetupWizardController controller, int screenNumber) {
    super(controller, screenNumber);
  }

  @Override
  public void initDatas() {
    controller.fireModelLoaded(screenNumber);
  }

}
