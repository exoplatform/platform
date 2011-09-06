package org.exoplatform.setting.client.ui.view;

import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class IdmSetupWizardView extends WizardView {
  
  RadioButton dbRadio;
  RadioButton ldapRadio;

  public IdmSetupWizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    super(controller, stepNumber, mode);
  }

  @Override
  protected String getWizardTitle() {
    return constants.idmSetup();
  }

  @Override
  protected String getWizardDescription() {
    return constants.idmSetupDesc();
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

    dbRadio = new RadioButton("IdmSetup", constants.database());
    dbRadio.setValue(true);
    ldapRadio = new RadioButton("IdmSetup", constants.ldap());
    
    Grid table = new Grid(2, 1);
    table.setCellSpacing(6);
    table.setWidget(0, 0, dbRadio);
    table.setWidget(1, 0, ldapRadio);
    //table.getCellFormatter().setHeight(0, 0, "50px");
    
    return table;
  }

  @Override
  public Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void executeOnDisplay() {
    // TODO Auto-generated method stub

  }

}
