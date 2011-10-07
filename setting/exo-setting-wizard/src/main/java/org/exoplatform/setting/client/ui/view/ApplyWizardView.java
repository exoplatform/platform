package org.exoplatform.setting.client.ui.view;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * View Apply Setting
 * 
 * @author Clement
 *
 */
public class ApplyWizardView extends WizardView {
  
  public ApplyWizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    super(controller, stepNumber, mode);
  }

  @Override
  protected String getWizardTitle() {
    return constants.applySettings();
  }

  @Override
  protected String getWizardDescription() {
    return constants.applySettingsDescription();
  }

  @Override
  protected Widget buildStepToolbar() {
    
    FlowPanel panel = new FlowPanel();
    panel.add(prepareButton(constants.finish(), 0));
    
    return panel;
  }

  @Override
  protected Widget buildStepContent() {

    Grid advancedOptions = new Grid(4, 2);
    advancedOptions.setCellSpacing(6);
    
    return advancedOptions;
  }

  @Override
  public Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException {
    return null;
  }

  @Override
  public void executeOnDisplay() {
    // Build callback method
    AsyncCallback<String> callback = new AsyncCallback<String>() {

      public void onFailure(Throwable arg0) {
        Logger.getLogger("ApplySettingsWizardView").log(Level.SEVERE, "Problem");
      }

      public void onSuccess(String arg0) {
        controller.displayMessage("Serveur démarré !");
      }
    };
    
    controller.startPlatform(callback);
  }
  
}
