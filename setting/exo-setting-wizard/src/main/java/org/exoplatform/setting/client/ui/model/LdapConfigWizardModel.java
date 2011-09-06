package org.exoplatform.setting.client.ui.model;

import java.util.LinkedList;
import java.util.List;

import org.exoplatform.setting.client.ui.controller.SetupWizardController;

public class LdapConfigWizardModel extends WizardModel {

  private List<String> serverTypes;

  public LdapConfigWizardModel(SetupWizardController controller, int screenNumber) {
    super(controller, screenNumber);
  }

  @Override
  public void initDatas() {
    serverTypes = new LinkedList<String>();
    serverTypes.add("Apache Directory Server");
    serverTypes.add("OpenLDAP");
    serverTypes.add("OpenDS");
    
    // Indicate to controller that model is loaded
    controller.fireModelLoaded(screenNumber);
  }

  public List<String> getServerTypes() {
    return serverTypes;
  }

}
