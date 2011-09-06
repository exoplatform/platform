package org.exoplatform.setting.client.ui.model;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.exoplatform.setting.client.ui.controller.SetupWizardController;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SystemInfoWizardModel extends WizardModel {

  private Map<String, String> systemInfoOptions;

  public SystemInfoWizardModel(SetupWizardController controller, int screenNumber) {
    super(controller, screenNumber);
  }

  @Override
  public void initDatas() {

    // Build callback method to get system properties
    AsyncCallback<Map<String, String>> callbackSystemProperties = new AsyncCallback<Map<String, String>>() {

      public void onFailure(Throwable arg0) {
        Logger.getLogger("SystemInfoWizardModel").log(Level.SEVERE, "Model is not loaded");
      }

      public void onSuccess(Map<String, String> arg0) {
        systemInfoOptions = arg0;
        controller.fireModelLoaded(screenNumber);
      }
    };
    controller.getSystemInfoProperties(callbackSystemProperties);
  }

  public Map<String, String> getSystemInfoOptions() {
    return systemInfoOptions;
  }

}
