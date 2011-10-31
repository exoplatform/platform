package org.exoplatform.setting.client.ui.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.client.ui.model.DatabaseJcrWizardModel;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class JcrDBWizardView extends WizardView {

  RadioButton chooseDsRadio;
  RadioButton setDsRadio;
  ListBox dsList;
  TextBox newDsText;
  CheckBox storeFilesCheck;

  private DatabaseJcrWizardModel model;
  
  
  public JcrDBWizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    super(controller, stepNumber, mode);
    
    model = (DatabaseJcrWizardModel) getModel();
  }

  @Override
  protected String getWizardTitle() {
    return constants.jcrDbSetup();
  }

  @Override
  protected String getWizardDescription() {
    return constants.jcrDbSetupDesc();
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

    chooseDsRadio = new RadioButton("jcrDbSetup", constants.chooseDs());
    setDsRadio = new RadioButton("jcrDbSetup", constants.setYourDs());
    dsList = new ListBox();
    newDsText = new TextBox();
    storeFilesCheck = new CheckBox(constants.storeFilesInDb());
    chooseDsRadio.setValue(true);
    
    // Get datasources in model
    List<String> dss = model.getDatasources();
    if(dss != null && dss.size() > 0) {
      for(String ds : dss) {
        dsList.addItem(ds);
      }
    }
    else {
      dsList.addItem(constants.noDs());
      dsList.setEnabled(false);
      chooseDsRadio.setEnabled(false);
      setDsRadio.setValue(true);
    }

    Grid table = new Grid(3, 2);
    table.setCellSpacing(6);
    table.setWidget(0, 0, chooseDsRadio);
    table.setWidget(0, 1, dsList);
    table.setWidget(1, 0, setDsRadio);
    table.setWidget(1, 1, newDsText);
    table.setWidget(2, 0, storeFilesCheck);
    table.getCellFormatter().setHeight(2, 0, "50px");
    
    return table;
  }

  @Override
  public Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException {
    
    String datasource = "";
    if(setDsRadio.getValue().equals(true)) {
      if(newDsText.getText() == null || newDsText.getText().equals("")) {
        throw new InvalidWizardViewFieldException(constants.indicateYourDs());
      }
      else {
        datasource = newDsText.getText();
      }
    }
    else {
      datasource = dsList.getItemText(dsList.getSelectedIndex());
    }

    Map<SetupWizardData, String> datas = new HashMap<SetupWizardData, String>();
    datas.put(SetupWizardData.JCR_DATA_SOURCE, datasource);
    datas.put(SetupWizardData.STORE_FILES_IN_DB, String.valueOf(storeFilesCheck.getValue()));
    
    return datas;
  }

  @Override
  public void executeOnDisplay() {
    // TODO Auto-generated method stub
    
  }
}
