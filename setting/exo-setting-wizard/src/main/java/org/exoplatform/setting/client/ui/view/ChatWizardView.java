package org.exoplatform.setting.client.ui.view;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.shared.WizardFieldVerifier;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ChatWizardView extends WizardView {

  TextBox ip;
  TextBox port;
  
  
  public ChatWizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    super(controller, stepNumber, mode);
  }

  @Override
  protected String getWizardTitle() {
    return constants.chatServer();
  }

  @Override
  protected String getWizardDescription() {
    return constants.configureChatServer();
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

    ip = new TextBox();
    port = new TextBox();

    Grid table = new Grid(2, 2);
    table.setCellSpacing(6);
    table.setHTML(0, 0, constants.ipHostName());
    table.setWidget(0, 1, ip);
    table.setHTML(1, 0, constants.port());
    table.setWidget(1, 1, port);
    
    return table;
  }

  @Override
  public Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException {
    
    if(! WizardFieldVerifier.isValidTextField(ip.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidIpHostName());
    }

    if(! WizardFieldVerifier.isValidNumberField(port.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidPort());
    }
    
    Map<SetupWizardData, String> datas = new HashMap<SetupWizardData, String>();
    datas.put(SetupWizardData.CHAT_IP, ip.getText());
    datas.put(SetupWizardData.CHAT_PORT, port.getText());
    
    return datas;
  }

  @Override
  public void executeOnDisplay() {
    // TODO Auto-generated method stub
    
  }
}
