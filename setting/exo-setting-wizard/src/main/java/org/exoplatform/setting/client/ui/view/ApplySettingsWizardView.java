package org.exoplatform.setting.client.ui.view;

import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * View corresponding to <b>STEP 0 - Setup</b>
 * 
 * @author Clement
 *
 */
public class ApplySettingsWizardView extends WizardView {
  
  public ApplySettingsWizardView(SetupWizardController controller, int stepNumber) {
    super(controller, stepNumber);
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
  
}
