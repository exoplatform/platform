package org.exoplatform.setting.client.ui.view;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.shared.WizardFieldVerifier;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MailWizardView extends WizardView {

  TextBox smtpHost;
  TextBox port;
  CheckBox isSecured;
  TextBox userName;
  PasswordTextBox password;
  TextBox email;
  
  public MailWizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    super(controller, stepNumber, mode);
  }

  @Override
  protected String getWizardTitle() {
    return constants.mailSettings();
  }

  @Override
  protected String getWizardDescription() {
    return constants.mailSettingDesc();
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

    smtpHost = new TextBox();
    port = new TextBox();
    isSecured = new CheckBox(constants.securedConnection());
    userName = new TextBox();
    password = new PasswordTextBox();
    email = new TextBox();

    Grid table = new Grid(6, 2);
    table.setCellSpacing(6);
    table.setHTML(0, 0, constants.smtpHost());
    table.setWidget(0, 1, smtpHost);
    table.setHTML(1, 0, constants.port());
    table.setWidget(1, 1, port);
    table.setWidget(2, 0, isSecured);
    table.setHTML(3, 0, constants.userName());
    table.setWidget(3, 1, userName);
    table.setHTML(4, 0, constants.password());
    table.setWidget(4, 1, password);
    table.setHTML(5, 0, constants.email());
    table.setWidget(5, 1, email);
    
    return table;
  }

  @Override
  public Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException {
    
    if(! WizardFieldVerifier.isValidTextField(smtpHost.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidSmtpHost());
    }

    if(! WizardFieldVerifier.isValidNumberField(port.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidPort());
    }

    if(! WizardFieldVerifier.isValidTextField(userName.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidUserName());
    }

    if(! WizardFieldVerifier.isValidPassword(password.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidPassword());
    }

    if(! WizardFieldVerifier.isValidEmail(email.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidMail());
    }
    
    Map<SetupWizardData, String> datas = new HashMap<SetupWizardData, String>();
    datas.put(SetupWizardData.SMTP_HOST, smtpHost.getText());
    datas.put(SetupWizardData.SMTP_PORT, port.getText());
    datas.put(SetupWizardData.SMTP_SECURED_CONNECTION, isSecured.getValue().toString());
    datas.put(SetupWizardData.SMTP_USERNAME, userName.getText());
    datas.put(SetupWizardData.SMTP_PASSWORD, password.getText());
    datas.put(SetupWizardData.SMTP_EMAIL, email.getText());
    
    return datas;
  }

  @Override
  public void executeOnDisplay() {
    // TODO Auto-generated method stub
    
  }
}
