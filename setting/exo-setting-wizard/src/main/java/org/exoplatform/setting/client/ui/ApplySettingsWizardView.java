package org.exoplatform.setting.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.setting.client.WizardModule;
import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
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
  
  public ApplySettingsWizardView(WizardModule gui, int stepNumber) {
    super(gui,
          "Apply settings", 
          "Please wait a few minutes while your server is restarting." +
          "<br />You can watch the log file at /var/logs/exo/startup.log",
          stepNumber);
  }

  @Override
  protected Widget buildStepToolbar() {
    
    FlowPanel panel = new FlowPanel();
    panel.add(prepareButton("Finish", 0));
    
    return panel;
  }

  @Override
  protected Widget buildStepContent() {

    Grid advancedOptions = new Grid(4, 2);
    advancedOptions.setCellSpacing(6);
    
    return advancedOptions;
  }

  @Override
  protected void storeDatas(int toStep) {
    Map<SetupWizardData, String> datas = new HashMap<SetupWizardData, String>();
    
    gui.storeDatas(datas, toStep);
  }
  
  @Override
  protected void verifyDatas() throws InvalidWizardViewFieldException {
    
  }

  @Override
  public void initScreen() {
    // TODO Auto-generated method stub
    
  }
  
}
