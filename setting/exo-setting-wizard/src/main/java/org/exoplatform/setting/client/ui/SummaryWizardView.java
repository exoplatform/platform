package org.exoplatform.setting.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.setting.client.WizardModule;
import org.exoplatform.setting.client.data.InvalidWizardViewFieldException;
import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * View corresponding to <b>STEP 0 - Setup</b>
 * 
 * @author Clement
 *
 */
public class SummaryWizardView extends WizardView {
  
  private Grid summary = new Grid();
  
  public SummaryWizardView(WizardModule gui, int stepNumber) {
    super(gui, stepNumber);
  }

  @Override
  protected String getWizardTitle() {
    return constants.setupSummary();
  }

  @Override
  protected String getWizardDescription() {
    return constants.weAreReady();
  }

  @Override
  protected Widget buildStepToolbar() {
    
    Grid gridToolbar = new Grid(1, 3);
    gridToolbar.setWidth("100%");
    gridToolbar.getColumnFormatter().setWidth(0, "100%");
    gridToolbar.setWidget(0, 0, prepareExportButton());
    gridToolbar.setWidget(0, 1, preparePreviousButton());
    gridToolbar.setWidget(0, 2, prepareNextButton(constants.apply()));
    
    return gridToolbar;
  }

  @Override
  protected Widget buildStepContent() {

    summary = new Grid(1, 2);
    summary.setCellSpacing(6);
    
    return summary;
  }

  @Override
  protected void storeDatas(int toStep) {
    Map<SetupWizardData, String> datas = new HashMap<SetupWizardData, String>();
    
    gui.storeDatas(datas, toStep);
  }
  
  @Override
  protected void verifyDatas() throws InvalidWizardViewFieldException {
    
  }
  
  /***
   * Constructs a button with text string and with step target
   * @param text
   * @param toStep
   * @return build button
   */
  protected Button prepareExportButton() {
    Button button = new Button();
    button.setText("Export");
    button.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        // Todo Export
        gui.displayMessage(constants.notYetImplemented());
      }
    });
    return button;
  }

  @Override
  public void initScreen() {
    //TODO
    /*Map<SetupWizardData, String> setupMap = gui.getSetupWizardDatas();
    if(setupMap != null && setupMap.size() > 0) {
      summary.resize(setupMap.size(), 2);
    
      int row = 0;
      for(Map.Entry<SetupWizardData, String> e : gui.getSetupWizardDatas().entrySet()) {
        summary.setHTML(row, 0, e.getKey().getName());
        summary.setHTML(row, 1, e.getValue());
        row++;
      }
    }*/
  }
  
}
