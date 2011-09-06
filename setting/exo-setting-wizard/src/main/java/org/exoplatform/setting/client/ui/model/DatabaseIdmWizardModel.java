package org.exoplatform.setting.client.ui.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.exoplatform.setting.client.ui.controller.SetupWizardController;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class DatabaseIdmWizardModel extends WizardModel {

  private List<String> datasources;

  public DatabaseIdmWizardModel(SetupWizardController controller, int screenNumber) {
    super(controller, screenNumber);
  }

  @Override
  public void initDatas() {

    // Build callback method to get datasources
    AsyncCallback<List<String>> callbackDs = new AsyncCallback<List<String>>() {

      public void onFailure(Throwable arg0) {
        Logger.getLogger("DatabaseIdmWizardModel").log(Level.SEVERE, "Model is not loaded");
      }

      public void onSuccess(List<String> arg0) {
        datasources = arg0;
        controller.fireModelLoaded(screenNumber);
      }
    };
    controller.getDatasources(callbackDs);
  }

  public List<String> getDatasources() {
    return datasources;
  }

}
