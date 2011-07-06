package org.exoplatform.setting.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.setting.client.WizardGui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * View corresponding to <b>STEP 0 - Setup</b>
 * 
 * @author Clement
 *
 */
public class SetupWizardView extends WizardView {
  
  private TextBox data1;
  private TextBox data2;
  
  public SetupWizardView(WizardGui gui, int stepNumber) {
    super(gui,
          "Setup", 
          "We have detected the following environement on your server.",
          stepNumber);
  }

  @Override
  protected Widget buildStepToolbar() {
    
    FlowPanel panel = new FlowPanel();
    panel.add(prepareNextButton());
    
    return panel;
  }

  @Override
  protected Widget buildStepContent() {
    
    data1 = new TextBox();
    data2 = new TextBox();

    Grid advancedOptions = new Grid(2, 2);
    advancedOptions.setCellSpacing(6);
    advancedOptions.setHTML(0, 0, "Name: ");
    advancedOptions.setWidget(0, 1, data1);
    advancedOptions.setHTML(1, 0, "Description: ");
    advancedOptions.setWidget(1, 1, data2);
    
    return advancedOptions;
  }

  @Override
  protected void storeDatas(int toStep) {
    Map<String, String> datas = new HashMap<String, String>();
    datas.put("name", data1.getText());
    datas.put("description", data2.getText());
    
    gui.storeDatas(datas, toStep);
  }
  
}
