package org.exoplatform.setting.client.ui;

import org.exoplatform.setting.client.WizardModule;
import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * View corresponding to <b>STEP 0 - Setup</b>
 * 
 * @author Clement
 *
 */
public class SystemInfoWizardView extends WizardView {
  
  public SystemInfoWizardView(WizardModule gui, int stepNumber) {
    super(gui,
          "Setup", 
          "We have detected the following environement on your server.",
          stepNumber);
  }

  @Override
  protected Widget buildStepToolbar() {
    
    Grid gridToolbar = new Grid(1, 2);
    gridToolbar.setWidth("100%");
    gridToolbar.getColumnFormatter().setWidth(0, "100%");
    gridToolbar.setWidget(0, 1, prepareNextButton("Start"));
    
    return gridToolbar;
  }

  @Override
  protected Widget buildStepContent() {

    Grid advancedOptions = new Grid(2, 2);
    advancedOptions.setCellSpacing(6);
    advancedOptions.setHTML(0, 0, "<b>App Server: </b>");
    advancedOptions.setHTML(0, 1, "Apache Tomcat");
    advancedOptions.setHTML(1, 0, "<b>JVM: </b>");
    advancedOptions.setHTML(1, 1, "Sun 1.6");
    
    return advancedOptions;
  }

  @Override
  protected void storeDatas(int toStep) {
    
    gui.storeDatas(null, toStep);
  }
  
  @Override
  protected void verifyDatas() throws InvalidWizardViewFieldException {
    
  }

  @Override
  public void initScreen() {
  }
  
}
