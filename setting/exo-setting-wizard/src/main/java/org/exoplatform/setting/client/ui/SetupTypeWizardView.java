package org.exoplatform.setting.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.setting.client.WizardGui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class SetupTypeWizardView extends WizardView {

  public SetupTypeWizardView(WizardGui gui, int stepNumber) {
    super(gui,
          "Select a setup type.", 
          "",
          stepNumber);
  }

  @Override
  protected Widget buildStepToolbar() {
    
    FlowPanel panel = new FlowPanel();
    panel.add(preparePreviousButton());
    panel.add(prepareNextButton());
    
    return panel;
  }

  @Override
  protected Widget buildStepContent() {
    return new HTML("Step 1");
  }

  @Override
  protected void storeDatas(int toStep) {
    
    Map<String, String> datas = new HashMap<String, String>();
    
    gui.storeDatas(datas, toStep);
  }
}
