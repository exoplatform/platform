package org.exoplatform.setting.client.ui;

import org.exoplatform.setting.client.WizardModule;
import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class SetupTypeWizardView extends WizardView {

  private RadioButton standard;
  private RadioButton advanced;
  
  public SetupTypeWizardView(WizardModule gui, int stepNumber) {
    super(gui,
          "Select a setup type.", 
          "",
          stepNumber);
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

    standard = new RadioButton("WizardSetupType", "Standard (Recommended)");
    advanced = new RadioButton("WizardSetupType", "Advanced");
    
    if(gui.getSetupWizardMode().equals(SetupWizardMode.STANDARD)) {
      standard.setValue(true);
    }
    else {
      advanced.setValue(true);
    }

    Grid advancedOptions = new Grid(5, 1);
    advancedOptions.setCellSpacing(6);
    advancedOptions.setWidget(0, 0, standard);
    advancedOptions.setWidget(1, 0, new HTML("Display standard options that most administrators have to configure."));
    advancedOptions.getCellFormatter().setHeight(2, 0, "50px");
    advancedOptions.setWidget(3, 0, advanced);
    advancedOptions.setWidget(4, 0, new HTML("Display all advanced options, like JCR cache & indexer."));
    
    return advancedOptions;
  }

  @Override
  protected void storeDatas(int toStep) {
    
    // Change mode
    if(standard.getValue().equals(true)) {
      gui.setSetupWizardMode(SetupWizardMode.STANDARD);
    }
    else {
      gui.setSetupWizardMode(SetupWizardMode.ADVANCED);
    }
    
    gui.storeDatas(null, toStep);
  }
  
  @Override
  protected void verifyDatas() throws InvalidWizardViewFieldException {
    
  }

  @Override
  public void initScreen() {
    
  }
}
