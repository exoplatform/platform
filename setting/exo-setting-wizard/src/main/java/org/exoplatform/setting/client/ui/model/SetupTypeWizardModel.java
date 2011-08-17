package org.exoplatform.setting.client.ui.model;

import org.exoplatform.setting.client.ui.controller.SetupWizardController;

public class SetupTypeWizardModel extends WizardModel {

  public SetupTypeWizardModel(SetupWizardController controller, int screenNumber) {
    super(controller, screenNumber);
  }

  @Override
  public void initDatas() {
    controller.fireModelLoaded(screenNumber);
  }

}
