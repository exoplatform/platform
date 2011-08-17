package org.exoplatform.setting.client;

import org.exoplatform.setting.client.ui.controller.SetupWizardController;

import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WizardModule implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    SetupWizardController controller = new SetupWizardController();
    controller.start();
  }
}
