package org.exoplatform.setting.client.ui.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.client.data.SetupWizardMode;
import org.exoplatform.setting.client.ui.controller.SetupWizardController;
import org.exoplatform.setting.client.ui.model.LdapConfigWizardModel;
import org.exoplatform.setting.shared.WizardFieldVerifier;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LdapWizardView extends WizardView {

  ListBox serverType;
  TextBox providerUrl;
  TextBox baseDn;
  TextBox rootDn;
  PasswordTextBox password;

  private LdapConfigWizardModel model;
  
  
  public LdapWizardView(SetupWizardController controller, int stepNumber, SetupWizardMode mode) {
    super(controller, stepNumber, mode);
    
    model = (LdapConfigWizardModel) getModel();
  }

  @Override
  protected String getWizardTitle() {
    return constants.ldapConfig();
  }

  @Override
  protected String getWizardDescription() {
    return constants.ldapConfigDesc();
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

    serverType = new ListBox();
    providerUrl = new TextBox();
    baseDn = new TextBox();
    rootDn = new TextBox();
    password = new PasswordTextBox();
    
    // Get datasources in model
    List<String> serverTypes = model.getServerTypes();
    for(String type : serverTypes) {
      serverType.addItem(type);
    }

    Grid table = new Grid(5, 2);
    table.setCellSpacing(6);
    table.setHTML(0, 0, constants.serverType());
    table.setWidget(0, 1, serverType);
    table.setHTML(1, 0, constants.providerUrl());
    table.setWidget(1, 1, providerUrl);
    table.setHTML(2, 0, constants.baseDN());
    table.setWidget(2, 1, baseDn);
    table.setHTML(3, 0, constants.rootDN());
    table.setWidget(3, 1, rootDn);
    table.setHTML(4, 0, constants.password());
    table.setWidget(4, 1, password);
    
    return table;
  }

  @Override
  public Map<SetupWizardData, String> verifyDatas(int toStep) throws InvalidWizardViewFieldException {
    
    if(! WizardFieldVerifier.isValidTextField(providerUrl.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidProviderUrl());
    }

    if(! WizardFieldVerifier.isValidTextField(baseDn.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidBaseDn());
    }

    if(! WizardFieldVerifier.isValidTextField(rootDn.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidRootDn());
    }
    
    if(! WizardFieldVerifier.isValidPassword(password.getText())) {
      throw new InvalidWizardViewFieldException(constants.invalidPassword());
    }
    
    Map<SetupWizardData, String> datas = new HashMap<SetupWizardData, String>();
    datas.put(SetupWizardData.LDAP_SERVER_TYPE, serverType.getItemText(serverType.getSelectedIndex()));
    datas.put(SetupWizardData.LDAP_PROVIDER_URL, providerUrl.getText());
    datas.put(SetupWizardData.LDAP_BASE_DN, baseDn.getText());
    datas.put(SetupWizardData.LDAP_ROOT_DN, rootDn.getText());
    datas.put(SetupWizardData.LDAP_PASSWORD, password.getText());
    
    return datas;
  }

  @Override
  public void executeOnDisplay() {
    // TODO Auto-generated method stub
    
  }
}
