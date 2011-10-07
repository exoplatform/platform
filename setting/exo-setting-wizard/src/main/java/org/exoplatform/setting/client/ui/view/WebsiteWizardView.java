package org.exoplatform.setting.client.ui.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class WebsiteWizardView extends WizardView {

  RadioButton blankPortal;
  RadioButton sample;
  
  CheckBox cbAcme;
  CheckBox cbDefault;
  CheckBox cbIntranet;
  
  
  public WebsiteWizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    super(controller, stepNumber, mode);
  }

  @Override
  protected String getWizardTitle() {
    return constants.installWebsite();
  }

  @Override
  protected String getWizardDescription() {
    return constants.installWebsiteToYourPortal();
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

    blankPortal = new RadioButton("WebsiteWizard", constants.startBlankPortal());
    sample = new RadioButton("WebsiteWizard", constants.installSample());

    Grid table2 = new Grid(3, 1);
    cbAcme = new CheckBox("ACME");
    cbDefault = new CheckBox("Default");
    cbIntranet = new CheckBox("Intranet");
    table2.setWidget(0, 0, cbAcme);
    table2.setWidget(1, 0, cbDefault);
    table2.setWidget(2, 0, cbIntranet);
    
    Grid table = new Grid(2, 2);
    table.setCellSpacing(6);
    table.setWidget(0, 0, blankPortal);
    table.setWidget(1, 0, sample);
    table.setWidget(1, 1, table2);
    
    return table;
  }

  @Override
  public Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException {
    
    Map<SetupWizardData, String> datas = new HashMap<SetupWizardData, String>();
    datas.put(SetupWizardData.WS_BLANK, blankPortal.getValue().toString());
    if(sample.getValue().equals(Boolean.TRUE)) {
      List<String> lstSamples = new LinkedList<String>();
      if(cbAcme.getValue().equals(Boolean.TRUE)) {
        lstSamples.add(cbAcme.getText());
      }
      if(cbDefault.getValue().equals(Boolean.TRUE)) {
        lstSamples.add(cbDefault.getText());
      }
      if(cbIntranet.getValue().equals(Boolean.TRUE)) {
        lstSamples.add(cbIntranet.getText());
      }
      StringBuffer buf = new StringBuffer();
      for(int i=0; i<lstSamples.size(); i++) {
        buf.append(lstSamples.get(i));
        if(i < lstSamples.size() - 1) {
          buf.append(",");
        }
      }
      datas.put(SetupWizardData.WS_SAMPLES, buf.toString());
    }
    else {
      datas.put(SetupWizardData.WS_SAMPLES, null);
    }
    return datas;
  }

  @Override
  public void executeOnDisplay() {
    // TODO Auto-generated method stub
    
  }
}
