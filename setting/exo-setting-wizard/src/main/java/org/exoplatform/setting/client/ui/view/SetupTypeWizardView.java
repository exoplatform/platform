package org.exoplatform.setting.client.ui.view;

import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class SetupTypeWizardView extends WizardView {

  private RadioButton standard;
  private RadioButton advanced;
  
  public SetupTypeWizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    super(controller, stepNumber, mode);
  }

  @Override
  protected String getWizardTitle() {
    return constants.selectSetupType();
  }

  @Override
  protected String getWizardDescription() {
    return "";
  }

  @Override
  protected Widget buildStepToolbar() {

    Grid gridToolbar = new Grid(1, 3);
    gridToolbar.setWidth("100%");
    gridToolbar.getColumnFormatter().setWidth(0, "100%");
    gridToolbar.setWidget(0, 1, preparePreviousButton());
    gridToolbar.setWidget(0, 2, prepareNextButton());
    
    return gridToolbar;
  }

  @Override
  protected Widget buildStepContent() {

    standard = new RadioButton("WizardSetupType", constants.standard());
    advanced = new RadioButton("WizardSetupType", constants.advanced());
    
    if(controller.getSetupWizardMode().equals(SetupWizardMode.STANDARD)) {
      standard.setValue(true);
    }
    else {
      advanced.setValue(true);
    }

    Grid advancedOptions = new Grid(5, 1);
    advancedOptions.setCellSpacing(6);
    advancedOptions.setWidget(0, 0, standard);
    advancedOptions.setWidget(1, 0, new HTML(constants.displayStandard()));
    advancedOptions.getCellFormatter().setHeight(2, 0, "50px");
    advancedOptions.setWidget(3, 0, advanced);
    advancedOptions.setWidget(4, 0, new HTML(constants.displayAll()));
    
    return advancedOptions;
  }

  @Override
  public Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException {
    
    // Change mode
    if(standard.getValue().equals(true)) {
      controller.setSetupWizardMode(SetupWizardMode.STANDARD);
    }
    else {
      controller.setSetupWizardMode(SetupWizardMode.ADVANCED);
    }
    
    return null;
  }
}
